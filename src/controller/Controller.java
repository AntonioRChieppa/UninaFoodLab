package controller;

import dao.ChefDAO;
import dto.ChefDTO;
import dao.CorsoDAO;
import dto.CorsoDTO;
import dto.RicettaDTO;
import dao.RicettaDAO;

import java.time.LocalDate;
import session.SessionChef;

import exception.*;

import java.util.*;
import java.sql.SQLException;

public class Controller {

		private ChefDAO chefDAO;
		private CorsoDAO corsoDAO;
		private RicettaDAO ricettaDAO;
		
		public Controller() {
			this.chefDAO = new ChefDAO();
			this.corsoDAO = new CorsoDAO();
			this.ricettaDAO = new RicettaDAO();
		}
		
		//METODO PER NORMALIZZARE UNA STRINGA
		public static String normalizzaNomeInserito(String nome) {
			if(nome == null) {
				return null;
			}
			return nome.trim().toLowerCase().replaceAll("\\s", "");
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
				throw new ChefOperationException("Errore durante il login");
			}
		}
		
		// METODO PER AGGIORNARE I DATI DI UNO CHEF
		public void aggiornaChef(String newNome, String newCognome, String newEmail) throws ChefNotFoundException, ChefOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
				
				if(chef==null) {
					throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
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
				throw new ChefOperationException("Errore durante l'aggiornamento dello chef");
			}
			
		}
		
		// METODO PER AGGIORNARE PASSWORD CHEF
		public void aggiornaChefPassword(String newPassword) throws ChefNotFoundException, ChefOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
				
				if(chef==null) {
					throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
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
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        
		        if (chef == null) {
		            throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
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
		    	int idChefLoggato = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        
		        if (chef == null) {
		            throw new ChefNotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
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
				int idChefLoggato = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        corso.setChefCorso(chef);

		        
	            corsoDAO.insertCorso(corso);
			}
			catch(SQLException ex) {
				throw new CorsoOperationException("Errore in fase di inserimento del nuovo corso!");
			}
		}

		// METODO PER AGGIORNARE I DATI RELATIVI AD UN CORSO
		public void aggiornaCorso(String newNomeCorso, String newArgomento, LocalDate newDataInizio, LocalDate newDataFine, int newAnno, String newFrequenzaSessioni) throws UnauthorizedOperationException, CorsoNotFoundException, InvalidDateRangeException, CorsoOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corso==null) {
					throw new CorsoNotFoundException("Impossibile trovare il corso: "+newNomeCorso);
				}
				
				if (newDataInizio.isAfter(newDataFine)) {
				    throw new InvalidDateRangeException("La data di inizio non può essere successiva alla data di fine!");
				}
				
				// RECUPERO DELLO CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				
				if(idChefLoggato==corso.getChefCorso().getId()) { // VERIFICA SE GLI ID CORRISPONDONO
					CorsoDTO updateCorso = new CorsoDTO();
					updateCorso.setId(corso.getId());
					updateCorso.setNomeCorso(newNomeCorso);
					updateCorso.setArgomento(newArgomento);
					updateCorso.setDataInizio(newDataInizio);
					updateCorso.setDataFine(newDataFine);
					updateCorso.setAnno(newAnno);
					updateCorso.setFrequenzaSessioni(newFrequenzaSessioni);
					
					// VERIFICA DEL VALORE IDENTICO AL PRECEDENTE -> LO IMPOSTA A NULL (COALESCE)
					if(updateCorso.getNomeCorso()!=null && updateCorso.getNomeCorso().equals(corso.getNomeCorso())) {
						updateCorso.setNomeCorso(null);
					}
					
					if(updateCorso.getArgomento()!=null && updateCorso.getArgomento().equals(corso.getArgomento())) {
						updateCorso.setArgomento(null);
					}
					
					if(updateCorso.getDataInizio()!=null && updateCorso.getDataInizio().equals(corso.getDataInizio())) {
						updateCorso.setDataInizio(null);
					}
					
					if(updateCorso.getDataFine()!=null && updateCorso.getDataFine().equals(corso.getDataFine())) {
						updateCorso.setDataFine(null);
					}
					
					if(updateCorso.getAnno()!=null && updateCorso.getAnno().equals(corso.getAnno())) {
						updateCorso.setAnno(null);
					}
					
					if(updateCorso.getFrequenzaSessioni()!=null && updateCorso.getFrequenzaSessioni().equals(updateCorso.getFrequenzaSessioni())) {
						updateCorso.setFrequenzaSessioni(null);
					}
					
					corsoDAO.updateCorso(updateCorso);
				}
				else {
					throw new UnauthorizedOperationException("Impossibile modificare corsi degli altri chef!");
				}
				
			}
			catch(SQLException ex) {
				throw new CorsoOperationException("Errore durante l'aggiornamento dello chef");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI
		public List<CorsoDTO> visualizzaTuttiCorsi() throws CorsoOperationException{
			try {
				return corsoDAO.getAllCorsi();
			} catch (SQLException e) {
		        throw new CorsoOperationException("Errore durante il recupero di tutti i corsi");
		    }
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI TENUTI DA UNO CHEF
		public List<CorsoDTO> visualizzaCorsiPerChef() throws CorsoNotFoundException, CorsoOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chefCorso = chefDAO.getChefById(idChefLoggato);
				
				List<CorsoDTO> elencoCorsiChef = corsoDAO.getCorsoByChefId(idChefLoggato);
				
				// VERIFICA SE SONO PRESENTI DEI CORSI
				if(elencoCorsiChef==null) {
					throw new CorsoNotFoundException("Impossibile trovare i corsi dello chef "+chefCorso.getNome()+" "+chefCorso.getCognome());
				}
				else {
					return elencoCorsiChef;
				}
				
			}catch(SQLException ex) {
				throw new CorsoOperationException("Errore durante il recupero dei corsi");
			}
		}
		
		// METODO PER CERCARE UN CORSO DAL NOME
		public CorsoDTO cercaCorsoPerNome(String newNomeCorso) throws CorsoNotFoundException, CorsoOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corso==null) {
					throw new CorsoNotFoundException("Il corso "+newNomeCorso+" non è presente. Registralo!");
				}
				
				return corso;
			}catch(SQLException ex) {
				throw new CorsoOperationException("Errore durante la ricerca del corso");
			}
		}
		
		// METODO PER ELIMINARE UN CORSO
		public void eliminaCorso(int idCorso) throws CorsoNotFoundException, UnauthorizedOperationException, CorsoOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoById(idCorso);
				
				if(corso==null) {
					throw new CorsoNotFoundException("Impossibile trovare il corso!");
				}
				
				int idChefLoggato = SessionChef.getChefId();
				if(idChefLoggato == corso.getChefCorso().getId()) {
					corsoDAO.deleteCorso(corso);
				}
				else {
					throw new UnauthorizedOperationException("Impossibile eliminare i corsi degli altri chef!");
				}
			}catch(SQLException ex) {
				throw new CorsoOperationException("Impossibile eliminare il corso selezionato!");
			}
		}
		
		//---------- FINE METODI CORSO ----------
		
		//-------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI SESSIONE ----------
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//-----------FINE METODI SESSIONE
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		
		//------------INIZIO METODI RICETTA ----------
		
		
		public void inserisciRicetta(String newNomeRicetta,int newTempoPreparazione, int newPorzioni,String newDifficolta)throws RicettaAlreadyExistsException, RicettaOperationException {
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeRicetta);  
				RicettaDTO ricettaEsistente = ricettaDAO.getRicettaByName(nomeNormalizzato);
				
				if(ricettaEsistente !=null) {
					throw new RicettaAlreadyExistsException("Ricetta già registrata! Aggiungere una nuova ricetta");
				}
				
				RicettaDTO ricetta = new RicettaDTO();
				ricetta.setNomeRicetta (newNomeRicetta);
				ricetta.setTempoPreparazione (newTempoPreparazione);
				ricetta.setPorzioni (newPorzioni);
				ricetta.setDifficolta (newDifficolta);
				ricettaDAO.insertRicetta(ricetta);
			}
			catch(SQLException ex){
				throw new RicettaOperationException ("Errore durante l'inserimento dei dati");
			}
		}

		public void aggiornaRicetta(String newNomeRicetta, int newTempoPreparazione, int newPorzioni, String newDifficolta )throws RicettaNotFoundException, RicettaOperationException {
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeRicetta);
				RicettaDTO ricettaCercata = ricettaDAO.getRicettaByName(nomeNormalizzato);
				
				if(ricettaCercata==null) {
					throw new RicettaNotFoundException("Ricetta cercata non trovata");
				}
				
				RicettaDTO updateRicetta = new RicettaDTO();
				updateRicetta.setNomeRicetta(newNomeRicetta);
				updateRicetta.setTempoPreparazione(newTempoPreparazione);
				updateRicetta.setPorzioni(newPorzioni);
				updateRicetta.setDifficolta(newDifficolta);
				
				ricettaDAO.updateRicetta(updateRicetta);
			}
			catch(SQLException ex) {
				throw new RicettaOperationException ("Errore nell'inserimento dei dati");
			}
		}
		//------------FINE METODI RICETTA-------------
}		
