package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;

import static java.lang.Math.tan;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;

public final class PanoramaComputer {

    private static final double factor = (1.0 - 0.13)
            / (2 * Distance.EARTH_RADIUS);
    private static final double limit = 64.0;
    private static final double epsilon = 4.0;

    private final ContinuousElevationModel dem;

    public PanoramaComputer(ContinuousElevationModel dem) {
        this.dem = requireNonNull(dem);
    }

    public Panorama computePanorama(PanoramaParameters parameters) {
        Panorama.Builder pb = new Panorama.Builder(parameters);
        double angle;
        for (int x = 0; x < parameters.width(); ++x) {
            ElevationProfile profile = new ElevationProfile(dem,
                    parameters.observerPosition(), parameters.azimuthForX(x),
                    parameters.maxDistance());
            double[] val = new double[parameters.height()+1];
            for (int y = parameters.height() - 1; y >= 0; --y) {
                angle = parameters.altitudeForY(y);
                DoubleUnaryOperator f = rayToGroundDistance(profile,
                        parameters.observerElevation(), angle);
                val[y] = firstIntervalContainingRoot(f,
                        val[y+1],
                        parameters.maxDistance(), limit);
                if (val[y] == Double.POSITIVE_INFINITY)
                    break;
                val[y] = improveRoot(f, val[y], val[y] + limit, epsilon);
                GeoPoint point = profile.positionAt(val[y]);
                pb.setDistanceAt(x, y, (float) (val[y] / Math.cos(angle)))
                        .setLongitudeAt(x, y, (float) point.longitude())
                        .setLatitudeAt(x, y, (float) point.latitude())
                        .setElevationAt(x, y,
                                (float) profile.elevationAt(val[y]))
                        .setSlopeAt(x, y, (float) profile.slopeAt(val[y]));
            }
        }
        return pb.build();
    }

    public static DoubleUnaryOperator rayToGroundDistance(
            ElevationProfile profile, double ray0, double raySlope) {
        return x -> ray0 + x * tan(raySlope) - profile.elevationAt(x)
                + factor * sq(x);
    }

}
