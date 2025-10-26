package dto;

public class SessioneRicettaDTO {
	
	// ATTRIBUTI
	private SessioneInPresenzaDTO sessioneRicetta;
	private RicettaDTO ricettaSessione;
	
	// DEFAULT CONSTRUCTOR
	public SessioneRicettaDTO() {
		
	}
	
	// SPECIFIC CONSTRUCTOR
	public SessioneRicettaDTO(SessioneInPresenzaDTO sessioneRicetta, RicettaDTO ricettaSessione){
		this.sessioneRicetta = sessioneRicetta;
		this.ricettaSessione = ricettaSessione;
	}
	
	// GETTER E SETTER
	public SessioneDTO getSessioneRicetta() {
		return sessioneRicetta;
	}

	public void setSessioneRicetta(SessioneInPresenzaDTO sessioneRicetta) {
		this.sessioneRicetta = sessioneRicetta;
	}

	public RicettaDTO getRicettaSessione() {
		return ricettaSessione;
	}

	public void setRicettaSessione(RicettaDTO ricettaSessione) {
		this.ricettaSessione = ricettaSessione;
	}

}
