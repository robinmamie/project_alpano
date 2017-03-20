package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.min;
import static java.lang.Math.max;

import java.util.Objects;

/**
 * Représente un intervalle unidimensionnel d'entiers. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Interval1D {

    /**
     * La borne inférieure de l'intervalle.
     */
    private final int includedFrom;

    /**
     * La borne supérieure de l'intervalle.
     */
    private final int includedTo;

    /**
     * Construit un intervalle unidimensionnel d'entiers.
     * 
     * @param includedFrom
     *            Entier représentant la borne inférieure de l'intervalle.
     * @param includedTo
     *            Entier représentant la borne supérieure de l'intervalle.
     * 
     * @throws IllegalArgumentException
     *             si la borne inférieure de l'intervalle passée en argument est
     *             supérieure à la borne supérireure.
     */
    public Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedFrom <= includedTo,
                "The upper bound is lower than the lower bound.");

        this.includedFrom = includedFrom;
        this.includedTo = includedTo;
    }

    /**
     * Retourne la borne inférieure de l'intervalle.
     * 
     * @return La borne inférieure de l'intervalle.
     */
    public int includedFrom() {
        return includedFrom;
    }

    /**
     * Retourne la borne supérieure de l'intervalle.
     * 
     * @return La borne supérieure de l'intervalle.
     */
    public int includedTo() {
        return includedTo;
    }

    /**
     * Permet d'indiquer si l'intervalle contient une certaine valeur passée en
     * argument.
     * 
     * @param v
     *            L'entier dont ou souhaite connaître l'apopartenance à
     *            l'intervalle.
     * 
     * @return si l'entier appartient à l'intervalle
     */
    public boolean contains(int v) {
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
     * @param that
     *            L'autre intervalle.
     * 
     * @return La taille de l'intersection entre deux intervalles.
     */
    public int sizeOfIntersectionWith(Interval1D that) {
        int size = min(this.includedTo(), that.includedTo())
                - max(this.includedFrom(), that.includedFrom()) + 1;
        return size < 0 ? 0 : size;
    }

    /**
     * Construit l'union englobante de deux intervalles.
     * 
     * @param that
     *            L'autre intervalle
     * 
     * @return L'union englobante de deux intervalles.
     */
    public Interval1D boundingUnion(Interval1D that) {
        return new Interval1D(min(this.includedFrom(), that.includedFrom()),
                max(this.includedTo(), that.includedTo()));
    }

    /**
     * Indique si deux intervalles sont unionables, i.e. si leur union produit
     * un autre intervalle.
     * 
     * @param that
     *            L'autre intervalle.
     * 
     * @return True si deux intervalles sont unionables.
     */
    public boolean isUnionableWith(Interval1D that) {
        return this.size() + that.size()
                - this.sizeOfIntersectionWith(that) == this.boundingUnion(that)
                        .size();
    }

    /**
     * Construit l'union de deux intervalles.
     * 
     * @param that
     *            autre intervalle
     * 
     * @return L'union de deux intervalles, qui est elle-même un intervalle
     *         unidimensionnel.
     * 
     * @throws IllegalArgumentException
     *             si les deux intervalles ne sont pas unionables.
     */
    public Interval1D union(Interval1D that) {
        checkArgument(this.isUnionableWith(that),
                "The union of the given Interval1Ds does not produce an Interval1D.");
        return this.boundingUnion(that);
    }

    @Override
    public boolean equals(Object thatO) {
        if (this == thatO)
            return true;

        if (thatO == null || getClass() != thatO.getClass())
            return false;

        Interval1D that = (Interval1D) thatO;

        return this.includedFrom() == that.includedFrom()
                && this.includedTo() == that.includedTo();
    }

    @Override
    public int hashCode() {
        return Objects.hash(includedFrom(), includedTo());
    }

    @Override
    public String toString() {
        return new StringBuilder("[").append(includedFrom()).append("..")
                .append(includedTo()).append("]").toString();
    }

}
