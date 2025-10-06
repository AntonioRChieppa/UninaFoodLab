package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessioneOnlineDTO extends SessioneDTO{
	
	// ATTRIBUTI
	private String linkConferenza;
	
	// DEFAULT CONSTRUCTOR
	public SessioneOnlineDTO() {
		super();
	}
	
	// SPECIFIC CONSTRUCTOR
	public SessioneOnlineDTO(int idSessione, String argomento, LocalTime oraInizio, LocalDate dataSessione, CorsoDTO corsoSessione, String linkConferenza) {
		super(idSessione, argomento, oraInizio, dataSessione, corsoSessione);
		this.linkConferenza = linkConferenza;
	}
	
	// GETTER E SETTER
	public String getLinkConferenza() {
		return linkConferenza;
	}

	public void setLinkConferenza(String linkConferenza) {
		this.linkConferenza = linkConferenza;
	}
	
	

}
