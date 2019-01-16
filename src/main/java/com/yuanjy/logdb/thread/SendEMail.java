package com.yuanjy.logdb.thread;

import com.yuanjy.logdb.util.QQEMailUtil;

public class SendEMail implements Runnable {

    private String userMail;
    private String title;
    private String content;

    public SendEMail(String userMail, String title, String content) {
        this.userMail = userMail;
        this.title = title;
        this.content = content;
    }

    @Override
    public void run() {
        try {
            Boolean result = QQEMailUtil.sendMail(userMail, title, content);
            System.out.println("[careful] " + Thread.currentThread().getName() + " 邮件发送结果：" + result);
        } catch (Exception e) {
            System.out.println("[careful] " + Thread.currentThread().getName() + " 邮件发送异常");
        }
    }
}
