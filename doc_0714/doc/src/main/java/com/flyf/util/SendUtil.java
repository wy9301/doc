package com.flyf.util;

import java.util.Date;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendUtil {
    // 发件人 账号和密码
    public static final String MY_EMAIL_ACCOUNT = "wangyuan9301@163.com";
    public static final String MY_EMAIL_PASSWORD = "IHTSEYMANGIHKCWO";// 密码,是你自己的设置的授权码

    // SMTP服务器(这里用的163 SMTP服务器)
    public static final String MEAIL_163_SMTP_HOST = "smtp.163.com";
    public static final String SMTP_163_PORT = "25";// 端口号,这个是163使用到的;QQ的应该是465或者875

    // 收件人1
    public static final String RECEIVE_EMAIL_ACCOUNT = "1208296225@qq.com";

    public int send(String email,String subject,String content) throws AddressException, MessagingException {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties p = new Properties();
        p.setProperty("mail.smtp.host", MEAIL_163_SMTP_HOST); // 设置邮件服务器
        p.setProperty("mail.smtp.port", SMTP_163_PORT);
        p.setProperty("mail.smtp.socketFactory.port", SMTP_163_PORT);
        p.setProperty("mail.smtp.auth", "true");// 打开认证
        p.setProperty("mail.smtp.socketFactory.class", "SSL_FACTORY");

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(p, new Authenticator() {
            // 设置认证账户信息
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL_ACCOUNT, MY_EMAIL_PASSWORD);
            }
        });
        session.setDebug(true);
        System.out.println("创建邮件");
        // 3. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 发件人
        message.setFrom(new InternetAddress(MY_EMAIL_ACCOUNT));
        // 收件人和抄送人
        message.setRecipients(Message.RecipientType.TO, email);
//				message.setRecipients(Message.RecipientType.CC, MY_EMAIL_ACCOUNT);

        // 内容(这个内容还不能乱写,有可能会被SMTP拒绝掉;多试几次吧)2
        message.setSubject(subject);
        message.setContent(content, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        System.out.println("准备发送");
        Transport.send(message);
        return 0;

    }

    public int sendtocheck(String email,String check)  {
        //EmailDao ed=new EmailDao();

        String subject="在线文档管理系统申请验证码";
        String content = "欢迎使用620在线文档管理系统，您的验证码是【"+check+"】，若非本人操作请忽略本邮件。";
        try {
            send(email, subject,content);
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;

    }
}

