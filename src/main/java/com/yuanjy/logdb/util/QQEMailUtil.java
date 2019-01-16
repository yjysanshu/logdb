package com.yuanjy.logdb.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class QQEMailUtil {

    private static final String EMAIL_TRANSPORT_PROTOCOL = "smtp";
    private static final String EMAIL_SMTP_HOST = "smtp.qq.com";
    private static final int EMAIL_SMTP_PORT = 465;
    private static final boolean EMAIL_SMTP_AUTH = true;
    private static final boolean EMAIL_SMTP_SSL_ENABLE = true;
    private static final boolean EMAIL_DEBUG = true;

    private static final String EMAIL_USER = "QQ邮箱";        //发送者的账号
    private static final String EMAIL_PASSWORD = "开通的密码，非登录密码";    //发送者的密码
    //SENTRY_EMAIL_USE_TLS=true

    public static boolean sendMail(String userMail, String title, String content) {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", EMAIL_TRANSPORT_PROTOCOL);// 连接协议
        properties.put("mail.smtp.host", EMAIL_SMTP_HOST);// 主机名
        properties.put("mail.smtp.port", EMAIL_SMTP_PORT);// 端口号
        properties.put("mail.smtp.auth", EMAIL_SMTP_AUTH);
        properties.put("mail.smtp.ssl.enable", EMAIL_SMTP_SSL_ENABLE);// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", EMAIL_DEBUG);// 设置是否显示debug信息 true 会在控制台显示相关信息

        // 得到回话对象
        Session session = Session.getInstance(properties);
        // 获取邮件对象
        Message message = new MimeMessage(session);

        try {
            // 设置发件人邮箱地址
            message.setFrom(new InternetAddress(EMAIL_USER));
            // 设置一个收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(userMail));//一个收件人
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            message.setText(content);
            // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户
            transport.connect(EMAIL_USER, EMAIL_PASSWORD);  // 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
