package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.flyf.entity.Statistics;
import com.flyf.entity.SyFile;
import com.flyf.entity.User;
import com.flyf.service.QiniuService;
import com.flyf.service.StatisticsService;
import com.flyf.service.SyFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller

@RequestMapping("/qiniu")
public class QiniuController {

    @Autowired
    private QiniuService qiniuService;
    @Autowired
    SyFileService syFileService;
    @Autowired
    StatisticsService statisticsService;

    /**
     * 以文件的形式上传图片
     *
     * @param file
     * @return 返回访问路径

     */
    @PostMapping("/upload")
    public String uploadFile(HttpSession session, @RequestParam(value = "head") MultipartFile file) throws IOException
    {
        if (StringUtils.isNotBlank(file.getOriginalFilename()))
        {
           // String filePath = qiniuService.uploadFile(file.getInputStream());
            String filePath = qiniuService.uploadFile(file);
            System.out.println("++++++++++++++++++++++++++++++++++++++++-----------------------------------------------" +
                    ""+filePath);
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
}
