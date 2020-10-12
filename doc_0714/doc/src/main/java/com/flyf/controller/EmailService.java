package com.flyf.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.flyf.util.RandomUtil;
import com.flyf.util.SendUtil;


/**
 * Servlet implementation class EmailServlet
 */
public class EmailService extends HttpServlet {

    protected void getEmail(String email, HttpSession session) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String code = RandomUtil.getRandom();
        //EmailService ec=new EmailService();
        SendUtil ec = new SendUtil();
        ec.sendtocheck(email,code);
        System.out.println("邮箱验证码：" + code);
       // req.getSession().setAttribute("code", code);
        session.setAttribute("code",code);
    }

    protected String check(String inputCode, HttpSession session){
        // 获取session中的验证码
        //String sessionCode = (String) req.getSession().getAttribute("code");
        String sessionCode = (String)session.getAttribute("code");
        System.out.println(sessionCode);

        if(sessionCode != null) {
            //  获取页面提交的验证码
            System.out.println("页面提交的验证码:" + inputCode);
            if (sessionCode.toLowerCase().equals(inputCode.toLowerCase())) {
                session.removeAttribute("code");
                return "1";

            }else {
                //  验证失败
                session.removeAttribute("code");
                return "0";
            }
        }else {
            //  验证失败
            session.removeAttribute("code");
            return "0";
        }
        //  移除session中的验证码

    }
}

