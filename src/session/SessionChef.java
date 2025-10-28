package session;

// CLASSE CHE SIMULA UNA SESSIONE PER SALVARE E UTILIZZARE IN QUALSIASI PUNTO DEL PROGRAMMA L'ID DELLO CHEF AUTENTICATO
public class SessionChef {

	private static int chefId; //valori condivisi da tutte le istanze della classe
	private static String chefName;
	private static String chefSurname;
	private static String chefMail;
	private static String chefPassword;
	
	public static void setChefId(int id) {
		chefId = id;
	}
	
	public static int getChefId() {
		return chefId;
	}
	
	public static void setNameChef(String name) {
		chefName = name;
	}
	
	public static String getNameChef() {
		return chefName;
	}
	
	public static String getChefSurname() {
		return chefSurname;
	}

	public static void setChefSurname(String chefSurname) {
		SessionChef.chefSurname = chefSurname;
	}

	public static String getChefMail() {
		return chefMail;
	}

	public static void setChefMail(String chefMail) {
		SessionChef.chefMail = chefMail;
	}

	public static String getChefPassword() {
		return chefPassword;
	}

	public static void setChefPassword(String chefPassword) {
		SessionChef.chefPassword = chefPassword;
	}
}
