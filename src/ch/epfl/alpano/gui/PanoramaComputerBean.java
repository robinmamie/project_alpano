package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.ImagePainter.stdPanorama;
import static ch.epfl.alpano.gui.PanoramaRenderer.renderPanorama;
import static java.util.Collections.unmodifiableList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * La liste des sommets passés au constructeur.
     */
    private final List<Summit> summits;

    /**
     * Le MNT continu passé au constructeur.
     */
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
        this.summits = unmodifiableList(new ArrayList<>(summits));
        this.cem = cem;
        this.panorama = new SimpleObjectProperty<>(null);
        this.parameters = new SimpleObjectProperty<>(null);
        this.parameters.addListener((b, o, n) -> synchronizeParameters());
        this.image = new SimpleObjectProperty<>(null);
        this.labels = new SimpleObjectProperty<>(observableArrayList());
    }

    /**
     * Permet de s'assurer que tous les éléments (propriétés) de la classe
     * correspondent aux paramètres existants. Elle est appelée à chaque fois
     * que les paramètres sont modifiés.
     */
    private void synchronizeParameters() {
        PanoramaParameters parametersDisplay = parameters.get()
                .panoramaDisplayParameters();
        panorama.set(
                new PanoramaComputer(cem).computePanorama(parametersDisplay));
        image.set(renderPanorama(panorama.get(), stdPanorama(panorama.get())));
        System.out.println(parameters.get().superSamplingExponent());
        labels.get().setAll(new Labelizer(cem, summits)
                .labels(parameters.get().panoramaParameters()));
        System.out.println(labels.get().size());
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
}
