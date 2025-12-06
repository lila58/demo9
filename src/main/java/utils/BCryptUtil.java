package utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {

    public static String hasher(String motDePasse){
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
    }

    public static boolean verifierMotDePasse(String motDePasse, String hash){
        return BCrypt.checkpw(motDePasse, hash);
    }
}
