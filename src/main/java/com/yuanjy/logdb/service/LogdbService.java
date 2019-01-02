package com.yuanjy.logdb.service;

import com.yuanjy.logdb.constant.CommonConst;
import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.pojo.Table;
import com.yuanjy.logdb.util.DateUtil;
import com.yuanjy.logdb.util.RedisPoolUtil;
import com.yuanjy.logdb.util.SessionFactoryUtil;
import io.sentry.Sentry;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LogdbService {

    private Logger logger = LogManager.getLogger(LogdbService.class);

    /**
     * 是否创建数据表
     * @param tableName 表名
     * @param session mysql
     * @return bool
     */
    public Boolean checkAndCreateTable(String tableName, SqlSession session) {
        String key = "IS_HAVING_TABLE_" + tableName;
        Jedis jedis = null;
        try {
            jedis = RedisPoolUtil.getJedis();
            if (jedis != null) {
                String str = jedis.get(key);
                if (str != null) {
                    return true;
                }

                //查看是否存在数据表，不存在，则创建数据表
                if (this.isExitTable(tableName, session) || this.createTable(tableName, session)) {
                    jedis.set(key, "1", "EX", 86400);
                    return true;
                }
                logger.error("创建数据表：" + tableName + " 失败");
            } else {
                //查看是否存在数据表，不存在，则创建数据表
                if (this.isExitTable(tableName, session) || this.createTable(tableName, session)) {
                    return true;
                }
                logger.error("未获取到redis，创建数据表： " + tableName + " 失败");
            }
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            Sentry.capture(e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 批量插入log信息
     * @param map logdb列表
     * @return int
     */
    public Integer batchSaveByMap(Map<String, List<Logdb>> map) throws Exception {
        if (map.isEmpty()) {
            return CommonConst.ERROR;
        }
        Integer affectRow = 0;
        SqlSession session = SessionFactoryUtil.getSession();
        try {
            for (Entry<String, List<Logdb>> entry : map.entrySet()) {
                if (entry.getValue().size() <= 0) {
                    continue;
                }
                String tableName = this.getTableName(entry.getKey());
                if (this.checkAndCreateTable(tableName, session)) {
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("tableName", tableName);
                    param.put("list", entry.getValue());
                    affectRow += session.insert("batchInsert", param);
                } else {
                    logger.error("查询或创建数据表失败：" + tableName);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        return affectRow;
    }

    /**
     * 批量插入log信息
     * @param logdbList logdb列表
     * @return int
     */
    public Integer batchSave(List<Logdb> logdbList) {
        String tableName = this.getTableName();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tableName", tableName);
        param.put("list", logdbList);
        SqlSession session = SessionFactoryUtil.getSession();
        return session.insert("batchInsert", param);
    }

    /**
     * 单个插入log信息
     * @param logdb logdb对象
     * @return int
     */
    public Integer save(Logdb logdb) {
        String tableName = this.getTableName();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("tableName", tableName);
        param.put("logdb", logdb);
        SqlSession session = SessionFactoryUtil.getSession();
        return session.insert("insert", param);
    }

    /**
     * 判断表是否存在
     * @param tableName 表名
     * @param session mysql
     * @return bool
     */
    private Boolean isExitTable(String tableName, SqlSession session) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tableName", tableName);
        map.put("databaseName", "logdb");
        List<Table> list = session.selectList("isExitTable", map);
        return list.size() > 0;
    }

    /**
     * 新建数据表
     * @param tableName 表名
     * @param session mysql
     * @return bool
     */
    private Boolean createTable(String tableName, SqlSession session) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tableName", tableName);
        try {
            session.insert("createTable", map);
            return true;
        } catch (Exception e) {
            logger.error("创建数据库失败: " + tableName);
            logger.error(e.getMessage());
            Sentry.capture(e);
            return false;
        }
    }

    /**
     * 获取表名称
     * @return string
     */
    private String getTableName(String module) {
        return module + "_log_" + DateUtil.getTYmd();
    }

    /**
     * 获取表名称
     * @return string
     */
    private String getTableName() {
        return "api_log_" + DateUtil.getTYmd();
    }
}
