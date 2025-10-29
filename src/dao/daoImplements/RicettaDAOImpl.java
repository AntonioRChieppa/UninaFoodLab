package dao.daoImplements;

import dto.RicettaDTO;
import db_connection.db_connection;
import dao.daoInterfaces.RicettaDAOInt;

import java.sql.*;
import java.util.*;

public class RicettaDAOImpl implements RicettaDAOInt{
	
	//READ (SELECT) Ricetta by IdChef
	@Override 
    public List<RicettaDTO> getAllRicetteByIdChef(int idChef) throws SQLException {
        String sql = "SELECT DISTINCT r.idricetta, r.nomericetta, r.tempopreparazione, r.porzioni, r.difficolta "
                   + "FROM ricetta r "
                   + "JOIN sessioneinp_ricetta sr ON r.idricetta = sr.fkricetta "
                   + "JOIN sessione s ON sr.fksessioneinpresenza = s.idsessione "
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
			ricetta.setId(rs.getInt ("idricetta"));
			ricetta.setNomeRicetta(rs.getString ("nomericetta"));
			ricetta.setTempoPreparazione(rs.getInt("tempopreparazione"));
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
				ricetta.setId(rs.getInt("idricetta"));
				ricetta.setNomeRicetta(rs.getString("nomeRicetta"));
				ricetta.setTempoPreparazione(rs.getInt("tempoPreparazione"));
				ricetta.setPorzioni(rs.getInt("porzioni"));
				ricetta.setDifficolta(rs.getString("difficolta"));
				return ricetta;
			}
			else {
				return null;
			}
		}			
	}
	
}
