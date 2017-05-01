package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static java.lang.Float.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
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

    public PanoramaComputerBean(PanoramaUserParameters parameters)
            throws IOException {
        this.summits = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        this.cem = new ContinuousElevationModel(
                new SuperHgtDiscreteElevationModel());
        this.parameters = new SimpleObjectProperty<>(parameters);
        this.parameters.addListener(
                (b, o, n) -> runLater(this::synchronizeParameters));
        this.labels = new SimpleObjectProperty<>(observableArrayList());
        this.image = new SimpleObjectProperty<>();
        synchronizeParameters();
    }

    private void synchronizeParameters() {
        PanoramaParameters parametersDisplay = parameters.get()
                .panoramaDisplayParamters();
        panorama.set(
                new PanoramaComputer(cem).computePanorama(parametersDisplay));

        ChannelPainter distance = panorama.get()::distanceAt;
        ChannelPainter slope = panorama.get()::slopeAt;
        ChannelPainter h = distance.div(100_000).cycle().mul(360);
        ChannelPainter s = distance.div(200_000).clamp().invert();
        ChannelPainter b = slope.mul(2).div((float) PI).invert().mul(0.7f)
                .add(0.3f);
        ChannelPainter o = distance.map(d -> d == POSITIVE_INFINITY ? 0 : 1);
        ImagePainter l = ImagePainter.hsb(h, s, b, o);
        image.set(renderPanorama(panorama.get(), l));

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
        return labels.get();
    }
}
