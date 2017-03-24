package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;
import static ch.epfl.alpano.Distance.EARTH_RADIUS;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;

/**
 * Classe permettant de calculer un Panorama à l'aide d'un MNT continu. Classe
 * immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaComputer {

    private static final double K = 0.13;
    private static final double FACTOR = (1.0 - K) / (2 * EARTH_RADIUS);
    private static final double LIMIT = 64.0;
    private static final double EPSILON = 4.0;

    private final ContinuousElevationModel dem;

    /**
     * Constructeur de la classe PanoramaComputer prenant un MNT continu en
     * argument.
     * 
     * @param dem
     *            Le MNT continu qui permettra de construire le Panorama.
     * 
     * @throws NullPointerException
     *             si le MNT passé en argument est null.
     */
    public PanoramaComputer(ContinuousElevationModel dem) {
        this.dem = requireNonNull(dem);
    }

    /**
     * Calcule le Panorama à l'aide des paramètres passés en argument.
     * 
     * @param parameters
     *            Les paramètres qui définissent le Panorama à construire.
     * 
     * @return Le Panorama créé à l'aide des paramètres passés en argument.
     */
    public Panorama computePanorama(PanoramaParameters parameters) {
        Panorama.Builder pb = new Panorama.Builder(parameters);
        for (int x = 0; x < parameters.width(); ++x) {
            ElevationProfile profile = new ElevationProfile(dem,
                    parameters.observerPosition(), parameters.azimuthForX(x),
                    parameters.maxDistance());
            double dist = 0;
            for (int y = parameters.height() - 1; y >= 0; --y) {
                double angle = parameters.altitudeForY(y);
                DoubleUnaryOperator f = rayToGroundDistance(profile,
                        parameters.observerElevation(), tan(angle));
                dist = firstIntervalContainingRoot(f, dist,
                        parameters.maxDistance(), LIMIT);
                if (dist == Double.POSITIVE_INFINITY)
                    break;
                dist = improveRoot(f, dist, dist + LIMIT, EPSILON);
                GeoPoint point = profile.positionAt(dist);
                pb.setDistanceAt(x, y, (float) (dist / cos(angle)))
                        .setLongitudeAt(x, y, (float) point.longitude())
                        .setLatitudeAt(x, y, (float) point.latitude())
                        .setElevationAt(x, y, (float) profile.elevationAt(dist))
                        .setSlopeAt(x, y, (float) profile.slopeAt(dist));
            }
        }
        return pb.build();
    }

    /**
     * Définit la fonction donnant la distance des rayons-vecteurs lancés depuis
     * l'observateur par rapport au sol.
     * 
     * @param profile
     *            Le profil calculé au préalable. Permet de connaître l'altitude
     *            du sol en tout point sous le rayon vecteur.
     * @param ray0
     *            L'élévation de l'obervateur d'où partent les rayons-vecteurs.
     * @param raySlope
     *            La pente du rayon-vecteur.
     * 
     * @return Un DoubleUnaryOperator donnant la distance des rayons-vecteurs
     *         lancés depuis l'observateur par rapport au sol.
     */
    public static DoubleUnaryOperator rayToGroundDistance(
            ElevationProfile profile, double ray0, double raySlope) {
        return x -> ray0 + x * (raySlope + FACTOR * x) - profile.elevationAt(x);
    }

}
