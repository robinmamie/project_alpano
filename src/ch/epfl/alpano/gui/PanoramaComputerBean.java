package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.ImagePainter.stdPanorama;
import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.io.Serializable;
import java.util.List;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.summit.Summit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;

@SuppressWarnings("serial")
public class PanoramaComputerBean implements Serializable {

    private ObjectProperty<Panorama> panorama;
    private ObjectProperty<PanoramaUserParameters> parameters;
    private ObjectProperty<Image> image;
    private ObjectProperty<ObservableList<Node>> labels;

    private final List<Summit> summits;
    private final ContinuousElevationModel cem;

    public PanoramaComputerBean(ContinuousElevationModel cem,
            List<Summit> summits) {
        this.summits = summits;
        this.cem = cem;
        this.parameters = new SimpleObjectProperty<>(null);
        this.parameters.addListener((b, o, n) -> synchronizeParameters());
        this.image = new SimpleObjectProperty<>(null);
        this.labels = new SimpleObjectProperty<>(observableArrayList());
    }

    private void synchronizeParameters() {
        PanoramaParameters parametersDisplay = parameters.get()
                .panoramaDisplayParameters();
        panorama.set(
                new PanoramaComputer(cem).computePanorama(parametersDisplay));
        image.set(renderPanorama(panorama.get(), stdPanorama(panorama.get())));
        labels.get()
                .setAll(new Labelizer(cem, summits).labels(parametersDisplay));
    }

    public ObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters;
    }

    public PanoramaUserParameters getParameters() {
        return parameters.get();
    }

    public void setParameters(PanoramaUserParameters newParameters) {
        parameters.set(newParameters);
    }

    public ReadOnlyObjectProperty<Panorama> panoramaProperty() {
        return panorama;
    }

    public Panorama getPanorama() {
        return panorama.get();
    }

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return image;
    }

    public Image getImage() {
        return image.get();
    }

    public ReadOnlyObjectProperty<ObservableList<Node>> labelsProperty() {
        return new SimpleObjectProperty<>(
                unmodifiableObservableList(labels.get()));
    }

    public ObservableList<Node> getLabels() {
        return unmodifiableObservableList(labels.get());
    }
}
