package Database;

import java.sql.*;

public class DbConnection {
    private static final String DB_URL = "jdbc:postgresql://rangesh-pt6115:5432/";
    private static final String USER = "postgres";
    private static final String PASS = "1234";
    static Connection connection;

    private DbConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL + "zoho_social", USER, PASS);
                System.out.println("connected...");
            } catch (Exception e) {
                System.out.println(e);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement s = connection.createStatement();
                s.execute("CREATE DATABASE zoho_social;");
                connection = DriverManager.getConnection(DB_URL + "zoho_social", USER, PASS);
            }
            return connection;
        }
        return connection;
    }
}
