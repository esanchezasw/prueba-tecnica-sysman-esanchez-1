package com.erika.tasks.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; 
    private static final String USER = "tasks_app";
    private static final String PASSWORD = "tareas123";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver"); 
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver JDBC de Oracle", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
