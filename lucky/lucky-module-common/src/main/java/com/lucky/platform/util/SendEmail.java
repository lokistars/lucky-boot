package com.lucky.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class SendEmail {
    private static final Logger log = LoggerFactory.getLogger(SendEmail.class);

    /**
     *  发送邮件
     * @param HOST
     * @param USERNAME
     * @param PASSWORD
     * @param FROM
     * @param TO
     * @param tile
     * @param conten
     * @return
     */
    public static Boolean sendOutLognMail(String HOST,String USERNAME,String PASSWORD,String FROM,String TO,String tile,String conten) {
        // 创建Properties 对象
        log.info("开始发送邮件------"+TO);
        Properties props = new Properties();
        // 添加smtp服务器属性
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        //是否开启身份验证
        props.setProperty("mail.smtp.auth", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.host", HOST);
        //超时
        props.setProperty("mail.smtp.timeout", "80000");
        log.info("接收着"+TO+"创建连接");
        Session session = Session.getDefaultInstance(props, null);
        try {
            log.info("接收着"+TO+"开始发邮件");
            // 定义邮件信息
            MimeMessage message = new MimeMessage(session);
            InternetAddress[] address =  InternetAddress.parse(TO);
            message.setRecipients(Message.RecipientType.TO, address);
            //message.addRecipient(Message.RecipientType.TO, address);
            log.info("接收着"+TO);
            message.setSubject(tile);
            message.setText(conten);
            // 发送消息
            //session.getTransport("smtp").send(message);
            //也可以这样创建Transport对象发送
            Transport.send(message);
            System.out.println("SendMail Process Over!");
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean sendEmail(String sendTo,String host,String tile,String conten,String username,String password) {
        //收件箱
        log.info("发送邮件"+host+"---sendTo:"+sendTo);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        //不做服务器证书校验
        props.put("mail.smtp.host", host);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", "465");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendTo));
            message.setSubject(tile);
            message.setText(conten);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
