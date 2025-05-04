package br.com.ccr.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    public static final String URL = System.getenv("DB_URL");
    public static final String USER = System.getenv("DB_USER");
    public static final String KEY = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, KEY);
    }
}
