package dao.daoInterfaces;

import dto.IngredienteDTO;
import java.sql.SQLException;

public interface IngredienteDAOInt {

	public void insertIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public void updateIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public void deleteIngrediente(IngredienteDTO ingrediente) throws SQLException;
	
	public IngredienteDTO getIngredienteByName(String nomeIngrediente) throws SQLException;
	
	public IngredienteDTO getIngredienteById(int id) throws SQLException;
	
}
