package dao.daoImplements;

import dto.ChefDTO;
import dto.CorsoDTO;
import db_connection.db_connection;
import dao.daoInterfaces.CorsoDAOInt;

import java.sql.*;
import java.util.*;

public class CorsoDAOImpl implements CorsoDAOInt{

		// INSERT newCorso
		@Override
		public void insertCorso(CorsoDTO newCorso) throws SQLException{
			String sql = "INSERT INTO corso (nomecorso, categoria, datainizio, numerosessioni, fkchef, frequenzasessioni) VALUES (?, ?, ?, ?, ?, ?)";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newCorso.getNomeCorso());
				ps.setString(2, newCorso.getCategoria());
				ps.setDate(3, java.sql.Date.valueOf(newCorso.getDataInizio()));
				ps.setInt(4, newCorso.getNumeroSessioni());
				ps.setInt(5, newCorso.getChefCorso().getId());
				ps.setString(6, newCorso.getFrequenzaSessioni());
				ps.executeUpdate();
			}
		}
		
		// UPDATE newCorso - LOGICA COALESCE
		@Override
		public void updateCorso(CorsoDTO newCorso) throws SQLException{
			String sql = "UPDATE corso SET "
		               + "nomecorso = COALESCE(?, nomecorso), "
		               + "categoria = COALESCE(?, categoria), "
		               + "datainizio = COALESCE(?, datainizio) "
		               + "numeroSessioni = COALESCE(?, numeroSessioni) "
		               + "frequenzasessioni = COALESCE(?, frequenzasessioni) "
		               + "WHERE idcorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newCorso.getNomeCorso());
				ps.setString(2, newCorso.getCategoria());
				
				//gestione null SQL per evitare errori a runtime
				if(newCorso.getDataInizio()==null) {
					ps.setNull(3, java.sql.Types.DATE);
				} else {
					ps.setDate(3, java.sql.Date.valueOf(newCorso.getDataInizio()));
				}
				
				if(newCorso.getNumeroSessioni()==null) {
					ps.setNull(5, java.sql.Types.INTEGER);
				} else {
					ps.setInt(5, newCorso.getNumeroSessioni());
				}
				
				ps.setString(6, newCorso.getFrequenzaSessioni());
				ps.setInt(7, newCorso.getId());
				ps.executeUpdate();
			}
		}
		
		// READ (SELECT) Corso by Id
		@Override
		public CorsoDTO getCorsoById(int idCorso) throws SQLException{
			String sql = "SELECT * FROM corso WHERE idCorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setInt(1, idCorso);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idCorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setCategoria(rs.getString("categoria"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					corso.setNumeroSessioni(rs.getInt("numerosessioni"));
					ChefDAOImpl chefCorsoDAO = new ChefDAOImpl();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkchef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					return corso;
				}
				else {
					return null;
				}
			}
		}
		
		// READ (SELECT) Corso by nomeCorso
		@Override
		public CorsoDTO getCorsoByName(String nomeCorso) throws SQLException{
			String sql = "SELECT * FROM corso WHERE nomecorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, nomeCorso);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idCorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setCategoria(rs.getString("categoria"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					corso.setNumeroSessioni(rs.getInt("numerosessioni"));
					ChefDAOImpl chefCorsoDAO = new ChefDAOImpl();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkchef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					return corso;
				}
				else {
					return null;
				}
			}
		}
		
		// READ (SELECT) Corsi by idChef (corsi di un determinato chef)
		@Override
		public List<CorsoDTO> getCorsoByChefId(int idChef) throws SQLException{
			String sql = "SELECT * FROM corso WHERE fkchef = ?";
			List<CorsoDTO> elencoCorsi = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) { 
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idCorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setCategoria(rs.getString("categoria"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					corso.setNumeroSessioni(rs.getInt("numerosessioni"));
					ChefDAOImpl chefCorsoDAO = new ChefDAOImpl();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkchef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					
					elencoCorsi.add(corso);
				}
				
			}
			return elencoCorsi;
		}
		
		@Override
		public List<CorsoDTO> getAllCorsi() throws SQLException{
			String sql = "SELECT * FROM corso";
			List<CorsoDTO> elencoCorsi = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) { 
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idCorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setCategoria(rs.getString("categoria"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					corso.setNumeroSessioni(rs.getInt("numerosessioni"));
					ChefDAOImpl chefCorsoDAO = new ChefDAOImpl();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkchef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					
					elencoCorsi.add(corso);
				}
				return elencoCorsi;
			}
		}
		
		// READ (SELECT) Corsi by argomento 
		
		//DELETE Corso
		@Override
		public void deleteCorso(CorsoDTO corso) throws SQLException{
			String deleteSql = "DELETE FROM corso WHERE idcorso = ?"; 
			String countSql = "SELECT COUNT(*) FROM corso"; 
			String resetSeqId = "ALTER SEQUENCE corso_idcorso_seq RESTART WITH 1"; // RESTART DEGLI ID IN CASO DI RECORD MANCANTI
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
					Statement checkStmt = conn.createStatement()){
					
					deleteStmt.setInt(1, corso.getId());
			        deleteStmt.executeUpdate();
			        
			        ResultSet rs = checkStmt.executeQuery(countSql);
			        if(rs.next() && rs.getInt(1)==0) {
			        	checkStmt.executeUpdate(resetSeqId);
			        }
			        
				}
		}
}
