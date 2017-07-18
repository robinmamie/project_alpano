package ch.epfl.alpano;

import static ch.epfl.alpano.Distance.EARTH_RADIUS;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.cos;
import static java.lang.Math.tan;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Classe permettant de calculer un Panorama à l'aide d'un MNT continu. Classe
 * immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaComputer {

    /**
     * Le coefficient de réfraction.
     */
    private static final double K = 0.13;

    /**
     * Le facteur utilisé pour le calcul de la réfraction atmosphérique.
     */
    private static final double FACTOR = (1.0 - K) / (2 * EARTH_RADIUS);

    /**
     * Premier intervalle utilisé afin de trouver une racine à la fonction.
     */
    public static final double INTERVAL = 64;

    /**
     * Second intervalle utilisé afin d'affiner la recherche de la racine.
     */
    private static final double EPSILON = 4;

    private static final int THREAD_SPACING = 20;

    /**
     * MNT continu passé au constructeur.
     */
    private final ContinuousElevationModel dem;

    private final DoubleProperty status;

    private final boolean slopeNecessary;

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
    public PanoramaComputer(ContinuousElevationModel dem,
            boolean slopeNecessary) {
        this.dem = requireNonNull(dem,
                "The given ContinuousElevationModel is null.");
        status = new SimpleDoubleProperty(0d);
        this.slopeNecessary = slopeNecessary;
    }
    
    public PanoramaComputer(ContinuousElevationModel dem) {
        this(dem, true);
    }

    /**
     * Calcule le Panorama à l'aide des paramètres passés en argument.
     * 
     * @param parameters
     *            Les paramètres qui définissent le Panorama à construire.
     * 
     * @return Le Panorama créé à l'aide des paramètres passés en argument.
     * @throws InterruptedException
     */
    public Panorama computePanorama(PanoramaParameters parameters)
            throws InterruptedException {
        Panorama.Builder pb = new Panorama.Builder(parameters);
        List<Thread> threads = new ArrayList<>();
        status.set(0d);
        double statusIncrement = 1d / parameters.width();
        for (int i = 0; i < parameters.width(); i += THREAD_SPACING) {
            final int stop = i + THREAD_SPACING > parameters.width()
                    ? parameters.width() : i + THREAD_SPACING;
            threads.add(createThread(parameters, pb, i, stop, statusIncrement));
        }
        for (Thread r : threads)
            r.start();
        for (Thread r : threads)
            r.join();
        return pb.build();
    }

    private Thread createThread(PanoramaParameters parameters,
            Panorama.Builder pb, int start, int stop, double statusIncrement) {
        return new Thread() {
            @Override
            public void run() {
                for (int x = start; x < stop; ++x) {
                    ElevationProfile profile = new ElevationProfile(dem,
                            parameters.observerPosition(),
                            parameters.azimuthForX(x),
                            parameters.maxDistance());
                    double dist = 0;
                    for (int y = parameters.height() - 1; y >= 0; --y) {
                        double angle = parameters.altitudeForY(y);
                        DoubleUnaryOperator f = rayToGroundDistance(profile,
                                parameters.observerElevation(), tan(angle));
                        dist = firstIntervalContainingRoot(f, dist,
                                parameters.maxDistance(), INTERVAL);
                        if (dist == POSITIVE_INFINITY)
                            break;
                        dist = improveRoot(f, dist, dist + INTERVAL, EPSILON);
                        GeoPoint point = profile.positionAt(dist);
                        float distance = (float) (dist / cos(angle));
                        float longitude = (float) point.longitude();
                        float latitude = (float) point.latitude();
                        float elevation = (float) dem.elevationAt(point);
                        float slope = slopeNecessary
                                ? (float) dem.slopeAt(point) : 0;
                        synchronized (pb) {
                            pb.setDistanceAt(x, y, distance)
                                    .setLongitudeAt(x, y, longitude)
                                    .setLatitudeAt(x, y, latitude)
                                    .setElevationAt(x, y, elevation)
                                    .setSlopeAt(x, y, slope);
                        }
                    }
                    synchronized (status) {
                        status.set(status.get() + statusIncrement);
                    }
                }
            }
        };
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

    public ReadOnlyDoubleProperty statusProperty() {
        return status;
    }

}
