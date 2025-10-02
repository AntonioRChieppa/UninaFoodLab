package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessioneInPresenzaDTO extends SessioneDTO{

		private String sede;
		private String edificio;
		private String aula;
		
		// DEFAULT CONSTRUCTOR
		public SessioneInPresenzaDTO() {
			super();
		}
		
		// SPECIFIC CONSTRUCTOR
		public SessioneInPresenzaDTO(int idSessione, int durataSessione, LocalTime oraInizio, LocalDate dataSessione, CorsoDTO corsoSessione, String sede, String edificio, String aula) {
			super(idSessione, durataSessione, oraInizio, dataSessione, corsoSessione);
			this.sede = sede;
			this.edificio = edificio;
			this.aula = aula;
		}
		
		// GETTER E SETTER
		public String getSede() {
			return sede;
		}

		public void setSede(String sede) {
			this.sede = sede;
		}

		public String getEdificio() {
			return edificio;
		}

		public void setEdificio(String edificio) {
			this.edificio = edificio;
		}

		public String getAula() {
			return aula;
		}

		public void setAula(String aula) {
			this.aula = aula;
		}
		
		
}
