package controller;

import dto.ChefDTO;
import dao.ChefDAO;

import java.time.*;
import java.sql.SQLException;
import java.util.*;
import session.SessionChef;
import exception.*;


public class ChefController {

	private ChefDAO chefDAO;
	
	public ChefController(){
		this.chefDAO = new ChefDAO();
	}
	
	// ---------- METODI AUSILIARI ------------
	
	// METODO DI VERIFICA FORMATO EMAIL
	public boolean isValidEmail(String email) {
	    if (email == null) return false;
	    // Regex: at least one character before and after @, ends with a dot and 2-6 letters
	    return email.matches("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,6}$");
	}
	
	// METODO DI VERIFICA INSERIMENTO NOME E COGNOME
	public boolean isValidNameOrSurname(String input) {
	    if (input == null || input.isEmpty()) return false;
	    return input.matches("^[a-zA-Z]+$");
	}
	
	// METODO PER CONTROLLARE I REQUISITI DI SICUREZZA DI UNA PASSWORD
	public boolean checkPasswordRequirements(String password) {
		if (password == null || password.length() <= 6) {
			return false; // lunghezza minima > 6
		}

		boolean hasUppercase = false;
		boolean hasSpecialChar = false;

		// Caratteri speciali consentiti
		String specialChars = "!@#$%^&*_";

		int i = 0;
		while (i < password.length() && !(hasUppercase && hasSpecialChar)) {
			char c = password.charAt(i); // prendo il carattere alla posizione i

			if (Character.isUpperCase(c)) {
				hasUppercase = true;
			}
			if (specialChars.indexOf(c) >= 0) {
				hasSpecialChar = true;
			}

			i++; 
		}

		return hasUppercase && hasSpecialChar;
	}
	
	// ------------------------------------------------------------
			
	// METODO PER REGISTRARE UNO CHEF (insert)
	public void registrazioneChef(String newNome, String newCognome, String newEmail, String newPassword) throws AlreadyExistsException, OperationException {
		try {
			ChefDTO chefEsistente = chefDAO.getChefByEmail(newEmail);
									
			if(chefEsistente!=null) {
				throw new AlreadyExistsException("Chef già registrato! Effettua l'accesso!");
			}
			
			if(!isValidNameOrSurname(newNome) || !isValidNameOrSurname(newCognome)) {
				throw new OperationException("Nome o cognome inseriti non validi! Riprovare");
			}
				
			if(!isValidEmail(newEmail)) {
				throw new OperationException("Mail non valida! Riprovare!");
			}
									
			if(!checkPasswordRequirements(newPassword)) {
				throw new OperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
			}
									
			ChefDTO chef = new ChefDTO();
			chef.setNome(newNome);
			chef.setCognome(newCognome);
		    chef.setEmail(newEmail);
		    chef.setPassword(newPassword);

			chefDAO.insertChef(chef);
		} 
		catch(SQLException ex) {
			throw new OperationException("Errore in fase di registrazione");
		}
	}
					
	// METODO DI AUTENTICAZIONE CHEF (LOGIN)
	public ChefDTO loginChef(String newEmail, String newPassword) throws InvalidCredentialsException, OperationException{
		try {
			ChefDTO chef = chefDAO.getChefByEmailAndPassword(newEmail, newPassword);
									
			if(chef==null || !isValidEmail(newEmail)) {
				throw new InvalidCredentialsException("Email o password non valide! Riprova o iscriviti!");
			}
									
			SessionChef.setChefId(chef.getId());
			return chef;
		}
		catch(SQLException ex) {
			throw new OperationException("Errore durante il login");
		}
	}
			
