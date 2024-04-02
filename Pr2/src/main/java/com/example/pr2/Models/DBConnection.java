package com.example.pr2.Models;
import java.security.spec.ECField;
import java.sql.*;


public class DBConnection {
    public static Connection connection;
    public DBConnection()
    {
        connection=getConnection();
    }

    public Connection getConnection()
    {
        if(connection==null)
            connection=connect_to_db();
        return connection;
    }
    public Connection connect_to_db() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + "hospital", "postgres", "tresspress79");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
            return conn;
        }

}