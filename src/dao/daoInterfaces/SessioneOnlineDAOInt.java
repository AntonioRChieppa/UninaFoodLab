package dao.daoInterfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import dto.SessioneOnlineDTO;
import java.util.List;

public interface SessioneOnlineDAOInt {
	
	public void insertSessioneOnline(SessioneOnlineDTO sessioneOn) throws SQLException;
	
	public void updateSessioneOnline(SessioneOnlineDTO upSessioneOn) throws SQLException;
	
	public SessioneOnlineDTO getSessioneOnByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException;
	
	public SessioneOnlineDTO getSessioneOnById(int idSessioneOnline) throws SQLException;
	
	public List<SessioneOnlineDTO> getSessioniOnByCorso(int idCorso) throws SQLException;
	
	public List<SessioneOnlineDTO> getSessioniOnByChefId(int idChef) throws SQLException;
	
	public void deleteSessioneOnline(SessioneOnlineDTO sessioneOn) throws SQLException;
	
	public int countSessioniOnlineByChefInMese(int idChef, int mese, int anno) throws SQLException;
}
