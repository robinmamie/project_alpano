package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.util.Objects.requireNonNull;
import static java.lang.Math.floor;

import ch.epfl.alpano.Distance;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Math2;


/**
 * Représente un MNT continu. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class ContinuousElevationModel {


	/**
	 * MNT discret utilisé.
	 */
	private DiscreteElevationModel dem;

	private final double d    = Distance.toMeters(1 / SAMPLES_PER_RADIAN);
	
	private final double nInf = Double.NEGATIVE_INFINITY;


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
	
	
	/**
	 * Retourne les index correspondant au coint inf�rieur gauche du carr�
	 * unitaire dans lequel se trouve un point g�ographique.
	 * 
	 * @param p
	 * 			un point g�ogrpahique
	 * 
	 * @return les index du coin inf�rieur droit correspondant au point g�ographique.
	 */
	private int[] floorIndex(GeoPoint p) {
		double lon = sampleIndex(p.longitude());
		double lat = sampleIndex(p.latitude());
		
		int[] t    = {(int)floor(lon), (int)floor(lat)};
		
		return t;
	}
	
	
	/**
	 * Retourne la distance en index en coordonn�es cart�siennes par rapport au coin inf�rieur
	 * gauche du carr� unitaire dans lequel se trouve un point g�ographique.
	 * 
	 * @param p
	 * 			un point g�ographique
	 * 
	 * @return la distance en index du point par rapport au coin inf�rieure gauche
	 */
	private double[] modIndex(GeoPoint p) {
		double lon = sampleIndex(p.longitude());
		double lat = sampleIndex(p.latitude());
		
		double[] t = {floorMod(lon, 1), floorMod(lat, 1)};
		
		return t;
	}

	/**
	 * Retourne une altitude coh�rente correspondant � l'index donn�.
	 * 
	 * @param x
	 * 			index horizontal
	 * @param y
	 * 			index vertical
	 * 
	 * @return l'altitude du point ou Double.NEGATIVE_INFINITY si elle n'existe pas
	 * 			dans le MNT.
	 */
	private double elevationAtIndex(int x, int y) {
		if(!dem.extent().contains(x, y))
			return nInf;

		return dem.elevationSample(x, y);

	}

	/**
	 * Retourne une pente coh�rente correspondant � l'index donn�.
	 * 
	 * @param x
	 * 			index horizontal
	 * @param y
	 * 			index vertical
	 * 
	 * @return la pente du point ou Double.NEGATIVE_INFINITY si elle n'existe pas
	 * 			dans le MNT.
	 */
	private double slopeAtIndex(int x, int y) {
		double a = elevationAtIndex(x    , y    );
		double b = elevationAtIndex(x + 1, y    );
		double c = elevationAtIndex(x    , y + 1);

		if(a == nInf || b == nInf || c == nInf)
			return nInf;

		return Math.acos(d / Math.sqrt( Math2.sq(b-a) + Math2.sq(c-a) + d*d ) );
	}
	
	
	/**
	 * V�rifie si aucun des param�tres donn�s n'est �gal
	 * � Double.NEGATIVE_INFINITY et donc s'ils appartiennent
	 * au MNT.
	 * 
	 * @param a
	 * 			Premier parametre
	 * @param b
	 * 			Deuxieme parametre
	 * @param c
	 * 			Troisieme parametre
	 * @param d
	 * 			Quatrieme parametre
	 * 
	 * @return si les parametres sont contenus dans le MNT
	 */
	private boolean demContainsPoint(double a, double b, double c, double d) {
		return a == nInf || b == nInf || c == nInf || d == nInf;
	}
	
	
	private double parameterAtIndex(int x, int y, boolean slope) {
		if(slope)
			return slopeAtIndex(x, y);
		
		return elevationAtIndex(x, y);
	}
	
	private double bilinearInterpolation(GeoPoint p, boolean slope) {
		int[]    i = floorIndex(p);
		double[] v = modIndex(p);
		
		double z00 = parameterAtIndex(i[0]    , i[1]    , slope);
		double z10 = parameterAtIndex(i[0] + 1, i[1]    , slope);
		double z01 = parameterAtIndex(i[0]    , i[1] + 1, slope);
		double z11 = parameterAtIndex(i[0] + 1, i[1] + 1, slope);
		
		if(demContainsPoint(z00, z10, z01, z11))
			return 0.0;

		return bilerp(z00, z10, z01, z11, v[0], v[1]);
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
		return bilinearInterpolation(p, false);
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
		return bilinearInterpolation(p, true);
	}

}
