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

    private ObjectProperty<PanoramaUserParameters> parameters;
    private Map<UserParameter, ObjectProperty<Integer>> properties;

    public PanoramaParametersBean(PanoramaUserParameters parameters) {
        this.parameters = new SimpleObjectProperty<>(parameters);
        this.properties = new EnumMap<>(UserParameter.class);
        parameters.map().forEach((u, x) -> this.properties.put(u,
                new SimpleObjectProperty<>(x)));
        properties.forEach((u, x) -> x.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters)));
    }

    private void synchronizeParameters() {
        Map<UserParameter, Integer> map = new EnumMap<>(UserParameter.class);
        properties.forEach((u, x) -> map.put(u, x.get()));
        parameters.set(new PanoramaUserParameters(map));
        properties.forEach((u, x) -> x.set(parameters.get().get(u)));
    }

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
