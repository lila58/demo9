package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class requestDB {

    public void saveScore(int idJoueur, int niveau, int scoreActuel) throws SQLException {
        Connection conn = DBConfig.getConnection();
        if (conn == null) return;

        try {

            String queryCheck = "SELECT meilleur_score FROM score WHERE id_user = ? AND niveau = ?";
            PreparedStatement pstmtCheck = conn.prepareStatement(queryCheck);
            pstmtCheck.setInt(1, idJoueur);
            pstmtCheck.setInt(2, niveau);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {

                int ancienMeilleur = rs.getInt("meilleur_score");

                if (scoreActuel > ancienMeilleur) {

                    String updateBoth = "UPDATE score SET meilleur_score = ?, dernier_score = ? WHERE id_user = ? AND niveau = ?";
                    PreparedStatement pstmtUpdate = conn.prepareStatement(updateBoth);
                    pstmtUpdate.setInt(1, scoreActuel);
                    pstmtUpdate.setInt(2, scoreActuel);
                    pstmtUpdate.setInt(3, idJoueur);
                    pstmtUpdate.setInt(4, niveau);
                    pstmtUpdate.executeUpdate();
                } else {

                    String updateLast = "UPDATE score SET dernier_score = ? WHERE id_user = ? AND niveau = ?";
                    PreparedStatement pstmtUpdate = conn.prepareStatement(updateLast);
                    pstmtUpdate.setInt(1, scoreActuel);
                    pstmtUpdate.setInt(2, idJoueur);
                    pstmtUpdate.setInt(3, niveau);
                    pstmtUpdate.executeUpdate();
                }
            } else {

                String insert = "INSERT INTO score (id_user, niveau, meilleur_score, dernier_score) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtInsert = conn.prepareStatement(insert);
                pstmtInsert.setInt(1, idJoueur);
                pstmtInsert.setInt(2, niveau);
                pstmtInsert.setInt(3, scoreActuel);
                pstmtInsert.setInt(4, scoreActuel);
                pstmtInsert.executeUpdate();
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}