package dto;

import java.time.LocalDate;

public class CorsoDTO {

		// ATTRIBUTI
		private int id;
		private String nomeCorso;
		private String argomento;
		private LocalDate dataInizio;
		private LocalDate dataFine;
		private int anno;
		private ChefDTO chefCorso;
		
		
		// COSTRUTTORE DI DEFAULT
		public CorsoDTO() {
			// TODO Auto-generated constructor stub
		}
		
		// COSTRUTTORE
		public CorsoDTO(int id, String nomeCorso, String argomento, LocalDate dataInizio, LocalDate dataFine, int anno, ChefDTO chefCorso) {
			this.id = id;
			this.nomeCorso = nomeCorso;
			this.argomento = argomento;
			this.dataInizio = dataInizio;
			this.dataFine = dataFine;
			this.anno = anno;
			this.chefCorso = chefCorso;
		}
		
		// GETTER E SETTER
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getNomeCorso() {
			return nomeCorso;
		}
		
		public void setNomeCorso(String nomeCorso) {
			this.nomeCorso = nomeCorso;
		}
		
		public String getArgomento() {
			return argomento;
		}
		
		public void setArgomento(String argomento) {
			this.argomento = argomento;
		}
		
		public LocalDate getDataInizio() {
			return dataInizio;
		}

		public void setDataInizio(LocalDate dataInizio) {
			this.dataInizio = dataInizio;
		}

		public LocalDate getDataFine() {
			return dataFine;
		}

		public void setDataFine(LocalDate dataFine) {
			this.dataFine = dataFine;
		}
		
		public int getAnno() {
			return anno;
		}
		
		public void setAnno(int anno) {
			this.anno = anno;
		}
		
		public ChefDTO getChefCorso() {
			return chefCorso;
		}
		
		public void setChefCorso(ChefDTO chefCorso) {
			this.chefCorso = chefCorso;
		}
		
}
