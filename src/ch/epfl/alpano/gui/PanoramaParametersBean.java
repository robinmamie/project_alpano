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

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
@SuppressWarnings("serial")
public class PanoramaParametersBean implements Serializable {

    /**
     * Stocke les paramètres utilisateurs du Panorama dans une propriété javafx.
     */
    private ObjectProperty<PanoramaUserParameters> parameters;

    /**
     * Stocke une copie de ces paramètres utilisateurs, qui elles peuvent être
     * modifiées par l'utilisateur de cette classe.
     */
    private Map<UserParameter, ObjectProperty<Integer>> properties;

    /**
     * Constructeur du PanoramaParametersBean.
     * 
     * @param parameters
     *            Les paramètres utilisateurs du panorama.
     */
    public PanoramaParametersBean(PanoramaUserParameters parameters) {
        this.parameters = new SimpleObjectProperty<>(parameters);
        this.properties = new EnumMap<>(UserParameter.class);
        parameters.map().forEach((u, x) -> this.properties.put(u,
                new SimpleObjectProperty<>(x)));
        properties.forEach((u, x) -> x.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters)));
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

    public ObjectProperty<Integer> observerLongitudeProperty() {
        return properties.get(OBSERVER_LONGITUDE);
    }

    public ObjectProperty<Integer> observerLatitudeProperty() {
        return properties.get(OBSERVER_LATITUDE);
    }

    public ObjectProperty<Integer> observerElevationProperty() {
        return properties.get(OBSERVER_ELEVATION);
    }

    public ObjectProperty<Integer> centerAzimuthProperty() {
        return properties.get(CENTER_AZIMUTH);
    }

    public ObjectProperty<Integer> horizontalFieldOfViewProperty() {
        return properties.get(HORIZONTAL_FIELD_OF_VIEW);
    }

    public ObjectProperty<Integer> maxDistanceProperty() {
        return properties.get(MAX_DISTANCE);
    }

    public ObjectProperty<Integer> widthProperty() {
        return properties.get(WIDTH);
    }

    public ObjectProperty<Integer> heightProperty() {
        return properties.get(HEIGHT);
    }

    public ObjectProperty<Integer> superSamplingExponentProperty() {
        return properties.get(SUPER_SAMPLING_EXPONENT);
    }

}
