package ch.epfl.alpano.summit;

import ch.epfl.alpano.GeoPoint;

public abstract class Labelizable {

    /**
     * Donne la priorité d'une étiquette par rapport à une autre.
     * 
     * @return la priorité d'une étiquette par rapport à une autre. Un grand
     *         chiffre indique une grande priorité.
     */
    public abstract int priority();
    
    /**
     * Retourne le nom de l'étiquette.
     * 
     * @return Le nom de l'étiquette.
     */
    public abstract String name();
    
    /**
     * Retourne la position de l'étiquette.
     * 
     * @return La position de l'étiquette.
     */
    public abstract GeoPoint position();

    /**
     * Retourne l'altitude de l'étiquette.
     * 
     * @return L'altitude de l'étiquette.
     */
    public abstract int elevation();
    
    @Override
    public String toString() {
        return name() + " " + position() + " " + elevation();
    }
}
