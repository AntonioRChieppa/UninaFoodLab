package db_connection;

import java.sql.*;

public class db_connection {
	
	private static final String URL = "jdbc:postgresql://localhost:5432/uninafoodlab";
	private static final String USER = "postgres";
	private static final String PASSWORD = "Aurorina2013";
	
	private static Connection conn = null;
	
	// metodo per instaurare la comunicazione con il DB
	public static Connection getConnection() throws SQLException{
		if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }
	
}
