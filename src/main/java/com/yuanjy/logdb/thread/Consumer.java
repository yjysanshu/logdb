package com.yuanjy.logdb.thread;

import com.google.gson.Gson;
import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.service.LogdbService;
import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    private Logger logger = LogManager.getLogger(Consumer.class);

    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        LogdbService logdbService = new LogdbService();
        Gson gson = new Gson();
        Logdb logdb;

        try {
            String data;
            do {
                data = queue.take();
                try {
                    try {
                        logdb = gson.fromJson(data, Logdb.class);
                    } catch (Exception e) {
                        Sentry.capture(e);
                        e.printStackTrace();
                        logger.error("[careful] " + "logdb数据，json解析失败: " + data);
                        continue;
                    }
                    logger.info(logdb.toString());

                    int effectRow = logdbService.save(logdb);
                    if (effectRow <= 0) {
                        logger.error("[careful] " + "logdb批量插入异常");
                    }
                } catch (Exception e) {
                    Sentry.capture(e);
                    e.printStackTrace();
                    logger.error("[careful] " + Thread.currentThread().getName() + " 线程异常：" + e.getMessage());
                }
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
