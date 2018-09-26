package com.lssemail.datasource;

public interface PoolDataSource {

    PoolConnection getDataSource() throws Exception;
}
