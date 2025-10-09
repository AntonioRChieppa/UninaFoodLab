package dao.daoImplements;

import dto.SessioneInPresenzaDTO;
import dto.CorsoDTO;
import db_connection.db_connection;
import java.time.*;
import dao.daoInterfaces.SessioneInPresenzaDAOInt;

import java.sql.*;
import java.util.*;

public class SessioneInPresenzaDAOImpl implements SessioneInPresenzaDAOInt{
	
	// INSERT newSessioneInPresenza - GESTIONE DEGLI INSERIMENTI IN UN'UNICA TRANSAZIONE
	@Override
	public void insertSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException{
		String insertSessioneSql = "INSERT INTO sessione (argomento, orainizio, datasessione, fkcorso) VALUES (?, ?, ?, ?) RETURNING idsessione";
		String insertSessioneInPresenzaSql = "INSERT INTO sessioneinpresenza (idsessioneinpresenza, sede, edificio, aula, fksessione) VALUES (?, ?, ?, ?, ?)";
		
		try(Connection conn = db_connection.getConnection()){
			conn.setAutoCommit(false); // inizio transazione
			try(PreparedStatement psSessione = conn.prepareStatement(insertSessioneSql)){
				psSessione.setString(1, sessioneIP.getArgomento());
		        psSessione.setTime(2, Time.valueOf(sessioneIP.getOraInizio()));
		        psSessione.setDate(3, java.sql.Date.valueOf(sessioneIP.getDataSessione()));
		        psSessione.setInt(4, sessioneIP.getCorsoSessione().getId());
		        
		        ResultSet rs = psSessione.executeQuery();
		        if(rs.next()) {
		        	int idSessione = rs.getInt("idSessione");
		            sessioneIP.setIdSessione(idSessione);
		            
		            try (PreparedStatement psPresenza = conn.prepareStatement(insertSessioneInPresenzaSql)) {
		                psPresenza.setInt(1, idSessione);
		                psPresenza.setString(2, sessioneIP.getSede());
		                psPresenza.setString(3, sessioneIP.getEdificio());
		                psPresenza.setString(4, sessioneIP.getAula());
		                psPresenza.setInt(5, idSessione);
		                psPresenza.executeUpdate();
		            }
		        }
		        
		        conn.commit(); // conferma, concludi transazione
		        }catch(SQLException ex) {
		        	conn.rollback(); // se uno dei due inserimenti fallisce, annulla tutto
		        	throw ex; // rilancia l'eccezione al fine che il controller possa gestirla
		        }finally {
		        	conn.setAutoCommit(true); // ripristina lo stato originale
		        }
		}
	}
	
	// UPDATE SessioneInPresenza - LOGICA COALESCE
	@Override
	public void updateSessioneInPresenza(SessioneInPresenzaDTO upSessioneIp) throws SQLException{
		String updateSessione = "UPDATE sessione SET "+
								"argomento = COALESCE(?, argomento), " +
								"orainizio = COALESCE(?, orainizio), " +
								"datasessione = COALESCE(?, datasessione), " +
								"fkcorso = COALESCE(?, fkcorso) " +
								"WHERE idsessione = ?";
		
		String updateSessioneInPresenza = "UPDATE sessioneinpresenza SET"+
										  "sede = COALESCE(?, sede)"+
										  "edificio = COALESCE(?, edificio)"+
										  "aula = COALESCE(?, aula) " +
										  "WHERE idsessioneinpresenza = ?";
		
		try(Connection conn = db_connection.getConnection()){
			conn.setAutoCommit(false);
			try(PreparedStatement psSessione = conn.prepareStatement(updateSessione); PreparedStatement psPresenza = conn.prepareStatement(updateSessioneInPresenza)){
				
				// AGGIORNAMENTO CAMPI SESSIONE
				psSessione.setString(1, upSessioneIp.getArgomento());
				
				if(upSessioneIp.getOraInizio() == null) {
					psSessione.setNull(2, java.sql.Types.TIME);
				} else {
					psSessione.setTime(2, Time.valueOf(upSessioneIp.getOraInizio()));
				}
				
				if (upSessioneIp.getDataSessione() == null) {
	                psSessione.setNull(3, java.sql.Types.DATE);
	            } else {
	                psSessione.setDate(3, java.sql.Date.valueOf(upSessioneIp.getDataSessione()));
	            }
				
				psSessione.setInt(4, upSessioneIp.getCorsoSessione().getId());
				psSessione.setInt(5,  upSessioneIp.getIdSessione());
				psSessione.executeUpdate();
				
				// AGGIORNAMENTO VALORI SPECIFICI SESSIONE IN PRESENZA
				psPresenza.setString(1, upSessioneIp.getSede());
	            psPresenza.setString(2, upSessioneIp.getEdificio());
	            psPresenza.setString(3, upSessioneIp.getAula());
	            psPresenza.setInt(4, upSessioneIp.getIdSessione());
	            psPresenza.executeUpdate();
				
	            conn.commit(); // LANCIA ENTRAMBE GLI UPDATE
			}catch(SQLException ex) {
				conn.rollback(); // EFFETTUA IL ROLLBACK SE UNO DEI DUE UPDATE E' FALLITO
				throw ex;
			}finally {
				conn.setAutoCommit(true);
			}
		}
	}
	
