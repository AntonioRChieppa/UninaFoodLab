package dao;

import dto.SessioneInPresenzaDTO;
import dto.CorsoDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class SessioneInPresenzaDAO {

	// INSERT newSessioneInPresenza
	public void insertSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException{
		String insertSessioneSql = "INSERT INTO sessione (durataSessione, orarioInizio, dataSessione) VALUES (?, ?, ?) RETURNING idSessione";
		String insertSessioneInPresenzaSql = "INSERT INTO sessioneInPresenza (idSessioneInPresenza, sede, edificio, aula) VALUES (?, ?, ?, ?)";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement psSessione = conn.prepareStatement(insertSessioneSql)){
			psSessione.setInt(1, sessioneIP.getDurataSessione());
	        psSessione.setTime(2, Time.valueOf(sessioneIP.getOraInizio()));
	        psSessione.setDate(3, java.sql.Date.valueOf(sessioneIP.getDataSessione()));

	        ResultSet rs = psSessione.executeQuery();
	        if(rs.next()) {
	        	int idSessione = rs.getInt("idSessione");
	            sessioneIP.setIdSessione(idSessione);
	            
	            try (PreparedStatement psPresenza = conn.prepareStatement(insertSessioneInPresenzaSql)) {
	                psPresenza.setInt(1, idSessione);
	                psPresenza.setString(2, sessioneIP.getSede());
	                psPresenza.setString(3, sessioneIP.getEdificio());
	                psPresenza.setString(4, sessioneIP.getAula());
	                psPresenza.executeUpdate();
	            }
	        }
		}
	}
	
	// UPDATE newSessioneInPresenza - LOGICA COALESCE
	
	
}
