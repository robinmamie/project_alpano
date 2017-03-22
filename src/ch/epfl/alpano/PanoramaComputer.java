package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;
import static ch.epfl.alpano.Math2.sq;
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

    private static final double k = 0.13;
    private static final double factor = (1.0 - k) / (2 * EARTH_RADIUS);
    private static final double limit = 64.0;
    private static final double epsilon = 4.0;

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
     * @return Le Panorama à l'aide des paramètres passés en argument.
     */
    public Panorama computePanorama(PanoramaParameters parameters) {
        Panorama.Builder pb = new Panorama.Builder(parameters);
        double angle;
        double[] val = new double[parameters.height() + 1];
        for (int x = 0; x < parameters.width(); ++x) {
            ElevationProfile profile = new ElevationProfile(dem,
                    parameters.observerPosition(), parameters.azimuthForX(x),
                    parameters.maxDistance());
            for (int y = parameters.height() - 1; y >= 0; --y) {
                angle = parameters.altitudeForY(y);
                DoubleUnaryOperator f = rayToGroundDistance(profile,
                        parameters.observerElevation(), angle);
                val[y] = firstIntervalContainingRoot(f, val[y + 1],
                        parameters.maxDistance(), limit);
                if (val[y] == Double.POSITIVE_INFINITY)
                    break;
                val[y] = improveRoot(f, val[y], val[y] + limit, epsilon);
                GeoPoint point = profile.positionAt(val[y]);
                pb.setDistanceAt(x, y, (float) (val[y] / cos(angle)))
                        .setLongitudeAt(x, y, (float) point.longitude())
                        .setLatitudeAt(x, y, (float) point.latitude())
                        .setElevationAt(x, y,
                                (float) profile.elevationAt(val[y]))
                        .setSlopeAt(x, y, (float) profile.slopeAt(val[y]));
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
        return x -> ray0 + x * tan(raySlope) - profile.elevationAt(x)
                + factor * sq(x);
    }

}
