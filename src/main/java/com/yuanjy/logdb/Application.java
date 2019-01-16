package com.yuanjy.logdb;

import com.yuanjy.logdb.thread.Consumer;
import com.yuanjy.logdb.thread.SendEMail;
import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Application {

    private static Logger logger = LogManager.getLogger(Application.class);

    private final static int PORT = 1218;       //UDP监听的端口

    private final static int ACTIVE = 360;      //进程存活时间（分钟）

    /**
     * 接收UDP报文
     * @param args 命令行参数
     */
    public static void main(String[] args) throws SocketException {
        int port = judgeAndAssignment(args, 0, PORT);
        int activeTime = judgeAndAssignment(args, 1, ACTIVE);

        //注册sentry服务
        Sentry.init("http://747893dba61648a6b03a41f397cef977:fc6f1f77674442fc94fedfdff83d2bc0@115.159.59.248:9000/7");
        //Sentry.init("http://d3630ab3e81843d5bac3ba2a744a3d4d:f777d611417644eba2af2a53fe790cb1@115.159.59.248:9000/2");

        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("[careful] 开始监听UDP端口: " + port + " 进程活跃时间: " + activeTime);

        long start = System.currentTimeMillis();    //进程开始时间
        boolean flag;

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(1000);
        Consumer consumerThread = new Consumer(blockingQueue);
        Thread thread1 = new Thread(consumerThread, "consumer-logdb-1");
        Thread thread2 = new Thread(consumerThread, "consumer-logdb-2");
        thread1.start();
        thread2.start();

        do {
            byte[] buf = new byte[10240];   //暂定接收的数据为10k，前端传过来的json不能超过这么大，否则解析异常
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                buf = packet.getData();
                String data = new String(buf, 0, packet.getLength());
                blockingQueue.add(data);
                int size = blockingQueue.size();
                if (size > 0 && (size % 20 == 0 || size >= 800)) {
                    String info = "队列信息过大 -- " + blockingQueue.size() +
                            "\n  thread1:alive-" + thread1.isAlive() + "    thread1:state-" + thread1.getState() +
                            "\n  thread2:alive-" + thread2.isAlive() + "    thread2:state-" + thread2.getState();

                    //发送邮件
                    new Thread(new SendEMail("1686731979@qq.com", "logdb报错信息", info), "send-email-" + System.currentTimeMillis()).start();

                    logger.error("[careful] " + info);

                    // 线程中断后，重启
                    if (thread1.getState() == Thread.State.TERMINATED) {
                        thread1 = new Thread(consumerThread, "consumer-logdb-1");
                        thread1.start();
                    } else if (!thread1.isAlive()) {
                        //QQEMailUtil.sendMail("1686731979@qq.com", "logdb报错信息", info);
                    }
                    if (thread2.getState() == Thread.State.TERMINATED) {
                        thread2 = new Thread(consumerThread, "consumer-logdb-2");
                        thread2.start();
                    } else if (!thread2.isAlive()) {
                        //QQEMailUtil.sendMail("1686731979@qq.com", "logdb报错信息", info);
                    }
                }
            } catch (Exception e) {
                logger.error("[careful] logdb 执行异常");
                Sentry.capture(e);
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();

            //判断是否要关闭进程
            if (activeTime == 0) {
                flag = true;
            } else {
                flag = (((end - start) / 1000 / 60) < activeTime);  //防止僵尸进程，执行一段时间重启进程
            }
        } while (flag);
    }

    /**
     * 判断并获取值
     * @param args 数组参数
     * @param index 下标
     * @param defaultValue 默认值
     * @return int
     */
    private static int judgeAndAssignment(String[] args, int index, int defaultValue) {
        if (args.length > index) {
            return Integer.parseInt(args[index]);
        }
        return defaultValue;
    }
}