package com.yuanjy.logdb.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties properties;


    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dds = new DruidDataSource();
        dds.setDriverClassName(this.properties.getProperty("driver"));
        dds.setUrl(this.properties.getProperty("url"));
        dds.setUsername(this.properties.getProperty("username"));
        dds.setPassword(this.properties.getProperty("password"));

        // 其他配置可以根据MyBatis主配置文件进行配置
        //dds.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(this.properties.getProperty("maxPoolPreparedStatementPerConnectionSize")));
        dds.setMaxActive(Integer.parseInt(this.properties.getProperty("maxActive")));
        try {
            dds.setFilters(this.properties.getProperty("filters"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dds.setValidationQuery(this.properties.getProperty("validationQuery"));
        dds.setTestWhileIdle(Boolean.parseBoolean(this.properties.getProperty("testWhileIdle")));
        dds.setTestOnBorrow(Boolean.parseBoolean(this.properties.getProperty("testOnBorrow")));
        dds.setTestOnReturn(Boolean.parseBoolean(this.properties.getProperty("testOnReturn")));
        dds.setPoolPreparedStatements(Boolean.parseBoolean(this.properties.getProperty("poolPreparedStatements")));
        dds.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(this.properties.getProperty("maxPoolPreparedStatementPerConnectionSize")));

        try {
            dds.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dds;
    }
}
