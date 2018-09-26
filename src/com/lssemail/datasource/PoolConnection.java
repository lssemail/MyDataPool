package com.lssemail.datasource;

import java.sql.Connection;

public class PoolConnection {

    private Connection connection;
    /**
     * false 空闲 true 占用
     */
    private boolean flag;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public PoolConnection() {
    }

    public PoolConnection(Connection connection, boolean flag) {
        this.connection = connection;
        this.flag = flag;
    }

    public void closeConnection(){
        System.out.println("释放连接.......");
        this.flag = false;
    }
}

