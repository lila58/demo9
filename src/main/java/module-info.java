module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // Le module java.desktop est requis pour AWT (Point, etc.)
    // Il contient souvent ce qu'il faut.
    requires java.desktop;
    requires jbcrypt;
    requires javafx.graphics;
    requires javafx.base;



    // Dépendances pour les bibliothèques tierces si utilisées (ex: jbcrypt)
    // requires je bcrypt;

    opens org.example.demo9 to javafx.fxml;
    exports org.example.demo9;
    
    // Vos exports de packages
    exports controller;
    opens controller to javafx.fxml;
    
    exports database;
    opens database to javafx.fxml;
    
    exports model;
    opens model to javafx.fxml;
    
    exports utils;
    opens utils to javafx.fxml;

    // Exports pour les niveaux
    exports niveau1;
    opens niveau1 to javafx.fxml;
    
    exports niveau2;
    opens niveau2 to javafx.fxml;
    
    exports niveau3;
    opens niveau3 to javafx.fxml;
}