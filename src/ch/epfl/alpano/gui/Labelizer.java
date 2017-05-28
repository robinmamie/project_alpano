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
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Labelizable;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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
    private final List<Labelizable> labels;

    /**
     * Table associative utilisée pour sauvegarder les coordonées d'un sommet
     * sur l'image.
     */
    private final Map<Labelizable, Integer[]> values;

    /**
     * Construit un Labelizer, qui servira à étiquetter les sommets visibles
     * d'un Panorama.
     * 
     * @param cem
     *            Un MNT continu.
     * @param summits
     *            Une liste complètes de tous les sommets.
     */
    public Labelizer(ContinuousElevationModel cem, List<Labelizable> summits) {
        this.cem = requireNonNull(cem);
        this.labels = unmodifiableList(
                new ArrayList<>(requireNonNull(summits)));
        values = new HashMap<Labelizable, Integer[]>();
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
    private double distanceToSummit(Labelizable s, PanoramaParameters parameters) {
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
    private double azimuthToSummit(Labelizable s, PanoramaParameters parameters) {
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
    private double altitudeToSummit(Labelizable s, PanoramaParameters parameters,
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
    private boolean summitIsVisible(Labelizable summit,
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

        double distanceUntilGround = firstIntervalContainingRoot(
                rayToGroundDistance(profile, parameters.observerElevation(),
                        tan(altitude)),
                0, distance, INTERVAL);

        if (distanceUntilGround != POSITIVE_INFINITY
                && distanceUntilGround < distance - TOLERANCE)
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
    private List<Labelizable> visibleSummits(PanoramaParameters parameters) {
        List<Labelizable> visible = new ArrayList<>();

        for (Labelizable l : labels)
            if (summitIsVisible(l, parameters))
                visible.add(l);

        visible.sort((a, b) -> {
            int typeOfLabel = compare(b.priority(), a.priority());
            if(typeOfLabel != 0)
                return typeOfLabel;
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

        List<Labelizable> visible = visibleSummits(parameters);
        List<Node> nodes = new ArrayList<>();
        
        BitSet positions = new BitSet(parameters.width() + PIXELS_NEEDED);
        positions.set(0, PIXELS_NEEDED);
        positions.set(parameters.width(), parameters.width() + PIXELS_NEEDED);

        int labelPlace = Integer.MAX_VALUE;
        
        for(Labelizable l: visible) {
            int y = values.get(l)[INDEX_Y];
            if(y > PIXEL_THRESHOLD && y < labelPlace)
                labelPlace = y - PIXELS_NEEDED - PIXELS_ROOM;
        }

        for (Labelizable l : visible) {
            int x = values.get(l)[INDEX_X], y = values.get(l)[INDEX_Y];

            if (y > PIXEL_THRESHOLD
                    && positions.get(x, x + PIXELS_NEEDED).isEmpty()) {

                positions.set(x, x + PIXELS_NEEDED);

                Text text = new Text(l.name() + " (" + l.elevation() + " m)");
                text.getTransforms().addAll(new Translate(x, labelPlace),
                        new Rotate(TEXT_ANGLE, 0, 0));
                Line line = new Line(x, labelPlace + PIXELS_ROOM, x, y);
                if(l.priority() > 0)
                    text.setFill(Color.RED);
                else if (l.priority() < 0)
                    text.setFill(Color.BLUE);
                nodes.addAll(Arrays.asList(text, line));
            }
        }
        return unmodifiableList(nodes);
    }

}
