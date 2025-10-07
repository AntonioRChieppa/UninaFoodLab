package dao;

import dto.RicettaDTO;
import dto.SessioneDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class RicettaDAO {
	
	//Insert newRicetta
	public void insertRicetta(RicettaDTO ricetta) throws SQLException{
		String sql = "INSERT INTO ricetta (nomericetta, tempopreparazione, porzioni, difficolta,fkSessione VALUES (?,?,?,?,?)" ;
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString (1, ricetta.getNomeRicetta());
			ps.setInt (2, ricetta.getTempoPreparazione());
			ps.setInt (3, ricetta.getPorzioni());
			ps.setString (4, ricetta.getDifficolta());
			ps.setInt(5,ricetta.getSessioneRicetta().getIdSessione());
			ps.executeUpdate();
		}
	}
	
	//UPDATE newRicetta - LOGICA COALESCE
	public void updateRicetta(RicettaDTO ricetta) throws SQLException{
		String sql = "UPDATE ricetta SET "
				+"nomericetta = COALESCE(?,nomericetta)," 
				+"tempoPreparazione = COALESCE(?,tempoPreparazione),"
				+"porzioni =  COALESCE(?,porzioni),"
				+"difficolta = COALESCE(?,difficolta),"
				+"tipoSessione = COALESCE (?,sessioneRicetta)"
				+"WHERE idricetta = ?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString (1, ricetta.getNomeRicetta());
			ps.setInt (2, ricetta.getTempoPreparazione());
			ps.setInt (3, ricetta.getPorzioni());
			ps.setString(4, ricetta.getDifficolta());
			ps.executeUpdate();
		}
	}
	
	//DELETE Ricetta
	public void deleteRicetta(RicettaDTO ricetta) throws SQLException {
		String deleteSql = "DELETE FROM ricetta WHERE idRicetta? ?";
		String countSql = "SELECT COUNT(*) FROM ricetta";
		String resetSqlId = "ALTER SEQUENCE ricetta_idricetta_seq RESTART WITH 1";
		
		Connection conn = db_connection.getConnection(); PreparedStatement deleteStatement = conn.prepareStatement(deleteSql);
		Statement checkStatement = conn.createStatement();
		
		deleteStatement.setInt(1,ricetta.getId());
		deleteStatement.executeUpdate();
		
		ResultSet rs = checkStatement.executeQuery(countSql);
		if(rs.next() && rs.getInt(1)==0) {
			checkStatement.executeUpdate(resetSqlId);
		}
		
		
	}
	
	//READ (SELECT) Ricetta by name
	
	public RicettaDTO getRicettaByName(String nomeRicetta) throws SQLException {
		String sql = "SELECT * FROM ricetta WHERE nomeRicetta = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, nomeRicetta);
			ResultSet rs = ps.executeQuery(); 
			
			if(rs.next()) {
				RicettaDTO ricetta = new RicettaDTO();
				ricetta.setId(rs.getInt("idRicetta"));
				ricetta.setNomeRicetta(rs.getString("nomeRicetta"));
				ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
				ricetta.setPorzioni(rs.getInt("porzioni"));
				ricetta.setDifficolta(rs.getString("difficolta"));
				return ricetta;
			}
			else {
				return null;
					
				}
			}
		}
	
	//READ (SELECT) all recipe
	public List<RicettaDTO> getAllRecipes() throws SQLException{
		String sql = "SELECT * FROM ricetta";
		List <RicettaDTO> listaricette = new ArrayList();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			RicettaDTO ricetta = new RicettaDTO();
			ricetta.setId(rs.getInt ("idRicetta"));
			ricetta.setNomeRicetta(rs.getString ("nomeRicetta"));
			ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
			ricetta.setPorzioni(rs.getInt("porzioni"));
			ricetta.setDifficolta(rs.getString("difficolta"));
			
			listaricette.add(ricetta);
		}
		
		}
	return listaricette;
	}
	
	public RicettaDTO getRicettaById(int id) throws SQLException{
		String sql = "SELECT * FROM ricetta WHERE idRicetta=?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				RicettaDTO ricetta = new RicettaDTO();
				ricetta.setNomeRicetta(rs.getString("nomeRicetta"));
				ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
				ricetta.setPorzioni(rs.getInt("porzioni"));
				ricetta.setDifficolta(rs.getString("difficoltà"));
				return ricetta;
			}
			else {
				return null;
			}
		}			
	}
	
}
