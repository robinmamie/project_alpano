package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.UserParameter.*;
import static java.lang.Math.toRadians;
import static java.lang.Math.scalb;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.ToIntFunction;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.Preconditions;

/**
 * Classe immuable permettant de définir les paramètres utilisateurs du
 * Panorama.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaUserParameters {

    /**
     * Table associative des paramètres.
     */
    private final Map<UserParameter, Integer> map;

    /**
     * Constructeur primaire de la classe, prend une table associative en
     * argument.
     * 
     * @param map
     *            La table associative des paramètres utilisateur.
     */
    public PanoramaUserParameters(Map<UserParameter, Integer> map) {
        checkArgument(map.size() == 9, "The given map not valid.");
        map = new EnumMap<>(map);
        map.replaceAll(UserParameter::sanitize);
        int limit = (int) (1 + 170 * (map.get(WIDTH) - 1.)
                / map.get(HORIZONTAL_FIELD_OF_VIEW));
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
                new GeoPoint(toRadians(observerLongitude() / 10_000.),
                        toRadians(observerLatitude() / 10_000.)),
                observerElevation(), toRadians(centerAzimuth()),
                toRadians(horizontalFieldOfView()), maxDistance() * 1_000,
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
    // TODO: wtf, see step 8, what is what

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

}
