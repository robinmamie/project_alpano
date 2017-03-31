package ch.epfl.alpano;

/**
 * Interface faisant le lien entre angles et distances terrestres. Permet de
 * convertir des radians en mètres en suivant le rayon de la Terre, défini à
 * 6.371.800 mètres.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Distance {

    /**
     * Rayon de la Terre, en mètres.
     */
    double EARTH_RADIUS = 6_371_000;

    /**
     * Transforme une distance terrestre en mètres en radians, selon le rayon de
     * la Terre. Suit automatiquement un grand cercle.
     * 
     * @param distanceInMeters
     *            Distance terrestre en mètres.
     * 
     * @return Le nombre de radians correspondant à la distance en mètres donnée
     *         en paramètre.
     */
    static double toRadians(double distanceInMeters) {
        return distanceInMeters / EARTH_RADIUS;
    }

    /**
     * Transforme les radians déterminant une distance inscrite dans un grand
     * cercle en mètres.
     * 
     * @param distanceInRadians
     *            Distance terrestre en radians.
     * 
     * @return La distance en mètres correspondant au nombre de radians donné en
     *         paramètre.
     */
    static double toMeters(double distanceInRadians) {
        return distanceInRadians * EARTH_RADIUS;
    }

}
