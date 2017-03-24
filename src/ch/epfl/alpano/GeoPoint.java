package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.fromMath;
import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.haversin;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.String.format;

import java.util.Locale;

/**
 * Représente un point géographique sur Terre. Il est défini par sa longitude
 * puis sa latitude. Classe immuable.
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
     * Construit un point géographique terrestre à l'aide de deux angles en
     * radians passés en argument.
     * 
     * @param longitude
     *            La longitude du point, en radians, entre -Pi et Pi compris.
     * @param latitude
     *            La latitude du point, en radians, entre -Pi/2 et Pi/2 compris.
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument(-PI <= longitude && longitude <= PI,
                "The given longitude is not defined between -Pi and Pi.");
        checkArgument(-PI / 2 <= latitude && latitude <= PI / 2,
                "The given latitude is not defined between -Pi/2 and Pi/2.");

        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Retourne la longitude du point en radians.
     * 
     * @return La longitude du point en radians.
     */
    public double longitude() {
        return longitude;
    }

    /**
     * Retourne la latitude du point en radians.
     * 
     * @return La latitude du point en radians.
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Calcule la distance en mètres entre deux points géographiques.
     * 
     * @param that
     *            L'autre point géographique dont on souhaite connaître la
     *            distance par rapport au premier.
     * 
     * @return La distance en mètres entre les deux points géographiques.
     */
    public double distanceTo(GeoPoint that) {
        return toMeters(2 * asin(sqrt(haversin(
                this.latitude() - that.latitude())
                + cos(this.latitude()) * cos(that.latitude())
                        * haversin(this.longitude() - that.longitude()))));
    }

    /**
     * Calcule à quel azimuth se trouve un point d'un autre.
     * 
     * @param that
     *            L'autre point géographique dont on souhaite savoir à quel
     *            azimut il se trouve du premier.
     * 
     * @return L'azimuth en radians à partir du premier point jusqu'au second
     *         point géographique.
     */
    public double azimuthTo(GeoPoint that) {
        return fromMath(canonicalize(atan2(
                sin(this.longitude() - that.longitude()) * cos(that.latitude()),
                cos(this.latitude()) * sin(that.latitude())
                        - sin(this.latitude()) * cos(that.latitude())
                                * cos(this.longitude() - that.longitude()))));
    }

    @Override
    public String toString() {
        Locale l = null;
        return format(l, "(%.4f,%.4f)", toDegrees(longitude()),
                toDegrees(latitude()));
    }

}
