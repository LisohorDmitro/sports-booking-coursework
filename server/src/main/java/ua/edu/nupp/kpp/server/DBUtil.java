package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Підключення до MySQL.
 * Параметри можна перевизначити змінними середовища (рекомендовано для переносимості):
 *   DB_URL, DB_USER, DB_PASSWORD
 * або системними властивостями: -Ddb.url=... -Ddb.user=... -Ddb.password=...
 */
public final class DBUtil {
    private static final String URL = firstNonBlank(
            System.getenv("DB_URL"),
            System.getProperty("db.url"),
            "jdbc:mysql://localhost:3306/sports_booking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String USER = firstNonBlank(
            System.getenv("DB_USER"),
            System.getProperty("db.user"),
            "root"
    );
    private static final String PASSWORD = firstNonBlank(
            System.getenv("DB_PASSWORD"),
            System.getProperty("db.password"),
            ""
    );

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return "";
    }
}
