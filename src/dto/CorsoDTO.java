package dto;

import java.time.LocalDate;

public class CorsoDTO {

		// ATTRIBUTI
		private int id;
		private String nomeCorso;
		private String categoria;
		private LocalDate dataInizio;
		private String frequenzaSessioni;
		private Integer numeroSessioni; //USIAMO LA CLASSE WRAPPER COSì DA POTER GESTIRE numeroSessioni = NULL
		private ChefDTO chefCorso;
		
		
		// DEFAULT CONSTRUCTOR
		public CorsoDTO() {
			// TODO Auto-generated constructor stub
		}
		
		// SPECIFIC CONSTRUCTOR
		public CorsoDTO(int id, String nomeCorso, String argomento, LocalDate dataInizio, LocalDate dataFine, int anno, ChefDTO chefCorso) {
			this.id = id;
			this.nomeCorso = nomeCorso;
			this.categoria = argomento;
			this.dataInizio = dataInizio;
			this.numeroSessioni = anno;
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
		
		public String getCategoria() {
			return categoria;
		}
		
		public void setCategoria(String categoria) {
			this.categoria = categoria;
		}
		
		public LocalDate getDataInizio() {
			return dataInizio;
		}

		public void setDataInizio(LocalDate dataInizio) {
			this.dataInizio = dataInizio;
		}
		
		public String getFrequenzaSessioni() {
			return frequenzaSessioni;
		}
		
		public void setFrequenzaSessioni(String frequenzaSessioni) {
			this.frequenzaSessioni = frequenzaSessioni;
		}
		
		public Integer getNumeroSessioni() {
			return numeroSessioni;
		}
		
		public void setNumeroSessioni(Integer numeroSessioni) {
			this.numeroSessioni = numeroSessioni;
		}
		
		public ChefDTO getChefCorso() {
			return chefCorso;
		}
		
		public void setChefCorso(ChefDTO chefCorso) {
			this.chefCorso = chefCorso;
		}
		
}
