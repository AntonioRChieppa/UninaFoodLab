package dto;

import java.time.*;

public abstract class SessioneDTO {

		// ATTRIBUTI
		private int idSessione;
		private int durataSessione;
		private LocalTime oraInizio;
		private LocalDate dataSessione;
		private CorsoDTO corsoSessione;
		
		// DEFAULT CONSTRUCTOR
		public SessioneDTO() {
			// TODO Auto-generated constructor stub
		}
		
		// SPECIFIC CONSTRUCTOR
		public SessioneDTO(int idSessione, int durataSessione, LocalTime oraInizio, LocalDate dataSessione, CorsoDTO corsoSessione) {
			this.idSessione = idSessione;
			this.durataSessione = durataSessione;
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

		public int getDurataSessione() {
			return durataSessione;
		}

		public void setDurataSessione(int durataSessione) {
			this.durataSessione = durataSessione;
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
}
