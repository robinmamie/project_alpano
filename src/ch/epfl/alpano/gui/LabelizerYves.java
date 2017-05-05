package ch.epfl.alpano.gui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

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

/**
 *
 *
 * @author Charline Montial (274902)
 * @author Yves Zumbach (269845)
 */
public final class LabelizerYves {

    private final static int BIG_PIXEL_LIMIT_TO_DRAW_LABEL = 170;
    private final static int SMALL_PIXEL_LIMIT_TO_DRAW_LABEL = 20;
    private final static int DISTANCE_FROM_LINE_TO_LABEL = 2;
    private final static int TOLERANCE = 200;
    private final static int MIN_DISTANCE_BETWEEN_LABEL = 20;
    private final static int LABEL_ROTATION = 60;

    private final ContinuousElevationModel cem;
    private final List<Summit> summits;

    public LabelizerYves(ContinuousElevationModel cem, List<Summit> summits) {
        this.cem = cem;
        this.summits = summits;
    }

    private List<VisibleSummit> visibleSummits(PanoramaParameters p) {
        List<VisibleSummit> visibleSummits = new ArrayList<>();
        for (Summit s : summits) {
            double azimuthfromObserverToSummit = p.observerPosition()
                    .azimuthTo(s.position());
            if (Math.abs(Math2.angularDistance(azimuthfromObserverToSummit,
                    p.centerAzimuth())) >= p.horizontalFieldOfView() / 2.0) {
                continue;
            }
            double distanceFromObserverToSummit = p.observerPosition()
                    .distanceTo(s.position());
            if (distanceFromObserverToSummit > p.maxDistance()) {
                continue;
            }
            ElevationProfile profile = new ElevationProfile(cem,
                    p.observerPosition(), azimuthfromObserverToSummit,
                    distanceFromObserverToSummit);
            double summitElevationInImage = Math.atan2(
                    -PanoramaComputer
                            .rayToGroundDistance(profile, p.observerElevation(),
                                    0)
                            .applyAsDouble(distanceFromObserverToSummit),
                    distanceFromObserverToSummit);
            if (Math.abs(summitElevationInImage) >= p.verticalFieldOfView()
                    / 2) {
                continue;
            }

            if (Math2.firstIntervalContainingRoot(
                    PanoramaComputer.rayToGroundDistance(profile,
                            p.observerElevation(),
                            Math.tan(summitElevationInImage)),
                    0, distanceFromObserverToSummit - TOLERANCE,
                    PanoramaComputer.INTERVAL) == Double.POSITIVE_INFINITY) {
                visibleSummits.add(new VisibleSummit(s,
                        p.xForAzimuth(azimuthfromObserverToSummit),
                        p.yForAltitude(summitElevationInImage)));
            }
        }
        return visibleSummits;
    }

    public List<Node> labels(PanoramaParameters p) {
        List<VisibleSummit> visibleSummits = visibleSummits(p);
        visibleSummits.forEach(System.out::println);

        visibleSummits.sort((vs1, vs2) -> {
            int higherPixel = Integer.compare(vs1.correspondingVerticalPixel,
                    vs2.correspondingVerticalPixel);
            return (higherPixel == 0)
                    ? Integer.compare(vs2.getSummit().elevation(),
                            vs1.getSummit().elevation())
                    : higherPixel;
        });

        Iterator<VisibleSummit> it = visibleSummits.iterator();
        while (it.hasNext()) {
            if (!isInLabelizableArea(p, it.next(),
                    BIG_PIXEL_LIMIT_TO_DRAW_LABEL)) {
                it.remove();
            }
        }

        BitSet bs = new BitSet(p.width());
        bs.flip(0, SMALL_PIXEL_LIMIT_TO_DRAW_LABEL);
        bs.flip(p.width() - SMALL_PIXEL_LIMIT_TO_DRAW_LABEL - 1, p.width());
        List<VisibleSummit> summitsToLabelize = new ArrayList<>();
        /**
         * Contains the height at which the text label must begin.
         */
        int minSummitPixelHeight = p.height();
        for (VisibleSummit vs : visibleSummits) {
            if (areNextPixelsFree(bs, vs.correspondingHorizontalPixel)) {
                summitsToLabelize.add(vs);
                // TODO: Improve
                if (minSummitPixelHeight > vs.getCorrespondingVerticalPixel())
                    minSummitPixelHeight = vs.getCorrespondingVerticalPixel();
                setNextPixelsToFalse(bs, vs.getCorrespondingHorizontalPixel());
            } else {
                // System.out.println(vs + ": collides with another summit!");
            }
        }

        minSummitPixelHeight -= 20;
        List<Node> labels = new ArrayList<>(visibleSummits.size());
        for (VisibleSummit vs : summitsToLabelize) {
            addSummitLabel(vs, labels, minSummitPixelHeight);
        }
        return labels;
    }

    private boolean isInLabelizableArea(PanoramaParameters p, VisibleSummit vs,
            int topOffset) {
        return vs.getCorrespondingVerticalPixel() > topOffset;
    }

    private void addSummitLabel(VisibleSummit vs, List<Node> labels,
            int minSummitPixelHeight) {
        labels.add(new Line(vs.getCorrespondingHorizontalPixel(),
                vs.getCorrespondingVerticalPixel(),
                vs.getCorrespondingHorizontalPixel(), minSummitPixelHeight));
        Text t = new Text(vs.getCorrespondingHorizontalPixel(),
                minSummitPixelHeight - DISTANCE_FROM_LINE_TO_LABEL,
                vs.summit.name() + "(" + vs.summit.elevation() + ")");
        t.getTransforms().add(new Rotate(LABEL_ROTATION, 0, 0));
        labels.add(t);
    }

    private void setNextPixelsToFalse(BitSet bs, int index) {
        bs.set(index, index + MIN_DISTANCE_BETWEEN_LABEL);
    }

    private boolean areNextPixelsFree(BitSet freePixels, int index) {
        return !freePixels.get(index) && freePixels
                .get(index, index + MIN_DISTANCE_BETWEEN_LABEL).isEmpty();
    }

    private static class VisibleSummit {
        private final Summit summit;
        private final int correspondingHorizontalPixel;
        private final int correspondingVerticalPixel;

        public VisibleSummit(Summit s, double horizontalPixel,
                double verticalPixel) {
            this.summit = s;
            this.correspondingVerticalPixel = (int) Math.round(verticalPixel);
            this.correspondingHorizontalPixel = (int) Math
                    .round(horizontalPixel);
        }

        /**
         * @return the summit
         */
        public Summit getSummit() {
            return summit;
        }

        /**
         * @return the correspondingVerticalPixel
         */
        public int getCorrespondingVerticalPixel() {
            return correspondingVerticalPixel;
        }

        /**
         * @return the correspondingHorizontalPixel
         */
        public int getCorrespondingHorizontalPixel() {
            return correspondingHorizontalPixel;
        }

        public String toString() {
            return summit.toString();
        }
    }
}