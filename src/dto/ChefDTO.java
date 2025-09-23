package dto;

import java.util.*;

public class ChefDTO {
	
	// ATTRIBUTI
	private int id;
	private String nome;
	private String cognome;
	private String email;
	private String password;
	private List<CorsoDTO> elencoCorsi;
	
	// COSTRUTTORE DI DEFAULT
		public ChefDTO() {
			this.elencoCorsi = new ArrayList<>();
		}
	
	// COSTRUTTORE SPECIFICO
	public ChefDTO(int id, String nome, String cognome, String email, String password) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.email = email;
		this.password = password;
		this.elencoCorsi = new ArrayList<>();
	}
	
	// GETTER E SETTER
	public int getId() {
		return id;
	}
		
	public void setId(int id) {
		this.id = id;
	}
		
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<CorsoDTO> getCorsi(){
		return elencoCorsi;
	}
	
	// METODI DI GESTIONE
	public void addCorso(CorsoDTO newCorso) {
		elencoCorsi.add(newCorso);
	}
	
	public void removeCorso(CorsoDTO newCorso) {
		elencoCorsi.add(newCorso);
	}

}
