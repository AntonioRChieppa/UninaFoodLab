package dao;

import dto.ChefDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class ChefDAO {
	
	// CREATE newChef
	public void insertChef(ChefDTO chef) throws SQLException{
		String sql = "INSERT INTO Chef (nomechef, cognomechef, email, password) VALUES (?, ?, ?, ?)";
		// USO DEL TRY-WITH-RESOURCES
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, chef.getNome());
			ps.setString(2, chef.getCognome());
			ps.setString(3, chef.getEmail());
			ps.setString(4, chef.getPassword());
			ps.executeUpdate();
		}
	}
	
	// UPDATE newChef <-- viene passato l'oggetto "updatedChef" che avrà tutti i campi null tranne quelli da modificare recuperati dalla GUI
	public void updateChef(ChefDTO chef) throws SQLException {
		String sql = "UPDATE chef SET "
	               + "nomechef = COALESCE(?, nomechef), "
	               + "cognomechef = COALESCE(?, cognomechef), "
	               + "email = COALESCE(?, email) "
	               + "WHERE idchef = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, chef.getNome());
			ps.setString(2, chef.getCognome());
			ps.setString(3, chef.getEmail());
			ps.setInt(4, chef.getId());
	        ps.executeUpdate();
	        
		}
	}
	
	// UPDATE newChef password
	public void updateChefPassword(ChefDTO chef) throws SQLException{
		String sql = "UPDATE chef SET password = ? WHERE idchef = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, chef.getPassword());
			ps.setInt(2, chef.getId());
	        ps.executeUpdate();
	        
		}
	}
	
	// DELETE newChef
	public void deleteChef(ChefDTO chef) throws SQLException{
		String deleteSql = "DELETE FROM chef WHERE idchef = ?"; //Stringa che si occupa della cancellazione
		String countSql = "SELECT COUNT(*) FROM chef"; //Stringa che conta il numero di chef nel sistema
		String resetSeqId = "ALTER SEQUENCE chef_idchef_seq RESTART WITH 1"; //Stringa che resetta gli id ad uno, in caso di tabella vuota
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
			Statement checkStmt = conn.createStatement()){
			
			deleteStmt.setInt(1, chef.getId());
	        deleteStmt.executeUpdate();
	        
	        ResultSet rs = checkStmt.executeQuery(countSql);
	        if(rs.next() && rs.getInt(1)==0) {
	        	checkStmt.executeUpdate(resetSeqId);
	        }
	        
		}
	}
	
	// READ (SELECT) newChef by id
	public ChefDTO getChefById(int id) throws SQLException{
		String sql = "SELECT * FROM chef WHERE idchef = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, id);
	        ResultSet rs = ps.executeQuery();
	        
	        if(rs.next()) {
	        	ChefDTO chef = new ChefDTO();
	            chef.setId(rs.getInt("idchef"));
	            chef.setNome(rs.getString("nomechef"));
	            chef.setCognome(rs.getString("cognomechef"));
	            chef.setEmail(rs.getString("email"));
	            chef.setPassword(rs.getString("password"));
	            return chef;
	        }
	        else {
	        	return null;
	        }
		}
	}
	
	public ChefDTO getChefByEmailAndPassword(String email, String password) throws SQLException{
		String sql = "SELECT * FROM chef WHERE email = ? AND password = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				ChefDTO chef = new ChefDTO();
				chef.setId(rs.getInt("idchef"));
				chef.setNome(rs.getString("nomechef"));
				chef.setCognome(rs.getString("cognomechef"));
				chef.setEmail(rs.getString("email"));
	            chef.setPassword(rs.getString("password"));
	            return chef;
	        }
	        else {
	        	return null;
	        }	
		}
	}
	
	public ChefDTO getChefByEmail(String email) throws SQLException{
		String sql = "SELECT * FROM chef WHERE email = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				ChefDTO chef = new ChefDTO();
				chef.setId(rs.getInt("idchef"));
				chef.setNome(rs.getString("nomechef"));
				chef.setCognome(rs.getString("cognomechef"));
				chef.setEmail(rs.getString("email"));
	            chef.setPassword(rs.getString("password"));
	            return chef;
	        }
	        else {
	        	return null;
	        }	
		}
	}
	
	// READ (SELECT) all chefs
	public List<ChefDTO> getAllChefs() throws SQLException{
		String sql = "SELECT * FROM chef";
		List<ChefDTO> chefList = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				ChefDTO chef = new ChefDTO();
	            chef.setId(rs.getInt("idchef"));
	            chef.setNome(rs.getString("nomechef"));
	            chef.setCognome(rs.getString("cognomechef"));
	            chef.setEmail(rs.getString("email"));
	            chef.setPassword(rs.getString("password"));
	            
	            chefList.add(chef);
			}
			
		}
		return chefList;
		
	}
	
	//READ (SELECT) Chef by name and surname
	public List<ChefDTO> getChefByNameAndSurname(String nome, String cognome) throws SQLException{
		String sql = "SELECT * FROM chef WHERE nomechef = ? AND cognomechef = ?";
		List<ChefDTO> chefListEqualName = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, nome);
			ps.setString(2, cognome);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next() == false) {
				return null;
			}
			else {
				while(rs.next()) {
					ChefDTO chef = new ChefDTO();
					chef.setId(rs.getInt("idchef"));
					chef.setNome(rs.getString("nomechef"));
					chef.setCognome(rs.getString("cognomechef"));
					chef.setEmail(rs.getString("email"));
		            chef.setPassword(rs.getString("password"));
	
		            chefListEqualName.add(chef);
		        }
		        return chefListEqualName;
			}
		}
	}
	
}
