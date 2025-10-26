package dao.daoImplements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

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
	
	

}
