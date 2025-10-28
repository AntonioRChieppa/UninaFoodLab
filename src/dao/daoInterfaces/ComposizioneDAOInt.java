package dao.daoInterfaces;

import dto.ComposizioneDTO;
import java.sql.SQLException;
import java.util.List;

import dto.IngredienteDTO;

public interface ComposizioneDAOInt {

	public void insertComposizione(ComposizioneDTO composizione) throws SQLException;
	
	public List<IngredienteDTO> getAllIngredientiRicetta(int id) throws SQLException;
	
}
