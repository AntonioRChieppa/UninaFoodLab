package dao.daoInterfaces;

import java.sql.SQLException;
import java.util.List;
import dto.SessioneRicettaDTO;
import dto.StatisticheRicetteDTO;
import dto.RicettaDTO;

public interface SessioneRicettaDAOInt {
	
	public void insertNewAssociazione(SessioneRicettaDTO associazioneSessioneRicetta) throws SQLException;
	
	public void deleteAssociazioniByIdSessione(int idSessioneInPresenza) throws SQLException;
	
	public List<RicettaDTO> getAllRicetteByIdSessione(int idSessioneInPresenza) throws SQLException;
	
	public StatisticheRicetteDTO getStatisticheRicette(int idChef, int mese, int anno) throws SQLException;
	
	
}
