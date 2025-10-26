package controller;

import dao.daoImplements.*;
import dao.daoInterfaces.*;
import dto.*;
import exception.*;
import java.sql.SQLException;
import java.time.*;
import session.SessionChef;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class Controller {

		private ChefDAOInt chefDAO;
		private CorsoDAOInt corsoDAO;
		private RicettaDAOInt ricettaDAO;
		private SessioneInPresenzaDAOInt sessioneIpDAO;
		private SessioneOnlineDAOInt sessioneOnDAO;
		private IngredienteDAOInt ingredienteDAO;
		private ComposizioneDAOInt composizioneDAO;
		
		public Controller() {
			this.chefDAO = new ChefDAOImpl();
			this.corsoDAO = new CorsoDAOImpl();
			this.ricettaDAO = new RicettaDAOImpl();
			this.sessioneIpDAO = new SessioneInPresenzaDAOImpl();
			this.sessioneOnDAO = new SessioneOnlineDAOImpl();
			this.ingredienteDAO = new IngredienteDAOImpl();
			this.composizioneDAO = new ComposizioneDAOImpl();
		}
		
		//METODO PER NORMALIZZARE UNA STRINGA
		public static String normalizzaNomeInserito(String nome) {
		    if(nome == null) {
		        return null;
		    }
		    
		    return nome.trim().toLowerCase().replaceAll("\\s+", " ");
		}
		
		//METODO PER CONTROLLARE SE UNA STRINGA NON CONTIENE NUMERI
		public boolean isOnlyLettersAndSpaces(String text) {
		    if (text == null) {
		        return false;
		    }
		    
		    return text.matches("^[\\p{L} ]+$");
		}
		
		//METODO DI VERIFICA DI LINK VALIDO
		public boolean isValidHttpUrl(String url) {
		    if (url == null || url.isEmpty()) {
		        return false; 
		    }
		    
		    Pattern urlPattern = Pattern.compile("^(https?://).+", Pattern.CASE_INSENSITIVE);
		    Matcher matcher = urlPattern.matcher(url.trim()); 
		    return matcher.matches();
		}
		
		//---------- INIZIO METODI CORSO ----------
		
		// METODO PER INSERIRE UN NUOVO CORSO
		public void inserimentoCorso(String newNomeCorso, String newCategoria, java.util.Date newDataInizioUtil, String newNumeroSessioni, String newFrequenzaSessioni, int newFkChef) throws OperationException, AlreadyExistsException {
			try {
				CorsoDTO corsoEsistente = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corsoEsistente!=null) {
					throw new AlreadyExistsException("Corso già registrato!");
				}
				
				if(newNomeCorso.isEmpty() || newCategoria.isEmpty() || newDataInizioUtil == null || newNumeroSessioni==null) {
					throw new OperationException("Tutti i campi sono obbligatori!");
				}
				
				if(!isOnlyLettersAndSpaces(newNomeCorso) || !isOnlyLettersAndSpaces(newCategoria)) {
					throw new OperationException("Nome del corso e categoria ammettono solo caratteri!");
				}
				
				Integer numSessioni = Integer.parseInt(newNumeroSessioni);
				if(numSessioni <= 0) {
					throw new OperationException("Il numero di sessioni deve essere un intero positivo.");
				}
				
				// Conversione da Date -> LocalDate in java
				LocalDate newDataInizio = Instant.ofEpochMilli(newDataInizioUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
				
				CorsoDTO corso = new CorsoDTO();
				corso.setNomeCorso(normalizzaNomeInserito(newNomeCorso));
				corso.setCategoria(normalizzaNomeInserito(newCategoria));
				corso.setDataInizio(newDataInizio);
				corso.setNumeroSessioni(numSessioni);
				corso.setFrequenzaSessioni(newFrequenzaSessioni);
				int idChefLoggato = SessionChef.getChefId();
		        ChefDTO chef = chefDAO.getChefById(idChefLoggato);
		        corso.setChefCorso(chef);

		        
	            corsoDAO.insertCorso(corso);
			}
			catch(SQLException ex) {
				throw new OperationException("Errore in fase di inserimento del nuovo corso!");
			}
		}

		// METODO PER AGGIORNARE I DATI RELATIVI AD UN CORSO
		public void aggiornaCorso(String nomeCorsoOriginale, String newCategoria, java.util.Date newDataInizioUtil, String newNumeroSessioni, String newFrequenzaSessioni) throws UnauthorizedOperationException, NotFoundException, OperationException{
			try {
				
				CorsoDTO corso = corsoDAO.getCorsoByName(nomeCorsoOriginale);
				if(corso==null) {
					throw new NotFoundException("Impossibile trovare il corso: "+nomeCorsoOriginale);
				}
				
				if(!isOnlyLettersAndSpaces(newCategoria)) {
					throw new OperationException("Categoria ammette solo caratteri!");
				}
				
				if(newCategoria.isEmpty() || newDataInizioUtil==null || newNumeroSessioni.isEmpty()) {
					throw new OperationException("Tutti i campi sono obbligatori per la modifica.");
				}
				
				Integer numSessioni;
				try {
					numSessioni = Integer.parseInt(newNumeroSessioni);
					if(numSessioni <= 0) {
						throw new NumberFormatException();
					}
				}catch(NumberFormatException ex) {
					throw new OperationException("Il numero di sessioni deve essere un intero positivo.");
				}
				
				// RECUPERO DELLO CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				if(corso.getChefCorso() == null || idChefLoggato != corso.getChefCorso().getId()) {
					throw new UnauthorizedOperationException("Impossibile modificare corsi degli altri chef o corsi senza chef assegnato!");
				}
				
				LocalDate newDataInizio = Instant.ofEpochMilli(newDataInizioUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
				
				CorsoDTO updateCorso = new CorsoDTO();
				updateCorso.setId(corso.getId());
				updateCorso.setNomeCorso(nomeCorsoOriginale);
				updateCorso.setCategoria(normalizzaNomeInserito(newCategoria));
				updateCorso.setDataInizio(newDataInizio);
				updateCorso.setNumeroSessioni(numSessioni);
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
			catch(SQLException ex) {
				throw new OperationException("Errore durante l'aggiornamento del corso");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI
		public List<CorsoDTO> visualizzaTuttiCorsi() throws OperationException{
			try {
				return corsoDAO.getAllCorsi();
			} catch (SQLException ex) {
		        throw new OperationException("Errore durante il recupero di tutti i corsi");
		    }
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI TENUTI DA UNO CHEF
		public List<CorsoDTO> visualizzaCorsiPerChef() throws NotFoundException, OperationException{
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
				throw new OperationException("Errore durante il recupero dei corsi");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI DI UNA DETERMINATA CATEGORIA
		public List<CorsoDTO> visualizzaCorsiPerCategoria(String categoria) throws NotFoundException, OperationException{
			try {
				return corsoDAO.getCorsiByCategory(categoria);
			} catch(SQLException ex) {
				throw new OperationException("Errore nel recupero dei corsi filtrati!");
			}
		}
		
		// METODO PER PRELEVARE TUTTE LE CATEGORIE PRESENTI
		public List<String> getAllCategorie() throws OperationException{
			try {
				return corsoDAO.getAllCategories();
			}catch(SQLException ex) {
				throw new OperationException("Errore nel recupero delle categorie");
			}
		}
		
		// METODO PER ELIMINARE UN CORSO
		public void eliminaCorso(String nomeCorso) throws NotFoundException, UnauthorizedOperationException, OperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(nomeCorso);
				
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
				throw new OperationException("Impossibile eliminare il corso selezionato!");
			}
		}
		
		//---------- FINE METODI CORSO ----------
		
		//-------------------------------------------------------------------------------------------------------------
		
		//---------- METODO PER VISUALIZZARE TUTTE LE SESSIONI ----------
		public List<SessioneDTO> visualizzaTutteSessioniPerChef() throws NotFoundException, OperationException{
			try {
				List<SessioneDTO> elencoTutteSessioni = new ArrayList<>();
				
				int idChefLoggato = SessionChef.getChefId();
				List<SessioneInPresenzaDTO> sessionIP = sessioneIpDAO.getSessioniIpByChefId(idChefLoggato);
				elencoTutteSessioni.addAll(sessionIP);
				
				List<SessioneOnlineDTO> sessioniOn = sessioneOnDAO.getSessioniOnByChefId(idChefLoggato);
				elencoTutteSessioni.addAll(sessioniOn);
				
				return elencoTutteSessioni;
			} catch(SQLException ex) {
				throw new OperationException("Errore in fase di visualizzazione delle sessioni!");
			}
		}
		
		//---------- INIZIO METODI SESSIONE IN PRESENZA ----------
		
		// METODO PER INSERIRE UNA NUOVA SESSIONE IN PRESENZA
		public void inserimentoSessioneIP(String newArgomento, LocalTime newOraInizio, java.util.Date newDataSessioneUtil, Integer newFkCorso, String newSede, String newEdificio, String newAula) throws OperationException, AlreadyExistsException {
			try {
				
				if(newArgomento.isEmpty() || newOraInizio == null || newDataSessioneUtil == null || newFkCorso == null || newSede.isEmpty() || newEdificio.isEmpty() || newAula.isEmpty()) {
					throw new OperationException("Tutti i campi sono obbligatori!");
				}
				
				//Conversione da java.util.Date -> LocalDate
				LocalDate newDataSessione = Instant.ofEpochMilli(newDataSessioneUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
				
				SessioneInPresenzaDTO sessioneIpEsistente = sessioneIpDAO.getSessioneIpByArgumentAndDate(newArgomento, newDataSessione);
				
				if(sessioneIpEsistente!=null) {
					throw new AlreadyExistsException("Sessione in presenza già inserita!");
				}
				
				if(!isOnlyLettersAndSpaces(newArgomento) || !isOnlyLettersAndSpaces(newSede)) {
					throw new OperationException("I campi argomento e sede possono contenere solo caratteri!");
				}
				
				SessioneInPresenzaDTO sessioneIp = new SessioneInPresenzaDTO();
				sessioneIp.setArgomento(normalizzaNomeInserito(newArgomento));
				sessioneIp.setOraInizio(newOraInizio);
				sessioneIp.setDataSessione(newDataSessione);
				CorsoDTO corsoSessione = corsoDAO.getCorsoById(newFkCorso);
				sessioneIp.setCorsoSessione(corsoSessione);
				sessioneIp.setTipoSessione("presenza");
				sessioneIp.setSede(normalizzaNomeInserito(newSede));
				sessioneIp.setEdificio(normalizzaNomeInserito(newEdificio));
				sessioneIp.setAula(normalizzaNomeInserito(newAula));
				
				sessioneIpDAO.insertSessioneInPresenza(sessioneIp);
			}
			catch(SQLException ex) {
				throw new OperationException("Errore in fase di inserimento della sessione!");
			}
		}
		
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE IN PRESENZA
		public void aggiornaSessioneInPresenza(int idSessione, String newArgomento, LocalTime newOraInizio, java.util.Date newDataSessioneUtil, Integer newFkCorso, String newSede, String newEdificio, String newAula) throws UnauthorizedOperationException, NotFoundException, OperationException{
			try {
				if(newArgomento.isEmpty() || newOraInizio == null || newDataSessioneUtil == null || newFkCorso == null || newSede.isEmpty() || newEdificio.isEmpty() || newAula.isEmpty()) {
					throw new OperationException("Tutti i campi sono obbligatori!");
				}
				
				SessioneInPresenzaDTO sessioneIp = sessioneIpDAO.getSessioneIpById(idSessione);
				if(sessioneIp == null) {
					throw new NotFoundException("Impossibile trovare la sessione cercata!");
				}
				
				if(!isOnlyLettersAndSpaces(newArgomento) || !isOnlyLettersAndSpaces(newSede)) {
					throw new OperationException("I campi argomento e sede possono contener solo caratteri!");
				}
				
				//RECUPERO ID CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				if(sessioneIp.getCorsoSessione().getChefCorso()==null || idChefLoggato!=sessioneIp.getCorsoSessione().getChefCorso().getId()) {
					throw new UnauthorizedOperationException("Impossibile modificare sessioni di corsi di altri chef!");
				}
				
				//Conversione da java.util.Date -> LocalDate
				LocalDate newDataSessione = Instant.ofEpochMilli(newDataSessioneUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
			
				SessioneInPresenzaDTO updateSessioneIp = new SessioneInPresenzaDTO();
				updateSessioneIp.setIdSessione(sessioneIp.getIdSessione());
				updateSessioneIp.setArgomento(normalizzaNomeInserito(newArgomento));
				updateSessioneIp.setOraInizio(newOraInizio);
				updateSessioneIp.setDataSessione(newDataSessione);
				CorsoDTO updateCorsoSessione = corsoDAO.getCorsoById(newFkCorso);
				updateSessioneIp.setCorsoSessione(updateCorsoSessione);
				updateSessioneIp.setSede(normalizzaNomeInserito(newSede));
				updateSessioneIp.setEdificio(normalizzaNomeInserito(newEdificio));
				updateSessioneIp.setAula(normalizzaNomeInserito(newAula));
					
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
					
				if (updateSessioneIp.getCorsoSessione() != null && sessioneIp.getCorsoSessione() != null &&
					    updateSessioneIp.getCorsoSessione().getId() == sessioneIp.getCorsoSessione().getId()) {
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
			catch(SQLException ex) {
				throw new OperationException("Errore durante l'aggiornamento della sessione");
			}
			
		}
		
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI IN PRESENZA DI UN CORSO
		public List<SessioneInPresenzaDTO> visualizzaSessioniIPerCorso(int newIdCorso) throws NotFoundException, OperationException{
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
				throw new OperationException("Errore durante la visualizzazione delle sessioni in presenza del corso");
			}
		}
		
		// METODO PER RECUPERARE UNA SESSIONE IN PRESENZA DALL'ID DELLA SESSIONE
		public SessioneInPresenzaDTO getSessioneInPresenzaById(int idSessione) throws OperationException {
	        try {
	            SessioneInPresenzaDTO sessioneIP = sessioneIpDAO.getSessioneIpById(idSessione);
	            return sessioneIP;
	        } catch (SQLException e) {
	            throw new OperationException("Errore database nel recupero dettagli sessione in presenza");
	        }
	    }
		
		// METODO PER ELIMINARE UNA SESSIONE IN PRESENZA DI UN CORSO
		public void eliminaSessioneIp(int idSessioneInPresenza) throws NotFoundException, UnauthorizedOperationException, OperationException{
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
				throw new OperationException("Errore durante l'eliminazione della sessione in presenza");
			}
		}
		//----------- FINE METODI SESSIONE IN PRESENZA ----------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI SESSIONE ONLINE ----------
		
		// METODO PER INSERIRE UNA NUOVA SESSIONE ONLINE
		public void inserimentoSessioneOn(String newArgomento, LocalTime newOraInizio, java.util.Date newDataSessioneUtil, Integer newFkCorso, String newLinkConferenza) throws OperationException, AlreadyExistsException {
			try {
				//Conversione da java.util.Date -> LocalDate
				LocalDate newDataSessione = Instant.ofEpochMilli(newDataSessioneUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
				
				if(newArgomento.isEmpty() || newOraInizio == null || newDataSessione == null || newFkCorso == null || newLinkConferenza.isEmpty()) {
					throw new OperationException("Tutti i campi sono obbligatori");
				}
				
				SessioneOnlineDTO sessioneOnEsistente = sessioneOnDAO.getSessioneOnByArgumentAndDate(newArgomento, newDataSessione);
				if(sessioneOnEsistente!=null) {
					throw new AlreadyExistsException("Sessione online già inserita!");
				}
				
				if(!isValidHttpUrl(newLinkConferenza)) {
					throw new OperationException("Link non valido!");
				}
				
				if(!isOnlyLettersAndSpaces(newArgomento)) {
					throw new OperationException("Argomento può contenere solo caratteri!");
				}
						
				SessioneOnlineDTO sessioneOn = new SessioneOnlineDTO();
				sessioneOn.setArgomento(normalizzaNomeInserito(newArgomento));
				sessioneOn.setOraInizio(newOraInizio);
				sessioneOn.setDataSessione(newDataSessione);
				CorsoDTO corsoSessione = corsoDAO.getCorsoById(newFkCorso);
				sessioneOn.setCorsoSessione(corsoSessione);
				sessioneOn.setTipoSessione("online");
				sessioneOn.setLinkConferenza(newLinkConferenza);
				
				sessioneOnDAO.insertSessioneOnline(sessioneOn);
			}
			catch(SQLException ex) {
				throw new OperationException("Errore in fase di inserimento della sessione!");
			}
		}
				
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE ONLINE
		public void aggiornaSessioneOnline(int idSessione, String newArgomento, LocalTime newOraInizio, java.util.Date newDataSessioneUtil, Integer newFkCorso, String newLinkConferenza) throws UnauthorizedOperationException, NotFoundException, OperationException{
			try {
				//Conversione da java.util.Date -> LocalDate
				LocalDate newDataSessione = Instant.ofEpochMilli(newDataSessioneUtil.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
				
				if(newArgomento.isEmpty() || newOraInizio == null || newDataSessione == null || newFkCorso == null || newLinkConferenza.isEmpty()) {
					throw new OperationException("Tutti i campi sono obbligatori");
				}
				
				SessioneOnlineDTO sessioneOn = sessioneOnDAO.getSessioneOnById(idSessione);
				if(sessioneOn == null) {
					throw new NotFoundException("Impossibile trovare la sessione richiesta!");
				}
				
				if(!isValidHttpUrl(newLinkConferenza)) {
					throw new OperationException("Link non valido!");
				}
				
				if(!isOnlyLettersAndSpaces(newArgomento)) {
					throw new OperationException("Argomento può contenere solo caratteri!");
				}
				
				//RECUPERO ID CHEF CORRENTE
				int idChefLoggato = SessionChef.getChefId();
				if(sessioneOn.getCorsoSessione().getChefCorso()==null || idChefLoggato!=sessioneOn.getCorsoSessione().getChefCorso().getId()) {
					throw new UnauthorizedOperationException("Impossibile modificare sessioni di corsi di altri chef!");
				}
						
				SessioneOnlineDTO updateSessioneOn = new SessioneOnlineDTO();
				updateSessioneOn.setIdSessione(sessioneOn.getIdSessione());
				updateSessioneOn.setArgomento(normalizzaNomeInserito(newArgomento));
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
							
				if (updateSessioneOn.getCorsoSessione() != null && sessioneOn.getCorsoSessione() != null &&
						updateSessioneOn.getCorsoSessione().getId() == sessioneOn.getCorsoSessione().getId()) {
						updateSessioneOn.setCorsoSessione(null); 
				}
					
				if(updateSessioneOn.getLinkConferenza()!=null && updateSessioneOn.getLinkConferenza().equals(sessioneOn.getLinkConferenza())) {
					updateSessioneOn.setLinkConferenza(null);
				}
							
				sessioneOnDAO.updateSessioneOnline(updateSessioneOn);
				
			} catch(SQLException ex) {
				throw new OperationException("Errore durante l'aggiornamento della sessione");
			}
					
		}
				
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI ONLINE DI UN CORSO
		public List<SessioneOnlineDTO> visualizzaSessioniOnPerCorso(int newIdCorso) throws NotFoundException, OperationException{
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
				throw new OperationException("Errore durante la visualizzazione delle sessioni online del corso");
			}
		}
		
		// METODO PER RECUPERARE UNA SESSIONE ONLINE DALL'ID DELLA SESSIONE
		public SessioneOnlineDTO getSessioneOnlineById(int idSessione) throws OperationException {
	        try {
	            SessioneOnlineDTO sessioneOn = sessioneOnDAO.getSessioneOnById(idSessione);
	            return sessioneOn;
	        } catch (SQLException e) {
	            throw new OperationException("Errore database nel recupero dettagli sessione online: " + e.getMessage());
	        }
	    }
				
		// METODO PER ELIMINARE UNA SESSIONE ONLINE DI UN CORSO
		public void eliminaSessioneOn(int idSessioneOnline) throws NotFoundException, UnauthorizedOperationException, OperationException{
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
				throw new OperationException("Errore durante l'eliminazione della sessione online");
			}
		}
		
		//----------- FINE METODI SESSIONE ONLINE ----------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//------------ INIZIO METODI RICETTA ----------
		
		// METODO PER INSERIRE UNA RICETTA
		public void inserisciRicetta(String newNomeRicetta, int newTempoPreparazione, int newPorzioni, String newDifficolta) throws AlreadyExistsException, OperationException {
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
				throw new OperationException ("Errore durante l'inserimento dei dati");
			}
		}

		// METODO PER AGGIORNARE UNA RICETTA
		public void aggiornaRicetta(String newNomeRicetta, int newTempoPreparazione, int newPorzioni, String newDifficolta )throws NotFoundException, OperationException {
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeRicetta);
				RicettaDTO ricetta = ricettaDAO.getRicettaByName(nomeNormalizzato);
				
				if(ricetta==null) {
					throw new NotFoundException("Ricetta cercata non trovata");
				}
				
				RicettaDTO updateRicetta = new RicettaDTO();
				updateRicetta.setNomeRicetta(newNomeRicetta);
				updateRicetta.setTempoPreparazione(newTempoPreparazione);
				updateRicetta.setPorzioni(newPorzioni);
				updateRicetta.setDifficolta(newDifficolta);
				
				if(updateRicetta.getNomeRicetta()!=null && updateRicetta.getNomeRicetta().equals(ricetta.getNomeRicetta())) {
					updateRicetta.setNomeRicetta(null);
				}
				
				if(updateRicetta.getTempoPreparazione()!=null && updateRicetta.getTempoPreparazione().equals(ricetta.getTempoPreparazione())) {
					updateRicetta.setTempoPreparazione(null);
				}
				
				if(updateRicetta.getPorzioni()!=null && updateRicetta.getPorzioni().equals(ricetta.getPorzioni())) {
					updateRicetta.setPorzioni(null);
				}
				
				if(updateRicetta.getDifficolta()!=null && updateRicetta.getDifficolta().equals(ricetta.getDifficolta())) {
					updateRicetta.setDifficolta(null);
				}
				ricettaDAO.updateRicetta(updateRicetta);
			}
			catch(SQLException ex) {
				throw new OperationException ("Errore nell'inserimento dei dati");
			}
		}
		
		//METODO PER VISUALIZZARE TUTTE LE RICETTE
		public List<RicettaDTO> visualizzaTutteRicette() throws OperationException{
			try {
				return ricettaDAO.getAllRecipes();
			}catch (SQLException e) {
				throw new OperationException("Errore durante il recupero delle ricette");
			}
		}
		
		//METODO PER VISUALIZZARE LE RICETTE PER NOME
		public RicettaDTO cercaRicettaPerNome (String newNomeRicetta) throws NotFoundException, OperationException{
			try{
				RicettaDTO ricetta = ricettaDAO.getRicettaByName(newNomeRicetta);
				
				if(ricetta==null) {
					throw new NotFoundException("La ricetta" + newNomeRicetta+ "non è presente. Registrala!");
				}
				return ricetta;
			}catch (SQLException ex) {
				throw new OperationException("Errore durante la ricerca della ricetta");
			}
		}
		
		//METODO PER ELIMINARE LE RICETTE
		public void eliminaRicetta(int id) throws NotFoundException, OperationException{
			try {
				RicettaDTO eliminaRicetta = ricettaDAO.getRicettaById(id);
				
				if(eliminaRicetta == null) {
					throw new NotFoundException("Nessuna ricetta trovata");
				}
				ricettaDAO.deleteRicetta(eliminaRicetta);
			}catch(SQLException e) {
				throw new OperationException("Errore nell'eliminazione della ricetta");
			}
		}
		
		//------------FINE METODI RICETTA-------------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//-----------INIZIO METODI INGREDIENTE---------
		
		//METODO PER INSERIRE UN INGREDIENTE
		public void inserisciIngrediente(String newNomeIngrediente, String newTipologia)throws AlreadyExistsException, OperationException{
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
				throw new OperationException("Errore nell'inserimento dell'ingrediente");
			}
		}
		
		//METODO PER AGGIORNARE UN INGREDIENTE
		public void aggiornaIngrediente(String newNomeIngrediente, String newTipologia) throws NotFoundException, OperationException{
			try {
				String nomeNormalizzato = normalizzaNomeInserito(newNomeIngrediente);
				IngredienteDTO ingrediente = ingredienteDAO.getIngredienteByName(nomeNormalizzato);
				
				if(ingrediente == null) {
					throw new NotFoundException("Ingrediente inserito non trovato. Registralo!");
				}
				
				IngredienteDTO updateIngrediente = new IngredienteDTO();
		
				updateIngrediente.setNomeIngrediente(newNomeIngrediente);
				updateIngrediente.setTipologia(newTipologia);
				
				if(updateIngrediente.getNomeIngrediente()!=null && updateIngrediente.getNomeIngrediente().equals(ingrediente.getNomeIngrediente())) {
					updateIngrediente.setNomeIngrediente(null);
				}
				
				if(updateIngrediente.getTipologia()!=null && updateIngrediente.getTipologia().equals(ingrediente.getTipologia())){
					ingrediente.setTipologia(null);
				}
				
				ingredienteDAO.updateIngrediente(updateIngrediente);
			}catch(SQLException e) {
				throw new OperationException("Errore dirante l'aggiornamento dell'ingrediente");
			}
		}
		
		//METODO PER ELIMINARE UN INGREDIENTE
		public void eliminaIngrediente(int idIngrediente) throws NotFoundException, OperationException{
			try {
				IngredienteDTO eliminaIngrediente = ingredienteDAO.getIngredienteById(idIngrediente);
				
				if(eliminaIngrediente == null) {
					throw new NotFoundException("Ingrediente non trovato");
				}
				
				ingredienteDAO.deleteIngrediente(eliminaIngrediente);
			}catch(SQLException e) {
				throw new OperationException("Errore nell'eliminazione dell'ingrediente");
			}
		}
		
		//------------FINE METODI INGREDIENTE-------------
		
		//----------------------------------------------------------------------------------------------------------------------------
		
		//INIZIO METODI COMPOSIZIONE
		
		//METODO PER ASSOCIARE UN INGREDIENTE AD UNA RICETTA
		public List<ComposizioneDTO> associaIngredienteARicetta(int newIdIngrediente, int newIdRicetta) throws NotFoundException, AlreadyExistsException, OperationException{
			try {
				IngredienteDTO ingrediente = ingredienteDAO.getIngredienteById(newIdIngrediente);
				RicettaDTO ricetta = ricettaDAO.getRicettaById(newIdRicetta);
				
				if(ingrediente == null) {
					throw new NotFoundException("Ingrediente non trovato. Registralo!");
				}
				
				if(ricetta == null) {
					throw new NotFoundException("Ricetta non trovata. Registrala!");
				}
				
				List<ComposizioneDTO> elencoIngredientiRicetta = composizioneDAO.getAllIngredientiRicetta(newIdRicetta);
				
				if(elencoIngredientiRicetta == null) {
					throw new AlreadyExistsException("L'ingrediente selezionato è già associato alla ricetta scelta");
				}
				
				ComposizioneDTO composizione = new ComposizioneDTO();
				
				composizione.setIngredienteRicetta(ingrediente);
				composizione.setRicettaIngrediente(ricetta);
				
				composizioneDAO.insertComposizione(composizione);
				return elencoIngredientiRicetta;
			
			}catch(SQLException ex) {
				throw new OperationException("Errore durante l'associazione dell'ingrediente alla ricetta");
			}
		}
		
		
		/*METODO PER VISUALIZZARE TUTTI GLI INGREDIENTI DI UNA RICETTA
				public List<String> visualizzaTuttiIngredientiRicetta(int newId) throws NotFoundException, OperationException{
					try {
						List<String> listaIngredientiRicetta = composizioneDAO.getAllIngredientiRicetta(newId);
						
						if(listaIngredientiRicetta == null) {
							throw new NotFoundException("Non sono presenti ingredienti nella ricetta selezionata");
						}
						else {
							return listaIngredientiRicetta;
						}
					}catch(SQLException ex) {
							throw new OperationException("Errore durante la visualizzazione degli ingredienti della ricetta");	
					}
				}
				*/
}