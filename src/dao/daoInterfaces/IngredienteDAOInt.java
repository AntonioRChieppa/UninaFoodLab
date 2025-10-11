package dao.daoInterfaces;

import dto.IngredienteDTO;
import java.sql.SQLException;
import java.util.List;

public interface IngredienteDAOInt {

	public void insertIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public void updateIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public void deleteIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public IngredienteDTO getIngredienteByName(String nomeIngrediente) throws SQLException;
	
	public IngredienteDTO getIngredienteById(int id) throws SQLException;
	
	public List<String> getAllIngredientiRicetta(int id) throws SQLException;
}
