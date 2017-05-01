package ch.epfl.alpano.gui;

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
public class PanoramaParametersBean implements Serializable {

    private boolean changed;

    private ObjectProperty<PanoramaUserParameters> parameters;
    private Map<UserParameter, ObjectProperty<Integer>> properties;
    private ObjectProperty<Integer> observerLongitudeProperty;
    private ObjectProperty<Integer> observerLatitudeProperty;
    private ObjectProperty<Integer> observerElevationProperty;
    private ObjectProperty<Integer> centerAzimuthProperty;
    private ObjectProperty<Integer> horizontalFieldOfViewProperty;
    private ObjectProperty<Integer> maxDistanceProperty;
    private ObjectProperty<Integer> widthProperty;
    private ObjectProperty<Integer> heightProperty;
    private ObjectProperty<Integer> superSamplingExponentProperty;

    public PanoramaParametersBean() {
        this.properties = new EnumMap<>(UserParameter.class);
        this.observerLongitudeProperty = new SimpleObjectProperty<>();
        this.observerLongitudeProperty.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
    }

    private void synchronizeParameters() {
        
    }

    public ReadOnlyObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters;
    }

    public ObjectProperty<Integer> observerLongitudeProperty() {
        return observerLongitudeProperty;
    }

    public ObjectProperty<Integer> observerLatitudeProperty() {
        return observerLatitudeProperty;
    }

    public ObjectProperty<Integer> observerElevationProperty() {
        return observerElevationProperty;
    }

    public ObjectProperty<Integer> centerAzimuthProperty() {
        return centerAzimuthProperty;
    }

    public ObjectProperty<Integer> horizontalFieldOfViewProperty() {
        return horizontalFieldOfViewProperty;
    }

    public ObjectProperty<Integer> maxDistanceProperty() {
        return maxDistanceProperty;
    }

    public ObjectProperty<Integer> widthProperty() {
        return widthProperty;
    }

    public ObjectProperty<Integer> heightProperty() {
        return heightProperty;
    }

    public ObjectProperty<Integer> superSamplingExponentProperty() {
        return superSamplingExponentProperty;
    }

}
