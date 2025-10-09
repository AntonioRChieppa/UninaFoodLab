package controller;

import dao.daoInterfaces.*;
import dao.daoImplements.*;
import dto.*;

import java.time.*;
import session.SessionChef;

import exception.*;

import java.util.*;
import java.sql.SQLException;

public class Controller {

		private ChefDAOInt chefDAO;
		private CorsoDAOInt corsoDAO;
		private RicettaDAOInt ricettaDAO;
		private SessioneInPresenzaDAOInt sessioneIpDAO;
		private SessioneOnlineDAOInt sessioneOnDAO;
		private IngredienteDAOInt ingredienteDAO;
		
		public Controller() {
			this.chefDAO = new ChefDAOImpl();
			this.corsoDAO = new CorsoDAOImpl();
			this.ricettaDAO = new RicettaDAOImpl();
			this.sessioneIpDAO = new SessioneInPresenzaDAOImpl();
			this.sessioneOnDAO = new SessioneOnlineDAOImpl();
			this.ingredienteDAO = new IngredienteDAOImpl();
		}
		
		//METODO PER NORMALIZZARE UNA STRINGA
		public static String normalizzaNomeInserito(String nome) {
			if(nome == null) {
				return null;
			}
			return nome.trim().toLowerCase().replaceAll("\\s", "");
			}
		
		//---------- INIZIO METODI CORSO ----------
		
		// METODO PER INSERIRE UN NUOVO CORSO
		public void inserimentoCorso(String newNomeCorso, String newCategoria, LocalDate newDataInizio, Integer newNumeroSessioni, String newFrequenzaSessioni, int newFkChef) throws OperationException, AlreadyExistsException {
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
				throw new OperationException("Errore in fase di inserimento del nuovo corso!");
			}
		}

		// METODO PER AGGIORNARE I DATI RELATIVI AD UN CORSO
		public void aggiornaCorso(String newNomeCorso, String newCategoria, LocalDate newDataInizio, Integer newNumeroSessioni, String newFrequenzaSessioni) throws UnauthorizedOperationException, NotFoundException, OperationException{
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
				throw new OperationException("Errore durante l'aggiornamento dello chef");
			}
		}
		
		// METODO PER VISUALIZZARE TUTTI I CORSI
		public List<CorsoDTO> visualizzaTuttiCorsi() throws OperationException{
			try {
				return corsoDAO.getAllCorsi();
			} catch (SQLException e) {
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
		
		// METODO PER CERCARE UN CORSO DAL NOME
		public CorsoDTO cercaCorsoPerNome(String newNomeCorso) throws NotFoundException, OperationException{
			try {
				CorsoDTO corso = corsoDAO.getCorsoByName(newNomeCorso);
				
				if(corso==null) {
					throw new NotFoundException("Il corso "+newNomeCorso+" non è presente. Registralo!");
				}
				
				return corso;
			}catch(SQLException ex) {
				throw new OperationException("Errore durante la ricerca del corso");
			}
		}
		
		// METODO PER ELIMINARE UN CORSO
		public void eliminaCorso(int idCorso) throws NotFoundException, UnauthorizedOperationException, OperationException{
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
				throw new OperationException("Impossibile eliminare il corso selezionato!");
			}
		}
		
		//---------- FINE METODI CORSO ----------
		
		//-------------------------------------------------------------------------------------------------------------
		
		//---------- INIZIO METODI SESSIONE IN PRESENZA ----------
		
		// METODO PER INSERIRE UNA NUOVA SESSIONE IN PRESENZA
		public void inserimentoSessioneIP(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newSede, String newEdificio, String newAula) throws OperationException, AlreadyExistsException {
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
				throw new OperationException("Errore in fase di inserimento della sessione!");
			}
		}
		
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE IN PRESENZA
		public void aggiornaSessioneInPresenza(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newSede, String newEdificio, String newAula) throws UnauthorizedOperationException, NotFoundException, OperationException{
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
				throw new OperationException("Errore durante l'aggiornamento della sessione");
			}
			
		}
		
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI IN PRESENZA
		public List<SessioneInPresenzaDTO> visualizzaTutteSessioniIp() throws OperationException{
			try {
				return sessioneIpDAO.getAllSessioniIP();
			}
			catch(SQLException ex) {
				throw new OperationException("Impossibile visualizzare le sessioni in presenza");
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
		public void inserimentoSessioneOn(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newLinkConferenza) throws OperationException, AlreadyExistsException {
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
				throw new OperationException("Errore in fase di inserimento della sessione!");
			}
		}
				
		// METODO PER AGGIORNARE I DATI RELATIVI AD UNA SESSIONE ONLINE
		public void aggiornaSessioneOnline(String newArgomento, LocalTime newOraInizio, LocalDate newDataSessione, int newFkCorso, String newLinkConferenza) throws UnauthorizedOperationException, NotFoundException, OperationException{
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
				throw new OperationException("Errore durante l'aggiornamento della sessione");
			}
					
		}
				
		// METODO PER VISUALIZZARE TUTTE LE SESSIONI ONLINE
		public List<SessioneOnlineDTO> visualizzaTutteSessioniOn() throws OperationException{
			try {
				return sessioneOnDAO.getAllSessioniOn();
			}
			catch(SQLException ex) {
				throw new OperationException("Impossibile visualizzare le sessioni in presenza");
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
				throw new OperationException("Erorre nell'inserimento dell'ingrediente");
			}
		}
		
		//METODO PER AGGIORNARE UN INGREDIENTE
		public void aggiornaIngrediente(String newNomeIngrediente, String newTipologia) throws NotFoundException, OperationException{
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
		
}		
