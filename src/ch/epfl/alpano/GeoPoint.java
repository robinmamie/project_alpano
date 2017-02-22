package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Locale;

public class GeoPoint {
    
    private final double longitude;
    private final double latitude;

    public GeoPoint(double longitude, double latitude) {
        checkArgument(-Math.PI <= longitude && longitude <= Math.PI, "invalid longitude");
        checkArgument(-Math.PI/2 <= latitude && latitude <= Math.PI/2, "invalid latitude");
        this.longitude = longitude;
        this.latitude  = latitude;
    }
    
    public GeoPoint(double longitude, double latitude, boolean degrees) {
        this(degrees ? Math.toRadians(longitude) : longitude, degrees ? Math.toRadians(latitude) : latitude);
    }
    
    public double longitude() { return longitude; }
    public double latitude()  { return latitude;  }
    
    public double distanceTo(GeoPoint that) {
        return Distance.toMeters(
                2 * Math.asin(
                        Math.sqrt(
                                Math2.haversin(this.longitude - that.longitude)
                + Math.cos(this.longitude) * Math.cos(that.longitude) * Math2.haversin(this.latitude - that.latitude))));
    }
    
    public double azimuthTo(GeoPoint that) {
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
