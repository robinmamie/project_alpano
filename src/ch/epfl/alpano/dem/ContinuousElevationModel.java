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
    
    
    private double[] parametersForBilerp(GeoPoint p, boolean slope) {
        double[] t = new double[6];
        
        double aIndex = sampleIndex(p.longitude());
        double bIndex = sampleIndex(p.latitude());
        
        int a = (int)Math.floor(aIndex);
        int b = (int)Math.floor(bIndex);
        
        try {
        t[0] = dem.elevationSample(a  , b  );
        t[1] = dem.elevationSample(a+1, b  );
        t[3] = dem.elevationSample(a  , b+1);
        t[2] = dem.elevationSample(a+1, b+1);
        } catch(IllegalArgumentException e) {
            return new double[6];
        }
        
        if(slope) {
            double[] s = new double[4];
            final double d = Distance.toMeters(1 / SAMPLES_PER_RADIAN);
            for(int i = 0; i < 4; ++i)
                s[i] = Math.acos(d / Math.sqrt( Math2.sq(t[(i+1)%4] - t[i]) + Math2.sq(t[(i+2)%4] - t[i]) + d*d ));
            for(int i = 0; i < 4; ++i)
                t[i] = s[i];
        }
        
        double temp = t[2];
        t[2] = t[3];
        t[3] = temp;
        
        t[4]   = aIndex - a;
        t[5]   = bIndex - b;
        
        return t;
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
        double[] a = parametersForBilerp(p, false);
        
        return Math2.bilerp(a[0], a[1], a[2], a[3], a[4], a[5]);
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
        double[] a = parametersForBilerp(p, true);            
        
        return Math2.bilerp(a[0], a[1], a[2], a[3], a[4], a[5]);
    }

}
