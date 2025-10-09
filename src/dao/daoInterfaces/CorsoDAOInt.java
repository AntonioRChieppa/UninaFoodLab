package dao.daoInterfaces;

import dto.CorsoDTO;
import java.sql.SQLException;
import java.util.List;

public interface CorsoDAOInt {

	public void insertCorso(CorsoDTO newCorso) throws SQLException;
	
	public void updateCorso(CorsoDTO newCorso) throws SQLException;
	
	public CorsoDTO getCorsoById(int idCorso) throws SQLException;
	
	public CorsoDTO getCorsoByName(String nomeCorso) throws SQLException;
	
	public List<CorsoDTO> getCorsoByChefId(int idChef) throws SQLException;
	
	public List<CorsoDTO> getAllCorsi() throws SQLException;
	
	// public List<CorsoDTO> getCorsiByCategory() throws SQLException;
	
	public void deleteCorso(CorsoDTO corso) throws SQLException;
}
