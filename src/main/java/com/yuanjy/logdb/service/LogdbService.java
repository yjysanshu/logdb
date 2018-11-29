package com.yuanjy.logdb.service;

import com.yuanjy.logdb.pojo.Logdb;
import com.yuanjy.logdb.util.DateUtil;
import com.yuanjy.logdb.util.SessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogdbService {

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
     * 新建数据表
     * @return int
     */
    public Integer createTable() {
        String tableName = this.getTableName();
        SqlSession session = SessionFactoryUtil.getSession();
        return session.insert("createTable", tableName);
    }

    /**
     * 获取表名称
     * @return string
     */
    private String getTableName() {
        return "api_log_" + DateUtil.getTYmd();
    }
}
