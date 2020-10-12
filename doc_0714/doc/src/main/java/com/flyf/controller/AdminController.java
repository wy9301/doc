package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyf.entity.*;
import com.flyf.mapper.StatisticsMapper;
import com.flyf.service.MyFileService;
import com.flyf.service.RelationService;
import com.flyf.service.StatisticsService;
import com.flyf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MyFileService myFileService;
    @Autowired
    RelationService relationService;
    @Autowired
    UserService userService;
    @Autowired
    StatisticsService statisticsService;

    @Autowired
    MyFileController mc=new MyFileController();

    /*
    * 总上传，总下载，总用户量
    * 七日上传下载量
    *
    * */
    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
/*        Integer up_cnt=statisticsMapper.getsta("upload");
        Integer down_cnt=statisticsMapper.getsta("download");
*/

        List<String>time_list=new ArrayList<>();
        List<Integer>up_list=new ArrayList<>();
        List<Integer>down_list=new ArrayList<>();
        for(int i=6;i>=0;i--)
        {
            LocalDateTime date1=LocalDateTime.now().minusDays(i);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date2Str = formatter2.format(date1);

            /*java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());*/
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = date1.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
            java.util.Date date = java.util.Date.from(zdt.toInstant());
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());


            QueryWrapper<Statistics> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("theday",sqlDate);
            Statistics sta=statisticsService.getOne(wrapper1);
            if(sta==null)
            {
                up_list.add(0);
                down_list.add(0);
            }
            else{
                up_list.add(sta.getUpload());
                down_list.add(sta.getDownload());
            }
            time_list.add(date2Str);
        }
        model.addAttribute("time_list", time_list);
        model.addAttribute("up_list", up_list);
        model.addAttribute("down_list", down_list);
        System.out.println(down_list);
        model.addAttribute("user",user);
        return "index";
    }
    @GetMapping("/file_list")//废弃，改用syfile中的
    public String file_list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        QueryWrapper<MyFile> wrapper1 = new QueryWrapper<>();
        List<MyFile> list1 = null;
        list1 =myFileService.list(wrapper1);
        model.addAttribute("list1", list1);
        model.addAttribute("user",user);
        return "admin_allfile";
    }
    @GetMapping("/user_list")
    public String user_list(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        QueryWrapper<User> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("right1",1);
        List<User> list1 = null;
        list1 = userService.list(wrapper1);
        model.addAttribute("list1", list1);
        model.addAttribute("user",user);
        return "admin_user";
    }
    @PostMapping("/adduser")
    @ResponseBody
    public String adduser(String username,String tel) {
        User user=new User();
        user.setUsername(username);
        user.setTel(tel);
        user.setRight1(1);
        user.setPassword("123123");
        user.setHead("avatar0.jpg");
        LocalDateTime date1=LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date2Str = formatter2.format(date1);
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        user.setIntime(currentDate);
        boolean bool = userService.save(user);
        if (bool) {
            return "1";
        } else {
            return "0";
        }

    }

    @PostMapping("/changetel")
    @ResponseBody
    public String adduser(int id,String new_tel) {
        User one = userService.getById(id);
        one.setTel(new_tel);
        boolean bool = userService.updateById(one);
        if (bool) {
            System.out.println("..............................................++++++++++++++++++++++++++");
            return "1";
        }
      else  return "0";
    }
    /*批量删除用户*/
    @PostMapping("/deletelist")
    public String deletelist(@RequestParam(value = "checkData[]") ArrayList<Integer> checkData, HttpSession session) {
        System.out.println(checkData);
        for (int id : checkData) {
            delete(id, session);
        }
        return null;
    }
    /*删除用户
    * 1 在doc_relation中删除(作为id1 和 id2都删)
    * 2 在doc_file中删除 其名下文件（连带删除名下分享码），可见文件的可见性
    * 3 在doc_user中除名
    * */
    @PostMapping("/delete")
    public String delete(int id,HttpSession session) {

        QueryWrapper<MyFile> wrapper2 = new QueryWrapper<>();//3.1 获取此人的所有文档编号

        QueryWrapper<User> wrapper21 = new QueryWrapper<>();//3.2 获取此人id
        wrapper21.eq("id", id);
        User user=userService.getOne(wrapper21);

        wrapper2.eq("master", user.getUsername());
        List<MyFile> file_list=myFileService.list(wrapper2);

        for (MyFile myfile : file_list) {
            mc.delete((int)myfile.getId(),session);
        }
        //3.3 删除与 本人可见性 相关的文件
        QueryWrapper<MyFile> wrapper3 = new QueryWrapper<>();
        wrapper3.like("visible", ","+id+",");
        List<MyFile>vi_list=myFileService.list(wrapper3);
        for(MyFile myFile:vi_list){//带他的文件列表
            String vi=myFile.getVisible();
            vi=vi.replace(","+id+",",",");
            myFile.setVisible(vi);
            myFileService.updateById(myFile);
        }

        QueryWrapper<Relation> wrapper1 = new QueryWrapper<>();//2
        wrapper1.eq("id1", id).or().eq("id2",id);
        relationService.remove(wrapper1);
        boolean bool = userService.removeById(id);//1
        if (bool) {
            return "redirect:/admin/user_list";
        }
        return null;
    }

    @PostMapping("/search")
    public String search(Model model, String sname, HttpSession session) {
        User user = (User) session.getAttribute("user");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", sname);
        List<User> list = userService.list(wrapper);
        model.addAttribute("list1", list);
        model.addAttribute("sc", sname);
        model.addAttribute("user",user);
        return "admin_user";
    }

}
