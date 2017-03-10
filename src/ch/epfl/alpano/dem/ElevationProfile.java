package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.toMath;
import static ch.epfl.alpano.Distance.toRadians;
import static ch.epfl.alpano.Math2.lerp;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.scalb;
import static java.lang.Math.sin;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

import ch.epfl.alpano.GeoPoint;

/**
 * Représente un profil altimétrique suivant un arc
 * de grand cercle. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class ElevationProfile {

    private final ArrayList<GeoPoint> values;
    private final ContinuousElevationModel cem;
    private final double length;
    private final int step = 4096;

    /**
     * Construit un profil altimétrique.
     * 
     * @param elevation
     *          MNT continu dans lequel la classe doit chercher
     *          ses valeurs.
     * @param origin
     *          Point d'origine du profil altimétrique.
     * @param azimuth
     *          Azimuth (sous forme canonique) qui détermine la
     *          direction du profil altimétrique.
     * @param length
     *          Longueur (strictement positive) du profil altimétrique (en mètres).
     */
    public ElevationProfile(ContinuousElevationModel elevation
            , GeoPoint origin, double azimuth, double length) {
        
        this.cem    = requireNonNull(elevation);
        requireNonNull(origin);
        
        checkArgument(0 < length, "the given length isn't strictly positive");
        this.length = length;
        
        checkArgument(isCanonical(azimuth), "the given azimuth is not canonical");
        azimuth     = toMath(azimuth);

        this.values = new ArrayList<>();
        
        for(int i = 0; i < length + step; i += step)
            values.add(newPoint(origin, azimuth, toRadians(i)));
    }

    /**
     * Calcule les points utiles pour l'interpolation linéaire.
     * 
     * @param p
     *          Point d'origine du profil.
     * @param a
     *          Azimuth du profil (angle mathématique).
     * @param x
     *          Distance du point par rapport à l'origine
     *          (en radians).
     *          
     * @return Point utile pour l'interpolation linéaire.
     */
    private GeoPoint newPoint(GeoPoint p, double a, double x) {
        double lat = asin(sin(p.latitude())*cos(x) + cos(p.latitude())*sin(x)*cos(a));
        double lon = ((p.longitude() - asin(sin(a)*sin(x)/cos(lat)) + PI)%(2*PI)) - PI;

        return new GeoPoint(lon, lat);
    }

    
    /**
     * Calcule la position du point dans le profil altimétrique
     * situé à la distance demandée de l'origine.
     * 
     * @param x
     *          Distance demandée (en mètres).
     *          
     * @return Le point demandé après interpolation linéaire.
     */
    public GeoPoint positionAt(double x) {
        checkArgument(0 <= x && x <= length, "position not defined in the ElevationProfile");
        double   div  = scalb(x, -12);
        int      v    = (int)div;
        double   s    = div % 1;
        GeoPoint fstP = values.get(v);
        GeoPoint sndP = values.get(v+1);
        double   lon  = lerp(fstP.longitude(), sndP.longitude(), s);
        double   lat  = lerp(fstP.latitude() , sndP.latitude() , s);
        
        return new GeoPoint(lon, lat);
    }

    
    /**
     * Retourne l'altitude du point dans le profil altimétrique
     * situé à la distance demandée de l'origine.
     * 
     * @param x
     *          Distance demandée (en mètres).
     *          
     * @return L'altitude à la distance demandée.
     */
    public double elevationAt(double x) {
        return cem.elevationAt(positionAt(x));
    }

    
    /**
     * Retourne la pente au point situé dans le profil altimétrique
     * à la distance demandée de l'origine.
     * 
     * @param x
     *          Distance demandée (en mètres).
     *          
     * @return La pente à la distance demandée.
     */
    public double slopeAt(double x) {
        return cem.slopeAt(positionAt(x));
    }

}
