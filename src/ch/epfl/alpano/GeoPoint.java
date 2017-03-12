package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.Math2.haversin;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.asin;
import static java.lang.Math.sqrt;
import static java.lang.Math.atan2;

import java.util.Locale;

/**
 * Représente un point géographique sur Terre. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class GeoPoint {
    
    
    /**
     * La longitude du point en radians, entre -Pi et Pi compris
     */
    private final double longitude;
    
    
    /**
     * La latitude du point, entre -Pi/2 et Pi/2 compris
     */
    private final double latitude;

    
    /**
     * Construit un point géographique terrestre.
     * 
     * @param longitude
     *                  la longitude du point, en radians, entre -Pi et Pi compris.
     * @param latitude
     *                  la latitude du point, en radians, entre -Pi/2 et Pi/2 compris.
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument(-PI <= longitude && longitude <= PI
                , "The given longitude is out of range.");
        checkArgument(-PI/2 <= latitude && latitude <= PI/2
                , "The given latitude is out of range.");
        
        this.longitude = longitude;
        this.latitude  = latitude;
    }
    
    
    /**
     * Retourne la longitude du point en radians.
     * 
     * @return la longitude du point en radians
     */
    public double longitude() { return longitude; }
    
    
    /**
     * Retourne la latitude du point en radians.
     * 
     * @return la latitude du point en radians
     */
    public double latitude()  { return latitude;  }
    
    
    /**
     * Calcule la distance en mètres entre deux points géographiques.
     * 
     * @param that
     *          un autre point géographique
     *          
     * @return la distance en mètres entre les deux points géographiques
     */
    public double distanceTo(GeoPoint that) {
        return Distance.toMeters(
                2 * asin(sqrt(haversin(this.latitude - that.latitude)
                + cos(this.latitude) * cos(that.latitude) * haversin(this.longitude - that.longitude))));
    }
    
    
    /**
     * Calcule à quel azimuth se trouve un point d'un autre.
     * 
     * @param that
     *          un autre point géographique
     *          
     * @return l'azimuth en radians du premier au second point géographique
     */
    public double azimuthTo(GeoPoint that) {
        return Azimuth.fromMath(Azimuth.canonicalize(atan2(
                sin(this.longitude - that.longitude) * cos(that.latitude)
                , cos(this.latitude) * sin(that.latitude)
                - sin(this.latitude) * cos(that.latitude) * cos(this.longitude - that.longitude))));
    }
    
    
    @Override
    public String toString() {
        Locale l = null;
        return String.format(l, "(%.4f,%.4f)", Math.toDegrees(longitude), Math.toDegrees(latitude));
    }
    
}
