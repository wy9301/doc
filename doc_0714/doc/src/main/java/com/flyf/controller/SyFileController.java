package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyf.entity.MyFile;
import com.flyf.entity.Statistics;
import com.flyf.entity.SyFile;
import com.flyf.entity.User;
import com.flyf.service.MyFileService;
import com.flyf.service.QiniuService;
import com.flyf.service.StatisticsService;
import com.flyf.service.SyFileService;
import com.qiniu.common.QiniuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/syfile")
public class SyFileController {
    @Autowired
    SyFileService syFileService;
    @Autowired
    StatisticsService statisticsService;
    @Autowired
    MyFileService myFileService;
    @Autowired
    private QiniuService qiniuService;

    /*批量删除文件*/
    @PostMapping("/deletelist")
    public String deletelist(@RequestParam(value = "checkData[]") ArrayList<Integer> checkData, HttpSession session) throws QiniuException {
        System.out.println(checkData);
        for ( int id:checkData) {
            delete(id,session);
        }
        return "redirect:/syfile/file_list";
    }
    /*删除文件*/
    @PostMapping("/delete")
    public String delete(int id, HttpSession session) throws QiniuException {
        QueryWrapper<SyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("id", id);
        List<SyFile> list1 = null;
        list1 = syFileService.list(wrapper1);
        String address = list1.get(0).getAddress();
        //删除七牛云上的文档
        File tempFile= new File(address);
        String fileName=tempFile.getName();
        qiniuService.delete(fileName);
        /*String path = System.getProperty("user.dir")+"\\forfile\\";
        deleteFile(path, address);*/
        boolean bool = syFileService.removeById(id);
        if (bool) {
            return "redirect:/syfile/file_list";
        }
        return null;
    }
    /*删除文件的实现*/
    public  boolean deleteFile(String path, String fileName) {
        File directory = new File(path);
        File[] files = directory.listFiles();//把目录directory下的所有文件放在数组files
        if (files.length == 0) {
            return false;
        }
        for (File file : files) {
            if (file.getName().equals(fileName)) { //若文件名与待删除文件名相同，则删除文件
                file.delete();
                return true;
            }
        }
        return false;
    }

    /*查找文件*/
    @PostMapping("/search")
    public String search(Model model, String sname, HttpSession session) {
        QueryWrapper<SyFile> wrapper = new QueryWrapper<>();
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        User user = (User) session.getAttribute("user");
        if(user.getRight1()==1) {
            wrapper.like("name", sname);
            List<SyFile> list = syFileService.list(wrapper);
            model.addAttribute("list1", list);
            model.addAttribute("user", user);
            return "user_syfile";
        }
        else{
            wrapper.like("name", sname);
            List<SyFile> list = syFileService.list(wrapper);
            model.addAttribute("list1", list);
            model.addAttribute("user", user);
            return "admin_allfile";
        }
    }
    @GetMapping("/file_list")
    public String file_list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        QueryWrapper<SyFile> wrapper1 = new QueryWrapper<>();
        List<SyFile> list1 = null;
        list1 =syFileService.list(wrapper1);
        model.addAttribute("list1", list1);
        model.addAttribute("user",user);

