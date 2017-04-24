package ch.epfl.alpano.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;

public final class Labelizer {

    private final ContinuousElevationModel cem;
    private final List<Summit> summits;

    public Labelizer(ContinuousElevationModel cem, List<Summit> summits) {
        this.cem = cem;
        this.summits = summits;
    }

    private List<Summit> visibleSummits(PanoramaParameters parameters) {
        List<Summit> visible = new ArrayList<Summit>();

        for (Summit s : summits) {
            // Première condition, se trouve dans la zone visible
            double azimuth = parameters.observerPosition()
                    .azimuthTo(s.position());
            if (Math.abs(Math2.angularDistance(azimuth,
                    parameters.centerAzimuth())) > parameters
                            .horizontalFieldOfView() / 2.)
                continue;

            double distance = parameters.observerPosition()
                    .distanceTo(s.position());
            if (distance > parameters.maxDistance())
                continue;

            double altitude = Math.atan2(
                    s.elevation() - parameters.observerElevation(), distance);
            if (Math.abs(altitude) > parameters.verticalFieldOfView() / 2.)
                continue;

            // Seconde condition, est réellement visible par l'observateur.
            ElevationProfile profile = new ElevationProfile(cem,
                    parameters.observerPosition(), azimuth, distance);
            
            // firstIntevalContainingRoot
        }

        return visible;
    }

    public List<Node> labels(PanoramaParameters parameters) {

        List<Summit> visible = visibleSummits(parameters);

        return null;
    }

}
