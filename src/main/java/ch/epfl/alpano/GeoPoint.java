package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.fromMath;
import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.HALF_PI;
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

import java.io.Serializable;
import java.util.Locale;

/**
 * Représente un point géographique sur Terre. Il est défini par sa longitude
 * puis sa latitude. Classe immuable.
 * 
 * @param longitude La longitude du point, en radians, entre -Pi et Pi compris.
 * @param latitude  La latitude du point, en radians, entre -Pi/2 et Pi/2
 *                  compris.
 *
 * @author Robin Mamié
 */
public record GeoPoint(double longitude, double latitude) implements Serializable {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5086585006724787009L;

	public GeoPoint {
		checkArgument(-PI <= longitude && longitude <= PI, "The given longitude is not defined between -Pi and Pi.");
		checkArgument(-HALF_PI <= latitude && latitude <= HALF_PI,
				"The given latitude is not defined between -Pi/2 and Pi/2.");
	}

	/**
	 * Calcule la distance en mètres entre deux points géographiques.
	 * 
	 * @param that L'autre point géographique dont on souhaite connaître la distance
	 *             par rapport au premier.
	 * 
	 * @return La distance en mètres entre les deux points géographiques.
	 */
	public double distanceTo(final GeoPoint that) {
		return toMeters(2 * asin(sqrt(haversin(this.latitude() - that.latitude())
				+ cos(this.latitude()) * cos(that.latitude()) * haversin(this.longitude() - that.longitude()))));
	}

	/**
	 * Calcule à quel azimuth se trouve un point d'un autre.
	 * 
	 * @param that L'autre point géographique dont on souhaite savoir à quel azimut
	 *             il se trouve du premier.
	 * 
	 * @return L'azimuth en radians à partir du premier point jusqu'au second point
	 *         géographique.
	 */
	public double azimuthTo(final GeoPoint that) {
		return fromMath(canonicalize(atan2(sin(this.longitude() - that.longitude()) * cos(that.latitude()),
				cos(this.latitude()) * sin(that.latitude())
						- sin(this.latitude()) * cos(that.latitude()) * cos(this.longitude() - that.longitude()))));
	}

	@Override
	public String toString() {
		return format((Locale) null, "(%.4f,%.4f)", toDegrees(longitude()), toDegrees(latitude()));
	}

}
