package com.yuanjy.logdb.thread;

import com.google.gson.Gson;
import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.service.LogdbService;
import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    private Logger logger = LogManager.getLogger(Consumer.class);

    private BlockingQueue<String> queue;
    private Integer maxSize;

    private Map<String, List<Logdb>> map = new HashMap<String, List<Logdb>>();

    public Consumer(BlockingQueue<String> queue, int maxSize) {
        this.maxSize = maxSize;
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
                        Sentry.capture("logdb数据，json解析失败");
                        continue;
                    }
                    logger.info(logdb.toString());
                    if (map.get(logdb.getModule()) == null) {
                        map.put(logdb.getModule(), new ArrayList<Logdb>());
                    }
                    map.get(logdb.getModule()).add(logdb);
                    if (map.get(logdb.getModule()).size() >= maxSize) {
                        int effectRow = logdbService.batchSaveByMap(map);
                        if (effectRow > 0) {
                            logger.info("logdb批量插入成功");
                            map.clear();
                        } else {
                            logger.error("[careful] " + "logdb批量插入异常");
                        }
                    }
                } catch (Exception e) {
                    Sentry.capture(e);
                    e.printStackTrace();
                    logger.error("[careful] " + Thread.currentThread().getName() + " 线程异常：" + e.getMessage());
                    Sentry.capture(Thread.currentThread().getName() + " 线程异常：" + e.getMessage());
                }
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<Logdb>> getMap() {
        return map;
    }
}
