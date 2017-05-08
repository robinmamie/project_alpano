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
    private final static int TEXT_ANGLE = -60;

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
     * Calcule la distance entre l'observateur et le sommet.
     * 
     * @param s
     *            Le sommet.
     * @param parameters
     *            Les paramètres du Panorama.
     * 
     * @return La distance entre l'observateur et le sommet ou POSITIVE_INFINITY
     *         si le sommet se trouve trop loin.
     */
    private double distanceToSummit(Summit s, PanoramaParameters parameters) {
        double distance = parameters.observerPosition()
                .distanceTo(s.position());
        return distance > parameters.maxDistance() ? POSITIVE_INFINITY
                : distance;
    }

    /**
     * Calcule l'azimuth vers le sommet depuis l'observateur.
     * 
     * @param s
     *            Le sommet.
     * @param parameters
     *            Les paramètres du Panorama.
     * 
     * @return L'azimuth vers le sommet depuis l'observateur ou
     *         POSITIVE_INFINITY si le sommet n'apparaît pas dans cet angle de
     *         vue horizontal.
     */
    private double azimuthToSummit(Summit s, PanoramaParameters parameters) {
        double azimuth = parameters.observerPosition().azimuthTo(s.position());
        return abs(angularDistance(azimuth,
                parameters.centerAzimuth())) > parameters
                        .horizontalFieldOfView() / 2. ? POSITIVE_INFINITY
                                : azimuth;
    }

    /**
     * Calcule l'altitude vers le sommet depuis l'observateur.
     * 
     * @param s
     *            Le sommet.
     * @param parameters
     *            Les paramètres du Panorama.
     * @param profile
     *            Le profil altimétrique dirigé vers le sommet.
     * @param distance
     *            La distance entre l'observateur et le sommet.
     * 
     * @return L'altitude vers le sommet depuis l'observateur ou
     *         POSITIVE_INFINITY si le sommet n'apparaît pas dans cet angle de
     *         vue vertical.
     */
    private double altitudeToSummit(Summit s, PanoramaParameters parameters,
            ElevationProfile profile, double distance) {
        double altitude = atan2(
                -rayToGroundDistance(profile, parameters.observerElevation(), 0)
                        .applyAsDouble(distance),
                distance);
        return abs(altitude) > parameters.verticalFieldOfView() / 2.
                ? POSITIVE_INFINITY : altitude;
    }

    /**
     * Vérifie toutes les conditions nécessaires pour qu'un sommet soit visible
     * sur le Panorama.
     * 
     * @param summit
     *            Un sommet.
     * 
     * @return vrai si le sommet est visible sur le Panorama.
     */
    private boolean summitIsVisible(Summit summit,
            PanoramaParameters parameters) {

        double distance = distanceToSummit(summit, parameters);
        if (distance == POSITIVE_INFINITY)
            return false;

        double azimuth = azimuthToSummit(summit, parameters);
        if (azimuth == POSITIVE_INFINITY)
            return false;

        ElevationProfile profile = new ElevationProfile(cem,
                parameters.observerPosition(), azimuth, distance);

        double altitude = altitudeToSummit(summit, parameters, profile,
                distance);
        if (altitude == POSITIVE_INFINITY)
            return false;

        if (firstIntervalContainingRoot(
                rayToGroundDistance(profile, parameters.observerElevation(),
                        tan(altitude)),
                0, distance - TOLERANCE, INTERVAL) != POSITIVE_INFINITY)
            return false;

        values.put(summit,
                new Integer[] { (int) round(parameters.xForAzimuth(azimuth)),
                        (int) round(parameters.yForAltitude(altitude)) });
        return true;
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

        for (Summit s : summits)
            if (summitIsVisible(s, parameters))
                visible.add(s);

        visible.sort((a, b) -> {
            int higher = compare(values.get(a)[INDEX_Y],
                    values.get(b)[INDEX_Y]);
            return higher == 0 ? compare(b.elevation(), a.elevation()) : higher;
        });

        return unmodifiableList(visible);
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

        int labelPlaceInit = -1, labelPlace = labelPlaceInit;

        for (Summit s : visible) {
            int x = values.get(s)[INDEX_X], y = values.get(s)[INDEX_Y];

            if (y > PIXEL_THRESHOLD
                    && positions.get(x, x + PIXELS_NEEDED).isEmpty()) {

                if (labelPlace == labelPlaceInit)
                    labelPlace = y - PIXELS_NEEDED - PIXELS_ROOM;

                positions.flip(x, x + PIXELS_NEEDED);

                Text t = new Text(s.name() + " (" + s.elevation() + " m)");
                t.getTransforms().addAll(new Translate(x, labelPlace),
                        new Rotate(TEXT_ANGLE, 0, 0));
                nodes.add(t);

                nodes.add(new Line(x, labelPlace + PIXELS_ROOM, x, y));
            }
        }
        return unmodifiableList(nodes);
        // TODO *BONUS* User can give personalised inputs, e.g. cities, list and
        // save
        // them, and then force them to show on the Panorama, e.g. by inputing
        // an infinite elevation for the "Summit".
    }

}
