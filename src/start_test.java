import controller.Controller;
import dto.ChefDTO;
import exception.SQLOperationException;
import exception.NotFoundException;
import exception.InvalidCredentialsException;
import exception.AlreadyExistsException;
import java.util.*;

public class start_test {

	public static void main(String[] args) {
		Controller controller = new Controller();

        try {
        	controller.registrazioneChef("Mario", "De Maio", "mdm@email.com", "Password@123");
        	controller.loginChef("mdm@email.com", "Password@123");
        	System.out.println("Login effettuato con successo!");
        	ChefDTO mario = controller.visualizzaChef();
        	System.out.println("ID: "+mario.getId()+"Nome: "+mario.getNome()+" Cognome: "+mario.getCognome()+" Email: "+mario.getEmail());
        	controller.eliminaChef();
        	System.out.println("Chef eliminato con successo!\n");
        	System.out.println("--------------------------------------\n");
        	
        	controller.registrazioneChef("Antonio", "Chieppa", "antoniochieppa04@gmail.com", "Aurorina_2013");
        	controller.loginChef("antoniochieppa04@gmail.com", "Aurorina_2013");
        	controller.aggiornaChefPassword("Password@123");
        	System.out.println("\n------------- Visualizzazione tutti gli chef ----------------\n");
        	List<ChefDTO> list = controller.visualizzaTuttiChef();
        	for(ChefDTO c: list) {
        		System.out.println("ID: "+c.getId()+" Nome: "+c.getNome()+" Cognome: "+c.getCognome()+"Password: "+c.getPassword());
        	}
        	controller.eliminaChef();
        
        	
        }catch(AlreadyExistsException e) {
        	System.err.println("Errore: "+e.getMessage());
        }catch(InvalidCredentialsException e) {
    	System.err.println("Errore: "+e.getMessage());
    	}catch(NotFoundException e) {
    	System.err.println("Errore: "+e.getMessage());
        }catch (SQLOperationException e) {
            System.err.println("Errore di operazione: " + e.getMessage());
        }

	}

}
