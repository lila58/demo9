//fichier de stockage des donnees entree 
package model;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty mail;
    private final StringProperty mdp;
    private final StringProperty role;
    private final IntegerProperty meilleurScoreNiveau1;
    private final IntegerProperty dernierScoreNiveau1; // Nouveau champ

    // Constructeur création
    public User(String nom, String prenom, String mail, String mdp, String role) {
        this.id = new SimpleIntegerProperty(0);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.mail = new SimpleStringProperty(mail);
        this.mdp = new SimpleStringProperty(mdp);
        this.role = new SimpleStringProperty(role);
        this.meilleurScoreNiveau1 = new SimpleIntegerProperty(0);
        this.dernierScoreNiveau1 = new SimpleIntegerProperty(0);
    }

    // Constructeur complet (BDD)
    public User(int id, String nom, String prenom, String mail, String mdp, String role, int meilleurScore, int dernierScore) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.mail = new SimpleStringProperty(mail);
        this.mdp = new SimpleStringProperty(mdp);
        this.role = new SimpleStringProperty(role);
        this.meilleurScoreNiveau1 = new SimpleIntegerProperty(meilleurScore);
        this.dernierScoreNiveau1 = new SimpleIntegerProperty(dernierScore);
    }

    // Getters JavaFX
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getMail() { return mail.get(); }
    public String getRole() { return role.get(); }
    public String getMdp() { return mdp.get(); }
    public int getMeilleurScoreNiveau1() { return meilleurScoreNiveau1.get(); }
    public int getDernierScoreNiveau1() { return dernierScoreNiveau1.get(); }

    // Properties
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty mailProperty() { return mail; }
    public StringProperty roleProperty() { return role; }
    public IntegerProperty meilleurScoreNiveau1Property() { return meilleurScoreNiveau1; }
    public IntegerProperty dernierScoreNiveau1Property() { return dernierScoreNiveau1; }

    // Setters (utiles pour la modification)
    public void setNom(String n) { this.nom.set(n); }
    public void setPrenom(String p) { this.prenom.set(p); }
    public void setMail(String m) { this.mail.set(m); }
    public void setRole(String r) { this.role.set(r); }
    public void setMdp(String m) { this.mdp.set(m); }

    @Override
    public String toString() { return prenom.get() + " " + nom.get(); }
}
