package ch.epfl.alpano.dem;

import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.Distance;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Math2;


/**
 * Représente un MNT continu. Classe immuable,.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class ContinuousElevationModel {


    /**
     * MNT discret utilisé.
     */
    private DiscreteElevationModel dem;

    private static final double d = Distance.toMeters(1 / SAMPLES_PER_RADIAN);


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
        this.dem = requireNonNull(dem);
    }

    private double elevationAtIndex(int x, int y) {
        return dem.elevationSample(x, y);
    }

    private double slopeAtIndex(int x, int y) {
        double a = elevationAtIndex(x  , y  );
        double b = elevationAtIndex(x+1, y  );
        double c = elevationAtIndex(x  , y+1);
        return Math.acos(d / Math.sqrt( Math2.sq(b-a) + Math2.sq(c-a) + d*d ));
    }


    private double[] parametersForBilerp(GeoPoint p, boolean slope) {
        double[] t = new double[6];

        double aIndex = sampleIndex(p.longitude());
        double bIndex = sampleIndex(p.latitude());

        int a = (int)Math.floor(aIndex);
        int b = (int)Math.floor(bIndex);

        try {
            for(int i = 0; i < 4; ++i) {
                int addA = (i%2 == 1) ? 1 : 0;
                int addB = (i > 1) ? 1 : 0;
                if(slope)
                    t[i] = slopeAtIndex(a + addA, b + addB);
                else
                    t[i] = elevationAtIndex(a + addA, b + addB);
                
            }
        } catch(IllegalArgumentException e) {
            return new double[6];
        }

        t[4]   = aIndex - a;
        t[5]   = bIndex - b;

        return t;
    }
    
    private double bilerpChoice(GeoPoint p, boolean slope) {
        double[] a = parametersForBilerp(p, slope);

        return Math2.bilerp(a[0], a[1], a[2], a[3], a[4], a[5]);
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
        return bilerpChoice(p, false);
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
        return bilerpChoice(p, true);
    }

}
