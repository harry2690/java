package com.harrywu.springweb.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class XBasicDataSource extends BasicDataSource {
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
/*        
        System.out.println("====================================================");
        System.out.println("Connection Pool Active: " + getNumActive());
        System.out.println("Connection Pool Idle: " + getNumIdle());
*/
        return conn;
    }
    
    @Override
    public synchronized void close() throws SQLException {
        DriverManager.deregisterDriver(DriverManager.getDriver(url));
/*
        System.out.println("====================================================");
        System.out.println("Connection Pool Active: " + getNumActive());
        System.out.println("Connection Pool Idle: " + getNumIdle());
*/
        super.close();
    }

}
