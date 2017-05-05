package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.PanoramaComputer.INTERVAL;
import static ch.epfl.alpano.PanoramaComputer.rayToGroundDistance;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Integer.compare;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.round;
import static java.lang.Math.tan;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Étiquette les sommets visibles sur les paramètres d'un Panorama donné.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Labelizer {

    /**
     * Rayon de définition d'un sommet.
     */
    private final static int TOLERANCE = 200;

    /**
     * Nombre de pixels requis au minimum pour tracer une ligne, i.e. une ligne
     * ne sera jamais plus courte que ça.
     */
    private final static int PIXELS_NEEDED = 20;

    /**
     * Espace laissé entre les étiquettes et la ligne.
     */
    private final static int PIXELS_ROOM = 2;

    /**
     * Les étiquettes peuvent être dessinées à partir de ce pixel vertical.
     * Avant, l'espace laissé n'est pas suffisant.
     */
    private final static int PIXEL_THRESHOLD = 170;

    /**
     * Angle du texte par rapport à l'horizontale.
     */
    private final static int TEXT_ANGLE = 60;

    /**
     * Index du tableau de la map pour l'index x.
     */
    private final static int INDEX_X = 0;

    /**
     * Index du tableau de la map pour l'index y.
     */
    private final static int INDEX_Y = 1;

    /**
     * MNT continu.
     */
    private final ContinuousElevationModel cem;

    /**
     * Liste des sommets initialisée dans le constructeur.
     */
    private final List<Summit> summits;

    /**
     * Table associative utilisée pour sauvegarder les coordonées d'un sommet
     * sur l'image.
     */
    private final Map<Summit, Integer[]> values;

    /**
     * Construit un Labelizer, qui servira à étiquetter les sommets visibles
     * d'un Panorama.
     * 
     * @param cem
     *            Un MNT continu.
     * @param summits
     *            Une liste complètes de tous les sommets.
     */
    public Labelizer(ContinuousElevationModel cem, List<Summit> summits) {
        this.cem = cem;
        this.summits = unmodifiableList(new ArrayList<>(summits));
        values = new HashMap<Summit, Integer[]>();
    }

    /**
     * Calcule tous les sommets visibles depuis un position donnée et les trie.
     * 
     * @param parameters
     *            Les paramètres du Panorama en question.
     * 
     * @return La liste triée de tous les sommets visibles.
     */
    private List<Summit> visibleSummits(PanoramaParameters parameters) {
        List<Summit> visible = new ArrayList<>();

        for (Summit s : summits) {
            // 1ère condition, se trouve dans la zone visible

            // 1.a: se trouve à une distance possible, i.e. inférieure à
            // maxDistance
            double distance = parameters.observerPosition()
                    .distanceTo(s.position());
            if (distance > parameters.maxDistance())
                continue;

            // 1.b: se trouve à un azimuth possible, i.e. dans le domaine de
            // définition.
            double azimuth = parameters.observerPosition()
                    .azimuthTo(s.position());
            if (abs(angularDistance(azimuth,
                    parameters.centerAzimuth())) > parameters
                            .horizontalFieldOfView() / 2.)
                continue;

            // 1.c: se trouve à un angle vertical possible, i.e. ni trop haut,
            // ni trop bas.
            ElevationProfile profile = new ElevationProfile(cem,
                    parameters.observerPosition(), azimuth, distance);
            double altitude = atan2(-rayToGroundDistance(profile,
                    parameters.observerElevation(), 0).applyAsDouble(distance),
                    distance);
            if (abs(altitude) > parameters.verticalFieldOfView() / 2.)
                continue;

            // 2nde condition, est réellement visible par l'observateur, i.e. un
            // rayon lancé depuis ce dernier atteint le sommet.
            if (firstIntervalContainingRoot(
                    rayToGroundDistance(profile, parameters.observerElevation(),
                            tan(altitude)),
                    0, distance - TOLERANCE, INTERVAL) == POSITIVE_INFINITY) {
                visible.add(s);
                values.put(s, new Integer[] {
                        (int) round(parameters.xForAzimuth(azimuth)),
                        (int) round(parameters.yForAltitude(altitude)) });
            }
        }
        visible.sort((a, b) -> {
            int higher = compare(values.get(a)[INDEX_Y],
                    values.get(b)[INDEX_Y]);
            return higher == 0 ? compare(b.elevation(), a.elevation()) : higher;
        });
        return unmodifiableList(new ArrayList<>(visible));
    }

    /**
     * Étiquette le plus de sommets possibles avec leur nom et leur altitude en
     * retournant une liste de nœuds JavaFX.
     * 
     * @param parameters
     *            Les paramètres du Panorama.
     * 
     * @return Une liste de nœuds JavaFX.
     */
    public List<Node> labels(PanoramaParameters parameters) {
        List<Summit> visible = visibleSummits(parameters);
        List<Node> nodes = new ArrayList<>();
        BitSet positions = new BitSet(parameters.width());
        positions.flip(0, PIXELS_NEEDED);
        positions.flip(positions.size() - PIXELS_NEEDED - 1, positions.size());
        int labelPlaceInit = -1;
        int labelPlace = labelPlaceInit;

        for (Summit s : visible) {
            int xIndex = values.get(s)[INDEX_X];
            int yIndex = values.get(s)[INDEX_Y];
            if (yIndex > PIXEL_THRESHOLD && !positions.get(xIndex) && positions
                    .get(xIndex, xIndex + PIXELS_NEEDED).isEmpty()) {
                if (labelPlace == labelPlaceInit)
                    labelPlace = yIndex - PIXELS_NEEDED - PIXELS_ROOM;
                positions.flip(xIndex, xIndex + PIXELS_NEEDED);
                Text t = new Text(s.name() + " (" + s.elevation() + " m)");
                t.getTransforms().addAll(new Translate(xIndex, yIndex),
                        new Rotate(TEXT_ANGLE, 0, 0));
                nodes.add(t);
                nodes.add(new Line(xIndex, labelPlace + PIXELS_ROOM, xIndex,
                        yIndex));
            }
        }
        return unmodifiableList(new ArrayList<>(nodes));
    }

}
