package database;

import model.joueur;
import model.score;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InfosJoueurDB {
    private joueur joueur;

    public InfosJoueurDB(int id) throws SQLException {
        Connection conn = DBConfig.getConnection();
        if(conn == null) return;

        String sql = "SELECT users.id AS userId, users.prenom AS pseudo, score.niveau, score.dernier_score , score.meilleur_score  " +
                "FROM users LEFT JOIN score ON users.id = score.id_user " +
                "WHERE users.id = ? ORDER BY score.niveau";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            ArrayList<score> scores = new ArrayList<>();

            String pseudo = null;
            int userId = 0;

            while(rs.next()) {
                if(pseudo == null) {
                    pseudo = rs.getString("pseudo");
                    userId = rs.getInt("userId");
                }
                int niveau = rs.getInt("niveau");
                int meilleur_score = rs.getInt("meilleur_score");
                int scoreValeur = rs.getInt("dernier_score");


                scores.add(new score(userId,niveau,scoreValeur, meilleur_score));
            }

            joueur = new joueur(userId, pseudo, scores);

            rs.close();
            pstmt.close();
            conn.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public joueur getJoueur() {
        return joueur;
    }

}

