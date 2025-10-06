package dto;

public class IngredienteDTO {

	//ATTRIBUTI
	private int id;
	private String nomeIngrediente;
	private String tipologia;
	
	//COSTRUTTORE DEFAULT
		public IngredienteDTO() {
		
		}
	
	//COSTRUTTORE SPECIFICO
	public IngredienteDTO(int id, String nomeIngrediente, String tipologia) {
		this.id = id;
		this.nomeIngrediente = nomeIngrediente;
		this.tipologia = tipologia;
	}
	
	//GETTER E SETTER
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNomeIngrediente() {
		return nomeIngrediente;
	}
	
	public void setNomeIngrediente(String nomeIngrediente) {
		this.nomeIngrediente = nomeIngrediente;
	}
	
	public String getTipologia() {
		return tipologia;
	}
	
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	
}