	// READ (SELECT) ALL SESSIONI IN PRESENZA
	@Override
	public List<SessioneInPresenzaDTO> getAllSessioniIP() throws SQLException{
		String sql = "SELECT * FROM sessione s "
				+ "JOIN sessioneinpresenza sip "
				+ "ON s.idsessione = sip.fksessione"
				+ "ORDER BY s.datasessione ASC";
		
		List<SessioneInPresenzaDTO> elencoSessioniInPresenza = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
				sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            
	            elencoSessioniInPresenza.add(sessioneIP);
			}
			return elencoSessioniInPresenza;
		}
	}
	
	// READ (SELECT) ALL SESSIONI IN PRESENZA BY ARGOMENTO AND DATA
	@Override
	public SessioneInPresenzaDTO getSessioneIpByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException{
		String sql = "SELECT * FROM sessione s"
				+ "JOIN sessioneinpresenza sip"
				+ "ON s.idsessione = sip.fksessione"
				+ "WHERE argomento = ? AND datasessione = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, newArgomento);
            ps.setDate(3, java.sql.Date.valueOf(newDataSessione));
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
            	SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
            	sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            return sessioneIP;
            }
            else {
            	return null;
            }
            
		}
	}
	
	// READ (SELECT) SESSIONE IN PRESENZA BY ID
	@Override
	public SessioneInPresenzaDTO getSessioneIpById(int idSessioneInPresenza) throws SQLException{
		String sql = "SELECT * FROM sessione s "
	               + "JOIN sessioneinpresenza sip "
	               + "ON s.idsessione = sip.fksessione "
	               + "WHERE sip.idsessioneinpresenza = ?";

	    try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idSessioneInPresenza);
	        ResultSet rs = ps.executeQuery();
	        
	        if(rs.next()) {
            	SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
            	sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            return sessioneIP;
            }
            else {
            	return null;
            }
	    }
	}
	
	// READ (SELECT) ALL SESSIONI IN PRESENZA BY CORSO
	@Override
	public List<SessioneInPresenzaDTO> getSessioniIpByCorso(int idCorso) throws SQLException{
		String sql = "SELECT * FROM sessione s "
				+ "JOIN sessioneinpresenza sip "
				+ "ON s.idsessione = sip.fksessione"
				+ "WHERE s.fkcorso = ?"
				+ "ORDER BY s.datasessione ASC";
		
		List<SessioneInPresenzaDTO> elencoSessioniInPresenzaCorso = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, idCorso);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
				sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            
	            elencoSessioniInPresenzaCorso.add(sessioneIP);
			}
			return elencoSessioniInPresenzaCorso;
		}
	}
	
	// DELETE SESSIONE IN PRESENZA
	@Override
	public void deleteSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException{
		    String deleteSessioneSql = "DELETE FROM sessione WHERE idSessione = ?"; // eliminiamo solo da sessione, data la presenza del DELETE CASCADE
		    String countSql = "SELECT COUNT(*) FROM sessione";
		    String resetSeqId = "ALTER SEQUENCE sessione_idsessione_seq RESTART WITH 1"; // reset id se vuota

		    try (Connection conn = db_connection.getConnection();
		         PreparedStatement deleteSessioneStmt = conn.prepareStatement(deleteSessioneSql);
		         Statement checkStmt = conn.createStatement()) {

		        deleteSessioneStmt.setInt(1, sessioneIP.getIdSessione());
		        deleteSessioneStmt.executeUpdate();

		        ResultSet rs = checkStmt.executeQuery(countSql);
		        if (rs.next() && rs.getInt(1) == 0) {
		            checkStmt.executeUpdate(resetSeqId);
		        }

		    }
	}
	
}
