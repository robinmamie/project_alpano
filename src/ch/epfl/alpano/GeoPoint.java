package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Locale;

/**
 * Représente un point géographique sur Terre.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class GeoPoint {
    
    
    /**
     * La longitude du point
     */
    private final double longitude;
    
    
    /**
     * La latitude du point
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
     * @throws InvalidArgumentException
     *                  si la latitude et la longitude ne se trouvent pas dans leur inetervalle standard
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument(-Math.PI <= longitude && longitude <= Math.PI, "invalid longitude");
        checkArgument(-Math.PI/2 <= latitude && latitude <= Math.PI/2, "invalid latitude");
        this.longitude = longitude;
        this.latitude  = latitude;
    }
    
    
    /**
     * Appelle le constructeur en transformant les angles en radians si l'utilisateur le demande.
     * 
     * @param longitude
     *                  la longitude du point, en radians, entre -Pi et Pi compris,
     *                  éventuellement entre -180° et 180° compris. 
     * @param latitude
     *                  la latitude du point, en radians. Doit se trouver entre -Pi/2 et Pi/2 compris.
     *                  Éventuellement en degrls, entre -90° et 90° compris.
     *                  
     * @param degrees   
     *                  désigne la nécessité d'une conversion en radians.
     */
    public GeoPoint(double longitude, double latitude, boolean degrees) {
        this(degrees ? Math.toRadians(longitude) : longitude, degrees ? Math.toRadians(latitude) : latitude);
    }
    
    
    /**
     * Retourne la longitude du point.
     * 
     * @return la longitude du point
     */
    public double longitude() { return longitude; }
    
    
    /**
     * Retourne la latitude du point.
     * 
     * @return la latitude du point
     */
    public double latitude()  { return latitude;  }
    
    
    /**
     * Calcule la distance entre deux points géographiques.
     * 
     * @param that
     *          un point géographique
     *          
     * @return la distance entre les deux points géographiques
     * 
     * @throws IllegalArgumentException
     *          si l'argument donné est null.
     */
    public double distanceTo(GeoPoint that) {
        checkArgument(that != null, "null argument given");
        return Distance.toMeters(
                2 * Math.asin(
                        Math.sqrt(
                                Math2.haversin(this.longitude - that.longitude)
                + Math.cos(this.longitude) * Math.cos(that.longitude) * Math2.haversin(this.latitude - that.latitude))));
    }
    
    
    /**
     * Calcule à quel azimuth se trouve un point d'un autre.
     * 
     * @param that
     *          un point géographique
     *          
     * @return l'azimuth du premier au second point géographique
     * 
     * @throws IllegalArgumentException
     *          si l'argument donné est null.
     */
    public double azimuthTo(GeoPoint that) {
        checkArgument(that != null, "null argument given");
        return Math.atan2(
                Math.sin(this.latitude - that.latitude) * Math.cos(that.longitude)
                , Math.cos(this.longitude) * Math.sin(that.longitude)
                - Math.sin(this.longitude) * Math.cos(that.longitude) * Math.cos(this.latitude - that.latitude));
    }
    
    
    @Override
    public String toString() {
        Locale l = null;
        return String.format(l, "(%.4f,%.4)", Math.toDegrees(longitude), Math.toDegrees(latitude));
    }
    
}
