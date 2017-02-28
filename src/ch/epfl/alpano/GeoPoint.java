package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

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
     *                  
     * @throws IllegalArgumentException
     *                  si la latitude et la longitude ne se trouvent pas dans leur inetervalle standard
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument(-Math.PI <= longitude && longitude <= Math.PI, "invalid longitude");
        checkArgument(-Math.PI/2 <= latitude && latitude <= Math.PI/2, "invalid latitude");
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
                2 * Math.asin(
                        Math.sqrt(
                                Math2.haversin(this.latitude - that.latitude)
                + Math.cos(this.latitude) * Math.cos(that.latitude) * Math2.haversin(this.longitude - that.longitude))));
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
        return Azimuth.fromMath(Azimuth.canonicalize(Math.atan2(
                Math.sin(this.longitude - that.longitude) * Math.cos(that.latitude)
                , Math.cos(this.latitude) * Math.sin(that.latitude)
                - Math.sin(this.latitude) * Math.cos(that.latitude) * Math.cos(this.longitude - that.longitude))));
    }
    
    
    @Override
    public String toString() {
        Locale l = null;
        return String.format(l, "(%.4f,%.4f)", Math.toDegrees(longitude), Math.toDegrees(latitude));
    }
    
}
