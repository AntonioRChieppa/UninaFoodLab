package dao.daoImplements;

import dto.SessioneDTO;
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
	public void insertSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException {
	    String insertSessioneSql = "INSERT INTO sessione (argomento, orainizio, datasessione, fkcorso, tiposessione) VALUES (?, ?, ?, ?, ?)";
	    String insertSessioneInPresenzaSql = "INSERT INTO sessioneinpresenza (idsessioneinpresenza, sede, edificio, aula, fksessione) VALUES (?, ?, ?, ?, ?)";

	    // Usa try-with-resources per la connessione
	    try (Connection conn = db_connection.getConnection()) {
	        conn.setAutoCommit(false); // inizio transazione

	        // Usa try-catch-finally interno per gestire la transazione
	        try {
	            int idSessione = -1; 

	            // Primo inserimento (sessione) usando try-with-resources per lo statement
	            try (PreparedStatement psSessione = conn.prepareStatement(insertSessioneSql, Statement.RETURN_GENERATED_KEYS)) {
	                psSessione.setString(1, sessioneIP.getArgomento());
	                psSessione.setTime(2, Time.valueOf(sessioneIP.getOraInizio()));
	                psSessione.setDate(3, java.sql.Date.valueOf(sessioneIP.getDataSessione()));
	                psSessione.setInt(4, sessioneIP.getCorsoSessione().getId());
	                psSessione.setString(5, "presenza");

	                psSessione.executeUpdate(); // Esegui l'update

	                // Recupera l'ID generato usando try-with-resources per il ResultSet
	                try (ResultSet generatedKeys = psSessione.getGeneratedKeys()) {
	                    if (generatedKeys.next()) {
	                        idSessione = generatedKeys.getInt(1);
	                        sessioneIP.setIdSessione(idSessione); // Aggiorna DTO se necessario
	                    } else {
	                        conn.rollback();
	                        throw new SQLException("Inserimento sessione base fallito, nessun ID ottenuto.");
	                    }
	                } 
	            } 
	            
	            // Secondo inserimento (sessioneinpresenza) usando try-with-resources
	            try (PreparedStatement psPresenza = conn.prepareStatement(insertSessioneInPresenzaSql)) {
	                psPresenza.setInt(1, idSessione); // Usa l'ID recuperato
	                psPresenza.setString(2, sessioneIP.getSede());
	                psPresenza.setString(3, sessioneIP.getEdificio());
	                psPresenza.setString(4, sessioneIP.getAula());
	                psPresenza.setInt(5, idSessione); // Usa l'ID recuperato

	                psPresenza.executeUpdate(); // Esegui l'update
	            } 

	            conn.commit(); // Conferma transazione solo se tutto è andato a buon fine

	        } catch (SQLException ex) {
	            conn.rollback(); // Se uno degli inserimenti fallisce, annulla tutto
	            throw ex; // Rilancia l'eccezione originale al controller
	        } finally {
	            conn.setAutoCommit(true); // Ripristina lo stato originale (questo finally si riferisce al try interno)
	        }
	    } // conn chiusa automaticamente qui (dal try-with-resources esterno)
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
		
		String updateSessioneInPresenza = "UPDATE sessioneinpresenza SET "+
										  "sede = COALESCE(?, sede), "+
										  "edificio = COALESCE(?, edificio), "+
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
				
				if(upSessioneIp.getCorsoSessione() == null) {
					psSessione.setNull(4, java.sql.Types.INTEGER);
				} else {
					psSessione.setInt(4, upSessioneIp.getCorsoSessione().getId());
				}
				
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
	
	// READ (SELECT) ALL SESSIONI IN PRESENZA BY ARGOMENTO AND DATA
	@Override
	public SessioneInPresenzaDTO getSessioneIpByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException{
		String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, " // Aggiunto s.tipoSessione
	               + "sip.sede, sip.edificio, sip.aula "
	               + "FROM sessione s "
	               + "JOIN sessioneinpresenza sip "
	               + "ON s.idsessione = sip.fksessione "
	               + "WHERE argomento = ? AND datasessione = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, newArgomento);
            ps.setDate(2, java.sql.Date.valueOf(newDataSessione));
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()) {
            	SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
            	sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orainizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            sessioneIP.setTipoSessione(rs.getString("tipoSessione"));
	            
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
		String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, " // Aggiunto s.tipoSessione
	               + "sip.sede, sip.edificio, sip.aula "
	               + "FROM sessione s "
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
	            sessioneIP.setOraInizio(rs.getTime("orainizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            sessioneIP.setTipoSessione(rs.getString("tipoSessione"));
	            
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
		String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, " // Aggiunto s.tipoSessione
	               + "sip.sede, sip.edificio, sip.aula "
	               + "FROM sessione s "
	               + "JOIN sessioneinpresenza sip "
	               + "ON s.idsessione = sip.fksessione "
	               + "WHERE s.fkcorso = ? "
	               + "ORDER BY s.datasessione ASC";
		
		List<SessioneInPresenzaDTO> elencoSessioniInPresenzaCorso = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, idCorso);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
				sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orainizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            sessioneIP.setTipoSessione(rs.getString("tipoSessione"));
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            
	            elencoSessioniInPresenzaCorso.add(sessioneIP);
			}
			return elencoSessioniInPresenzaCorso;
		}
	}
	
	// SELECT (READ) ALL SESSIONI IN PRESENZA BY IDCHEF
	public List<SessioneInPresenzaDTO> getSessioniIpByChefId(int idChef) throws SQLException{
		String sql = "SELECT s.idsessione, s.argomento, s.orainizio, s.datasessione, s.fkcorso, s.tipoSessione, " // Aggiunto s.tipoSessione
	               + "sip.sede, sip.edificio, sip.aula "
	               + "FROM sessione s "
	               + "JOIN sessioneinpresenza sip "
	               + "ON s.idsessione = sip.fksessione "
	               + "JOIN corso c "
	               + "ON s.fkcorso = c.idcorso "
	               + "WHERE c.fkchef = ? "
	               + "ORDER BY s.datasessione ASC, s.orainizio ASC";

	    try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
	    	List<SessioneInPresenzaDTO> elencoSessioniIpCorsiChef = new ArrayList<>();
	        ps.setInt(1, idChef);
	        ResultSet rs = ps.executeQuery();
	        
	        while(rs.next()) {
         	SessioneInPresenzaDTO sessioneIP = new SessioneInPresenzaDTO();
         	sessioneIP.setIdSessione(rs.getInt("idsessione"));
				sessioneIP.setArgomento(rs.getString("argomento"));
	            sessioneIP.setOraInizio(rs.getTime("orainizio").toLocalTime());

	            java.sql.Date sqlDate = rs.getDate("datasessione");
	            if (sqlDate != null) {
	                sessioneIP.setDataSessione(sqlDate.toLocalDate());
	            }
	            CorsoDAOImpl corsoSessioneDAO = new CorsoDAOImpl();
	            CorsoDTO corsoSessione = corsoSessioneDAO.getCorsoById(rs.getInt("fkCorso"));
	            sessioneIP.setCorsoSessione(corsoSessione);
	            sessioneIP.setTipoSessione(rs.getString("tipoSessione"));
	            
	            sessioneIP.setSede(rs.getString("sede"));
	            sessioneIP.setEdificio(rs.getString("edificio"));
	            sessioneIP.setAula(rs.getString("aula"));
	            
	            elencoSessioniIpCorsiChef.add(sessioneIP);
         }
         return elencoSessioniIpCorsiChef;
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
	
	//METODO DAO GET NUMERO SESSIONI IN PRESENZA CHEF BY MESE AND ANNO
	@Override
	public int countSessioniInPresenzaByChefInMese(int idChef, int mese, int anno) throws SQLException {
	        String sql = "SELECT COUNT(s.idsessione) AS total "
	                   + "FROM sessione s "
	                   + "JOIN corso c ON s.fkcorso = c.idcorso "
	                   + "WHERE c.fkchef = ? "
	                   + "AND s.tipoSessione = 'presenza' " // Filtra per tipo 'online'
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
