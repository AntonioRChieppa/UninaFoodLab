package dao;

import dto.CorsoDTO;
import db_connection.db_connection;

import java.sql.*;
import java.util.*;

public class CorsoDAO {

		// INSERT newCorso
		public void insertCorso(CorsoDTO newCorso) throws SQLException{
			String sql = "INSERT INTO Corso (nomeCorso, argomento, dataInizio, dataFine, anno, fkChef)";
			
			try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
				ps.setString(1, newCorso.getNomeCorso());
				ps.setString(2, newCorso.getArgomento());
				ps.setDate(3, java.sql.Date.valueOf(newCorso.getDataInizio()));
				ps.setDate(4, java.sql.Date.valueOf(newCorso.getDataFine()));
				ps.setInt(5, newCorso.getAnno());
				ps.setInt(6, newCorso.getChefCorso().getId());
				ps.executeUpdate();
			}
		}
		
}
