package dao;

import dto.IngredienteDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class IngredienteDAO {

		//CREATE newIngrediente
	public void insertIngrediente(IngredienteDTO ingrediente) throws SQLException {
		String sql = "INSERT INTO Ingrediente(nomeIngrediente, tipologia) VALUES(?,?)" ;
		
		try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1,ingrediente.getNomeIngrediente());
			ps.setString(2,ingrediente.getTipologia());
			ps.executeUpdate();
		}
	}
	
	//UPDATE Ingrediente
	public void updateIngrediente(IngredienteDTO ingrediente) throws SQLException {
		String sql = "UPDATE Ingrediente SET"
				+ "nomeIngrediente = COALESCE(?,nomeIngrediente),"
				+ "tiopologia = COALESCE (?,tipologia)"
				+ "WHERE idIngrediente = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
		
			ps.setString(1,ingrediente.getNomeIngrediente());
			ps.setString(2,ingrediente.getTipologia());
			ps.executeUpdate();
		}
	}
	
	//DELETE Ingrediente
	public void deleteIngrediente(IngredienteDTO ingrediente) throws SQLException {
		String deleteSql = "DELETE FROM ingrediente WHERE idIngrediente = ?";
		String countSql = "SELECT COUNT(*) FROM ingredienti"; 
		String resetSql = "ALTER SEQUENCE ingrediente_idIngrediente_seq RESTART WITH 1";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement deleteStatement = conn.prepareStatement(deleteSql)){
			Statement check = conn.createStatement();
			
			deleteStatement.setInt(1,ingrediente.getId());
			deleteStatement.executeUpdate();
			
			ResultSet rs = check.executeQuery(countSql);
				
			if(rs.next() && rs.getInt(1)==0) {
				check.executeUpdate(resetSql);
			}
		}
	}

	}