        if(user.getRight1()==1)
            return "user_syfile";
        else
        return "admin_allfile";
    }

    /*修改文件名称*/
    @PostMapping("/changename")
    public String changename(int id, String new_name,HttpSession session) {
        SyFile syFile = syFileService.getById(id);
        syFile.setName(new_name);
        boolean bool = syFileService.updateById(syFile);
        if (bool) {
            User user=(User)session.getAttribute("user");
                return "redirect:/syfile/file_list";
        }
        return null;
    }
    /*上传文件*/
    @PostMapping("/upload")
    public String uploadFile(HttpSession session, @RequestParam(value = "head") MultipartFile file) throws IOException
    {
        if (StringUtils.isNotBlank(file.getOriginalFilename()))
        {
            // String filePath = qiniuService.uploadFile(file.getInputStream());
            String filePath = qiniuService.uploadFile(file);
            String filename = file.getOriginalFilename();// 文件名+后缀名
            String[] str = filename.split("\\.");
            int Index = str.length - 1;
            File dest = new File(filePath);
            SyFile syFile = new SyFile();
            User user = (User) session.getAttribute("user");
            syFile.setName(str[Index - 1]);//文件名
            syFile.setCategory(str[Index]);//后缀名
            LocalDateTime date1 = LocalDateTime.now();
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = date1.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
            Date date = Date.from(zdt.toInstant());
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            syFile.setUpdatetime(sqlDate);
            syFile.setMaster(user.getUsername());
            syFile.setAddress(filePath);
            long length = file.getSize();
            String printSize = getPrintSize(length);
            syFile.setSize(printSize);
            syFileService.save(syFile);
            QueryWrapper<Statistics> wrapper = new QueryWrapper<>();
            wrapper.eq("theday", sqlDate);
            Statistics sta = statisticsService.getOne(wrapper);
            if (sta == null) {
                Statistics new_sta = new Statistics();
                new_sta.setTheday(sqlDate);
                new_sta.setUpload(1);
                new_sta.setDownload(0);
                statisticsService.save(new_sta);
            } else {
                sta.setUpload(sta.getUpload() + 1);
                boolean bool = statisticsService.update(sta, wrapper);
            }
        }
        return "redirect:/syfile/file_list";
    }

    public String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }
    /*@PostMapping("/upload")
    public String upload(HttpSession session, @RequestParam("head") MultipartFile file) throws IOException {
        if (StringUtils.isNotBlank(file.getOriginalFilename())) {

            String picName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filePath = System.getProperty("user.dir")+"\\forfile\\" + picName;//在后台存储的名字
            String filename = file.getOriginalFilename();// 文件名+后缀名
            String[] str = filename.split("\\.");

            System.out.println(filePath);
            *//*存储到本地*//*
            int Index = str.length - 1;
            File dest = new File(filePath);
            file.transferTo(dest);
            *//*存储到数据库*//*
            SyFile syFile = new SyFile();
            User user = (User) session.getAttribute("user");
            syFile.setName(str[Index - 1]);//文件名
            syFile.setCategory(str[Index]);//后缀名
            *//*计算时间 localdatetime-->util.date-->sql.date*//*
            LocalDateTime date1 = LocalDateTime.now();
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date2Str = formatter2.format(date1);
            *//*java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());*//*

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = date1.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
            Date date = Date.from(zdt.toInstant());
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            syFile.setUpdatetime(sqlDate);
            syFile.setMaster(user.getUsername());
            syFile.setAddress(picName);
            *//*计算文件大小*//*
            long length = dest.length();
            String printSize = getPrintSize(length);
            syFile.setSize(printSize);
            syFileService.save(syFile);
            *//*统计
             * 查询是否存在今天的记录，有则改之，无则创建
             * *//*
            QueryWrapper<Statistics> wrapper = new QueryWrapper<>();
            wrapper.eq("theday", sqlDate);
            Statistics sta = statisticsService.getOne(wrapper);
            if (sta == null) {
                Statistics new_sta = new Statistics();
                new_sta.setTheday(sqlDate);
                new_sta.setUpload(1);
                new_sta.setDownload(0);
                statisticsService.save(new_sta);
            } else {
                sta.setUpload(sta.getUpload() + 1);
                boolean bool = statisticsService.update(sta, wrapper);
            }
        }
            return "redirect:/syfile/file_list";
    }

    public String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }*/

    /*下载文件*/
    /*@RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody*/
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void download(int id, HttpServletResponse resp) throws IOException {
        QueryWrapper<SyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.like("id", id);
        List<SyFile> list1 = null;
        list1 = syFileService.list(wrapper1);
        String downloadName = list1.get(0).getName()+"."+list1.get(0).getCategory();
        String name = list1.get(0).getAddress();
        File tempFile= new File(name);
        String fileName=tempFile.getName();
        String encodedFileName=qiniuService.download(fileName,resp);
        String realPath = System.getProperty("user.dir")+"\\forfile\\";//暂存
        String path = realPath +  encodedFileName;//加上文件名称
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("文件不存在");
        }
        resp.reset();
        resp.setContentType("application/octet-stream");
        resp.setCharacterEncoding("utf-8");
        resp.setContentLength((int) file.length());
        resp.setHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes("gbk"), "iso8859-1"));
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deleteFile(realPath,encodedFileName);
        /*统计
         * 查询是否存在今天的记录，有则改之，无则创建
         * */
        LocalDateTime date1 = LocalDateTime.now();
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date3Str = formatter3.format(date1);
        QueryWrapper<Statistics> wrapper = new QueryWrapper<>();
        wrapper.eq("theday", date3Str);
        Statistics sta = statisticsService.getOne(wrapper);
        sta.setDownload(sta.getDownload() + 1);
        boolean bool = statisticsService.update(sta, wrapper);
    }

}
