package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.ImagePainter.stdPanorama;
import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static java.util.Collections.unmodifiableList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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

    /**
     * La propriété des étiquettes des sommets.
     */
    private final ObjectProperty<ObservableList<Node>> labels;

    private final DoubleProperty status;

    /**
     * La liste des sommets passés au constructeur.
     */
    private final List<Summit> summits;

    private final PanoramaComputer pc;

    private final Service<Void> service;

    private final ContinuousElevationModel cem;

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
        this.pc = new PanoramaComputer(cem);
        this.cem = cem;
        this.summits = unmodifiableList(new ArrayList<>(summits));
        this.panorama = new SimpleObjectProperty<>(null);
        this.parameters = new SimpleObjectProperty<>(null);
        this.image = new SimpleObjectProperty<>(null);
        this.labels = new SimpleObjectProperty<>(observableArrayList());
        this.status = new SimpleDoubleProperty();
        status.bind(pc.statusProperty());
        service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        // Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PanoramaParameters parametersDisplay = parameters
                                            .get().panoramaDisplayParameters();
                                    panorama.set(pc.computePanorama(
                                            parametersDisplay));
                                    image.set(renderPanorama(panorama.get(),
                                            stdPanorama(panorama.get())));
                                    labels.get().setAll(new Labelizer(cem,
                                            summits).labels(parameters.get()
                                                    .panoramaParameters()));
                                    status.unbind();
                                    status.set(0);
                                } finally {
                                    latch.countDown();
                                }
                            }
                        });
                        latch.await();
                        // Keep with the background work
                        return null;
                    }
                };
            }
        };
        this.parameters.addListener((b, o, n) -> {
            Platform.runLater(new Runnable() {
                @Override
              public void run() {
                  PanoramaParameters parametersDisplay = parameters.get()
                          .panoramaDisplayParameters();
                  panorama.set(pc.computePanorama(parametersDisplay));
                  image.set(renderPanorama(panorama.get(),
                          stdPanorama(panorama.get())));
                  labels.get().setAll(new Labelizer(cem, summits)
                          .labels(parameters.get().panoramaParameters()));
              }
            });
//            new Thread() {
//                @Override
//                public void run() {
//                    PanoramaParameters parametersDisplay = parameters.get()
//                            .panoramaDisplayParameters();
//                    panorama.set(pc.computePanorama(parametersDisplay));
//                }
//            }.start();
            // service.reset();
            // service.start();
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
     * Retourne la propriété en lecture seule de la liste des sommets visibles
     * observables.
     * 
     * @return la propriété en lecture seule de la liste des sommets visibles
     *         observables.
     */
    public ReadOnlyObjectProperty<ObservableList<Node>> labelsProperty() {
        return new SimpleObjectProperty<>(
                unmodifiableObservableList(labels.get()));
    }

    /**
     * Retourne la liste des sommets visibles observables.
     * 
     * @return la liste des sommets visibles observables.
     */
    public ObservableList<Node> getLabels() {
        return labels.get();
    }

    public ReadOnlyDoubleProperty statusProperty() {
        return status;
    }

}
