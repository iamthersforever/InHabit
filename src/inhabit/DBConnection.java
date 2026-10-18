package inhabit;

import java.sql.*;
import java.util.*;
import java.io.*;

// Database Connection Manager for InHabitDBNew
public class DBConnection {
    // 1. Connection credentials
    private static String url = "jdbc:mysql://localhost:3306/InHabitDBNew?useSSL=false&allowPublicKeyRetrieval=true";
    private static String user = "root";
    private static String pass = "";
    private static boolean fallbackMode = false;

    // 2. Static block to initialize driver and load properties
    static {
        try {
            Properties p = new Properties();
            File f = new File("db.properties");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                p.load(fis);
                fis.close();
                if (p.getProperty("db.url") != null) url = p.getProperty("db.url");
                if (p.getProperty("db.user") != null) user = p.getProperty("db.user");
                if (p.getProperty("db.password") != null) pass = p.getProperty("db.password");
            }
        } catch (Exception e) {
            // Using default InHabitDBNew credentials
        }

        // Test connection at startup
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            con.close();
            fallbackMode = false;
            System.out.println("[DBConnection] Connected to MySQL InHabitDBNew successfully.");
        } catch (Exception e) {
            fallbackMode = true;
            System.out.println("[DBConnection] MySQL InHabitDBNew offline (" + e.getMessage() + "). Running in in-memory mode.");
        }
    }

    // 3. Get connection object
    public static Connection getConnection() {
        if (fallbackMode) {
            return null;
        }
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isFallbackMode() {
        return fallbackMode;
    }

    public static void setFallbackMode(boolean mode) {
        fallbackMode = mode;
    }
}
