package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.ImagePainter.stdPanorama;
import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.util.List;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.summit.Labelizable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
public final class PanoramaComputerBean {

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

    /**
     * La liste non modifiable des sommets.
     */
    private final ObservableList<Node> unmodifiableLabels;

    private final ObjectProperty<ContinuousElevationModel> cem;

    private final DoubleProperty status;

    private final BooleanProperty hideNonSummits;

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
            List<Labelizable> summits) {
        this.panorama = new SimpleObjectProperty<>(null);
        this.parameters = new SimpleObjectProperty<>(null);
        this.image = new SimpleObjectProperty<>(null);
        ObservableList<Node> labels = observableArrayList();
        this.unmodifiableLabels = unmodifiableObservableList(labels);
        this.cem = new SimpleObjectProperty<>(cem);
        this.status = new SimpleDoubleProperty();
        this.hideNonSummits = new SimpleBooleanProperty(false);

        this.parameters.addListener((b, o, n) -> {
            if (n == null)
                return;
            labels.clear();
            new Thread() {
                @Override
                public void run() {
                    PanoramaComputer pc = new PanoramaComputer(cemProperty().get());
                    System.out.println(
                            "\n*************************************************************");
                    if (panorama.get() != null)
                        System.out.println("Erasing previous panorama...");
                    panorama.set(null);
                    image.set(null);
                    System.out.println(
                            "Launching computation with the following parameters:");
                    System.out.println(
                            "-------------------------------------------");
                    System.out.println(parameters.get());
                    System.out.println(
                            "-------------------------------------------");
                    status.bind(pc.statusProperty());
                    long start = System.nanoTime();
                    try {
                        panorama.set(pc.computePanorama(
                                parameters.get().panoramaParameters()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.printf("Panorama computed after %.3f seconds.%n",
                            (System.nanoTime() - start) * 1e-9);
                    Image i = renderPanorama(panorama.get(),
                            stdPanorama(panorama.get()));

                    System.out.printf("Panorama rendered after %.3f seconds.%n",
                            (System.nanoTime() - start) * 1e-9);

                    List<Node> list = new Labelizer(cemProperty().get(), summits,
                            hideNonSummits.getValue())
                                    .labels(parameters.get()
                                            .panoramaDisplayParameters());
                    System.out.printf(
                            "Panorama's labels computed after %.3f seconds.%n",
                            (System.nanoTime() - start) * 1e-9);

                    runLater(() -> {
                        labels.setAll(list);
                        image.set(i);
                        status.unbind();
                        status.set(0);
                        System.out
                                .println("Computation and rendering finished.");
                        System.out.println(
                                "*************************************************************\n");
                    });
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
    
    public ObjectProperty<ContinuousElevationModel> cemProperty() {
        return cem;
    }

    public ReadOnlyDoubleProperty statusProperty() {
        return status;
    }

    public BooleanProperty hideNonSummitsProperty() {
        return hideNonSummits;
    }

}
