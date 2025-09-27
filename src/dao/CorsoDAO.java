package dao;

import dto.ChefDTO;
import dto.CorsoDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class CorsoDAO {

		// INSERT newCorso
		public void insertCorso(CorsoDTO newCorso) throws SQLException{
			String sql = "INSERT INTO corso (nomecorso, argomento, datainizio, datafine, anno, fkchef, frequenzasessioni) VALUES (?, ?, ?, ?, ?, ?, ?)";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newCorso.getNomeCorso());
				ps.setString(2, newCorso.getArgomento());
				ps.setDate(3, java.sql.Date.valueOf(newCorso.getDataInizio()));
				ps.setDate(4, java.sql.Date.valueOf(newCorso.getDataFine()));
				ps.setInt(5, newCorso.getAnno());
				ps.setInt(6, newCorso.getChefCorso().getId());
				ps.setString(7, newCorso.getFrequenzaSessioni());
				ps.executeUpdate();
			}
		}
		
		// UPDATE newCorso - LOGICA COALESCE
		public void updateCorso(CorsoDTO newCorso) throws SQLException{
			String sql = "UPDATE corso SET "
		               + "nomecorso = COALESCE(?, nomecorso), "
		               + "argomento = COALESCE(?, argomento), "
		               + "datainizio = COALESCE(?, datainizio) "
		               + "datafine = COALESCE(?, datafine) "
		               + "anno = COALESCE(?, anno) "
		               + "frequenzasessioni = COALESCE(?, frequenzasessioni) "
		               + "WHERE idcorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newCorso.getNomeCorso());
				ps.setString(2, newCorso.getArgomento());
				ps.setDate(3, java.sql.Date.valueOf(newCorso.getDataInizio()));
				ps.setDate(4, java.sql.Date.valueOf(newCorso.getDataFine()));
				ps.setInt(5, newCorso.getAnno());
				ps.setString(7, newCorso.getFrequenzaSessioni());
				ps.setInt(8, newCorso.getId());
				ps.executeUpdate();
			}
		}
		
		// READ (SELECT) Corso by id
		public CorsoDTO getCorsoById(int id) throws SQLException{
			String sql = "SELECT * FROM corso WHERE idcorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idcorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setArgomento(rs.getString("argomento"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					java.sql.Date sqlDataFine = rs.getDate("datafine");
					if(sqlDataFine!=null)
						corso.setDataFine(sqlDataInizio.toLocalDate());
					corso.setAnno(rs.getInt("anno"));
					ChefDAO chefCorsoDAO = new ChefDAO();
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
		public CorsoDTO getCorsoByName(String nomeCorso) throws SQLException{
			String sql = "SELECT * FROM corso WHERE nomecorso = ?";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, nomeCorso);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					CorsoDTO corso = new CorsoDTO();
					corso.setId(rs.getInt("idCorso"));
					corso.setNomeCorso(rs.getString("nomecorso"));
					corso.setArgomento(rs.getString("argomento"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					java.sql.Date sqlDataFine = rs.getDate("datafine");
					if(sqlDataFine!=null)
						corso.setDataFine(sqlDataInizio.toLocalDate());
					corso.setAnno(rs.getInt("anno"));
					ChefDAO chefCorsoDAO = new ChefDAO();
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
		public List<CorsoDTO> getCorsoByChefId(int idChef) throws SQLException{
			String sql = "SELECT * FROM corso WHERE fkchef = ?";
			List<CorsoDTO> elencoCorsi = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) { 
					CorsoDTO corso = new CorsoDTO();
		            corso.setId(rs.getInt("idCorso"));
		            corso.setNomeCorso(rs.getString("nomecorso"));
		            corso.setArgomento(rs.getString("argomento"));
					java.sql.Date sqlDataInizio = rs.getDate("datainizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					java.sql.Date sqlDataFine = rs.getDate("datafine");
					if(sqlDataFine!=null)
						corso.setDataFine(sqlDataInizio.toLocalDate());
					corso.setAnno(rs.getInt("anno"));
					ChefDAO chefCorsoDAO = new ChefDAO();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkchef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					
					elencoCorsi.add(corso);
				}
				
			}
			return elencoCorsi;
		}
		
		public List<CorsoDTO> getAllCorsi() throws SQLException{
			String sql = "SELECT * FROM corso";
			List<CorsoDTO> elencoCorsi = new ArrayList<>();
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) { 
					CorsoDTO corso = new CorsoDTO();
		            corso.setId(rs.getInt("idCorso"));
		            corso.setNomeCorso(rs.getString("nomeCorso"));
		            corso.setArgomento(rs.getString("argomento"));
					java.sql.Date sqlDataInizio = rs.getDate("dataInizio");
					if(sqlDataInizio!=null)
						corso.setDataInizio(sqlDataInizio.toLocalDate()); // conversione in tipo localDate
					java.sql.Date sqlDataFine = rs.getDate("dataFine");
					if(sqlDataFine!=null)
						corso.setDataFine(sqlDataInizio.toLocalDate());
					corso.setAnno(rs.getInt("anno"));
					ChefDAO chefCorsoDAO = new ChefDAO();
					ChefDTO chefCorso = chefCorsoDAO.getChefById(rs.getInt("fkChef"));
					corso.setChefCorso(chefCorso);
					corso.setFrequenzaSessioni(rs.getString("frequenzasessioni"));
					
					elencoCorsi.add(corso);
				}
				return elencoCorsi;
			}
		}
		
		//DELETE Corso
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
