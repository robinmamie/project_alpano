package ch.epfl.alpano.gui;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Énumération permettant de définir des limites aux paramètres d'un Panorama.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public enum UserParameter {

    /**
     * La longitude de l'observateur.
     */
    OBSERVER_LONGITUDE(6_0000, 12_0000),

    /**
     * La latitude de l'observateur.
     */
    OBSERVER_LATITUDE(45_0000, 48_0000),

    /**
     * L'élévation de l'observateur.
     */
    OBSERVER_ELEVATION(300, 10_000),

    /**
     * L'azimut central.
     */
    CENTER_AZIMUTH(0, 359),

    /**
     * Le champ de vue horizontal.
     */
    HORIZONTAL_FIELD_OF_VIEW(1, 360),

    /**
     * La distance maximale.
     */
    MAX_DISTANCE(10, 600),

    /**
     * La largeur de l'image.
     */
    WIDTH(30, 16_000),

    /**
     * La hauteur de l'image.
     */
    HEIGHT(10, 4_000),

    /**
     * L'exposant du suréchantillonage.
     */
    SUPER_SAMPLING_EXPONENT(0, 2);

    /**
     * Bornes inférieure et supérieure du paramètre.
     */
    private final int min, max;

    /**
     * Constructeur du paramètre.
     * 
     * @param min
     *            La borne inférieure.
     * @param max
     *            La borne supérieure.
     */
    private UserParameter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Permet de s'assurer que la valeur donnée est bien comprise entre la borne
     * inférieure et supérieure définies.
     * 
     * @param n
     *            La valeur que l'on veut assurer.
     * 
     * @return La valeur corrigée ou non.
     */
    public int sanitize(int n) {
        return max(min, min(n, max));
    }
}
