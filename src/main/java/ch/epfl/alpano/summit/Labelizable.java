package ch.epfl.alpano.summit;

import ch.epfl.alpano.GeoPoint;

public interface Labelizable {

	int PRIORITY_RANGE = 5;

	/**
	 * Donne la priorité d'une étiquette par rapport à une autre.
	 * 
	 * @return la priorité d'une étiquette par rapport à une autre. Un grand chiffre
	 *         indique une grande priorité.
	 */
	int priority();

	/**
	 * Retourne le nom de l'étiquette.
	 * 
	 * @return Le nom de l'étiquette.
	 */
	String name();

	/**
	 * Retourne la position de l'étiquette.
	 * 
	 * @return La position de l'étiquette.
	 */
	GeoPoint position();

	/**
	 * Retourne l'altitude de l'étiquette.
	 * 
	 * @return L'altitude de l'étiquette.
	 */
	int elevation();
}
