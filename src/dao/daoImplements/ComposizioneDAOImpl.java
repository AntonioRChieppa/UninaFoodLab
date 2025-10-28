package dao.daoImplements;

import dto.ComposizioneDTO;
import dto.IngredienteDTO;
import db_connection.db_connection;
import java.sql.*;
import java.util.*;
import dao.daoInterfaces.ComposizioneDAOInt;

public class ComposizioneDAOImpl implements ComposizioneDAOInt{ 

	
	//CREATE newComposizione
	@Override
	public void insertComposizione(ComposizioneDTO composizione) throws SQLException {
		String sql = "INSERT INTO Composizione(fkingrediente, fkricetta) VALUES(?,?)";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1,composizione.getIngredienteRicetta().getId());
			ps.setInt(2,composizione.getRicettaIngrediente().getId());
			ps.executeUpdate();
		}
	}
	
	//METODO PER AVERE I NOMI DI TUTTI GLI INGREDIENTI DI UNA RICETTA PASSATA PER ID
	@Override
		public List<IngredienteDTO> getAllIngredientiRicetta(int idRicetta) throws SQLException{
		String sql = "SELECT i.nomeIngrediente FROM Composizione c "
				+ "JOIN Ingrediente i ON c.fkIngrediente = i.idIngrediente "
				+ "WHERE c.fkRicetta = ?";
		
		List<IngredienteDTO> listaIngredienti = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, idRicetta);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				IngredienteDTO ingrediente = new IngredienteDTO();
				ingrediente.setNomeIngrediente(rs.getString("nomeIngrediente"));
				
				listaIngredienti.add(ingrediente);
			}
		}
		return listaIngredienti;
	}
}
