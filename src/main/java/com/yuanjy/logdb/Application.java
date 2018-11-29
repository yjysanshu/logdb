package com.yuanjy.logdb;

import com.google.gson.Gson;
import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.service.LogdbService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    protected static Logger logger = Logger.getLogger(Application.class);

    private final static int PORT = 1217;

    /**
     * 接收UDP报文
     * @param args 命令行参数
     */
    public static void main(String[] args) throws SocketException, InterruptedException {
        LogdbService logdbService = new LogdbService();
        List<Logdb> logdbList = new ArrayList<Logdb>();
        DatagramSocket socket = new DatagramSocket(PORT);
        System.out.println("开始监听UDP端口: " + PORT);

        while (true) {
            byte[] buf = new byte[10240];   //暂定接收的数据为1M，前端传过来的json不能超过这么大，否则解析异常
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                buf = packet.getData();
                String data = new String(buf, 0, packet.getLength());
                Gson gson = new Gson();
                Logdb logdb = gson.fromJson(data, Logdb.class);
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
        }
    }
}
