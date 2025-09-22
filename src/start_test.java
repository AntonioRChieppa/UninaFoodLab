import controller.Controller;
import dto.ChefDTO;
import exception.ChefOperationException;
import exception.ChefNotFoundException;
import exception.InvalidCredentialsException;
import java.util.*;

public class start_test {

	public static void main(String[] args) {
		Controller controller = new Controller();

        try {
        	
        	controller.loginChef("mdm@email.com", "Password@123");
        	controller.eliminaChef();
        	
        	controller.loginChef("antoniochieppa04@gmail.com", "Aurorina_2013");
        	controller.eliminaChef();
        	System.out.println("\n------------- Visualizzazione tutti gli chef ----------------\n");
        	List<ChefDTO> list = controller.visualizzaTuttiChef();
        	for(ChefDTO c: list) {
        		System.out.println("ID: "+c.getId()+" Nome: "+c.getNome()+" Cognome: "+c.getCognome());
        	}
        	
        
        	
        } catch(InvalidCredentialsException e) {
        	System.err.println("Errore: "+e.getMessage());
        } catch(ChefNotFoundException e) {
        	System.err.println("Errore: "+e.getMessage());
        }catch (ChefOperationException e) {
            System.err.println("Errore di operazione: " + e.getMessage());
        }

	}

}
