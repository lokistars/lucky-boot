package com.lucky.platform.util;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class SendEmail {
    /*@Autowired
    private JavaMailSender javaMailSender;
    public void SendEmails() throws Exception{
        //创建一个MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //发送邮件的工具类
        MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage);
        helper.setFrom("dyc87112@qq.com"); //发件人
        helper.setTo("dyc87112@qq.com"); //收件人
        helper.setSubject("主题：");//主题
        helper.setText("有附件的邮件");//主体
        javaMailSender.send(mimeMessage);
    }*/


    /**
     * 发送邮件
     */
    public static Boolean sendOutLognMail(String HOST, String USERNAME, String PASSWORD, String FROM, String TO, String tile, String conten) {
        // 创建Properties 对象
//		Properties props = System.getProperties();
        // 添加smtp服务器属性
	/*	props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.auth.login.disable", "true");
		Session session = Session.getDefaultInstance(props, null);*/
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");// 发送邮件协议名称
        props.setProperty("mail.smtp.auth", "false");//是否开启身份验证
        props.setProperty("mail.smtp.port", "25");
        props.setProperty("mail.smtp.host", HOST);
        props.setProperty("mail.smtp.timeout", "80000");//超时
        Session session = Session.getDefaultInstance(props, null);
        try {
            // 定义邮件信息
            MimeMessage message = new MimeMessage(session);
//			message.setFrom("no-reply@wuushu.com");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
            message.setSubject(tile);
            message
                    .setText(conten);
            // 发送消息
//			session.getTransport("smtp").send(message);
            Transport.send(message); //也可以这样创建Transport对象发送
            System.out.println("SendMail Process Over!");
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean sendEmail(String sendTo, String host, String tile, String conten, String username, String password) {
        //收件箱
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
