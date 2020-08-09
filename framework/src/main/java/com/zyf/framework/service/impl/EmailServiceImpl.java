package com.zyf.framework.service.impl;

import com.sun.mail.util.MailSSLSocketFactory;
import com.zyf.framework.service.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 邮件服务
 *
 * @author yuanfeng.z
 * @date 2019/11/15 17:15
 */
@Slf4j
public class EmailServiceImpl implements IEmailService {

    public static void sendMail(String host,
                                String user,
                                String password,
                                String fromMail,
                                String toMail,
                                String mailTitle,
                                String mailContent) {

        // 1.创建连接对象javax.mail.Session
        // 2.创建邮件对象 javax.mail.Message
        // 3.发送一封激活邮件

        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        // 打开认证
        properties.setProperty("mail.smtp.auth", "true");

        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            // 1.获取默认session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    // 发件人邮箱账号、授权码
                    return new PasswordAuthentication(user, password);
                }
            });

            // 2.创建邮件对象
            Message message = new MimeMessage(session);
            // 2.1设置发件人
            message.setFrom(new InternetAddress(fromMail));
            // 2.2设置接收人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
            // 2.3设置邮件主题
            message.setSubject(mailTitle);
            // 2.4设置邮件内容
            message.setContent(mailContent, "text/html;charset=UTF-8");
            // 3.发送邮件
            Transport.send(message);
            System.out.println("邮件成功发送!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String host = "smtpdm.aliyun.com";
        String fromMail = "email@algotrade.cc";
        String user = "email@algotrade.cc";
        String password = "ZHIDUOduo2020";
        String toMail = "1004283115@qq.com";
        String mailTitle = "etwet";
        String mailContent = "fssd";
        try {
            EmailServiceImpl.sendMail(host, user, password, fromMail, toMail, mailTitle, mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}