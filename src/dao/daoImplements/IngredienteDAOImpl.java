package dao.daoImplements;

import dto.IngredienteDTO;
import db_connection.db_connection;
import dao.daoInterfaces.IngredienteDAOInt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAOImpl implements IngredienteDAOInt{

	//CREATE newIngrediente
	@Override
	public void insertIngrediente(IngredienteDTO ingrediente) throws SQLException {
		String sql = "INSERT INTO Ingrediente(nomeIngrediente, tipologia) VALUES(?,?)" ;
		
		try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1,ingrediente.getNomeIngrediente());
			ps.setString(2,ingrediente.getTipologia());
			ps.executeUpdate();
		}
	}
	
	//UPDATE Ingrediente
	@Override
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
	@Override
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
	
	@Override
	public IngredienteDTO getIngredienteByName(String nomeIngrediente) throws SQLException{
		String sql = "SELECT * FROM ingrediente WHERE nomeIngrediente=?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1,nomeIngrediente);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				IngredienteDTO ingrediente = new IngredienteDTO();
				ingrediente.setId(rs.getInt("idIngrediente"));
				ingrediente.setNomeIngrediente(rs.getString("nomeIngrediente"));
				ingrediente.setTipologia(rs.getString("tipologia"));
				return ingrediente;
				}
			else {
				return null;
			}
		}
	}

	@Override
	public IngredienteDTO getIngredienteById(int id) throws SQLException{
		String sql = "SELECT * FROM ingrediente WHERE idIngrediente=?";
		try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
		ps.setInt(1,id);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			IngredienteDTO ingrediente = new IngredienteDTO();
			ingrediente.setNomeIngrediente(rs.getString("nomeIngrediente"));
			ingrediente.setTipologia(rs.getString("tipologia"));
			return ingrediente;
		}
		else {
			return null;
		}
		}
	}
}
