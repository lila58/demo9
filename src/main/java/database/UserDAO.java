package database;

import utils.BCryptUtil;

import java.sql.*;

public class UserDAO{


    //INSCRIPTION
    public int insererUtilisateur(String nom, String prenom, String mail, String mdp){

        String sql = "INSERT INTO users(nom, prenom, mail, mdp, role) VALUES(?, ?, ?, ?, 0)";

        try(Connection conn = DBConfig.getConnection();



        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hash = BCryptUtil.hasher(mdp);

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, mail);
            ps.setString(4, hash);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    //VERIFICATION MAIL
    public boolean mailExiste(String mail){
        String sql = "SELECT id FROM users WHERE mail = ?";

        try(Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, mail);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // ✅ CONNEXION
    public int verifierConnexion(String mail, String mdp){

        String sql = "SELECT id, mdp FROM users WHERE mail = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mail);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                String hash = rs.getString("mdp");

                if(BCryptUtil.verifierMotDePasse(mdp, hash)){
                    return rs.getInt("id");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }

    //RÉCUPÉRER LE prénom pour l’afficher dans le jeu
    public String getPrenomById(int idUser) {

        String sql = "SELECT prenom FROM users WHERE id = ?";

        try(Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getString("prenom");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }
    //RECUPERER LE ROLE D'UN UTILISATEUR
    public int getRoleById(int idUser){
        String sql = "SELECT role FROM users WHERE id = ?";
        try(Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt("role");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return -1;
    }


    //SAUVEGARDER LE SCORE
    public void sauvegarderScore(int idUser, int niveau, int score){

        String req = "SELECT id_score, meilleur_score FROM score WHERE id_user = ? AND niveau = ?";
        String insert = "INSERT INTO score (id_user, niveau, meilleur_score, dernier_score) VALUES (?, ?, ?, ?)";
        String res = "UPDATE score SET meilleur_score = ?, dernier_score = ? WHERE id_score = ?";

        try(Connection conn = DBConfig.getConnection();
            PreparedStatement checkPs = conn.prepareStatement(req)){

            checkPs.setInt(1, idUser);
            checkPs.setInt(2, niveau);
            ResultSet rs = checkPs.executeQuery();

            if(rs.next()){
                int idScore = rs.getInt("id_score");
                int meilleurScore = rs.getInt("meilleur_score");

                if(score > meilleurScore){
                    meilleurScore = score;
                }

                try(PreparedStatement updatePs = conn.prepareStatement(res)){
                    updatePs.setInt(1, meilleurScore); // meilleur_score
                    updatePs.setInt(2, score);          // dernier_score
                    updatePs.setInt(3, idScore);
                    updatePs.executeUpdate();
                }

            }else{
                try(PreparedStatement insertPs = conn.prepareStatement(insert)){
                    insertPs.setInt(1, idUser);
                    insertPs.setInt(2, niveau);
                    insertPs.setInt(3, score); // meilleur_score = score
                    insertPs.setInt(4, score); // dernier_score = score
                    insertPs.executeUpdate();
                }
            }

            System.out.println("Score sauvegardé avec succès");

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //RÉCUPÉRER LE EILLEUR SCORE
    public int getMeilleurScore(int idUser, int niveau){

        String sql = "SELECT meilleur_score FROM score WHERE id_user = ? AND niveau = ?";

        try(Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idUser);
            ps.setInt(2, niveau);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt("meilleur_score");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }
}

