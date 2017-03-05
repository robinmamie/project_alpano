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
     * Retourne une altitude cohérente correspondant à l'index donné.
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
     * Calcule l'élévation d'un point selon un index réel.
     * 
     * @param x
     *          Premier index réel
     * @param y
     *          Second index réel
     *          
     * @return l'interpolation de l'altitude aux index donnés
     */
    private double elevationAt(double x, double y) {
        int[]  i   = { (int)floor(x) , (int)floor(y) };

        double z00 = elevationAtIndex(i[0]    , i[1]    );
        double z10 = elevationAtIndex(i[0] + 1, i[1]    );
        double z01 = elevationAtIndex(i[0]    , i[1] + 1);
        double z11 = elevationAtIndex(i[0] + 1, i[1] + 1);

        return bilerp(z00, z10, z01, z11, floorMod(x, 1), floorMod(y, 1));
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
        double lon = sampleIndex(p.longitude());
        double lat = sampleIndex(p.latitude());

        return elevationAt(lon, lat);
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
        double lon = sampleIndex(p.longitude());
        double lat = sampleIndex(p.latitude());

        double a   = elevationAt(lon    , lat    );
        double b   = elevationAt(lon + 1, lat    );
        double c   = elevationAt(lon    , lat + 1);

        return Math.acos(d / Math.sqrt( Math2.sq(b-a) + Math2.sq(c-a) + Math2.sq(d) ) );
    }

}
