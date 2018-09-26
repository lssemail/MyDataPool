package com.lssemail.datasource.impl;

import com.lssemail.datasource.PoolConnection;
import com.lssemail.datasource.PoolDataSource;
import com.lssemail.datasource.pros.PropertiesPlaceHolder;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PoolDataSourceImpl implements PoolDataSource {

    private ReentrantLock lock = new ReentrantLock();

    List<PoolConnection> poolList = Collections.synchronizedList(new ArrayList<>(256));

    private static final String DRIVER_CLASS = PropertiesPlaceHolder.getInstance().getProperty("jdbc.driver_class");
    private static final String URL = PropertiesPlaceHolder.getInstance().getProperty("jdbc.url");
    private static final String USERNAME = PropertiesPlaceHolder.getInstance().getProperty("jdbc.username");
    private static final String PASSWORD = PropertiesPlaceHolder.getInstance().getProperty("jdbc.password");

    private int init_size = 2;
    private int step_size = 6;
    private int max_size = 20;
    private int time_out = 1000;

    public PoolDataSourceImpl(){
        initPool();
    }

    private void initPool(){
        String init = PropertiesPlaceHolder.getInstance().getProperty("initSize");
        String step = PropertiesPlaceHolder.getInstance().getProperty("stepSize");
        String max = PropertiesPlaceHolder.getInstance().getProperty("maxSize");
        String timeout = PropertiesPlaceHolder.getInstance().getProperty("timeout");

        init_size = init == null? init_size : Integer.parseInt(init);
        step_size = step == null? step_size : Integer.parseInt(step);
        max_size = max == null? max_size : Integer.parseInt(max);
        time_out = timeout == null? time_out : Integer.parseInt(timeout);

        try {
            Driver driver = (Driver) Class.forName(DRIVER_CLASS).newInstance();
            //注册驱动
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public PoolConnection getDataSource() throws Exception{

        PoolConnection poolConnection = null;
        try {
            lock.lock();
            if(poolList.size() == 0){
                //执行初始化连接池操作
                createConnections(init_size);
            }
            poolConnection = getRealConnection();
            //敲黑板，重点思路;轮询，获取连接池中空闲的连接
            while (poolConnection == null){
                System.out.println("等待连接。。。。。。。。。。。");
                //如果连接数还没有到最大连接，继续添加到连接池
                createConnections(step_size);
                poolConnection = getRealConnection();
                if(poolConnection == null){
                    TimeUnit.MILLISECONDS.sleep(30);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return poolConnection;
    }

    /**
     * 判断获取的链接是否可用(判读超时)，如果超时，新建一个连接补回到连接池
     * @return
     */
    private PoolConnection getRealConnection() throws Exception{

        for(PoolConnection poolConnection: poolList){
            if(!poolConnection.isFlag()){
                Connection connection = poolConnection.getConnection();
                if(!connection.isValid(time_out)){
                    Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    poolConnection.setConnection(con);
                }
                poolConnection.setFlag(true);
                return poolConnection;
            }
        }
        return null;
    }

    private void createConnections(int count) throws Exception{

        if(poolList.size() + count <= max_size){
            for (int i=0; i< count; i++){
                System.out.println("初始化了" + (i + 1) + "个连接");
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PoolConnection poolConnection = new PoolConnection(connection, false);
                poolList.add(poolConnection);
            }
        }

    }


}


