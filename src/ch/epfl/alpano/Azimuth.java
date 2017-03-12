package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.floorMod;


/**
 * Fournit des opérations concernant l'azimut.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Azimuth {
    
    
    /**
     * Vérifie si l'azimut est canonique (compris entre 0 et 2Pi).
     * 
     * @param azimuth
     *          un azimut
     *          
     * @return si l'azimut est canonique ou non
     */
    static boolean isCanonical(double azimuth) {
        return 0 <= azimuth && azimuth < PI2;
    }
    
    
    /**
     * Transforme l'azimut en valeur canonique.
     * 
     * @param azimuth
     *          un azimut
     *          
     * @return la valeur canonique de l'azimut
     */
    static double canonicalize(double azimuth) {
        return floorMod(azimuth, PI2);
    }
    
    
    /**
     * Transforme un azimut en angle mathématique (sens antihoraire).
     * 
     * @param azimuth
     *          un azimut
     *          
     * @return l'angle mathématique correspondant à l'azimut
     */
    static double toMath(double azimuth) throws IllegalArgumentException {
        checkArgument(isCanonical(azimuth)
                , "The argument is not in canonical form.");
        return canonicalize(PI2 - azimuth);
    }
    
    
    /**
     * Transforme un angle mathématique en azimut (sens horaire).
     * 
     * @param angle
     *          un angle mathématique
     *          
     * @return l'azimut correspondant à l'angle mathématique
     */
    static double fromMath(double angle) throws IllegalArgumentException {
        return toMath(angle);
    }
    
    
    /**
     * Retourne une chaîne de caractères correspondant à l'octant
     * dans lequel se trouve l'azimut.
     * 
     * @param azimuth
     *          un azimut
     * @param n
     *          chaîne de caractère pour le nord
     * @param e
     *          chaîne de caractère pour l'est
     * @param s
     *          chaîne de caractère pour le sud
     * @param w
     *          chaîne de caractère pour l'ouest
     *          
     * @return la chaîne de caractère correspondant à l'octant
     *         dans lequel se trouve l'azimut
     */
    static String toOctantString(double azimuth, String n, String e, String s, String w) {
        checkArgument(isCanonical(azimuth)
                , "The given azimuth is not in canonical form.");
        
        StringBuilder answer   = new StringBuilder();
        final double  distance = 3*PI/8;
        
        if(abs(angularDistance(azimuth, 0)) < distance)
            answer.append(n);
        else if(abs(angularDistance(azimuth, PI)) < distance)
            answer.append(s);
        
        if(abs(angularDistance(azimuth, PI/2)) < distance)
            answer.append(e);
        else if(abs(angularDistance(azimuth, 3*PI/2)) < distance)
            answer.append(w);
        
        return answer.toString();
    }
    
}
