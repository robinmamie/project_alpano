package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;


/**
 * Fait le lien entre angles et distances terrestres.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Distance {
    
    
    /**
     * Rayon de la terre.
     */
    double EARTH_RADIUS = 6_371_000;
    
    
    /**
     * Transforme une distance en mètre inscrite dans un grand cercle en radians.
     * @param distanceInMeters distance en mètres
     * @return le nombre de radians correspondant à la distance en paramètre
     */
    static double toRadians(double distanceInMeters) {
        return distanceInMeters / EARTH_RADIUS;
    }
    
    
    /**
     * Transforme le nombre de radians d'une distance inscrite dans un grand cercle en mètres.
     * @param distanceInRadians distance en radians
     * @return le nombre de mètres correspondant au nombre de radians en paramètre
     */
    static double toMeters(double distanceInRadians) {
        return distanceInRadians * EARTH_RADIUS;
    }
    
}
