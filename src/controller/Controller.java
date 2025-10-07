package controller;

import dao.*;
import dto.*;

import java.time.*;
import session.SessionChef;

import exception.*;

import java.util.*;
import java.sql.SQLException;

public class Controller {

		private ChefDAO chefDAO;
		private CorsoDAO corsoDAO;
		private RicettaDAO ricettaDAO;
		private SessioneInPresenzaDAO sessioneIpDAO;
		private SessioneOnlineDAO sessioneOnDAO;
		private IngredienteDAO ingredienteDAO;
		
		public Controller() {
			this.chefDAO = new ChefDAO();
			this.corsoDAO = new CorsoDAO();
			this.ricettaDAO = new RicettaDAO();
			this.sessioneIpDAO = new SessioneInPresenzaDAO();
			this.sessioneOnDAO = new SessioneOnlineDAO();
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
		public void registrazioneChef(String newNome, String newCognome, String newEmail, String newPassword) throws AlreadyExistsException, SQLOperationException {
			try {
				ChefDTO chefEsistente = chefDAO.getChefByEmail(newEmail);
								
				if(chefEsistente!=null) {
					throw new AlreadyExistsException("Email già registrata! Effettua l'accesso!");
				}
								
				if(!checkPasswordRequirements(newPassword)) {
					throw new SQLOperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
				}
								
				ChefDTO chef = new ChefDTO();
				chef.setNome(newNome);
				chef.setCognome(newCognome);
			    chef.setEmail(newEmail);
			    chef.setPassword(newPassword);

			    chefDAO.insertChef(chef);
			} 
			catch(SQLException ex) {
				throw new SQLOperationException("Errore in fase di registrazione");
			}
		}
				
		// METODO DI AUTENTICAZIONE CHEF (LOGIN)
		public ChefDTO loginChef(String newEmail, String newPassword) throws InvalidCredentialsException, SQLOperationException{
			try {
				ChefDTO chef = chefDAO.getChefByEmailAndPassword(newEmail, newPassword);
								
				if(chef==null) {
					throw new InvalidCredentialsException("Email o password non valide! Riprova o iscriviti!");
				}
								
				SessionChef.setChefId(chef.getId());
				return chef;
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante il login");
			}
		}
		
		// METODO PER AGGIORNARE I DATI DI UNO CHEF
		public void aggiornaChef(String newNome, String newCognome, String newEmail) throws NotFoundException, SQLOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
				
				if(chef==null) {
					throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
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
				throw new SQLOperationException("Errore durante l'aggiornamento dello chef");
			}
			
		}
		
		// METODO PER AGGIORNARE PASSWORD CHEF
		public void aggiornaChefPassword(String newPassword) throws NotFoundException, SQLOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
				
				if(chef==null) {
					throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
				}
						
