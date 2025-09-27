package controller;

import dao.ChefDAO;
import dto.ChefDTO;
import dao.CorsoDAO;
import dto.CorsoDTO;

import java.time.LocalDate;
import session.SessionChef;

import exception.*;

import java.util.*;
import java.sql.SQLException;

public class Controller {

		private ChefDAO chefDAO;
		private CorsoDAO corsoDAO;
		
		public Controller() {
			this.chefDAO = new ChefDAO();
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
		
		//----------- METODI CLASSE CHEF ----------
		
		// METODO PER REGISTRARE UNO CHEF (insert)
		public void registrazioneChef(String newNome, String newCognome, String newEmail, String newPassword) throws ChefAlreadyExistsException, ChefOperationException {
			try {
				ChefDTO chefEsistente = chefDAO.getChefByEmail(newEmail);
				
				if(chefEsistente!=null) {
					throw new ChefAlreadyExistsException("Email già registrata! Effettua l'accesso!");
				}
				
				if(!checkPasswordRequirements(newPassword)) {
					throw new ChefOperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
				}
				
				ChefDTO chef = new ChefDTO();
				chef.setNome(newNome);
				chef.setCognome(newCognome);
	            chef.setEmail(newEmail);
	            chef.setPassword(newPassword);

	            chefDAO.insertChef(chef);
			}
			catch(SQLException ex) {
				throw new ChefOperationException("Errore in fase di registrazione");
			}
		}
		
		// METODO DI AUTENTICAZIONE CHEF (LOGIN)
		public ChefDTO loginChef(String newEmail, String newPassword) throws InvalidCredentialsException, ChefOperationException{
			try {
				ChefDTO chef = chefDAO.getChefByEmailAndPassword(newEmail, newPassword);
				
				if(chef==null) {
					throw new InvalidCredentialsException("Email o password non valide! Riprova o iscriviti!");
				}
				
				SessionChef.setChefId(chef.getId());
				return chef;
			}
			catch(SQLException ex) {
				ex.printStackTrace();
				throw new ChefOperationException("Errore durante il login");
			}
		}
		
		// METODO PER AGGIORNARE I DATI DI UNO CHEF
		public void aggiornaChef(String newNome, String newCognome, String newEmail) throws ChefNotFoundException, ChefOperationException{
			try {
				int id = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(id);
				
				if(chef==null) {
					throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + id);
				}
				
				ChefDTO updateChef = new ChefDTO();
				updateChef.setId(id);
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
				ex.printStackTrace();
				throw new ChefOperationException("Errore durante l'aggiornamento dello chef");
			}
			
		}
		
		// METODO PER AGGIORNARE PASSWORD CHEF
		public void aggiornaChefPassword(String newPassword) throws ChefNotFoundException, ChefOperationException{
			try {
				int id = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(id);
				
				if(chef==null) {
					throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + id);
				}
						
				if(newPassword!=null && newPassword.equals(chef.getPassword())==false) {
					if(!checkPasswordRequirements(newPassword)) {
						throw new ChefOperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
					}
					chef.setPassword(newPassword);
					chefDAO.updateChefPassword(chef);
				}
			}
			catch(SQLException ex) {
				throw new ChefOperationException("Errore nell'inserimento della nuova password!");
			}
				
		}
		
		// METODO PER VISUALIZZARE LO CHEF CORRENTE
		public ChefDTO visualizzaChef() throws ChefNotFoundException, ChefOperationException {
			try {
				int id = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(id);
		        
		        if (chef == null) {
		            throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + id);
		        }
		        
		        return chef;
		    } catch (SQLException e) {
		        throw new ChefOperationException("Errore durante il recupero dello chef");
		    }
		}
		
		// METODO PER VISUALIZZARE TUTTI GLI CHEF PRESENTI
		public List<ChefDTO> visualizzaTuttiChef() throws ChefOperationException {
		    try {
		        return chefDAO.getAllChefs();
		    } catch (SQLException e) {
		        throw new ChefOperationException("Errore durante il recupero di tutti gli chef");
		    }
		}
		
		// METODO PER RICERCARE LO/GLI CHEF PER NOME E COGNOME
		public List<ChefDTO> cercaChefPerNome(String newNome, String newCognome) throws ChefOperationException, ChefNotFoundException{
			try {
				List<ChefDTO> listaChefStessoNome = chefDAO.getChefByNameAndSurname(newNome, newCognome);
								
				if(listaChefStessoNome == null) {
					throw new ChefNotFoundException("Impossibile trovare chef con nome: "+newNome+" e cognome: "+newCognome);
				}
								
				return listaChefStessoNome;
			}
			catch(SQLException e) {
				throw new ChefOperationException("Errore durante la visualizzazione dello chef");
			}
		}

		// METODO PER ELIMINARE LO CHEF CORRENTE
		public void eliminaChef() throws ChefNotFoundException, ChefOperationException {
		    try {
		    	int id = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(id);
		        
		        if (chef == null) {
		            throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + id);
		        }
		        
		        chefDAO.deleteChef(chef);
		        logoutChef();
		    } catch (SQLException e) {
		        throw new ChefOperationException("Errore durante l'eliminazione dello chef");
		    }
		}		
		
		// METODO DI LOGOUT DELLO CHEF CORRENTE
		public void logoutChef() {
			SessionChef.setChefId(0);
			System.out.println("Logout effettuato con successo!");
		}

		//---------- FINE METODI CHEF ----------
		
		//-------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI CORSO ----------
		
		// METODO PER INSERIRE UN NUOVO CORSO
		public void inserimentoCorso(String newNomeCorso, String newArgomento, LocalDate newDataInizio, LocalDate newDataFine, int newAnno, String newFrequenzaSessioni, int newFkChef) throws CorsoOperationException, CorsoAlreadyExistsException, InvalidDateRangeException {
			try {
				CorsoDTO corsoEsistente = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corsoEsistente!=null) {
					throw new CorsoAlreadyExistsException("Corso già registrato!");
				}
				
				if (newDataInizio.isAfter(newDataFine)) {
				    throw new InvalidDateRangeException("La data di inizio non può essere successiva alla data di fine!");
				}
				
				CorsoDTO corso = new CorsoDTO();
				corso.setNomeCorso(newNomeCorso);
				corso.setArgomento(newArgomento);
				corso.setDataInizio(newDataInizio);
				corso.setDataFine(newDataFine);
				corso.setAnno(newAnno);
				corso.setFrequenzaSessioni(newFrequenzaSessioni);
				int id = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(id);
		        corso.setChefCorso(chef);

		        chef.addCorso(corso); // aggiunta del corso alla lista dei torni tenuti da chef
	            corsoDAO.insertCorso(corso);
			}
			catch(SQLException ex) {
				throw new CorsoOperationException("Errore in fase di inserimento del nuovo corso!");
			}
		}

		
		/*// METODO PER AGGIORNARE I DATI RELATIVI AD UN CORSO
		public void aggiornaCorso(String newNomeCorso, String newArgomento, LocalDate newDataInizio, LocalDate newDataFine, int newAnno, String newFrequenzaSessioni) {
			try {
			}
		} */
}
