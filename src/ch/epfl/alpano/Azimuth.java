package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;


/**
 * Fournit des opérations concernant l'azimut
 * 
 * @author Robin Mamie (257234)
 * @autjor Maxence Jouve
 */
public interface Azimuth {
    
    
    /**
     * Vérifie si l'azimut est canonique (compris entre 0 et 2Pi).
     * @param azimuth un azimut
     * @return si l'azimut est canonique ou non
     */
    static boolean isCanonical(double azimuth) {
        return (0 <= azimuth) && (azimuth < Math2.PI2);
    }
    
    
    /**
     * Transforme l'azimut en valeur canonique.
     * @param azimuth un azimut
     * @return la valeur canonique de l'azimut
     */
    static double canonicalize(double azimuth) {
        return Math2.floorMod(azimuth, Math2.PI2);
    }
    
    
    /**
     * Transforme un azimut en angle mathématique (sens antihoraire).
     * @param azimuth un azimut
     * @return l'angle mathématique correspondant à l'azimut
     * @throws IllegalArgumentException si l'azimut n'est pas canonique
     */
    static double toMath(double azimuth) {
        checkArgument(isCanonical(azimuth), "argument non canonical");
        return canonicalize(Math2.PI2 - azimuth);
    }
    
    
    /**
     * Transforme un angle mathématique en azimut (sens horaire).
     * @param angle un angle mathématique
     * @return l'azimut correspondant à l'angle mathématique
     * @throws IllegalArgumentException si l'angle n'est pas compris entre 0 et 2Pi exclu
     */
    static double fromMath(double angle) {
        return toMath(angle);
    }
    
    
    /**
     * Retourne une chaîne de caractères correspondant à l'octant
     * dans lequel se trouve l'azimut.
     * @param azimuth un azimut
     * @param n chaîne de caractère pour le nord
     * @param e chaîne de caractère pour l'est
     * @param s chaîne de caractère pour le sud
     * @param w chaîne de caractère pour l'ouest
     * @return la chaîne de caractère correspondant à l'octant
     * dans lequel se trouve l'azimut
     * @throw IllegalArgumentException si l'azimut n'est pas canonique
     */
    static String toOctantString(double azimuth, String n, String e, String s, String w) {
        checkArgument(isCanonical(azimuth), "azimuth non canonical");
        String answer = "";
        final double distance = 3*Math.PI/8;
        if(Math.abs(Math2.angularDistance(azimuth, 0)) < distance)
            answer += n;
        else if(Math.abs(Math2.angularDistance(azimuth, Math.PI)) < distance)
            answer += s;
        
        if(Math.abs(Math2.angularDistance(azimuth, Math.PI/2)) < distance)
            answer += e;
        else if(Math.abs(Math2.angularDistance(azimuth, 3*Math.PI/2)) < distance)
            answer += w;
        return answer;
    }
    
}
