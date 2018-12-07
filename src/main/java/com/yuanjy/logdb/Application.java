package com.yuanjy.logdb;

import com.google.gson.Gson;
import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.service.LogdbService;
import io.sentry.Sentry;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static Logger logger = Logger.getLogger(Application.class);

    private final static int PORT = 1217;       //UDP监听的端口

    private final static long ACTIVE = 120;      //进程存活时间（分钟）

    private static List<Logdb> logdbList = new ArrayList<Logdb>();

    private static LogdbService logdbService = new LogdbService();

    /**
     * 接收UDP报文
     * @param args 命令行参数
     */
    public static void main(String[] args) throws SocketException {
        //注册sentry服务
        Sentry.init("http://784a46eb444e420f80420a4bc9de2d17@115.159.59.248:9000/x");

        /*
          注册杀死进程回调函数
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdownCallback();
            }
        });

        DatagramSocket socket = new DatagramSocket(PORT);
        System.out.println("开始监听UDP端口: " + PORT);
        long start = System.currentTimeMillis();    //进程开始时间
        boolean flag = true;

        do {
            byte[] buf = new byte[10240];   //暂定接收的数据为1M，前端传过来的json不能超过这么大，否则解析异常
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                buf = packet.getData();
                String data = new String(buf, 0, packet.getLength());
                Gson gson = new Gson();
                Logdb logdb;
                try {
                    logdb = gson.fromJson(data, Logdb.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("logdb数据，json解析失败");
                    continue;
                }
                //logdbService.save(logdb);
                logger.info(logdb.toString());
                logdbList.add(logdb);
                if (logdbList.size() >= 20) {
                    if (logdbService.batchSave(logdbList) > 0) {
                        logger.info("logdb批量插入成功");
                        logdbList.clear();
                    } else {
                        logger.warn("logdb批量插入异常");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            flag = (((end - start) / 1000 / 60) < ACTIVE) || (logdbList.size() != 0);  //防止僵尸进程，执行一段时间重启进程
            //System.out.println((end - start) / 1000 / 60 + " ==== " + logdbList.size());
        } while (flag);
    }

    /**
     * 进程被 kill -15 时调用该方法
     */
    private static void shutdownCallback() {
        try {
            if (logdbList.size() > 0) {
                logdbService.batchSave(logdbList);
                logger.warn("进程关闭时，保存数据: " + logdbList.toString());
            }
        } catch (Exception e) {
            System.out.println("进程关闭时，回调异常");
            logger.warn("进程关闭时，回调异常: " + logdbList.toString());
            e.printStackTrace();
        }
    }
}
