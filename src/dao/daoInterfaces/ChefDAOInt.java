package dao.daoInterfaces;

import dto.ChefDTO;
import java.sql.SQLException;
import java.util.List;

public interface ChefDAOInt {

		public void insertChef(ChefDTO chef) throws SQLException;
		
		public void updateChef(ChefDTO chef) throws SQLException;
		
		public void updateChefPassword(ChefDTO chef) throws SQLException;
		
		public void deleteChef(ChefDTO chef) throws SQLException;
		
		public ChefDTO getChefById(int id) throws SQLException;
		
		public ChefDTO getChefByEmailAndPassword(String email, String password) throws SQLException;
		
		public ChefDTO getChefByEmail(String email) throws SQLException;
		
		public List<ChefDTO> getAllChefs() throws SQLException;
		
		public List<ChefDTO> getChefByNameAndSurname(String nome, String cognome) throws SQLException;
}
