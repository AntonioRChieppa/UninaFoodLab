package dao.daoInterfaces;

import java.sql.SQLException;
import java.util.List;

import dto.IngredienteDTO;

public interface ComposizioneDAOInt {
	
	public List<IngredienteDTO> getAllIngredientiRicetta(int id) throws SQLException;
	
}
