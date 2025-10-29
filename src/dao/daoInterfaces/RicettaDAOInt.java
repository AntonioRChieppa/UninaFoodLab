package dao.daoInterfaces;

import dto.RicettaDTO;
import java.sql.SQLException;
import java.util.List;

public interface RicettaDAOInt {
	
	public List<RicettaDTO> getAllRicetteByIdChef(int idChef) throws SQLException;
	
	public List<RicettaDTO> getAllRecipes() throws SQLException;
	
	public RicettaDTO getRicettaById(int id) throws SQLException;
	
}
