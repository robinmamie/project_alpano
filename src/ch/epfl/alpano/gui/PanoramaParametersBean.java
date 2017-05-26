package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.SUPER_SAMPLING_EXPONENT;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static javafx.application.Platform.runLater;

import java.util.EnumMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Représente l'état actuel des paramètres passés à l'interface graphique.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaParametersBean {

    /**
     * Stocke les paramètres utilisateurs du Panorama dans une propriété javafx.
     */
    private final ObjectProperty<PanoramaUserParameters> parameters;

    /**
     * Stocke une copie de ces paramètres utilisateurs, qui elles peuvent être
     * modifiées par l'utilisateur de cette classe.
     */
    private final Map<UserParameter, ObjectProperty<Integer>> properties;

    /**
     * Constructeur du PanoramaParametersBean.
     * 
     * @param parameters
     *            Les paramètres utilisateurs du panorama.
     */
    public PanoramaParametersBean(PanoramaUserParameters parameters) {
        this.parameters = new SimpleObjectProperty<>(parameters);
        this.properties = new EnumMap<>(UserParameter.class);
        parameters.map().forEach((u, x) -> {
            this.properties.put(u, new SimpleObjectProperty<>(x));
            properties.get(u).addListener(
                    (p, o, n) -> runLater(this::synchronizeParameters));
        });
    }

    /**
     * Permet de synchroniser les deux éléments stockés dans la classe. Elle est
     * appelée à chaque fois qu'un des éléments de la table associative est
     * modifié.
     */
    private void synchronizeParameters() {
        Map<UserParameter, Integer> map = new EnumMap<>(UserParameter.class);
        properties.forEach((u, p) -> map.put(u, p.get()));
        parameters.set(new PanoramaUserParameters(map));
        properties.forEach((u, p) -> p.set(parameters.get().get(u)));
    }

    /**
     * Retourne une copie en lecture seule de la propriété javafx des paramètres
     * utilisateurs du panorama.
     * 
     * @return une copie en lecture seule de la propriété javafx des paramètres
     *         utilisateurs du panorama.
     */
    public ReadOnlyObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters;
    }

    /**
     * Retourne la propriété spécifiée par un UserParameter.
     * 
     * @param uP
     * Le UserParameter concerné.
     * 
     * @return la propriété spécifiée par un UserParameter.
     */
    public ObjectProperty<Integer> getProperty(UserParameter uP) {
        return properties.get(uP);
    }

    /**
     * Retourne la propriété de la longitude de l'observateur entière stockée.
     * 
     * @return La propriété de la longitude de l'observateur entière stockée.
     */
    public ObjectProperty<Integer> observerLongitudeProperty() {
        return properties.get(OBSERVER_LONGITUDE);
    }

    /**
     * Retourne la propriété de la latitude de l'observateur entière stockée.
     * 
     * @return La propriété de la latitude de l'observateur entière stockée.
     */
    public ObjectProperty<Integer> observerLatitudeProperty() {
        return properties.get(OBSERVER_LATITUDE);
    }

    /**
     * Retourne la propriété de l'élévation de l'observateur entière stockée.
     * 
     * @return La propriété de l'élévation de l'observateur entière stockée.
     */
    public ObjectProperty<Integer> observerElevationProperty() {
        return properties.get(OBSERVER_ELEVATION);
    }

    /**
     * Retourne la propriété de l'azimuth central entier stocké.
     * 
     * @return La propriété de l'azimuth central entier stocké.
     */
    public ObjectProperty<Integer> centerAzimuthProperty() {
        return properties.get(CENTER_AZIMUTH);
    }

    /**
     * Retourne la propriété du champ de vue horizontal entier stocké.
     * 
     * @return La propriété du champ de vue horizontal entier stocké.
     */
    public ObjectProperty<Integer> horizontalFieldOfViewProperty() {
        return properties.get(HORIZONTAL_FIELD_OF_VIEW);
    }

    /**
     * Retourne la propriété de la distance maximale entière stockée.
     * 
     * @return La propriété de la distance maximale entière stockée.
     */
    public ObjectProperty<Integer> maxDistanceProperty() {
        return properties.get(MAX_DISTANCE);
    }

    /**
     * Retourne la propriété de la largeur maximale entière stockée.
     * 
     * @return La propriété de la largeur maximale entière stockée.
     */
    public ObjectProperty<Integer> widthProperty() {
        return properties.get(WIDTH);
    }

    /**
     * Retourne la propriété de la hauteur maximale entière stockée.
     * 
     * @return La propriété de la hauteur maximale entière stockée.
     */
    public ObjectProperty<Integer> heightProperty() {
        return properties.get(HEIGHT);
    }

    /**
     * Retourne la propriété de l'exposant de suréchantillonnage entier stocké.
     * 
     * @return La propriété de l'exposant de suréchantillonnage entier stocké.
     */
    public ObjectProperty<Integer> superSamplingExponentProperty() {
        return properties.get(SUPER_SAMPLING_EXPONENT);
    }

}
