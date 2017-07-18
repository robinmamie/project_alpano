package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Azimuth.toOctantString;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.SUPER_SAMPLING_EXPONENT;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static java.lang.Math.abs;
import static java.lang.Math.scalb;
import static java.lang.Math.toRadians;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.ToIntFunction;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;

/**
 * Classe immuable permettant de définir les paramètres utilisateurs du
 * Panorama.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaUserParameters implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = -5211994945756595388L;

    /**
     * Indique la valeur maximale possible pour le champ de vue vertical.
     */
    private static final int MAX_VERTICAL_FIELD_OF_VIEW = 170;

    /**
     * Table associative des paramètres.
     */
    private final Map<UserParameter, Integer> map;

    /**
     * Nombre de décimales dans un paramètre entier.
     */
    private static final double DECIMAL_SHIFT = 10_000;

    /**
     * Nombre de mètres dans un km.
     */
    public static final int M_PER_KM = 1_000;

    /**
     * Constructeur primaire de la classe, prend une table associative en
     * argument.
     * 
     * @param map
     *            La table associative des paramètres utilisateur.
     */
    public PanoramaUserParameters(Map<UserParameter, Integer> map) {
        checkArgument(map.size() == UserParameter.values().length,
                "The given map is not valid.");
        map = new EnumMap<>(map);
        map.replaceAll(UserParameter::sanitize);
        int limit = (int) (1 + MAX_VERTICAL_FIELD_OF_VIEW
                * (map.get(WIDTH) - 1.) / map.get(HORIZONTAL_FIELD_OF_VIEW));
        if (limit < map.get(HEIGHT))
            map.put(HEIGHT, limit);
        this.map = unmodifiableMap(map);
    }

    /**
     * Constructeur secondaire de la classe. Prend tous les arguments
     * individuellement et construit une table assoicative.
     * 
     * @param observerLongitude
     *            La longitude de lobservateur.
     * @param observerLatitude
     *            La latitude de lobservateur.
     * @param observerElevation
     *            L'élévation de lobservateur.
     * @param centerAzimuth
     *            L'azimut central.
     * @param horizontalFieldOfView
     *            Le champ de vue horizontal.
     * @param maxDistance
     *            La distance maximale.
     * @param width
     *            La largeur de l'image.
     * @param height
     *            La hauteur de l'image.
     * @param superSamplingExponent
     *            L'exposant de suréchantillonage.
     */
    @SuppressWarnings("serial")
    public PanoramaUserParameters(int observerLongitude, int observerLatitude,
            int observerElevation, int centerAzimuth, int horizontalFieldOfView,
            int maxDistance, int width, int height, int superSamplingExponent) {
        this(new EnumMap<UserParameter, Integer>(UserParameter.class) {
            {
                put(OBSERVER_LONGITUDE, observerLongitude);
                put(OBSERVER_LATITUDE, observerLatitude);
                put(OBSERVER_ELEVATION, observerElevation);
                put(CENTER_AZIMUTH, centerAzimuth);
                put(HORIZONTAL_FIELD_OF_VIEW, horizontalFieldOfView);
                put(MAX_DISTANCE, maxDistance);
                put(WIDTH, width);
                put(HEIGHT, height);
                put(SUPER_SAMPLING_EXPONENT, superSamplingExponent);
            }
        });
    }

    /**
     * Permet d'obtenir la valeur du paramètre demandé.
     * 
     * @param parameter
     *            Le paramètre utilisateur demandé.
     * 
     * @return La valeur du paramètrew utilisateur demandé.
     */
    public int get(UserParameter parameter) {
        return map.get(parameter);
    }

    /**
     * Retourne la longitude de l'observateur.
     * 
     * @return La longitude de l'observateur.
     */
    public int observerLongitude() {
        return get(OBSERVER_LONGITUDE);
    }

    /**
     * Retourne la latitude de l'observateur.
     * 
     * @return La latitude de l'observateur.
     */
    public int observerLatitude() {
        return get(OBSERVER_LATITUDE);
    }

    /**
     * Retourne l'élévation de l'observateur.
     * 
     * @return L'élévation de l'observateur.
     */
    public int observerElevation() {
        return get(OBSERVER_ELEVATION);
    }

    /**
     * Retourne l'azimut central.
     * 
     * @return L'azimut central.
     */
    public int centerAzimuth() {
        return get(CENTER_AZIMUTH);
    }

    /**
     * Retourne le champ de vue horizontal.
     * 
     * @return Le champ de vue horizontal.
     */
    public int horizontalFieldOfView() {
        return get(HORIZONTAL_FIELD_OF_VIEW);
    }

    /**
     * Retourne la distance maximale.
     * 
     * @return La distance maximale.
     */
    public int maxDistance() {
        return get(MAX_DISTANCE);
    }

    /**
     * Retourne la largeur de l'image.
     * 
     * @return La largeur de l'image.
     */
    public int width() {
        return get(WIDTH);
    }

    /**
     * Retourne la hauteur de l'image.
     * 
     * @return La hauteur de l'image.
     */
    public int height() {
        return get(HEIGHT);
    }

    /**
     * Retourne l'exposant du suréchantillonage.
     * 
     * @return L'exposant du suréchantillonage.
     */
    public int superSamplingExponent() {
        return get(SUPER_SAMPLING_EXPONENT);
    }

    /**
     * Méthode privée permettant de créer les paramètres du Panorama selon - ou
     * non - les paramètres de suréchantillonage.
     * 
     * @param s
     *            L'exposant du suréchantillonage.
     * 
     * @return La paramètres du Panorama.
     */
    private PanoramaParameters panoramaParametersSet(ToIntFunction<Integer> s) {
        return new PanoramaParameters(
                new GeoPoint(toRadians(observerLongitude() / DECIMAL_SHIFT),
                        toRadians(observerLatitude() / DECIMAL_SHIFT)),
                observerElevation(), toRadians(centerAzimuth()),
                toRadians(horizontalFieldOfView()), maxDistance() * M_PER_KM,
                s.applyAsInt((width())), s.applyAsInt((height())));
    }

    /**
     * Crée les paramètres du Panorama en prenant en compte l'exposant de
     * suréchantillonage.
     * 
     * @return Les paramètres du Panorama en prenant en compte l'exposant de
     *         suréchantillonage.
     */
    public PanoramaParameters panoramaParameters() {
        return panoramaParametersSet(
                x -> (int) scalb(x, superSamplingExponent()));
    }

    /**
     * Crée les paramètres du Panorama en ne prenant pas en compte l'exposant de
     * suréchantillonage.
     * 
     * @return Les paramètres du Panorama en ne prenant pas en compte l'exposant
     *         de suréchantillonage.
     */
    public PanoramaParameters panoramaDisplayParameters() {
        return panoramaParametersSet(x -> x);
    }

    /**
     * Retourne la table associative utilisée dans la classe.
     * 
     * @return La table associative utilisée dans la classe.
     */
    protected Map<UserParameter, Integer> map() {
        return map;
    }

    @Override
    public boolean equals(Object thato) {
        return thato instanceof PanoramaUserParameters
                && this.map().equals(((PanoramaUserParameters) thato).map());
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        char northOrSouth = observerLatitude() >= 0 ? 'N' : 'S';
        char eastOrWest = observerLongitude() >= 0.0 ? 'E' : 'W';

        return format("Position : %.4f°%c %.4f°%c%n",
                abs((double) observerLatitude() / DECIMAL_SHIFT), northOrSouth,
                abs((double) observerLongitude() / DECIMAL_SHIFT), eastOrWest)
                + format("Altitude : %d m  Angle de vue : %d°%n",
                        observerElevation(), horizontalFieldOfView())
                + format("Azimut : %d° (%s)  Visibilité : %d km%n",
                        centerAzimuth(),
                        toOctantString(Math.toRadians(centerAzimuth()), "N",
                                "E", "S", "W"),
                        maxDistance())
                + format("Largeur : %d px  Hauteur : %d px%n", width(),
                        height())
                + format("Suréchantillonage : %s", superSamplingExponent() == 0
                        ? "non" : superSamplingExponent() == 1 ? "2×" : "4×");
    }

}
