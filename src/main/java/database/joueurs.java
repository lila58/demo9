package database;

import model.joueur;
import model.score;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class joueurs {
    ArrayList<joueur> joueurs;

    public joueurs() throws SQLException {

        Connection conn = DBConfig.getConnection();
        if(conn == null) return;

        joueurs = new ArrayList<>();
        String sql = "SELECT * FROM users JOIN score ON users.id = score.id_user";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int lastUserId = -1;
            joueur currentJoueur = null;

            while(rs.next()){
               String pseudo = rs.getString("prenom");
               int id = rs.getInt("id");
               if(lastUserId != id){
                   currentJoueur = new joueur(id, pseudo, new ArrayList<>());
                   joueurs.add(currentJoueur);
                   lastUserId = id;
               }
                int niveau = rs.getInt("niveau");
                int meilleur_score = rs.getInt("meilleur_score");
                int scoreValeur = rs.getInt("dernier_score");

                currentJoueur.getScores().add(
                        new score(id,niveau, scoreValeur, meilleur_score)
                );

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public ArrayList<joueur> getJoueurs(int niveau) {
        ArrayList<joueur> result = new ArrayList<>();

        for (joueur j : joueurs) {
            for (score s : j.getScores()) {
                if (s.getNiveau() == niveau) {
                    result.add(j);
                    break;
                }
            }
        }

        return result;
    }

    public ArrayList<joueur> getJoueurs(){
        return joueurs;
    }

}
