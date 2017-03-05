package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.lang.Math.floor;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.Distance;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.Math2;


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
    private final double d = Distance.toMeters(1 / SAMPLES_PER_RADIAN);


    /**
     * Construit un MNT continu à partir d'un MNT discret.
     * 
     * @param dem
     *          MNT discret
     *          
     * @throws NullPointerException
     *          si l'argument donné est null
     */
    public ContinuousElevationModel(DiscreteElevationModel dem) {
        this.dem    = requireNonNull(dem);
        this.extent = dem.extent();
    }


    /**
     * Retourne l'altitude correspondant à l'index donné.
     * 
     * @param x
     * 			index horizontal
     * @param y
     * 			index vertical
     * 
     * @return l'altitude du point du MNT, ou 0 si l'index se trouve en-dehors
     *          de son champ de définition.
     */
    private double elevationAtIndex(int x, int y) {
        if(extent.contains(x, y))
            return dem.elevationSample(x, y);

        return 0.0;
    }

    /**
     * Retourne une pente correspondant à l'index donné.
     * 
     * @param x
     * 			index horizontal
     * @param y
     * 			index vertical
     * 
     * @return la pente du point à l'index donné
     */
    private double slopeAtIndex(int x, int y) {
        double a = elevationAtIndex(x    , y    );
        double b = elevationAtIndex(x + 1, y    );
        double c = elevationAtIndex(x    , y + 1);

        return Math.acos(d / Math.sqrt( Math2.sq(b-a) + Math2.sq(c-a) + Math2.sq(d) ) );
    }


    /**
     * Donne l'altitude ou la pente correspondant aux parametres
     * donnes.
     * 
     * @param x
     * 			Parametre horizontal
     * @param y
     * 			Parametre vertical
     * @param slope
     * 			Determine si l'utilisateur d�sire la pente ou l'altitude
     * 
     * @return La pente ou l'altitude aux index donnés.
     */
    private double parameterAtIndex(int x, int y, boolean slope) {
        return slope ? slopeAtIndex(x, y) : elevationAtIndex(x, y);
    }


    /**
     * Produit l'interpolation linéaire selon les paramètres donnés.
     * 
     * @param p
     *          Un point géographique
     * @param slope
     *          Determine si l'utilisateur désire la pente ou l'altitude
     *          
     * @return L'interpolation bilinéaire (pente ou altitude) du point donné.
     */
    private double bilinearInterpolation(GeoPoint p, boolean slope) {
        double lon = sampleIndex(p.longitude());
        double lat = sampleIndex(p.latitude());

        int[]    i = { (int)floor(lon) , (int)floor(lat) };

        double z00 = parameterAtIndex(i[0]    , i[1]    , slope);
        double z10 = parameterAtIndex(i[0] + 1, i[1]    , slope);
        double z01 = parameterAtIndex(i[0]    , i[1] + 1, slope);
        double z11 = parameterAtIndex(i[0] + 1, i[1] + 1, slope);

        return bilerp(z00, z10, z01, z11, floorMod(lon, 1), floorMod(lat, 1));
    }


    /**
     * Retourne l'altitude au point donné, en mètres. Elle est obtenue par
     * interpolation bilinéaire du MNT discret donné au constructeur.
     * 
     * @param p
     *          un point géographique
     * 
     * @return l'altitude au point <code>p</code>
     */
    public double elevationAt(GeoPoint p) {
        return bilinearInterpolation(p, false);
    }


    /**
     * Retourne la pente du point donné, en radians. Elle est obtenue par
     * interpolation bilinéaire du MNT discret donné au constructeur.
     * 
     * @param p
     *          un point géographique
     *          
     * @return la pente au point <code>p</code>
     */
    public double slopeAt(GeoPoint p) {
        return bilinearInterpolation(p, true);
    }

}
