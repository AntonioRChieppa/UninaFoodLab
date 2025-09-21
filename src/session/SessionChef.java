package session;

// CLASSE CHE SIMULA UNA SESSIONE PER SALVARE E UTILIZZARE IN QUALSIASI PUNTO DEL PROGRAMMA L'ID DELLO CHEF AUTENTICATO
public class SessionChef {

	private static int chefId; //valore condiviso da tutte le istanze della classe
	
	public static void setChefId(int id) {
		chefId = id;
	}
	
	public static int getChefId() {
		return chefId;
	}
}
