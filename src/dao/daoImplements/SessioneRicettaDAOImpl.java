package dao.daoImplements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import dto.StatisticheRicetteDTO;
import dto.SessioneRicettaDTO;
import dto.RicettaDTO;
import dao.daoImplements.*;

import dao.daoInterfaces.SessioneRicettaDAOInt;
import db_connection.db_connection;

public class SessioneRicettaDAOImpl implements SessioneRicettaDAOInt{
	
	// INSERT newAssociazione
	@Override
	public void insertNewAssociazione(SessioneRicettaDTO associazioneSessioneRicetta) throws SQLException{
		String sql = "INSERT INTO sessione_ricetta (fksessioneinpresenza, fkricetta) VALUES (?, ?)";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, associazioneSessioneRicetta.getSessioneRicetta().getIdSessione());
			ps.setInt(2, associazioneSessioneRicetta.getRicettaSessione().getId());
			ps.executeUpdate();
		}
	}
	
	// READ (SELECT) all ricette by idSessione
	@Override
    public List<RicettaDTO> getAllRicetteByIdSessione(int idSessioneInPresenza) throws SQLException{
        String sql = "SELECT r.idricetta, r.nomericetta, r.tempopreparazione, r.porzioni, r.difficolta "
                   + "FROM ricetta r "
                   + "JOIN sessione_ricetta sr ON r.idricetta = sr.fk_ricetta "
                   + "WHERE sr.fk_sessione = ?"; 

        List<RicettaDTO> elencoRicetteSessione = new ArrayList<>();

        try(Connection conn = db_connection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idSessioneInPresenza);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                RicettaDTO ricetta = new RicettaDTO();
                ricetta.setId(rs.getInt("idricetta"));
                ricetta.setNomeRicetta(rs.getString("nomericetta"));
                ricetta.setTempoPreparazione(rs.getInt("tempopreparazione"));
                ricetta.setPorzioni(rs.getInt("porzioni"));
                ricetta.setDifficolta(rs.getString("difficolta"));
                elencoRicetteSessione.add(ricetta);
            }
        }
        return elencoRicetteSessione;
    }
	
	// DELETE associazioni by idSessione
	@Override
	public void deleteAssociazioniByIdSessione(int idSessioneInPresenza) throws SQLException{
		String deleteSql = "DELETE FROM sessione_ricetta WHERE fksessioneinpresenza = ?";
		
		try(Connection conn = db_connection.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)){
			deleteStmt.setInt(1, idSessioneInPresenza);
			deleteStmt.executeUpdate();
		}
	}
	
	//METODO PER LE STATISTICHE DELLE RICETTE
	@Override
	public StatisticheRicetteDTO getStatisticheRicette(int idChef, int mese, int anno) throws SQLException {
        String sql = "SELECT "
                   + "    COALESCE(MIN(conteggio_ricette), 0) AS min_ricette, "
                   + "    COALESCE(MAX(conteggio_ricette), 0) AS max_ricette, "
                   + "    COALESCE(AVG(conteggio_ricette), 0.0) AS avg_ricette "
                   + "FROM ( "
                   + "    SELECT "
                   + "        s.idsessione, "
                   + "        COUNT(sr.fkricetta) AS conteggio_ricette "
                   + "    FROM "
                   + "        sessione s "
                   + "    JOIN "
                   + "        corso c ON s.fkcorso = c.idcorso "
                   + "    JOIN "
                   + "        sessioneinpresenza sip ON s.idsessione = sip.fksessione "
                   + "    LEFT JOIN "
                   + "        sessioneInp_ricetta sr ON s.idsessione = sr.fksessioneinpresenza "
                   + "    WHERE "
                   + "        c.fkchef = ? "
                   + "        AND s.tipoSessione = 'presenza' " // Assicura sia in presenza
                   + "        AND EXTRACT(MONTH FROM s.datasessione) = ? "
                   + "        AND EXTRACT(YEAR FROM s.datasessione) = ? "
                   + "    GROUP BY "
                   + "        s.idsessione "
                   + ") AS conteggi_sessioni";

        try (Connection conn = db_connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idChef);
            ps.setInt(2, mese);
            ps.setInt(3, anno);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StatisticheRicetteDTO stats = new StatisticheRicetteDTO();
                    stats.setMin(rs.getInt("min_ricette"));
                    stats.setMax(rs.getInt("max_ricette"));
                    stats.setAvg(rs.getDouble("avg_ricette"));
                    return stats;
                }
                else{
                    return new StatisticheRicetteDTO(0, 0, 0.0); //se da problemi cambia in null
                }
            }
        }
}

}
