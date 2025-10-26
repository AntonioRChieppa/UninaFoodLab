package dto;

public class ComposizioneDTO {
	
	
		//ATTRIBUTI
		private IngredienteDTO ingredienteRicetta;
		private RicettaDTO ricettaIngrediente;

		
		//COSTRUTTORE DI DEFAULT
		public ComposizioneDTO() {
		}
		
	
		//COSTRUTTORE SPECIFICO
		public ComposizioneDTO(IngredienteDTO ingredienteRicetta, RicettaDTO ricettaIngrediente) {
			this.ingredienteRicetta = ingredienteRicetta;
			this.ricettaIngrediente = ricettaIngrediente;
		}
		
		
		//GETTER E SETTER
		public IngredienteDTO getIngredienteRicetta() {
			return ingredienteRicetta;
		}
		
		public void setIngredienteRicetta(IngredienteDTO ingredienteRicetta) {
			this.ingredienteRicetta = ingredienteRicetta;
		}
		
		public RicettaDTO getRicettaIngrediente() {
			return ricettaIngrediente;
		}
		
		public void setRicettaIngrediente(RicettaDTO ricettaIngrediente) {
			this.ricettaIngrediente = ricettaIngrediente;
		}
		
}
