package br.com.ccr.infrastructure;

import org.eclipse.microprofile.config.ConfigProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    static String URL = ConfigProvider.getConfig().getValue("quarkus.datasource.jdbc.url", String.class);
    static String USER = ConfigProvider.getConfig().getValue("quarkus.datasource.username", String.class);
    static String KEY = ConfigProvider.getConfig().getValue("quarkus.datasource.password", String.class);

    public static Connection getConnection() throws SQLException {
        if (URL == null) {
            throw new SQLException("DB_URL environment variable is not set");
        }
        if (USER == null) {
            throw new SQLException("DB_USER environment variable is not set");
        }
        if (KEY == null) {
            throw new SQLException("DB_PASSWORD environment variable is not set");
        }
        return DriverManager.getConnection(URL, USER, KEY);
    }
}
