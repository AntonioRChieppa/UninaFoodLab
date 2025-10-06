package dao;

import dto.SessioneOnlineDTO;
import dto.CorsoDTO;
import db_connection.db_connection;
import java.time.*;

import java.sql.*;
import java.util.*;

public class SessioneOnlineDAO {

		// INSERT newSessioneOnline - GESTIONE DEGLI INSERIMENTI IN UN'UNICA TRANSIZIONE
		public void insertSessioneOnline(SessioneOnlineDTO sessioneOn) throws SQLException{
			String insertSessioneSql = "INSERT INTO sessione (argomento, orainizio, datasessione, fkcorso) VALUES (?, ?, ?, ?) RETURNING idsessione";
			String insertSessioneOnlineSql = "INSERT INTO sessioneonline (idsessioneonline, linkconferenza) VALUES (?, ?) ";
			
			try(Connection conn = db_connection.getConnection()){
				conn.setAutoCommit(false);
				try(PreparedStatement psSessione = conn.prepareStatement(insertSessioneSql)){
					psSessione.setString(1, sessioneOn.getArgomento());
			        psSessione.setTime(2, Time.valueOf(sessioneOn.getOraInizio()));
			        psSessione.setDate(3, java.sql.Date.valueOf(sessioneOn.getDataSessione()));
			        psSessione.setInt(4, sessioneOn.getCorsoSessione().getId());
			        
			        ResultSet rs = psSessione.executeQuery();
			        if(rs.next()) {
			        	int idSessione = rs.getInt("idSessione");
			        	sessioneOn.setIdSessione(idSessione);
			        	
			        	try(PreparedStatement psOnline = conn.prepareStatement(insertSessioneOnlineSql)){
			        		psOnline.setString(1, sessioneOn.getLinkConferenza());
			        		psOnline.executeUpdate();
			        	}
			        }
			        
			        conn.commit(); // conferma, concludi transazione
				}catch(SQLException ex) {
					conn.rollback(); // 
					throw ex;
				}finally {
					conn.setAutoCommit(true);
				}
			}
		}
}
