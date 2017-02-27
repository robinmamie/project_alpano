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
    
    
    private double[] parametersForBilerp(GeoPoint p) {
        double[] param = new double[6];
        
        double aIndex = sampleIndex(p.longitude());
        double bIndex = sampleIndex(p.latitude());
        
        int a = (int)Math.floor(aIndex);
        int b = (int)Math.floor(bIndex);
        
        param[0] = dem.elevationSample(a  , b  );
        param[1] = dem.elevationSample(a+1, b  );
        param[2] = dem.elevationSample(a  , b+1);
        param[3] = dem.elevationSample(a+1, b+1);
        
        param[4]   = aIndex - a;
        param[5]   = bIndex - b;
        
        return param;
    }
    
    
    /**
     * Retourne l'altitude au point donné, en mètres. Elle est obtenue par
     * interpolation bilinéaire du MNT discret donné au constructeur.
     * 
     * @param p
     *          Point géographique.
     * 
     * @return l'altitude au point <code>p</code>
     */
    public double elevationAt(GeoPoint p) {
        double[] a = parametersForBilerp(p);
        
        return Math2.bilerp(a[0], a[1], a[2], a[3], a[4], a[5]);
    }
    
    
    public double slopeAt(GeoPoint p) {
        double[] a = parametersForBilerp(p);
        
        final double d = Distance.toMeters(1 / SAMPLES_PER_RADIAN);
        
        double slope = 0;
        
        for(int i = 0; i < 4; ++i);
            
        
        return Math.acos(d / Math.sqrt( Math2.sq(a[1] - a[0]) + Math2.sq(a[2] - a[0]) + d*d ));
    }

}
