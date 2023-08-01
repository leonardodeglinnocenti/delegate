package dao;

import java.sql.*;
import java.io.*;

public class Database {

    // The scheme is located in ../../resources/schema.sql
    private static String dbName = "base.db";

    // Singleton pattern
    private static Database instance = null;

    private Database() {
        // Exists only to defeat instantiation.
    }

    public static void setDatabase(String dbName) {
        Database.dbName = dbName;
    }

    public static Connection getConnection(String dbName) throws SQLException {
        if (instance == null) {
            instance = new Database();
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(dbName);
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static int initDatabase() throws IOException, SQLException {
        StringBuilder resultStringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("resources/schema.sql"));
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }

        Connection connection = getConnection();
        Statement statement = getConnection().createStatement();
        int row = statement.executeUpdate(resultStringBuilder.toString());

        statement.close();
        closeConnection(connection);
        return row;
    }
}
