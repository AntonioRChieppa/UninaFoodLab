package dto;

public class DatiReportMensileDTO {

    private final int corsiTotali;
    private final int sessioniOnline;
    private final int sessioniInPresenza;
    private final double mediaRicette;
    private final int maxRicette;
    private final int minRicette;
    private final boolean datiDisponibili;

    // Costruttore per quando ci sono dati
    public DatiReportMensileDTO(int corsiTotali, int sessioniOnline, int sessioniInPresenza, double mediaRicette, int maxRicette, int minRicette) {
        this.corsiTotali = corsiTotali;
        this.sessioniOnline = sessioniOnline;
        this.sessioniInPresenza = sessioniInPresenza;
        this.mediaRicette = mediaRicette;
        this.maxRicette = maxRicette;
        this.minRicette = minRicette;
        this.datiDisponibili = true;
    }
    
    // Costruttore per quando non ci sono dati
    public DatiReportMensileDTO() {
        this.corsiTotali = 0;
        this.sessioniOnline = 0;
        this.sessioniInPresenza = 0;
        this.mediaRicette = 0;
        this.maxRicette = 0;
        this.minRicette = 0;
        this.datiDisponibili = false;
    }

    // Getters
    public int getCorsiTotali() {
    	return corsiTotali; 
    }
    
    public int getSessioniOnline() {
    	return sessioniOnline; 
    }
    
    
    public int sessioniInPresenza() { 
    	return sessioniInPresenza; 
    }
    
    
    public double getMediaRicette() {
    	return mediaRicette; 
    }
    
    
    public int getMaxRicette() {
    	return maxRicette; 
    }
    
    
    public int getMinRicette() {
    	return minRicette; 
    }
    
    
    public boolean isDatiDisponibili() {
    	return datiDisponibili; 
    }
}