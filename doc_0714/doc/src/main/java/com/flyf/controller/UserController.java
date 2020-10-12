package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyf.entity.MyFile;
import com.flyf.entity.Relation;
import com.flyf.entity.User;
import com.flyf.service.RelationService;
import com.flyf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import static com.flyf.util.MD5Util.crypt;
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RelationService relationService;

    @GetMapping("/index")
    public String index() {
        return "login";
    }

    @GetMapping("/checkUsername")
    @ResponseBody
    public String check(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        if(username.length()<6)
        {
            return "2";
        }
       else if (user != null) {
            return "1";
        }
        return "0";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(User user) {

        user.setRight1(1);
        System.out.println(user.getPassword());
        user.setPassword(crypt(user.getPassword()));
        LocalDateTime date1 = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date2Str = formatter2.format(date1);
        /*java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());*/

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = date1.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        java.util.Date date = java.util.Date.from(zdt.toInstant());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        user.setIntime(sqlDate);

        user.setHead("avatar0.jpg");
       // crypt(user.getPassword());
        boolean bool = userService.save(user);
        if (bool) {
            return "1";
        } else {
            return "0";
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(HttpSession session, String username, String password) {
        password=crypt(password);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        System.out.println("((((((((((((((((((((((("+wrapper);
        wrapper.eq("username", username).eq("password", password);
        User user = userService.getOne(wrapper);
        System.out.println("*************************"+user);
        if (user != null) {
            //把登录成功的用户添加到session
            session.setAttribute("user", user);
            System.out.println(user);
            if(user.getRight1()==0)//管理
                return "2";
            else
                return "1";
        } else {
            System.out.println("此用户不存在");
            return "0";

        }
    }

    @PostMapping("/addfriend")
    @ResponseBody
    public String add(String q_name,HttpSession session) {//客体用户名

        User user=(User)session.getAttribute("user");
        if(user.getUsername().equals(q_name.trim()))
        {return null;} //是本人
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", q_name.trim()).eq("right1","1");
        User one = userService.getOne(wrapper);
        if(one==null)//没这个客体
        {return "0";}else{
            //有此人，但无关系
            QueryWrapper<Relation>wrapper1 = new QueryWrapper<>();
            wrapper1.eq("id1", user.getId()).eq("id2",one.getId());
            Relation relation=relationService.getOne(wrapper1);
            if(relation==null) {
                Relation re=new Relation();
                re.setId1((int) user.getId());//本人
                re.setId2((int)one.getId());//客体
                relationService.save(re);
                return "1";
            }else{//已添加过此常用联系人
                return "3";
            }

        }
    }
    /*删除常用联系人*/
    @PostMapping("/delete_f")
    public String delete(int id,HttpSession session) {
        User user= (User) session.getAttribute("user");
        QueryWrapper<Relation> wrapper = new QueryWrapper<>();
        wrapper.eq("id2", id).eq("id1",user.getId());
        boolean bool =relationService.remove(wrapper);
        if (bool) {
            return "redirect:/admin/user_list";
        }
        return null;
    }

    @PostMapping("/uphead")
    @ResponseBody
    public String upload(@RequestParam("file")MultipartFile file,HttpSession session) throws IOException {
        if (file.isEmpty()){
            return "请选择一个文件";
        }
        System.out.println(file.getOriginalFilename());
        String picName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filePath = System.getProperty("user.dir")+"\\target\\classes\\static\\asserts\\image\\" + picName;
        File dest = new File(filePath);
        file.transferTo(dest);
        User one= (User) session.getAttribute("user");
        one.setHead(picName);
        User user = userService.getById(one.getId());
        user.setHead(picName);
        userService.updateById(user);
        return "修改头像成功";
    }

    @PostMapping("/change_psw")
    @ResponseBody
    public String change_psw(String oldpsw,String newpsw,HttpSession session) throws IOException {
        System.out.println(oldpsw);
        System.out.println(newpsw);
        User one= (User) session.getAttribute("user");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", one.getId()).eq("password",oldpsw);
        String msg=null;
        User user =userService.getOne(wrapper);
        if(user==null){
            msg="0";//密码输入错误
        }else{
            msg="1";//修改成功
            user.setPassword(newpsw);
            userService.updateById(user);
        }
        return msg;
    }
    @GetMapping("/logout")
    public String logout(HttpSession session){
        //销毁用户
        session.invalidate();
        //跳转页面
        return "login";
    }







}