				if(newPassword!=null && newPassword.equals(chef.getPassword())==false) {
					if(!checkPasswordRequirements(newPassword)) {
						throw new SQLOperationException("La password deve contenere almeno un carattere speciale, una lettera maiuscola e lunghezza > 6");
					}
					chef.setPassword(newPassword);
					chefDAO.updateChefPassword(chef);
				}
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore nell'inserimento della nuova password!");
			}	
		}
		
		// METODO PER VISUALIZZARE LO CHEF CORRENTE
		public ChefDTO visualizzaChef() throws NotFoundException, SQLOperationException {
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        
		        if (chef == null) {
		            throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
		        }
		        
		        return chef;
		    } catch (SQLException e) {
		        throw new SQLOperationException("Errore durante il recupero dello chef");
		    }
		}
		
		// METODO PER VISUALIZZARE TUTTI GLI CHEF PRESENTI
		public List<ChefDTO> visualizzaTuttiChef() throws SQLOperationException {
		    try {
		        return chefDAO.getAllChefs();
		    } catch (SQLException e) {
		        throw new SQLOperationException("Errore durante il recupero di tutti gli chef");
		    }
		}
		
		// METODO PER RICERCARE LO/GLI CHEF PER NOME E COGNOME
		public List<ChefDTO> cercaChefPerNome(String newNome, String newCognome) throws SQLOperationException, NotFoundException{
			try {
				List<ChefDTO> listaChefStessoNome = chefDAO.getChefByNameAndSurname(newNome, newCognome);
								
				if(listaChefStessoNome == null) {
					throw new NotFoundException("Impossibile trovare chef con nome: "+newNome+" e cognome: "+newCognome);
				}
								
				return listaChefStessoNome;
			}
			catch(SQLException e) {
				throw new SQLOperationException("Errore durante la visualizzazione dello chef");
			}
		}

		// METODO PER ELIMINARE LO CHEF CORRENTE
		public void eliminaChef() throws NotFoundException, SQLOperationException {
		    try {
		    	int idChefLoggato = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        
		        if (chef == null) {
		            throw new NotFoundException("Impossibile trovare lo chef con id: " + idChefLoggato);
		        }
		        
		        chefDAO.deleteChef(chef);
		        logoutChef();
		    } catch (SQLException e) {
		        throw new SQLOperationException("Errore durante l'eliminazione dello chef");
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
		public void inserimentoCorso(String newNomeCorso, String newCategoria, LocalDate newDataInizio, Integer newNumeroSessioni, String newFrequenzaSessioni, int newFkChef) throws SQLOperationException, AlreadyExistsException {
			try {
				CorsoDTO corsoEsistente = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corsoEsistente!=null) {
					throw new AlreadyExistsException("Corso già registrato!");
				}
				
				CorsoDTO corso = new CorsoDTO();
				corso.setNomeCorso(newNomeCorso);
				corso.setCategoria(newCategoria);
				corso.setDataInizio(newDataInizio);
				corso.setNumeroSessioni(newNumeroSessioni);
				corso.setFrequenzaSessioni(newFrequenzaSessioni);
				int idChefLoggato = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        corso.setChefCorso(chef);

		        
	            corsoDAO.insertCorso(corso);
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore in fase di inserimento del nuovo corso!");
			}
		}

		// METODO PER AGGIORNARE I DATI RELATIVI AD UN CORSO
		public void aggiornaCorso(String newNomeCorso, String newCategoria, LocalDate newDataInizio, Integer newNumeroSessioni, String newFrequenzaSessioni) throws UnauthorizedOperationException, NotFoundException, SQLOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corso==null) {
					throw new NotFoundException("Impossibile trovare il corso: "+newNomeCorso);
				}
				
				// RECUPERO DELLO CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				
				if(idChefLoggato==corso.getChefCorso().getId()) { // VERIFICA SE GLI ID CORRISPONDONO
					CorsoDTO updateCorso = new CorsoDTO();
					updateCorso.setId(corso.getId());
					updateCorso.setNomeCorso(newNomeCorso);
					updateCorso.setCategoria(newCategoria);
					updateCorso.setDataInizio(newDataInizio);
					updateCorso.setNumeroSessioni(newNumeroSessioni);
					updateCorso.setFrequenzaSessioni(newFrequenzaSessioni);
					
					// VERIFICA DEL VALORE IDENTICO AL PRECEDENTE -> LO IMPOSTA A NULL (COALESCE)
					if(updateCorso.getNomeCorso()!=null && updateCorso.getNomeCorso().equals(corso.getNomeCorso())) {
						updateCorso.setNomeCorso(null);
					}
					
					if(updateCorso.getCategoria()!=null && updateCorso.getCategoria().equals(corso.getCategoria())) {
						updateCorso.setCategoria(null);
					}
					
					if(updateCorso.getDataInizio()!=null && updateCorso.getDataInizio().equals(corso.getDataInizio())) {
						updateCorso.setDataInizio(null);
					}
					
					if(updateCorso.getNumeroSessioni()!=null && updateCorso.getNumeroSessioni().equals(corso.getNumeroSessioni())) {
						updateCorso.setNumeroSessioni(null);
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
				throw new SQLOperationException("Errore durante l'aggiornamento dello chef");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI
		public List<CorsoDTO> visualizzaTuttiCorsi() throws SQLOperationException{
			try {
				return corsoDAO.getAllCorsi();
			} catch (SQLException e) {
		        throw new SQLOperationException("Errore durante il recupero di tutti i corsi");
		    }
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI TENUTI DA UNO CHEF
		public List<CorsoDTO> visualizzaCorsiPerChef() throws NotFoundException, SQLOperationException{
			try {
				int idChefLoggato = SessionChef.getChefId();
				ChefDTO chefCorso = chefDAO.getChefById(idChefLoggato);
				
				List<CorsoDTO> elencoCorsiChef = corsoDAO.getCorsoByChefId(idChefLoggato);
				
				// VERIFICA SE SONO PRESENTI DEI CORSI
				if(elencoCorsiChef==null) {
					throw new NotFoundException("Impossibile trovare i corsi dello chef "+chefCorso.getNome()+" "+chefCorso.getCognome());
				}
				else {
					return elencoCorsiChef;
				}
				
			}catch(SQLException ex) {
				throw new SQLOperationException("Errore durante il recupero dei corsi");
			}
		}
		
		// METODO PER CERCARE UN CORSO DAL NOME
		public CorsoDTO cercaCorsoPerNome(String newNomeCorso) throws NotFoundException, SQLOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corso==null) {
					throw new NotFoundException("Il corso "+newNomeCorso+" non è presente. Registralo!");
				}
				
				return corso;
			}catch(SQLException ex) {
				throw new SQLOperationException("Errore durante la ricerca del corso");
			}
		}
		
		// METODO PER ELIMINARE UN CORSO
		public void eliminaCorso(int idCorso) throws NotFoundException, UnauthorizedOperationException, SQLOperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoById(idCorso);
				
				if(corso==null) {
					throw new NotFoundException("Impossibile trovare il corso!");
				}
				
				int idChefLoggato = SessionChef.getChefId();
				if(idChefLoggato == corso.getChefCorso().getId()) {
					corsoDAO.deleteCorso(corso);
				}
				else {
					throw new UnauthorizedOperationException("Impossibile eliminare i corsi degli altri chef!");
				}
			}catch(SQLException ex) {
				throw new SQLOperationException("Impossibile eliminare il corso selezionato!");
			}
		}
		
		//---------- FINE METODI CORSO ----------
		
		//-------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI SESSIONE IN PRESENZA ----------
		
		// METODO PER INSERIRE UNA NUOVA SESSIONE IN PRESENZA
		public void inserimentoSessioneIP(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newSede, String newEdificio, String newAula) throws SQLOperationException, AlreadyExistsException {
			try {
				SessioneInPresenzaDTO sessioneIpEsistente = sessioneIpDAO.getSessioneIpByArgumentAndDate(newArgomento, newDataSessione);
				
				if(sessioneIpEsistente!=null) {
					throw new AlreadyExistsException("Sessione in presenza già inserita!");
				}
				
				SessioneInPresenzaDTO sessioneIp = new SessioneInPresenzaDTO();
				sessioneIp.setArgomento(newArgomento);
				sessioneIp.setOraInizio(newOraInizio);
				sessioneIp.setDataSessione(newDataSessione);
				CorsoDTO corsoSessione = corsoDAO.getCorsoById(newFkCorso);
				sessioneIp.setCorsoSessione(corsoSessione);
				sessioneIp.setSede(newSede);
				sessioneIp.setEdificio(newEdificio);
				sessioneIp.setAula(newAula);
				
				sessioneIpDAO.insertSessioneInPresenza(sessioneIp);
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore in fase di inserimento della sessione!");
			}
		}
		
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE IN PRESENZA
		public void aggiornaSessioneInPresenza(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newSede, String newEdificio, String newAula) throws UnauthorizedOperationException, NotFoundException, SQLOperationException{
			try {
				SessioneInPresenzaDTO sessioneIp = sessioneIpDAO.getSessioneIpByArgumentAndDate(newArgomento, newDataSessione);
				
				if(sessioneIp == null) {
					throw new NotFoundException("Impossibile trovare la sessione cercata!");
				}
				
				//RECUPERO ID CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				
				if(idChefLoggato==sessioneIp.getCorsoSessione().getChefCorso().getId()) {
					SessioneInPresenzaDTO updateSessioneIp = new SessioneInPresenzaDTO();
					updateSessioneIp.setIdSessione(sessioneIp.getIdSessione());
					updateSessioneIp.setArgomento(newArgomento);
					updateSessioneIp.setOraInizio(newOraInizio);
					updateSessioneIp.setDataSessione(newDataSessione);
					CorsoDTO updateCorsoSessione = corsoDAO.getCorsoById(newFkCorso);
					updateSessioneIp.setCorsoSessione(updateCorsoSessione);
					updateSessioneIp.setSede(newSede);
					updateSessioneIp.setEdificio(newEdificio);
					updateSessioneIp.setAula(newAula);
					
					// VERIFICA DEL VALORE IDENTICO AL PRECEDENTE -> LO IMPOSTA A NULL (COALESCE)
					if(updateSessioneIp.getArgomento()!=null && updateSessioneIp.getArgomento().equals(sessioneIp.getArgomento())) {
						updateSessioneIp.setArgomento(null);
					}
					
					if(updateSessioneIp.getOraInizio()!=null && updateSessioneIp.getOraInizio().equals(sessioneIp.getOraInizio())) {
						updateSessioneIp.setOraInizio(null);
					}
					
					if(updateSessioneIp.getDataSessione()!=null && updateSessioneIp.getDataSessione().equals(sessioneIp.getDataSessione())) {
						updateSessioneIp.setDataSessione(null);
					}
					
					if(updateSessioneIp.getCorsoSessione()!=null && updateSessioneIp.getCorsoSessione().equals(sessioneIp.getCorsoSessione())) {
						updateSessioneIp.setCorsoSessione(null);
					}
					
					if(updateSessioneIp.getSede()!=null && updateSessioneIp.getSede().equals(sessioneIp.getSede())) {
						updateSessioneIp.setSede(null);
					}
					
					if(updateSessioneIp.getEdificio()!=null && updateSessioneIp.getEdificio().equals(sessioneIp.getEdificio())) {
						updateSessioneIp.setEdificio(null);
					}
					
					if(updateSessioneIp.getAula()!=null && updateSessioneIp.getAula().equals(sessioneIp.getAula())) {
						updateSessioneIp.setAula(null);
					}
					
					sessioneIpDAO.updateSessioneInPresenza(updateSessioneIp);
				}
				else {
					throw new UnauthorizedOperationException("Non è possibile modificare sessioni di corsi di altri chef!");
				}
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante l'aggiornamento della sessione");
			}
			
		}
		
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI IN PRESENZA
		public List<SessioneInPresenzaDTO> visualizzaTutteSessioniIp() throws SQLOperationException{
			try {
				return sessioneIpDAO.getAllSessioniIP();
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Impossibile visualizzare le sessioni in presenza");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI IN PRESENZA DI UN CORSO
		public List<SessioneInPresenzaDTO> visualizzaSessioniIPerCorso(int newIdCorso) throws NotFoundException, SQLOperationException{
			try {
				List<SessioneInPresenzaDTO> elencoSessioniIpCorso = sessioneIpDAO.getSessioniIpByCorso(newIdCorso);
				
				if(elencoSessioniIpCorso==null) {
					throw new NotFoundException("Non sono presenti sessioni in presenza del corso selezionato");
				}
				else {
					return elencoSessioniIpCorso;
				}
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante la visualizzazione delle sessioni in presenza del corso");
			}
		}
		
		// METODO PER ELIMINARE UNA SESSIONE IN PRESENZA DI UN CORSO
		public void eliminaSessioneIp(int idSessioneInPresenza) throws NotFoundException, UnauthorizedOperationException, SQLOperationException{
			try {
				SessioneInPresenzaDTO sessioneIp = sessioneIpDAO.getSessioneIpById(idSessioneInPresenza);
				
				if(sessioneIp==null) {
					throw new NotFoundException("Impossibile trovare la sessione in presenza richiesta!");
				}
				
				int idChefLoggato = SessionChef.getChefId();
				if(idChefLoggato==sessioneIp.getCorsoSessione().getChefCorso().getId()) {
					sessioneIpDAO.deleteSessioneInPresenza(sessioneIp);
				}
				else {
					throw new UnauthorizedOperationException("Impossibile eliminare una sessione in presenza di un corso di un altro chef!");
				}

			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante l'eliminazione della sessione in presenza");
			}
		}
		//----------- FINE METODI SESSIONE IN PRESENZA ----------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI SESSIONE ONLINE ----------
		
		// METODO PER INSERIRE UNA NUOVA SESSIONE ONLINE
		public void inserimentoSessioneOn(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newLinkConferenza) throws SQLOperationException, AlreadyExistsException {
			try {
				SessioneOnlineDTO sessioneOnEsistente = sessioneOnDAO.getSessioneOnByArgumentAndDate(newArgomento, newDataSessione);
				
				if(sessioneOnEsistente!=null) {
					throw new AlreadyExistsException("Sessione online già inserita!");
				}
						
				SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
				sessioneOn.setArgomento(newArgomento);
				sessioneOn.setOraInizio(newOraInizio);
				sessioneOn.setDataSessione(newDataSessione);
				CorsoDTO corsoSessione = corsoDAO.getCorsoById(newFkCorso);
				sessioneOn.setCorsoSessione(corsoSessione);
				sessioneOn.setLinkConferenza(newLinkConferenza);
				
				sessioneOnDAO.insertSessioneOnline(sessioneOn);
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore in fase di inserimento della sessione!");
			}
		}
				
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE ONLINE
		public void aggiornaSessioneOnline(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newLinkConferenza) throws UnauthorizedOperationException, NotFoundException, SQLOperationException{
			try {
				SessioneOnlineDTO sessioneOn = sessioneOnDAO.getSessioneOnByArgumentAndDate(newArgomento, newDataSessione);
						
				if(sessioneOn == null) {
					throw new NotFoundException("Impossibile trovare la sessione richiesta!");
				}
						
				//RECUPERO ID CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
						
				if(idChefLoggato==sessioneOn.getCorsoSessione().getChefCorso().getId()) {
					SessioneOnlineDTO updateSessioneOn = new SessioneOnlineDTO();
					updateSessioneOn.setIdSessione(sessioneOn.getIdSessione());
					updateSessioneOn.setArgomento(newArgomento);
					updateSessioneOn.setOraInizio(newOraInizio);
					updateSessioneOn.setDataSessione(newDataSessione);
					CorsoDTO updateCorsoSessione = corsoDAO.getCorsoById(newFkCorso);
					updateSessioneOn.setCorsoSessione(updateCorsoSessione);
					updateSessioneOn.setLinkConferenza(newLinkConferenza);
							
					// VERIFICA DEL VALORE IDENTICO AL PRECEDENTE -> LO IMPOSTA A NULL (COALESCE)
					if(updateSessioneOn.getArgomento()!=null && updateSessioneOn.getArgomento().equals(sessioneOn.getArgomento())) {
						updateSessioneOn.setArgomento(null);
					}
					
					if(updateSessioneOn.getOraInizio()!=null && updateSessioneOn.getOraInizio().equals(sessioneOn.getOraInizio())) {
						updateSessioneOn.setOraInizio(null);
					}
						
					if(updateSessioneOn.getDataSessione()!=null && updateSessioneOn.getDataSessione().equals(sessioneOn.getDataSessione())) {
						updateSessioneOn.setDataSessione(null);
					}
							
					if(updateSessioneOn.getCorsoSessione()!=null && updateSessioneOn.getCorsoSessione().equals(sessioneOn.getCorsoSessione())) {
						updateSessioneOn.setCorsoSessione(null);
					}
					
					if(updateSessioneOn.getLinkConferenza()!=null && updateSessioneOn.getLinkConferenza().equals(sessioneOn.getLinkConferenza())) {
						updateSessioneOn.setLinkConferenza(null);
					}
							
					sessioneOnDAO.updateSessioneOnline(updateSessioneOn);
				}
				else {
					throw new UnauthorizedOperationException("Non è possibile modificare sessioni online di corsi di altri chef!");
				}
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante l'aggiornamento della sessione");
			}
					
		}
				
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI ONLINE
		public List<SessioneOnlineDTO> visualizzaTutteSessioniOn() throws SQLOperationException{
			try {
				return sessioneOnDAO.getAllSessioniOn();
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Impossibile visualizzare le sessioni in presenza");
			}
		}
				
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI ONLINE DI UN CORSO
		public List<SessioneOnlineDTO> visualizzaSessioniOnPerCorso(int newIdCorso) throws NotFoundException, SQLOperationException{
			try {
				List<SessioneOnlineDTO> elencoSessioniOnCorso = sessioneOnDAO.getSessioniOnByCorso(newIdCorso);
						
				if(elencoSessioniOnCorso==null) {
					throw new NotFoundException("Non sono presenti sessioni online del corso selezionato");
				}
				else {
					return elencoSessioniOnCorso;
				}
			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante la visualizzazione delle sessioni online del corso");
			}
		}
				
		// METODO PER ELIMINARE UNA SESSIONE ONLINE DI UN CORSO
		public void eliminaSessioneOn(int idSessioneOnline) throws NotFoundException, UnauthorizedOperationException, SQLOperationException{
			try {
				SessioneOnlineDTO sessioneOn = sessioneOnDAO.getSessioneOnById(idSessioneOnline);
					
				if(sessioneOn==null) {
					throw new NotFoundException("Impossibile trovare la sessione online richiesta!");
				}
						
				int idChefLoggato = SessionChef.getChefId();
				if(idChefLoggato==sessioneOn.getCorsoSessione().getChefCorso().getId()) {
					sessioneOnDAO.deleteSessioneOnline(sessioneOn);
				}
				else {
					throw new UnauthorizedOperationException("Impossibile eliminare una sessione online di un corso di un altro chef!");
				}

			}
			catch(SQLException ex) {
				throw new SQLOperationException("Errore durante l'eliminazione della sessione online");
			}
		}
		
		//----------- FINE METODI SESSIONE ONLINE ----------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//------------ INIZIO METODI RICETTA ----------
		
		// METODO PER INSERIRE UNA RICETTA
		public void inserisciRicetta(String newNomeRicetta, int newTempoPreparazione, int newPorzioni, String newDifficolta) throws AlreadyExistsException, SQLOperationException {
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeRicetta);  
				RicettaDTO ricettaEsistente = ricettaDAO.getRicettaByName(nomeNormalizzato);
				
				if(ricettaEsistente != null) {
					throw new AlreadyExistsException("Ricetta già registrata! Aggiungere una nuova ricetta");
				}
				
				RicettaDTO ricetta = new RicettaDTO();
				ricetta.setNomeRicetta (newNomeRicetta);
				ricetta.setTempoPreparazione (newTempoPreparazione);
				ricetta.setPorzioni (newPorzioni);
				ricetta.setDifficolta (newDifficolta);
				ricettaDAO.insertRicetta(ricetta);
			}
			catch(SQLException ex){
				throw new SQLOperationException ("Errore durante l'inserimento dei dati");
			}
		}

		// METODO PER AGGIORNARE UNA RICETTA
		public void aggiornaRicetta(String newNomeRicetta, int newTempoPreparazione, int newPorzioni, String newDifficolta )throws NotFoundException, SQLOperationException {
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeRicetta);
				RicettaDTO ricettaCercata = ricettaDAO.getRicettaByName(nomeNormalizzato);
				
				if(ricettaCercata==null) {
					throw new NotFoundException("Ricetta cercata non trovata");
				}
				
				RicettaDTO updateRicetta = new RicettaDTO();
				updateRicetta.setNomeRicetta(newNomeRicetta);
				updateRicetta.setTempoPreparazione(newTempoPreparazione);
				updateRicetta.setPorzioni(newPorzioni);
				updateRicetta.setDifficolta(newDifficolta);
				
				ricettaDAO.updateRicetta(updateRicetta);
			}
			catch(SQLException ex) {
				throw new SQLOperationException ("Errore nell'inserimento dei dati");
			}
		}
		
		//METODO PER VISUALIZZARE TUTTE LE RICETTE
		public List<RicettaDTO> visualizzaTutteRicette() throws SQLOperationException{
			try {
				return ricettaDAO.getAllRecipes();
			}catch (SQLException e) {
				throw new SQLOperationException("Errore durante il recupero delle ricette");
			}
		}
		
		//METODO PER VISUALIZZARE LE RICETTE PER NOME
		public RicettaDTO cercaRicettaPerNome (String newNomeRicetta) throws NotFoundException, SQLOperationException{
			try{
				RicettaDTO ricetta = ricettaDAO.getRicettaByName(newNomeRicetta);
				
				if(ricetta==null) {
					throw new NotFoundException("La ricetta" + newNomeRicetta+ "non è presente. Registrala!");
				}
				return ricetta;
			}catch (SQLException ex) {
				throw new SQLOperationException("Errore durante la ricerca della ricetta");
			}
		}
		
		//METODO PER ELIMINARE LE RICETTE
		public void eliminaRicetta(int id) throws NotFoundException, SQLOperationException{
			try {
				RicettaDTO eliminaRicetta = ricettaDAO.getRicettaById(id);
				
				if(eliminaRicetta == null) {
					throw new NotFoundException("Nessuna ricetta trovata");
				}
				ricettaDAO.deleteRicetta(eliminaRicetta);
			}catch(SQLException e) {
				throw new SQLOperationException("Errore nell'eliminazione della ricetta");
			}
		}
		
		//------------FINE METODI RICETTA-------------
		
		
		//-----------INIZIO METODI INGREDIENTE---------
		
		//METODO PER INSERIRE UN INGREDIENTE
		public void inserisciIngrediente(String newNomeIngrediente, String newTipologia)throws AlreadyExistsException, SQLOperationException{
			try {
			String nomeNormalizzato = normalizzaNomeInserito(newNomeIngrediente);
			IngredienteDTO ingredienteEsistente = ingredienteDAO.getIngredienteByName(nomeNormalizzato);
			
			if(ingredienteEsistente != null) {
				throw new AlreadyExistsException("Ingrediente già registrato");
			}
			IngredienteDTO ingrediente = new IngredienteDTO();
			ingrediente.setNomeIngrediente(newNomeIngrediente);
			ingrediente.setTipologia(newTipologia);
			ingredienteDAO.insertIngrediente(ingrediente);
			}
			catch (SQLException e) {
				throw new SQLOperationException("Erorre nell'inserimento dell'ingrediente");
			}
		}
		
		//METODO PER AGGIORNARE UN INGREDIENTE
		public void aggiornaIngrediente(String newNomeIngrediente, String newTipologia) throws NotFoundException, SQLOperationException{
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeIngrediente);
				IngredienteDTO trovaIngrediente = ingredienteDAO.getIngredienteByName(nomeNormalizzato);
				
				if(trovaIngrediente == null) {
					throw new NotFoundException("Ingrediente inserito non trovato. Registralo!");
				}
				IngredienteDTO updateIngrediente = new IngredienteDTO();
				updateIngrediente.setNomeIngrediente(newNomeIngrediente);
				updateIngrediente.setTipologia(newTipologia);
				ingredienteDAO.updateIngrediente(updateIngrediente);
			}catch(SQLException e) {
				throw new SQLOperationException("Errore dirante l'aggiornamento dell'ingrediente");
			}
		}
		
		//METODO PER ELIMINARE UN INGREDIENTE
		public void eliminaIngrediente(int idIngrediente) throws NotFoundException, SQLOperationException{
			try {
				IngredienteDTO eliminaIngrediente = ingredienteDAO.getIngredienteById(idIngrediente);
				
				if(eliminaIngrediente == null) {
					throw new NotFoundException("Ingrediente non trovato");
				}
				
				ingredienteDAO.deleteIngrediente(eliminaIngrediente);
			}catch(SQLException e) {
				throw new SQLOperationException("Errore nell'eliminazione dell'ingrediente");
			}
		}
		
		//------------FINE METODI INGREDIENTE-------------
		
}		
