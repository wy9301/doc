package com.flyf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyf.entity.Code;
import com.flyf.entity.MyFile;
import com.flyf.entity.Relation;
import com.flyf.entity.User;
import com.flyf.service.CodeService;
import com.flyf.service.MyFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Controller
@RequestMapping("/gen_code")
public class CodeController {
    @Autowired
    CodeService codeService;
    @Autowired
    MyFileService myFileService;
    @PostMapping("/addcode")
    @ResponseBody
    public String addcode(String doc_id, String code_days, HttpSession session) {
        Code code = new Code();
        code.setDocid(Integer.valueOf(doc_id.trim()));
        code.setDays(Integer.valueOf(code_days.trim()));
        LocalDateTime date1 = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date2Str = formatter2.format(date1);
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        code.setTime(currentDate);
        String code_value = new PasswordGenerator(10, 3).generateRandomPassword();
        code.setCode(code_value);
        boolean bool = codeService.save(code);
        return code_value;
    }

    @PostMapping("/fetchfile")
    @ResponseBody
    public JSONObject fetchfile(String code,HttpSession session) {
        System.out.println(code);

        //使用QueryWrapper类实现条件查询,
        // ge表>=
        //gt表>
        //le表<=
        //lt表<
        //eq表等于
        //ne表不等于
        //第一个参数是数据库表的字段名，不是实体类的属性
        // 第二个参数就是条件值
        QueryWrapper<Code> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code.trim());
        Code one = codeService.getOne(wrapper);
        System.out.println(one);
        if(one==null)
            return null;
        else{
            QueryWrapper<MyFile> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("id", one.getDocid());
            MyFile file = myFileService.getOne(wrapper2);

            Map map = new HashMap();
            map.put("id", file.getId());
            map.put("name", file.getName());
            map.put("master", file.getMaster());
            map.put("size", file.getSize());
            JSONObject json = JSONObject.fromObject(map);
            System.out.println(json);
            //msg="文件名："+file.getName()+"  分享者："+file.getMaster()+"  文件大小："+file.getSize();
            return json;
        }
    }


}
