package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.lang.Math.acos;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;
import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT continu. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class ContinuousElevationModel {

    /**
     * MNT discret utilisé.
     */
    private final DiscreteElevationModel dem;

    /**
     * Étendue du MNT utilisé.
     */
    private final Interval2D extent;

    /**
     * Distance prise en compte pour le calcul de la pente
     */
    private static final double D = toMeters(1 / SAMPLES_PER_RADIAN);

    /**
     * Construit un MNT continu à partir d'un MNT discret.
     * 
     * @param dem
     *            Un MNT discret.
     * 
     * @throws NullPointerException
     *             si le MNT discret donné est null.
     */
    public ContinuousElevationModel(DiscreteElevationModel dem) {
        this.dem = requireNonNull(dem, "The given DEM is null.");

        /*
         * Sauvegarde directement l'étendue du MNT afin de gagner en vitesse
         * d'exécution.
         */
        this.extent = dem.extent();
    }

    /**
     * Retourne l'altitude correspondant à l'index donné.
     * 
     * @param x
     *            L'index de la longitude.
     * @param y
     *            L'index de la latitude.
     * 
     * @return L'altitude du point du MNT discret, ou 0 si l'index se trouve
     *         en-dehors de son champ de définition.
     */
    private double elevationAtIndex(int x, int y) {
        return extent.contains(x, y) ? dem.elevationSample(x, y) : 0;
    }

    /**
     * Retourne la pente correspondant à l'index donné.
     * 
     * @param x
     *            L'index de la longitude.
     * @param y
     *            L'index de la latitude.
     * 
     * @return La pente du point à l'index donné.
     */
    private double slopeAtIndex(int x, int y) {
        double a = elevationAtIndex(x, y);
        double b = elevationAtIndex(x + 1, y);
        double c = elevationAtIndex(x, y + 1);

        return acos(D / sqrt(sq(b - a) + sq(c - a) + sq(D)));
    }

    /**
     * Produit l'interpolation linéaire selon les paramètres donnés.
     * 
     * @param p
     *            Un point géographique
     * @param par
     *            Determine la fonction à utiliser pour l'interpolation
     *            bilinéaire.
     * 
     * @return L'interpolation bilinéaire des valeurs correspondant à la
     *         fonction passée en paramètre du point donné.
     */
    private double bilinearInterpolation(GeoPoint p,
            BiFunction<Integer, Integer, Double> par) {
        double lon = sampleIndex(p.longitude());
        double lat = sampleIndex(p.latitude());

        int indX = (int) floor(lon);
        int indY = (int) floor(lat);

        double z00 = par.apply(indX, indY);
        double z10 = par.apply(indX + 1, indY);
        double z01 = par.apply(indX, indY + 1);
        double z11 = par.apply(indX + 1, indY + 1);

        return bilerp(z00, z10, z01, z11, lon - indX, lat - indY);
    }

    /**
     * Retourne l'altitude au point donné, en mètres. Elle est obtenue par
     * interpolation bilinéaire du MNT discret donné au constructeur.
     * 
     * @param p
     *            Le point géographique dont on souhaite connaître l'altitude.
     * 
     * @return L'altitude au point passé en argument.
     */
    public double elevationAt(GeoPoint p) {
        return bilinearInterpolation(p, (x, y) -> elevationAtIndex(x, y));
    }

    /**
     * Retourne la pente du point donné, en radians. Elle est obtenue par
     * interpolation bilinéaire du MNT discret donné au constructeur.
     * 
     * @param p
     *            Le point géographique dont on souhaite connaître la pente.
     * 
     * @return La pente au point passé en argument.
     */
    public double slopeAt(GeoPoint p) {
        return bilinearInterpolation(p, (x, y) -> slopeAtIndex(x, y));
    }

}
