package dao.daoImplements;

import dto.RicettaDTO;
import dto.IngredienteDTO;
import db_connection.db_connection;
import dao.daoInterfaces.RicettaDAOInt;

import java.sql.*;
import java.util.*;

public class RicettaDAOImpl implements RicettaDAOInt{
	
	//Insert newRicetta
	@Override
	public void insertRicetta(RicettaDTO ricetta) throws SQLException{
		String sql = "INSERT INTO ricetta (nomericetta, tempopreparazione, porzioni, difficolta) VALUES (?,?,?,?)" ;
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString (1, ricetta.getNomeRicetta());
			ps.setInt (2, ricetta.getTempoPreparazione());
			ps.setInt (3, ricetta.getPorzioni());
			ps.setString (4, ricetta.getDifficolta());
			ps.executeUpdate();
		}
	}
	
	//UPDATE newRicetta - LOGICA COALESCE
	@Override
	public void updateRicetta(RicettaDTO ricetta) throws SQLException{
		String sql = "UPDATE ricetta SET "
				+"nomericetta = COALESCE(?,nomericetta), " 
				+"tempopreparazione = COALESCE(?,tempopreparazione), "
				+"porzioni =  COALESCE(?,porzioni),"
				+"difficolta = COALESCE(?,difficolta), "
				+"WHERE idricetta = ?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString (1, ricetta.getNomeRicetta());
			
			if(ricetta.getTempoPreparazione() == null) {
			ps.setNull (2, java.sql.Types.INTEGER);
			}
			else {
				ps.setInt(2, ricetta.getTempoPreparazione());
			}
			if(ricetta.getPorzioni() == null) {
			ps.setNull (3, java.sql.Types.INTEGER);
			}
			else {
				ps.setInt(3,ricetta.getPorzioni());
			}
			ps.setString(4, ricetta.getDifficolta());
			ps.setInt(5,ricetta.getId());
			
			ps.executeUpdate();
		}
	}
	
	//DELETE Ricetta
	@Override
	public void deleteRicetta(RicettaDTO ricetta) throws SQLException {
		String deleteSql = "DELETE FROM ricetta WHERE idricetta = ?";
		String countSql = "SELECT COUNT(*) FROM ricetta";
		String resetSqlId = "ALTER SEQUENCE ricetta_idricetta_seq RESTART WITH 1";
		
		Connection conn = db_connection.getConnection(); PreparedStatement deleteStatement = conn.prepareStatement(deleteSql);
		Statement checkStatement = conn.createStatement();
		
		deleteStatement.setInt(1,ricetta.getId());
		deleteStatement.executeUpdate();
		
		ResultSet rs = checkStatement.executeQuery(countSql);
		if(rs.next() && rs.getInt(1)==0) {
			checkStatement.executeUpdate(resetSqlId);
		}
	}
	
	// READ (SELECT) Ricetta by Name
	@Override
    public RicettaDTO getRicettaByName(String nomeRicetta) throws SQLException {
        String sql = "SELECT idricetta, nomericetta, tempopreparazione, porzioni, difficolta "
                   + "FROM ricetta "
                   + "WHERE nomericetta = ?"; 

        try (Connection conn = db_connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomeRicetta); 
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RicettaDTO ricetta = new RicettaDTO(); 
                ricetta.setId(rs.getInt("idricetta"));
                ricetta.setNomeRicetta(rs.getString("nomericetta"));
                ricetta.setTempoPreparazione(rs.getInt("tempopreparazione"));
                ricetta.setPorzioni(rs.getInt("porzioni"));
                ricetta.setDifficolta(rs.getString("difficolta"));
                return ricetta; 
            }
            else {
            	return null;
            } 
        } 
    }
	
	
	//READ (SELECT) Ricetta by IdChef
	@Override 
    public List<RicettaDTO> getAllRicetteByIdChef(int idChef) throws SQLException {
        String sql = "SELECT DISTINCT r.idricetta, r.nomericetta, r.tempopreparazione, r.porzioni, r.difficolta "
                   + "FROM ricetta r "
                   + "JOIN sessione_ricetta sr ON r.idricetta = sr.fk_ricetta "
                   + "JOIN sessione s ON sr.fk_sessione = s.idsessione "
                   + "JOIN corso c ON s.fkcorso = c.idcorso "
                   + "WHERE c.fkchef = ?";

        List<RicettaDTO> elencoRicetteChef = new ArrayList<>();

        try (Connection conn = db_connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idChef);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RicettaDTO ricetta = new RicettaDTO();
                ricetta.setId(rs.getInt("idricetta"));
                ricetta.setNomeRicetta(rs.getString("nomericetta"));
                ricetta.setTempoPreparazione(rs.getInt("tempopreparazione"));
                ricetta.setPorzioni(rs.getInt("porzioni"));
                ricetta.setDifficolta(rs.getString("difficolta"));
                elencoRicetteChef.add(ricetta);
            }
        }
        return elencoRicetteChef;
    }
	
	//READ (SELECT) all recipe
	@Override
	public List<RicettaDTO> getAllRecipes() throws SQLException{
		String sql = "SELECT * FROM ricetta";
		List<RicettaDTO> listaricette = new ArrayList<>();
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			RicettaDTO ricetta = new RicettaDTO();
			ricetta.setId(rs.getInt ("idRicetta"));
			ricetta.setNomeRicetta(rs.getString ("nomeRicetta"));
			ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
			ricetta.setPorzioni(rs.getInt("porzioni"));
			ricetta.setDifficolta(rs.getString("difficolta"));
			
			listaricette.add(ricetta);
		}
		
		}
	return listaricette;
	}
	
	@Override
	public RicettaDTO getRicettaById(int id) throws SQLException{
		String sql = "SELECT * FROM ricetta WHERE idRicetta=?";
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				RicettaDTO ricetta = new RicettaDTO();
				ricetta.setNomeRicetta(rs.getString("nomeRicetta"));
				ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
				ricetta.setPorzioni(rs.getInt("porzioni"));
				ricetta.setDifficolta(rs.getString("difficoltà"));
				return ricetta;
			}
			else {
				return null;
			}
		}			
	}
	/*
	public Map<Integer, Integer> getStatisticheRicetteMensili(int anno, int mese) {
        
        List<Integer> statisticheGiornaliere = new ArrayList<>();
        
        String sql = "SELECT DAY(data_creazione) AS Giorno, COUNT(*) AS TotaleRicette " +
                     "FROM Ricetta " +
                     "WHERE YEAR(data_creazione) = ? AND MONTH(data_creazione) = ? " +
                     "GROUP BY Giorno " +
                     "ORDER BY Giorno ASC";

        // Usa try-with-resources per chiudere automaticamente la connessione
        // Assicurati di avere la tua classe per ottenere la connessione (es. ConnectionManager.getInstance().getConnection())
        
        try (Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, anno);
            ps.setInt(2, mese);

            ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    int giorno = rs.getInt("Giorno");
                    int totale = rs.getInt("TotaleRicette");
                    
                    
                    statisticheGiornaliere.add();
                }
            }
            */
}
