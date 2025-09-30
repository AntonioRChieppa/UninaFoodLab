package dao;

import dto.RicettaDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class RicettaDAO {
	
	//Insert newRicetta
	public void insertRicetta(RicettaDTO newricetta) throws SQLException{
		String sql = "INSERT INTO ricetta (nomericetta, tempopreparazione, porzioni, difficolta VALUES (?,?,?,?)" ;
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, newricetta.getNomeRicetta());
			ps.setInt(2, newricetta.getTempoPreparazione());
			ps.setInt(3, newricetta.getPorzioni());
			ps.setString(4, newricetta.getDifficolta());
			ps.executeUpdate();
		}
	}
	
	
	
}
