package dao.daoInterfaces;

import dto.SessioneInPresenzaDTO;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;

public interface SessioneInPresenzaDAOInt {
	
	public void insertSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException;
	
	public void updateSessioneInPresenza(SessioneInPresenzaDTO upSessioneIp) throws SQLException;
	
	public List<SessioneInPresenzaDTO> getAllSessioniIP() throws SQLException;
	
	public SessioneInPresenzaDTO getSessioneIpByArgumentAndDate(String newArgomento, LocalDate newDataSessione) throws SQLException;
	
	public SessioneInPresenzaDTO getSessioneIpById(int idSessioneInPresenza) throws SQLException;
	
	public List<SessioneInPresenzaDTO> getSessioniIpByCorso(int idCorso) throws SQLException;
	
	public void deleteSessioneInPresenza(SessioneInPresenzaDTO sessioneIP) throws SQLException;

}
