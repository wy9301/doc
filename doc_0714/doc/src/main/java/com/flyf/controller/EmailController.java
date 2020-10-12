package com.flyf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/email")
public class EmailController {
    @PostMapping("/send")
    @ResponseBody
    public String sendEmail(String email,HttpSession session) throws IOException, ServletException {
        System.out.println("呵呵呵呵呵呵呵或或或或或或或");
        EmailService emailService = new EmailService();
        emailService.getEmail(email,session);

        String msg=null;
        return msg;
    }
    @PostMapping("/check")
    @ResponseBody
    public String checkCode(String code,HttpSession session) throws IOException, ServletException {
        System.out.println("checkController=++++++++++++++++++++++++++++++++++++++++++++");
        EmailService emailService = new EmailService();
        String msg=emailService.check(code,session);
        return msg;
    }
}
