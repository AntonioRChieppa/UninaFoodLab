package dao.daoInterfaces;

import dto.RicettaDTO;
import java.sql.SQLException;
import java.util.List;

public interface RicettaDAOInt {
	
	public void insertRicetta(RicettaDTO ricetta) throws SQLException;
	
	public void updateRicetta(RicettaDTO ricetta) throws SQLException;
	
	public void deleteRicetta(RicettaDTO ricetta) throws SQLException;
	
	public RicettaDTO getRicettaByName(String nomeRicetta) throws SQLException;
	
	public List<RicettaDTO> getAllRicetteByIdChef(int idChef) throws SQLException;
	
	public List<RicettaDTO> getAllRecipes() throws SQLException;
	
	public RicettaDTO getRicettaById(int id) throws SQLException;
	
}
