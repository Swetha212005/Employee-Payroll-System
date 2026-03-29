/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.employee.payrollsystem.servlet;

/**
 *
 * @author SWETHA
 */
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payrolldb";
    private static final String USER = "root";
    private static final String PASS = "25Mca0007^";

     public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
