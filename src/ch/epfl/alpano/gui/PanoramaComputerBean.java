package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.ImagePainter.stdPanorama;
import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.io.Serializable;
import java.util.List;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.summit.Summit;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Représente l'état actuel du panorama actuellement affiché à l'écran.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public class PanoramaComputerBean implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = 3959178526495134195L;

    /**
     * La propriété du Panorama.
     */
    private final ObjectProperty<Panorama> panorama;

    /**
     * La propriété des paramètres utilisateur du Panorama.
     */
    private final ObjectProperty<PanoramaUserParameters> parameters;

    /**
     * La propriété de l'image.
     */
    private final ObjectProperty<Image> image;

    
    private final ObservableList<Node> unmodifiableLabels;

    private final DoubleProperty status;

    /**
     * Construit un PanoramaComputerBean en prenant un MNT continu et une liste
     * de sommets en arguments.
     * 
     * @param cem
     *            Un MNT conitnu.
     * @param summits
     *            Une liste de sommets.
     */
    public PanoramaComputerBean(ContinuousElevationModel cem,
            List<Summit> summits) {
        this.panorama = new SimpleObjectProperty<>(null);
        this.parameters = new SimpleObjectProperty<>(null);
        this.image = new SimpleObjectProperty<>(null);
        ObservableList<Node> labels = observableArrayList();
        this.unmodifiableLabels = unmodifiableObservableList(labels);
        this.status = new SimpleDoubleProperty();
        this.parameters.addListener((b, o, n) -> {
            labels.clear();
            new Thread() {
                @Override
                public void run() {
                    PanoramaComputer pc = new PanoramaComputer(cem);
                    status.bind(pc.statusProperty());
                    panorama.set(null);
                    image.set(null);
                    panorama.set(pc.computePanorama(
                            parameters.get().panoramaParameters()));
                    image.set(renderPanorama(panorama.get(),
                            stdPanorama(panorama.get())));
                    runLater(() -> labels
                            .setAll(new Labelizer(cem, summits).labels(
                                    parameters.get().panoramaDisplayParameters())));
                    status.unbind();
                    status.set(0);
                }
            }.start();
        });
    }

    /**
     * Retourne la propriété des paramètres utilisateur du Panorama.
     * 
     * @return La propriété des paramètres utilisateur du Panorama.
     */
    public ObjectProperty<PanoramaUserParameters> parametersProperty() {
        return parameters;
    }

    /**
     * Retourne les paramètres utilisateur du Panorama.
     * 
     * @return Les paramètres utilisateur du Panorama.
     */
    public PanoramaUserParameters getParameters() {
        return parameters.get();
    }

    /**
     * Modifie la propriété des paramètres utilisateur du Panorama.
     * 
     * @param newParameters
     *            Les nouveaux paramètres utilisateur.
     */
    public void setParameters(PanoramaUserParameters newParameters) {
        parameters.set(newParameters);
    }

    /**
     * Retourne la propriété en lecture seule du Panorama.
     * 
     * @return la propriété en lecture seule du Panorama.
     */
    public ReadOnlyObjectProperty<Panorama> panoramaProperty() {
        return panorama;
    }

    /**
     * Retourne le Panorama.
     * 
     * @return le panorama.
     */
    public Panorama getPanorama() {
        return panorama.get();
    }

    /**
     * Retourne la propriété en lecture seule de l'image.
     * 
     * @return la propriété en lecture seule de l'image.
     */
    public ReadOnlyObjectProperty<Image> imageProperty() {
        return image;
    }

    /**
     * Retourne l'image.
     * 
     * @return l'image.
     */
    public Image getImage() {
        return image.get();
    }

    /**
     * Retourne la liste des sommets visibles observables.
     * 
     * @return la liste des sommets visibles observables.
     */
    public ObservableList<Node> getLabels() {
        return unmodifiableLabels;
    }

    public ReadOnlyDoubleProperty statusProperty() {
        return status;
    }

}
