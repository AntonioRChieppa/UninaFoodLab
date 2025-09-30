package dao;

import dto.RicettaDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class RicettaDAO {
	
	//Insert newRicetta
	public void insertRicetta(RicettaDTO newRicetta) throws SQLException{
		String sql = "INSERT INTO ricetta (nomericetta, tempopreparazione, porzioni, difficolta VALUES (?,?,?,?)" ;
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString (1, newRicetta.getNomeRicetta());
			ps.setInt (2, newRicetta.getTempoPreparazione());
			ps.setInt (3, newRicetta.getPorzioni());
			ps.setString (4, newRicetta.getDifficolta());
			ps.executeUpdate();
		}
	}
	
	//UPDATE newRicetta - LOGICA COALESCE
	public void updateRicetta(RicettaDTO newRicetta) throws SQLException{
		String sql = "UPDATE ricetta SET "
				+"nomericetta = COALESCE(?,nomericetta)," 
				+"tempoPreparazione = COALESCE(?,tempoPreparazione),"
				+"porzioni =  COALESCE(?,porzioni),"
				+"difficolta = COALESCE(?,difficolta)"
				+"WHERE idricetta = ?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString (1, newRicetta.getNomeRicetta());
			ps.setInt (2, newRicetta.getTempoPreparazione());
			ps.setInt (3, newRicetta.getPorzioni());
			ps.setString(4, newRicetta.getDifficolta());
			ps.executeUpdate();
		}
	}
	//READ (SELECT) Ricetta by nome
	
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
	}
