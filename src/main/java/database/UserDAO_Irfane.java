//fichier pour supprimer ou modifier un joueur et mettre a jour le score
package database;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO_Irfane {

    public List<User> findAllUsersWithScore() {
        List<User> users = new ArrayList<>();
        // On récupère le meilleur ET le dernier score
        String sql = "SELECT u.*, " +
                "COALESCE(s.meilleur_score, 0) AS meilleur, " +
                "COALESCE(s.dernier_score, 0) AS dernier " +
                "FROM users u " +
                "LEFT JOIN score s ON u.id = s.id_user AND s.niveau = 1 " +
                "ORDER BY meilleur DESC";

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("mail"),
                        rs.getString("mdp"),
                        rs.getString("role"),
                        rs.getInt("meilleur"),
                        rs.getInt("dernier") // Récupère le dernier score
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users (nom, prenom, mail, mdp, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getMail());
            pstmt.setString(4, user.getMdp());
            pstmt.setString(5, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- NOUVELLE FONCTION : MODIFIER UN JOUEUR ---
    public void updateUser(User user) {
        String sql = "UPDATE users SET nom=?, prenom=?, mail=?, role=?, mdp=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getMail());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getMdp());
            pstmt.setInt(6, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateScore(int userId, int score) {
        // Met à jour le score sans créer de doublon (grâce à la clé UNIQUE ajoutée en SQL)
        String sql = "INSERT INTO score (id_user, niveau, meilleur_score, dernier_score) VALUES (?, 1, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "dernier_score = VALUES(dernier_score), " +
                "meilleur_score = GREATEST(meilleur_score, VALUES(meilleur_score))";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, score);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
