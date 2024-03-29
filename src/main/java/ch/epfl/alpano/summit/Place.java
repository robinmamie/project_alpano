package ch.epfl.alpano.summit;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import ch.epfl.alpano.GeoPoint;

public record Place(String name, GeoPoint position, int elevation, int priority) implements Labelizable, Serializable {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -580071766793003692L;

	public Place {
		requireNonNull(name, "The given name is null.");
		requireNonNull(position, "The given position is null.");
		checkArgument(-PRIORITY_RANGE <= priority && priority <= PRIORITY_RANGE, "The given priority is out of range.");
	}

}
