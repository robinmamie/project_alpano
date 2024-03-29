package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Représente un sommet alpin par son nom, sa position et son altitude. Classe
 * immuable.
 * 
 * @param name      Le nom du sommet.
 * @param position  La position du sommet.
 * @param elevation L'altitude du sommet.
 *
 * @author Robin Mamié
 */
public record Summit(String name, GeoPoint position, int elevation) implements Labelizable {

	/**
	 * Construit un sommet.
	 * 
	 * @throws NullPointerException si le nom ou la position sont null
	 */
	public Summit {
		requireNonNull(name, "The given name is null.");
		requireNonNull(position, "The given position is null.");
	}

	@Override
	public int priority() {
		return 0;
	}

}
