package dto;

import dto.IngredienteDTO;
import dto.SessioneDTO;
import java.util.List;
import java.util.ArrayList;

public class RicettaDTO {

	//ATTRIBUTI
	private int id;
	private String nomeRicetta;
	private Integer tempoPreparazione;
	private Integer porzioni;
	private String difficolta;
	private SessioneDTO sessioneRicetta;
	private IngredienteDTO ingredienteRicetta; // added missing field
	
	
	//COSTRUTTORE DI DEFAULT
	public RicettaDTO () {
	
	}

	//COSTRUTTORE SPECIFICO
	public RicettaDTO(int id, String nomeRicetta, Integer tempoPreparazione, Integer porzioni, String difficolta, SessioneDTO sessioneRicetta, IngredienteDTO ingredienteRicetta) {
		this.id = id;
		this.nomeRicetta = nomeRicetta;
		this.tempoPreparazione = tempoPreparazione;
		this.porzioni = porzioni;
		this.difficolta = difficolta;
		this.sessioneRicetta = sessioneRicetta;
		this.ingredienteRicetta = ingredienteRicetta;
	}
	
	//GETTER E SETTER
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNomeRicetta() {
		return nomeRicetta;
	}
	
	public void setNomeRicetta(String nomeRicetta) {
		this.nomeRicetta = nomeRicetta;
	}
	
	public Integer getTempoPreparazione() {
		return tempoPreparazione;
	}
	
	public void setTempoPreparazione(Integer tempoPreparazione) {
		this.tempoPreparazione = tempoPreparazione;
	}
	
	public Integer getPorzioni() {
		return porzioni;
	}
	
	public void setPorzioni(Integer porzioni) {
		this.porzioni = porzioni;
	}
	
	public String getDifficolta() {
		return difficolta;
	}
	
	public void setDifficolta(String difficolta) {
		this.difficolta = difficolta;
	}
	
	public SessioneDTO getSessioneRicetta(){
	 	return sessioneRicetta;
	}
	
	public void setSessioneRicetta(SessioneDTO sessioneRicetta){
		this.sessioneRicetta = sessioneRicetta;
	}
	
	public IngredienteDTO getIngredienteRicetta() {
		return ingredienteRicetta;
	}
	
	public void setIngredienteRicetta(IngredienteDTO ingredienteRicetta) {
		this.ingredienteRicetta = ingredienteRicetta;
	}
}