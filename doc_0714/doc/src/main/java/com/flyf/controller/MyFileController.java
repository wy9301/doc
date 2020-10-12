package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flyf.entity.*;
import com.flyf.service.*;
import com.qiniu.common.QiniuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.io.File;

@Controller
@RequestMapping("/myfile")
public class MyFileController {
    @Autowired
    MyFileService myFileService;
    @Autowired
    RelationService relationService;
    @Autowired
    UserService userService;
    @Autowired
    StatisticsService statisticsService;
    @Autowired
    CodeService codeService;
    @Autowired
    private QiniuService qiniuService;

    /*用户操作*/
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        int recycle = 0;//列表显示未在回收站的文件
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("master", user.getUsername()).eq("recycle",recycle);
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        model.addAttribute("list1", list1);

        List<User> list3 = findfriend(user);//list3拿到了好友userlist
        model.addAttribute("list2", list3);
        model.addAttribute("user", user);
        return "allfile1";
    }

    /*展示好友列表   friend_list*/
    @GetMapping("/friend_list")
    public String friend_list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<User> list3 = findfriend(user);//list3拿到了好友userlist
        model.addAttribute("list1", list3);
        model.addAttribute("user", user);
        return "friend_list";
    }

    public List<User> findfriend(User user) {
        QueryWrapper<Relation> wrapper2 = new QueryWrapper<>();
        /*wrapper2.eq("id1", user.getId()).or().eq("id2", user.getId());
        List<Relation> list2 = null;
        list2 = relationService.list(wrapper2);
        List<User> list3 = new ArrayList<>();
        User tep =null;
        for (Relation i : list2) {
            QueryWrapper<User> wrapper3 = new QueryWrapper<>();//必须重新new，否则第二次sql语句会变成and
            if (i.getId1() != user.getId()) {
                wrapper3.eq("id", i.getId1());
            } else {
                wrapper3.eq("id", i.getId2());
            }
            tep = userService.getOne(wrapper3);//查看用户是否可见此文件，若是则不动；若否，则进入待选择列表
            if (tep != null) {
                list3.add(tep);
            }
        }//list3拿到了好友userlist*/
        wrapper2.eq("id1", user.getId());
        List<Relation> list2 = null;
        list2 = relationService.list(wrapper2);
        List<User> list3 = new ArrayList<>();
        User tep = null;
        for (Relation i : list2) {
            QueryWrapper<User> wrapper3 = new QueryWrapper<>();//必须重新new，否则第二次sql语句会变成and
            wrapper3.eq("id", i.getId2());
            tep = userService.getOne(wrapper3);//查看用户是否可见此文件，若是则不动；若否，则进入待选择列表
            if (tep != null) {
                list3.add(tep);
            }
        }
        return list3;
    }

    /*显示所有 我可见的 由他人分享的文件*/
    @GetMapping("/share_list")
    public String share_list(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.like("visible", "," + user.getId() + ",");//找到符合的文件，生成列表；找到符合的文件主人，生成列表；
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        List<User> list3 = findfriend(user);//list3拿到了好友userlist
        model.addAttribute("list2", list3);
        model.addAttribute("list1", list1);
        model.addAttribute("user", user);
        return "sharefile";
    }


    /*修改文件可见性*/
    @PostMapping("/share")//str 新可见人id，share_col文件id    3,2,1,   1,
    public String share(String str, String share_col, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println(str);
        System.out.println(share_col);
        int id = Integer.valueOf(share_col.trim());
        MyFile one = myFileService.getById(id);
        if (one.getVisible() == null || one.getVisible().trim() == "" || one.getVisible().isEmpty() || one.getVisible().isBlank()) {
            str = "," + str ;
            one.setVisible(str);
        } else {
            List<String> result = Arrays.asList(str.split(","));
            str = "";
            for (String item : result) {//如果原字段中包含此人（已可见），那就不动，否则添到添加str中
                if (one.getVisible().contains("," + item + ",")) {
                    continue;
                } else {
                    str = str + item + ",";
                }
            }
            if (str != null || str != "")
                one.setVisible(one.getVisible() + str);
        }
        myFileService.updateById(one);
        //找到符合的文件，生成列表；找到符合的文件主人，生成列表；
        /*

        SELECT doc_user.username from doc_user,doc_file where visible like '%,7,%' and doc_user.id = doc_file.`master`

        *//*
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        model.addAttribute("list1", list1);
        model.addAttribute("user", user);*/
        return "redirect:/myfile/list";
    }

    /*查找 我可见的 由他人分享的文件*/
    @PostMapping("/search_share")
    public String search_share(Model model, String sname, HttpSession session) {
        QueryWrapper<MyFile> wrapper = new QueryWrapper<>();
        User user = (User) session.getAttribute("user");
        wrapper.like("name", sname).like("visible", "," + user.getId() + ",");//而且被分享
        List<MyFile> list = myFileService.list(wrapper);
        model.addAttribute("list1", list);
        model.addAttribute("user", user);
        return "allfile1";
    }




    /*
    *

     @PostMapping("/delete")
    public String delete(int id, HttpSession session) {
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("id", id);
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        String address = list1.get(0).getAddress();

        String path = "C:\\Users\\hp\\IdeaProjects\\doc\\forfile\\";
        deleteFile(path, address);

        boolean bool = myFileService.removeById(id);
        if (bool) {
            User user=(User)session.getAttribute("user");
            if(user.getRight1()==1)
                return "redirect:/myfilest";
            else
                return "redirect:/admin/file_list";
        }
        return null;
    }

    * */


    /*原删除文件*/
    @PostMapping("/delete2")
    public String delete2(int id, HttpSession session) throws QiniuException {

        QueryWrapper<MyFile> wrapper5 = new QueryWrapper<>();
        wrapper5.eq("id", id);
        List<MyFile> list1 = null;
        System.out.println(wrapper5);
        list1 = myFileService.list(wrapper5);
        String address = list1.get(0).getAddress();
        String path = System.getProperty("user.dir")+"\\forfile\\";
        deleteFile(path, address);

        QueryWrapper<Code> wrapper6 = new QueryWrapper<>();
        wrapper6.eq("docid", id);
        codeService.remove(wrapper6);

        boolean bool = myFileService.removeById(id);
//        if (bool) {
//            User user = (User) session.getAttribute("user");
//            if (user.getRight1() == 1)
//                return "redirect:/myfile/list";
//            else
//                return "redirect:/admin/file_list";
//        }
        return null;
    }

    /*批量删除文件*/
    @PostMapping("/deletelist")
    public String deletelist(@RequestParam(value = "checkData[]") ArrayList<Integer> checkData, HttpSession session) {
        System.out.println(checkData);
        for (int id : checkData) {
            delete(id, session);
        }
        return "redirect:/myfile/list";
    }


    @PostMapping("/deletelist_share")
    public String deletelist_share(@RequestParam(value = "checkData[]") ArrayList<Integer> checkData, HttpSession session) {
        System.out.println(checkData);
        User user = (User) session.getAttribute("user");
        for (int id : checkData) {
            QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("id", id);
            MyFile one = myFileService.getOne(wrapper1);
            String old_v = one.getVisible();
            String temp = user.getId() + ",";
            old_v = old_v.replace(temp, "");
            System.out.println(old_v);
            one.setVisible(old_v);
            myFileService.updateById(one);
            /*delete(id,session);*/
        }

        return "redirect:/myfile/share_list";
    }

    /*删除文件的实现*/
    public boolean deleteFile(String path, String fileName) {
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

//    @PostMapping("/addfriend")
//    public String add(String name, String birthday) {
//        MyFile myFile = new MyFile();
//        myFile.setName(name);
//        boolean bool = myFileService.save(myFile);
//        if (bool) {
//            return "redirect:/myfile/list";
//        }
//        return null;
//    }

    /*查找文件*/
    @PostMapping("/search")
    public String search(Model model, String sname, HttpSession session) {
        QueryWrapper<MyFile> wrapper = new QueryWrapper<>();
        User user = (User) session.getAttribute("user");
        if (user.getRight1() == 1) {
            wrapper.like("name", sname).eq("master", user.getUsername());//而且拥有著是本人
            List<MyFile> list = myFileService.list(wrapper);
            model.addAttribute("list1", list);
            model.addAttribute("user", user);
            return "allfile1";
        } else {
            wrapper.like("name", sname);
            List<MyFile> list = myFileService.list(wrapper);
            model.addAttribute("list1", list);
            model.addAttribute("user", user);
            return "admin_allfile";
        }
    }

    /*修改文件名称*/
    @PostMapping("/changename")
    public String changename(int id, String new_name, HttpSession session) {
        MyFile myFile = myFileService.getById(id);
        myFile.setName(new_name);
        boolean bool = myFileService.updateById(myFile);
        if (bool) {
            User user = (User) session.getAttribute("user");
            if (user.getRight1() == 1)
                return "redirect:/myfile/list";
            else
                return "redirect:/admin/file_list";
        }
        return null;
    }

    /*上传文件*/
    @PostMapping("/upload")
    public String upload(HttpSession session, @RequestParam("head") MultipartFile file) throws IOException {
        if (StringUtils.isNotBlank(file.getOriginalFilename())) {

            //String picName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filePath = qiniuService.uploadFile(file);
            System.out.println("++++++++++++++++++++++++++++++++++++++++-----------------------------------------------" +
                    ""+filePath);
            //System.getProperty("user.dir")+"\\forfile\\" + picName;//在后台存储的名字
            String filename = file.getOriginalFilename();// 文件名+后缀名
            String[] str = filename.split("\\.");
            /*存储到本地*/
            int Index = str.length - 1;
            /*File dest = new File(filePath);
            file.transferTo(dest);*/
            /*存储到数据库*/
            MyFile myFile = new MyFile();
            User user = (User) session.getAttribute("user");
            myFile.setName(str[Index - 1]);//文件名
            myFile.setCategory(str[Index]);//后缀名
            /*计算时间 localdatetime-->util.date-->sql.date*/
            LocalDateTime date1 = LocalDateTime.now();
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date2Str = formatter2.format(date1);
            /*java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());*/

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = date1.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
            Date date = Date.from(zdt.toInstant());
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            myFile.setUpdatetime(sqlDate);
            myFile.setMaster(user.getUsername());
            myFile.setAddress(filePath);
            /*计算文件大小*/
            long length = file.getSize();
            String printSize = getPrintSize(length);
            myFile.setSize(printSize);
            myFileService.save(myFile);
            /*统计
             * 查询是否存在今天的记录，有则改之，无则创建
             * */
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
        /*User user = (User) session.getAttribute("user");
        if (user.getRight1() == 1)*/
            return "redirect:/myfile/list";
        /*else
            return "redirect:/admin/file_list";*/
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

    /*下载文件*/

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void download(int id, HttpServletResponse resp) throws IOException {
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.like("id", id);
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        String downloadName = list1.get(0).getName() + "." + list1.get(0).getCategory();
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
    /**/
    /*回收箱列表*/
    @GetMapping("/recycle_list")
    public String recycle_list(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        int recycle = 1;//列表显示在回收站的文件
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("master", user.getUsername()).eq("recycle",recycle);
        List<MyFile> list1 = null;
        list1 = myFileService.list(wrapper1);
        for( MyFile file : list1){
            //System.out.println(file);
            java.sql.Date recycletime = file.getRecycletime();
            java.sql.Date nowtime = new java.sql.Date(System.currentTimeMillis());
            long day=7 - (nowtime.getTime()-recycletime.getTime())/(24*60*60*1000);
            if(day<0)
                day=0;
            file.setDays(day);
        }
        //System.out.println(list1);
        model.addAttribute("list1", list1);
        List<User> list3 = findfriend(user);//st3拿到了好友userlist
        model.addAttribute("list2", list3);
        model.addAttribute("user", user);
        return "recycle_file";
    }

    /*用户操作删除文件，将文件放到回收站内*/
    @PostMapping("/delete")
    @ResponseBody
    public String delete(int id, HttpSession session) {
        MyFile myFile = myFileService.getById(id);
        int recycle = 1;
        myFile.setRecycle(recycle);

        java.sql.Date recyletime = new java.sql.Date(System.currentTimeMillis());
        //System.out.println(recyletime);
        myFile.setRecycletime(recyletime);
        boolean bool = myFileService.updateById(myFile);
//        if (bool) {
//            User user = (User) session.getAttribute("user");
//            if (user.getRight1() == 1)
//                return "redirect:/myfilest";
//            else
//                return "redirect:/admin/file_list";
//        }
        //删除所选文件的分享码
        QueryWrapper<Code> wrapper6 = new QueryWrapper<>();
        wrapper6.eq("docid", id);
        codeService.remove(wrapper6);

        return null;
    }

    /*用户操作恢复文件*/
    @PostMapping("/recover")
    @ResponseBody
    public String recover(int id, HttpSession session) {
        MyFile myFile = myFileService.getById(id);
        System.out.println(myFile);
        int recycle = 0;
        myFile.setRecycle(recycle);
        /*int days = -1;
        myFile.setDays(days);*/
        myFileService.updateById(myFile);
        return null;
    }
}
