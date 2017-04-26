package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.PanoramaComputer.rayToGroundDistance;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Integer.compare;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.round;
import static java.lang.Math.tan;
import static java.math.RoundingMode.HALF_UP;

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

    private final static int TOLERANCE = 200;

    private final ContinuousElevationModel cem;
    private final List<Summit> summits;
    private Map<Summit, Integer[]> values;

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
        this.summits = summits;
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
            // Première condition, se trouve dans la zone visible
            double distance = parameters.observerPosition()
                    .distanceTo(s.position());
            if (distance > parameters.maxDistance())
                continue;

            double azimuth = parameters.observerPosition()
                    .azimuthTo(s.position());
            if (abs(angularDistance(azimuth,
                    parameters.centerAzimuth())) > parameters
                            .horizontalFieldOfView() / 2.)
                continue;

            ElevationProfile profile = new ElevationProfile(cem,
                    parameters.observerPosition(), azimuth, distance);

            double altitude = atan2(-rayToGroundDistance(profile,
                    parameters.observerElevation(), 0).applyAsDouble(distance),
                    distance);
            if (abs(altitude) > parameters.verticalFieldOfView() / 2.)
                continue;

            // Seconde condition, est réellement visible par l'observateur.
            double cross = firstIntervalContainingRoot(
                    rayToGroundDistance(profile, parameters.observerElevation(),
                            tan(altitude)),
                    0, distance - TOLERANCE, 64);
            if (cross == POSITIVE_INFINITY) {
                visible.add(s);

                values.put(s, new Integer[] {
                        (int) round(parameters.xForAzimuth(azimuth)),
                        (int) round(parameters.yForAltitude(altitude)) });
            }
        }
        visible.sort((x, y) -> {
            int higher = compare(values.get(x)[1], values.get(y)[1]);
            if (higher == 0)
                return compare(y.elevation(), x.elevation());
            return higher;
        });
        return visible;
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
        positions.flip(0, 20);
        positions.flip(positions.size() - 21, positions.size());
        int labelPlace = -1;

        for (Summit s : visible) {
            int xIndex = values.get(s)[0];
            int yIndex = values.get(s)[1];
            if (yIndex > 170 && !positions.get(xIndex)
                    && positions.nextSetBit(xIndex) - xIndex >= 20) {
                if (labelPlace == -1)
                    labelPlace = yIndex - 22;
                positions.flip(xIndex, xIndex + 20);
                Text t = new Text(s.name() + " (" + s.elevation() + " m)");
                t.getTransforms().addAll(new Translate(xIndex, yIndex),
                        new Rotate(60, 0, 0));
                nodes.add(t);
                Line l = new Line();
                l.setStartX(xIndex);
                l.setStartY(labelPlace + 2);
                l.setEndX(xIndex);
                l.setEndY(yIndex);
                nodes.add(l);
            }
        }
        return nodes;
    }

}
