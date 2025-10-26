package dto;

import java.time.*;

public abstract class SessioneDTO {

		// ATTRIBUTI
		private int idSessione;
		private String argomento;
		private LocalTime oraInizio;
		private LocalDate dataSessione;
		private CorsoDTO corsoSessione;
		private String tipoSessione;

		// DEFAULT CONSTRUCTOR
		public SessioneDTO() {
			// TODO Auto-generated constructor stub
		}
		
		// SPECIFIC CONSTRUCTOR
		public SessioneDTO(int idSessione, String argomento, LocalTime oraInizio, LocalDate dataSessione, CorsoDTO corsoSessione) {
			this.idSessione = idSessione;
			this.argomento = argomento;
			this.oraInizio = oraInizio;
			this.dataSessione = dataSessione;
			this.corsoSessione = corsoSessione;
		}
		
		// GETTER E SETTER
		public int getIdSessione() {
			return idSessione;
		}

		public void setIdSessione(int idSessione) {
			this.idSessione = idSessione;
		}

		public String getArgomento() {
			return argomento;
		}

		public void setArgomento(String argomento) {
			this.argomento = argomento;
		}

		public LocalTime getOraInizio() {
			return oraInizio;
		}

		public void setOraInizio(LocalTime oraInizio) {
			this.oraInizio = oraInizio;
		}

		public LocalDate getDataSessione() {
			return dataSessione;
		}

		public void setDataSessione(LocalDate dataSessione) {
			this.dataSessione = dataSessione;
		}

		public CorsoDTO getCorsoSessione() {
			return corsoSessione;
		}

		public void setCorsoSessione(CorsoDTO corsoSessione) {
			this.corsoSessione = corsoSessione;
		}
		
		public String getTipoSessione() {
			return tipoSessione;
		}

		public void setTipoSessione(String tipoSessione) {
			this.tipoSessione = tipoSessione;
		}
}
