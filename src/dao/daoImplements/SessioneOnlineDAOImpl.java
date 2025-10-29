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
	public void insertSessioneOnline(SessioneOnlineDTO sessioneOn) throws SQLException {
	    String insertSessioneSql = "INSERT INTO sessione (argomento, orainizio, datasessione, fkcorso, tiposessione) VALUES (?, ?, ?, ?, ?)";
	    String insertSessioneOnlineSql = "INSERT INTO sessioneonline (idsessioneonline, linkconferenza, fksessione) VALUES (?, ?, ?)"; // Assumendo ci sia fksessione

	    try (Connection conn = db_connection.getConnection()) {
	        conn.setAutoCommit(false); // inizio transazione

	        try {
	            int idSessione = -1;

	            try (PreparedStatement psSessione = conn.prepareStatement(insertSessioneSql, Statement.RETURN_GENERATED_KEYS)) {
	                psSessione.setString(1, sessioneOn.getArgomento());
	                psSessione.setTime(2, Time.valueOf(sessioneOn.getOraInizio()));
	                psSessione.setDate(3, java.sql.Date.valueOf(sessioneOn.getDataSessione()));
	                psSessione.setInt(4, sessioneOn.getCorsoSessione().getId());
	                psSessione.setString(5, "online"); 

	                psSessione.executeUpdate();

	                try (ResultSet generatedKeys = psSessione.getGeneratedKeys()) {
	                    if (generatedKeys.next()) {
	                        idSessione = generatedKeys.getInt(1);
	                        sessioneOn.setIdSessione(idSessione);
	                    } else {
	                        conn.rollback();
	                        throw new SQLException("Inserimento sessione base fallito, nessun ID ottenuto.");
	                    }
	                }
	            }

	            try (PreparedStatement psOnline = conn.prepareStatement(insertSessioneOnlineSql)) {
	                psOnline.setInt(1, idSessione); 
	                psOnline.setString(2, sessioneOn.getLinkConferenza());
	                psOnline.setInt(3, idSessione); 

	                psOnline.executeUpdate();
	            }

	            conn.commit();

	        } catch (SQLException ex) {
	            conn.rollback();
	            throw ex;
	        } finally {
	            conn.setAutoCommit(true);
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
			
		String updateSessioneOn = "UPDATE sessioneonline SET "
								+ "linkconferenza = COALESCE(?, linkconferenza) "
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
				
				if(upSessioneOn.getCorsoSessione() == null) {
					psSessione.setNull(4, java.sql.Types.INTEGER);
				}else {
					psSessione.setInt(4, upSessioneOn.getCorsoSessione().getId());
				}
				
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
		
		// READ (SELECT) ALL SESSIONI ONLINE BY ARGOMENTO AND DATA
		@Override
		public SessioneOnlineDTO getSessioneOnByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException{
			String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, son.linkconferenza "
		               + "FROM sessione s "
		               + "JOIN sessioneonline son " 
		               + "ON s.idsessione = son.fksessione "
		               + "WHERE argomento = ? AND datasessione = ? ";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newArgomento);
	            ps.setDate(2, java.sql.Date.valueOf(newDataSessione));
	            ResultSet rs = ps.executeQuery();
	            
	            if(rs.next()) {
	            	SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
	            	sessioneOn.setIdSessione(rs.getInt("idsessione"));
	            	sessioneOn.setArgomento(rs.getString("argomento"));
	            	sessioneOn.setOraInizio(rs.getTime("orainizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            sessioneOn.setTipoSessione(rs.getString("tiposessione"));
		            
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
			String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, son.linkconferenza "
		               + "FROM sessione s "
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
		        	sessioneOn.setOraInizio(rs.getTime("orainizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            sessioneOn.setTipoSessione(rs.getString("tiposessione"));;
		            
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
			String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, son.linkconferenza "
		               + "FROM sessione s "
		               + "JOIN sessioneonline son "
		               + "ON s.idsessione = son.fksessione "
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
					sessioneOn.setOraInizio(rs.getTime("orainizio").toLocalTime());

		            java.sql.Date sqlDate = rs.getDate("datasessione");
		            if (sqlDate != null) {
		            	sessioneOn.setDataSessione(sqlDate.toLocalDate());
		            }
		            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
		            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
		            sessioneOn.setCorsoSessione(corsoSessione);
		            sessioneOn.setTipoSessione(rs.getString("tiposessione"));
		            
		            sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
		            
		            elencoSessioniOnlineCorso.add(sessioneOn);
				}
				return elencoSessioniOnlineCorso;
			}
		}
		
		// SELECT (READ) ALL SESSIONI ONLINE BY IDCHEF
	    public List<SessioneOnlineDTO> getSessioniOnByChefId(int idChef) throws SQLException {
	        String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, son.linkconferenza "
	                   + "FROM sessione s "
	                   + "JOIN sessioneonline son "
	                   + "ON s.idsessione = son.fksessione "
	                   + "JOIN corso c "
	                   + "ON s.fkcorso = c.idcorso "
	                   + "WHERE c.fkchef = ? "
	                   + "ORDER BY s.datasessione ASC, s.orainizio ASC";

	        List<SessioneOnlineDTO> elencoSessioniOnCorsiChef = new ArrayList<>();
	        CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();

	        try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, idChef);
	            ResultSet rs = ps.executeQuery();

	            while (rs.next()) {
	                SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
	                sessioneOn.setIdSessione(rs.getInt("idsessione"));
	                sessioneOn.setArgomento(rs.getString("argomento"));
	                sessioneOn.setOraInizio(rs.getTime("orainizio").toLocalTime());

	                java.sql.Date sqlDate = rs.getDate("datasessione");
	                if (sqlDate != null) {
	                    sessioneOn.setDataSessione(sqlDate.toLocalDate());
	                }

	                CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	                sessioneOn.setCorsoSessione(corsoSessione);
	                sessioneOn.setTipoSessione(rs.getString("tipoSessione"));
	                sessioneOn.setLinkConferenza(rs.getString("linkconferenza"));
	                elencoSessioniOnCorsiChef.add(sessioneOn);
	            }
	            return elencoSessioniOnCorsiChef;
	        }
	    }
		
		
		//METODO DAO GET NUMERO SESSIONI ONLINE CHEF BY MESE AND ANNO
		@Override
		public int countSessioniOnlineByChefInMese(int idChef, int mese, int anno) throws SQLException {
		        // Query che unisce sessione e corso
		        String sql = "SELECT COUNT(s.idsessione) AS total "
		                   + "FROM sessione s "
		                   + "JOIN corso c ON s.fkcorso = c.idcorso "
		                   + "WHERE c.fkchef = ? "
		                   + "AND s.tipoSessione = 'online' " // Filtra per tipo 'online'
		                   + "AND EXTRACT(MONTH FROM s.datasessione) = ? "
		                   + "AND EXTRACT(YEAR FROM s.datasessione) = ?";

		        try (Connection conn = db_connection.getConnection();
		             PreparedStatement ps = conn.prepareStatement(sql)) {

		            ps.setInt(1, idChef);
		            ps.setInt(2, mese);  
		            ps.setInt(3, anno);   

		            try (ResultSet rs = ps.executeQuery()) {
		                if (rs.next()) {
		                    return rs.getInt("total"); // Restituisce il conteggio
		                }
		            }
		        }
		        return 0; // Restituisce 0 se non trova nulla
		}
		
}
