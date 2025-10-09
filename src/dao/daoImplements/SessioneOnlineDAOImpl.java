package dao.daoImplements;

import dto.SessioneOnlineDTO;
import dto.CorsoDTO;
import db_connection.db_connection;
import java.time.*;
import dao.daoInterfaces.SessioneOnlineDAOInt;

import java.sql.*;
import java.util.*;

public class SessioneOnlineDAOImpl implements SessioneOnlineDAOInt{

		// INSERT newSessioneOnline - GESTIONE DEGLI INSERIMENTI IN UN'UNICA TRANSIZIONE
		@Override
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
			        		psOnline.setInt(1, idSessione);
			        		psOnline.setString(2, sessioneOn.getLinkConferenza());
			        		psOnline.executeUpdate();
			        	}
			        }
			        
			        conn.commit(); // conferma, concludi transazione
				}catch(SQLException ex) {
					conn.rollback(); // se uno dei due inserimenti fallisce, annulla tutto
					throw ex; // rilancia l'eccezione al controller, al fine che esso possa gestirla
				}finally {
					conn.setAutoCommit(true); // ripristina lo stato originale di autocommit
				}
			}
		}
		
		// UPDATE sessioneOnline - LOGICA COALESCE
		@Override
		public void updateSessioneOnline(SessioneOnlineDTO upSessioneOn) throws SQLException{
			String updateSessione = "UPDATE sessione SET "+
					"argomento = COALESCE(?, argomento), " +
					"orainizio = COALESCE(?, orainizio), " +
					"datasessione = COALESCE(?, datasessione), " +
					"fkcorso = COALESCE(?, fkcorso) " +
					"WHERE idsessione = ?";
			
			String updateSessioneOn = "UPDATE sessioneonline SET"
									+ "linkconferenza = COALESCE(?, linkconferenza)"
									+ "WHERE idsessioneonline = ?";
			
			try(Connection conn = db_connection.getConnection()){
				conn.setAutoCommit(false);
				try(PreparedStatement psSessione = conn.prepareStatement(updateSessione); PreparedStatement psOnline = conn.prepareStatement(updateSessioneOn)){
					
					// AGGIORNAMENTO CAMPI SESSIONE
					psSessione.setString(1, upSessioneOn.getArgomento());
					
					if(upSessioneOn.getOraInizio() == null) {
						psSessione.setNull(2, java.sql.Types.TIME);
					} else {
						psSessione.setTime(2, Time.valueOf(upSessioneOn.getOraInizio()));
					}
					
					if (upSessioneOn.getDataSessione() == null) {
		                psSessione.setNull(3, java.sql.Types.DATE);
		            } else {
		                psSessione.setDate(3, java.sql.Date.valueOf(upSessioneOn.getDataSessione()));
		            }
					
					psSessione.setInt(4, upSessioneOn.getCorsoSessione().getId());
					psSessione.setInt(5,  upSessioneOn.getIdSessione());
					psSessione.executeUpdate();
					
					// AGGIORNAMENTO CAMPO SPECIFICO SESSIONE ONLINE
					psOnline.setString(1, updateSessioneOn);
					psOnline.setInt(2, upSessioneOn.getIdSessione());
					psOnline.executeUpdate();
					
					conn.commit(); // lancia entrambi gli update
				}catch(SQLException ex) {
					conn.rollback(); // se uno dei due update fallisce, torna indietro
					throw ex; // rilancia l'eccezione al controller
				} finally {
					conn.setAutoCommit(true);
				}
			}
		}
		
		// READ (SELECT) ALL SESSIONI ONLINE
		@Override
		public List<SessioneOnlineDTO> getAllSessioniOn() throws SQLException{
			String sql = "SELECT * FROM sessione s"
						+ "JOIN sessioneonline son"
						+ "ON s.idsessione = son.fksessione"
						+ "ORDER BY s.datasessione ASC";
			
			List<SessioneOnlineDTO> elencoSessioniOnline = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
					sessioneOn.setIdSessione(rs.getInt("idsessione"));
					sessioneOn.setArgomento(rs.getString("argomento"));
					sessioneOn.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            
		            sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
		           
		            elencoSessioniOnline.add(sessioneOn);
				}
				return elencoSessioniOnline;
			}
			
		}
		
		// READ (SELECT) ALL SESSIONI ONLINE BY ARGOMENTO AND DATA
		@Override
		public SessioneOnlineDTO getSessioneOnByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException{
			String sql = "SELECT * FROM sessione s"
					+ "JOIN sessioneonline son"
					+ "ON s.idsessione = son.fksessione"
					+ "WHERE argomento = ? AND datasessione = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newArgomento);
	            ps.setDate(3, java.sql.Date.valueOf(newDataSessione));
	            ResultSet rs = ps.executeQuery();
	            
	            if(rs.next()) {
	            	SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
	            	sessioneOn.setIdSessione(rs.getInt("idsessione"));
	            	sessioneOn.setArgomento(rs.getString("argomento"));
	            	sessioneOn.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            
		            sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
		            return sessioneOn;
	            }
	            else {
	            	return null;
	            }
	            
			}
		}
		
		// READ (SELECT) SESSIONE ONLINE BY ID
		@Override
		public SessioneOnlineDTO getSessioneOnById(int idSessioneOnline) throws SQLException{
			String sql = "SELECT * FROM sessione s "
		               + "JOIN sessioneonline son "
		               + "ON s.idsessione = son.fksessione "
		               + "WHERE son.idsessioneonline = ?";

		    try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

		        ps.setInt(1, idSessioneOnline);
		        ResultSet rs = ps.executeQuery();
		        
		        if(rs.next()) {
		        	SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
		        	sessioneOn.setIdSessione(rs.getInt("idsessione"));
		        	sessioneOn.setArgomento(rs.getString("argomento"));
		        	sessioneOn.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            
		            sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
		            return sessioneOn;
	            }
	            else {
	            	return null;
	            }
		    }
		}
		
		// READ (SELECT) ALL SESSIONI ONLINE BY CORSO
		@Override
		public List<SessioneOnlineDTO> getSessioniOnByCorso(int idCorso) throws SQLException{
			String sql = "SELECT * FROM sessione s "
					+ "JOIN sessioneonline son "
					+ "ON s.idsessione = son.fksessione"
					+ "WHERE s.fkcorso = ?"
					+ "ORDER BY s.datasessione ASC";
			
			List<SessioneOnlineDTO> elencoSessioniOnlineCorso = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				
				ps.setInt(1, idCorso);
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
					sessioneOn.setIdSessione(rs.getInt("idsessione"));
					sessioneOn.setArgomento(rs.getString("argomento"));
					sessioneOn.setOraInizio(rs.getTime("orarioinizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            
		            sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
		            
		            elencoSessioniOnlineCorso.add(sessioneOn);
				}
				return elencoSessioniOnlineCorso;
			}
		}
		
		// DELETE SESSIONE ONLINE
		@Override
		public void deleteSessioneOnline(SessioneOnlineDTO sessioneOn) throws SQLException{
			    String deleteSessioneSql = "DELETE FROM sessione WHERE idSessione = ?"; // eliminiamo solo da sessione, data la presenza del DELETE CASCADE
			    String countSql = "SELECT COUNT(*) FROM sessione";
			    String resetSeqId = "ALTER SEQUENCE sessione_idsessione_seq RESTART WITH 1"; // reset id se vuota

			    try (Connection conn = db_connection.getConnection();
			         PreparedStatement deleteSessioneStmt = conn.prepareStatement(deleteSessioneSql);
			         Statement checkStmt = conn.createStatement()) {

			        deleteSessioneStmt.setInt(1, sessioneOn.getIdSessione());
			        deleteSessioneStmt.executeUpdate();

			        ResultSet rs = checkStmt.executeQuery(countSql);
			        if (rs.next() && rs.getInt(1) == 0) {
			            checkStmt.executeUpdate(resetSeqId);
			        }

			    }
		}
		
		
}
