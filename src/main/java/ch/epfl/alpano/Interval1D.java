package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.hash;

/**
 * Représente un intervalle unidimensionnel d'entiers. Classe immuable.
 * 
 * @param includedFrom Entier représentant la borne inférieure de l'intervalle.
 * @param includedTo   Entier représentant la borne supérieure de l'intervalle.
 * 
 * @throws IllegalArgumentException si la borne inférieure de l'intervalle
 *                                  passée en argument est supérieure à la borne
 *                                  supérireure.
 *
 * @author Robin Mamié
 */
public record Interval1D(int includedFrom, int includedTo) {

	public Interval1D {
		checkArgument(includedFrom <= includedTo, "The upper bound is lower than the lower bound.");
	}

	/**
	 * Permet d'indiquer si l'intervalle contient une certaine valeur passée en
	 * argument.
	 * 
	 * @param v L'entier dont ou souhaite connaître l'apopartenance à l'intervalle.
	 * 
	 * @return si l'entier appartient à l'intervalle
	 */
	public boolean contains(final int v) {
		return includedFrom() <= v && v <= includedTo();
	}

	/**
	 * Calcule la taille de l'intervalle.
	 * 
	 * @return La taille de l'intervalle.
	 */
	public int size() {
		return includedTo() - includedFrom() + 1;
	}

	/**
	 * Calcule la taille de l'intersection entre deux intervalles.
	 * 
	 * @param that L'autre intervalle.
	 * 
	 * @return La taille de l'intersection entre deux intervalles.
	 */
	public int sizeOfIntersectionWith(final Interval1D that) {
		return max(0, min(this.includedTo(), that.includedTo()) - max(this.includedFrom(), that.includedFrom()) + 1);
	}

	/**
	 * Construit l'union englobante de deux intervalles.
	 * 
	 * @param that L'autre intervalle
	 * 
	 * @return L'union englobante de deux intervalles.
	 */
	public Interval1D boundingUnion(final Interval1D that) {
		return new Interval1D(min(this.includedFrom(), that.includedFrom()), max(this.includedTo(), that.includedTo()));
	}

	/**
	 * Indique si deux intervalles sont unionables, i.e. si leur union produit un
	 * autre intervalle.
	 * 
	 * @param that L'autre intervalle.
	 * 
	 * @return True si deux intervalles sont unionables.
	 */
	public boolean isUnionableWith(final Interval1D that) {
		return this.size() + that.size() - this.sizeOfIntersectionWith(that) == this.boundingUnion(that).size();
	}

	/**
	 * Construit l'union de deux intervalles.
	 * 
	 * @param that autre intervalle
	 * 
	 * @return L'union de deux intervalles, qui est elle-même un intervalle
	 *         unidimensionnel.
	 * 
	 * @throws IllegalArgumentException si les deux intervalles ne sont pas
	 *                                  unionables.
	 */
	public Interval1D union(final Interval1D that) {
		checkArgument(this.isUnionableWith(that), "The union of the given Interval1Ds does not produce an Interval1D.");
		return this.boundingUnion(that);
	}

	@Override
	public boolean equals(final Object thatO) {
		return thatO instanceof Interval1D that && this.includedFrom() == that.includedFrom()
				&& this.includedTo() == that.includedTo();
	}

	@Override
	public int hashCode() {
		return hash(includedFrom(), includedTo());
	}

	@Override
	public String toString() {
		return "[" + includedFrom() + ".." + includedTo() + "]";
	}

}
