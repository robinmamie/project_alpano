package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Représente un sommet alpin par son nom, sa position et son altitude. Classe
 * immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Summit {

    private final String name;
    private final GeoPoint position;
    private final int elevation;

    /**
     * Construit un sommet.
     * 
     * @param name
     *            Le nom du sommet.
     * @param position
     *            La position du sommet.
     * @param elevation
     *            L'altitude du sommet.
     * 
     * @throws NullPointerException
     *             si le nom ou la position sont null
     */
    public Summit(String name, GeoPoint position, int elevation) {
        this.name = requireNonNull(name);
        this.position = requireNonNull(position);
        this.elevation = elevation;
    }

    /**
     * Retourne le nom du sommet.
     * 
     * @return Le nom du sommet.
     */
    public String name() {
        return name;
    }

    /**
     * Retourne la position du sommet.
     * 
     * @return La position du sommet.
     */
    public GeoPoint position() {
        return position;
    }

    /**
     * Retourne l'altitude du sommet.
     * 
     * @return L'altitude du sommet.
     */
    public int elevation() {
        return elevation;
    }

    @Override
    public String toString() {
        return new StringBuilder(name()).append(" ").append(position())
                .append(" ").append(elevation()).toString();
    }

}
