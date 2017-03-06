package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.toMath;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static ch.epfl.alpano.Math2.lerp;

import java.util.ArrayList;

import ch.epfl.alpano.Distance;
import ch.epfl.alpano.GeoPoint;

public final class ElevationProfile {

    private final ArrayList<GeoPoint> values;
    private final ContinuousElevationModel cem;
    private final double length;
    private final int step = 4096;

    public ElevationProfile(ContinuousElevationModel elevation
            , GeoPoint origin, double azimuth, double length) {
        checkArgument(isCanonical(azimuth), "the given azimuth is not canonical");
        
        this.cem    = elevation;
        this.length = length;
        this.values = new ArrayList<>();
        azimuth     = toMath(azimuth);

        for(int i = 0; i < length + step; i += step)
            values.add(newPoint(origin, azimuth, i));
    }

    private GeoPoint newPoint(GeoPoint p, double a, double x) {
        x          = Distance.toRadians(x);

        double lat = asin(sin(p.latitude())*cos(x) + cos(p.latitude())*sin(x)*cos(a));
        double lon = ((p.longitude() - asin(sin(a)*sin(x)/cos(lat)) + PI)%(2*PI)) - PI;
        
        return new GeoPoint(lon, lat);
    }

    public GeoPoint positionAt(double x) {
        checkArgument(0 <= x && x <= length, "position not defined in the ElevationProfile");
        double div    = Math.scalb(x, -12);
        int v         = (int)div;
        double s      = div % 1;
        GeoPoint fstP = values.get(v);
        GeoPoint sndP = values.get(v+1);
        double lon    = lerp(fstP.longitude(), sndP.longitude(), s);
        double lat    = lerp(fstP.latitude() , sndP.latitude() , s);
        
        return new GeoPoint(lon, lat);
    }

    public double elevationAt(double x) {
        return cem.elevationAt(positionAt(x));
    }

    public double slopeAt(double x) {
        return cem.slopeAt(positionAt(x));
    }

}