	// METODO PER AGGIORNARE I DATI DI UNO CHEF
	public void aggiornaChef(String newNome, String newCognome, String newEmail) throws NotFoundException, OperationException{
		try {
			int idChefLoggato = SessionChef.getChefId();
			ChefDTO chef = chefDAO.getChefById(idChefLoggato);
					
			if(chef==null) {
				throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
			}
			
			if(!isValidEmail(newEmail)) {
				throw new OperationException("Nuova email inserita non valida! Riprovare!");
			}
					
			ChefDTO updateChef = new ChefDTO();
			updateChef.setId(idChefLoggato);
			updateChef.setNome(newNome);
			updateChef.setCognome(newCognome);
			updateChef.setEmail(newEmail);
					
			// SE IL NUOVO NOME è NON NULL E UGUALE A QUELLO GIà PRESENTE, NON AGGIORNIAMO
			if(updateChef.getNome()!=null && updateChef.getNome().equals(chef.getNome())) {
				updateChef.setNome(null);
			}		
			if (updateChef.getCognome() != null && updateChef.getCognome().equals(chef.getCognome())) {
				updateChef.setCognome(null);
			}
			if (updateChef.getEmail() != null && updateChef.getEmail().equals(chef.getEmail())) {
			    updateChef.setEmail(null);
			}
					
			chefDAO.updateChef(updateChef);
		}
		catch(SQLException ex) {
			throw new OperationException("Errore durante l'aggiornamento dello chef");
		}
				
	}
			
	// METODO PER AGGIORNARE PASSWORD CHEF
	public void aggiornaChefPassword(String newPassword) throws NotFoundException, OperationException{
		try {
			int idChefLoggato = SessionChef.getChefId();
			ChefDTO chef = chefDAO.getChefById(idChefLoggato);
			
			if(chef==null) {
				throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
			}
							
			if(newPassword!=null && newPassword.equals(chef.getPassword())==false) {
				if(!checkPasswordRequirements(newPassword)) {
					throw new OperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
				}
				chef.setPassword(newPassword);
				chefDAO.updateChefPassword(chef);
			}
		}
		catch(SQLException ex) {
			throw new OperationException("Errore nell'inserimento della nuova password!");
		}	
	}
			
	// METODO PER VISUALIZZARE LO CHEF CORRENTE
	public ChefDTO visualizzaChef() throws NotFoundException, OperationException {
		try {
			int idChefLoggato = SessionChef.getChefId();
			ChefDTO chef = chefDAO.getChefById(idChefLoggato);
			        
			 if (chef == null) {
			    throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
			 }
			        
			 return chef;
		} 
		catch (SQLException e) {
			throw new OperationException("Errore durante il recupero dello chef");
		}
	}
			
	// METODO PER VISUALIZZARE TUTTI GLI CHEF PRESENTI
	public List<ChefDTO> visualizzaTuttiChef() throws OperationException {
	    try {
	        return chefDAO.getAllChefs();
	    } 
	    catch (SQLException e) {
			throw new OperationException("Errore durante il recupero di tutti gli chef");
	    }
	}
			
	// METODO PER RICERCARE LO/GLI CHEF PER NOME E COGNOME
	public List<ChefDTO> cercaChefPerNome(String newNome, String newCognome) throws OperationException, NotFoundException{
		try {
			List<ChefDTO> listaChefStessoNome = chefDAO.getChefByNameAndSurname(newNome, newCognome);
									
			if(listaChefStessoNome == null) {
				throw new NotFoundException("Impossibile trovare chef con nome: "+newNome+" e cognome: "+newCognome);
			}
									
			return listaChefStessoNome;
		}
		catch(SQLException e) {
			throw new OperationException("Errore durante la visualizzazione dello chef");
		}
	}

	// METODO PER ELIMINARE LO CHEF CORRENTE
	public void eliminaChef() throws NotFoundException, OperationException {
	    try {
		   	int idChefLoggato = SessionChef.getChefId();
	        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
			        
	        if (chef == null) {
		       throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
	        }
			        
		   chefDAO.deleteChef(chef);
		   logoutChef();
		} 
	    catch (SQLException e) {
			throw new OperationException("Errore durante l'eliminazione dello chef");
	    }
	}		
			
	// METODO DI LOGOUT DELLO CHEF CORRENTE
	public void logoutChef() {
		SessionChef.setChefId(0);
		System.out.println("Logout effettuato con successo!");
	}
}
