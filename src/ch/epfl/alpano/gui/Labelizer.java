package ch.epfl.alpano.gui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public final class Labelizer {

    private final ContinuousElevationModel cem;
    private final List<Summit> summits;
    private Map<Summit, Integer[]> values;

    public Labelizer(ContinuousElevationModel cem, List<Summit> summits) {
        this.cem = cem;
        this.summits = summits;
        values = new HashMap<Summit, Integer[]>();
    }

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
            if (Math.abs(Math2.angularDistance(azimuth,
                    parameters.centerAzimuth())) > parameters
                            .horizontalFieldOfView() / 2.)
                continue;

            ElevationProfile profile = new ElevationProfile(cem,
                    parameters.observerPosition(), azimuth, distance);

            DoubleUnaryOperator g = PanoramaComputer.rayToGroundDistance(
                    profile, parameters.observerElevation(), 0);

            double altitude = Math.atan2(-g.applyAsDouble(distance), distance);
            if (Math.abs(altitude) > parameters.verticalFieldOfView() / 2.)
                continue;

            // Seconde condition, est réellement visible par l'observateur.
            double cross = Math2.firstIntervalContainingRoot(
                    PanoramaComputer.rayToGroundDistance(profile,
                            parameters.observerElevation(), Math.tan(altitude)),
                    0, distance - 200, 64);
            if (cross == Double.POSITIVE_INFINITY) {
                visible.add(s);

                values.put(s, new Integer[] {
                        (int) Math.round(parameters.xForAzimuth(azimuth)),
                        (int) Math.round(parameters.yForAltitude(altitude)) });

            }
        }

        visible.sort((x, y) -> {
            int higher = Integer.compare(values.get(x)[1], values.get(y)[1]);
            if (higher == 0)
                return Integer.compare(y.elevation(), x.elevation());
            return higher;
        });

        return visible;
    }

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
            if (yIndex > 170) {
                if (!positions.get(xIndex)
                        && positions.nextSetBit(xIndex) - xIndex >= 20) {
                    if (labelPlace == -1)
                        labelPlace = yIndex - 22;
                    Text t = new Text(s.name() + " (" + s.elevation() + " m)");
                    t.getTransforms().addAll(new Translate(xIndex, yIndex),
                            new Rotate(30, 0, 0));
                    nodes.add(t);
                    Line l = new Line();
                    l.setStartX(xIndex);
                    l.setStartY(labelPlace + 2);
                    l.setEndX(xIndex);
                    l.setEndY(yIndex);
                    nodes.add(l);
                }
            }
        }
        
        return nodes;
    }

}
