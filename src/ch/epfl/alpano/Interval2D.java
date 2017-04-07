package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * Représente un intervalle bidimensionnel d'entiers, composé du produit
 * cartésien de deux intervalles unidimensionnels. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Interval2D {

    /**
     * Premier intervalle du produit cartésien.
     */
    private final Interval1D iX;

    /**
     * Second intervalle du produit cartésien.
     */
    private final Interval1D iY;

    /**
     * Construit un intervalle bidimensionnel.
     * 
     * @param iX
     *            Le premier intervalle unidimensionnel.
     * @param iY
     *            Le second intervalle unidimensionnel.
     * 
     * @throws NullPointerException
     *             si l'un des deux intervalles donnés en arguments est null.
     */
    public Interval2D(Interval1D iX, Interval1D iY) {
        this.iX = requireNonNull(iX, "The first Interval1D given is null.");
        this.iY = requireNonNull(iY, "The second Interval1D given is null.");
    }

    /**
     * Retourne le premier intervalle du produit cartésien.
     * 
     * @return Le premier intervalle du produit cartésien de l'intervalle
     *         bidimensionnel.
     */
    public Interval1D iX() {
        return iX;
    }

    /**
     * Retourne le second intervalle du produit cartésien.
     * 
     * @return Le second intervalle du produit cartésien de l'intervalle
     *         bidimensionnel.
     */
    public Interval1D iY() {
        return iY;
    }

    /**
     * Permet d'indiquer si l'intervalle bidimensionnel contient un certain
     * couple d'entiers.
     * 
     * @param x
     *            Le premier entier.
     * @param y
     *            Le second entier.
     * 
     * @return True si le couple appartient à l'intervalle bidimensionnel.
     */
    public boolean contains(int x, int y) {
        return iX().contains(x) && iY().contains(y);
    }

    /**
     * Calcule la taille de l'intervalle bidimensionnel.
     * 
     * @return La taille de l'intervalle bidimensionnel.
     */
    public int size() {
        return iX().size() * iY().size();
    }

    /**
     * Calcule la taille de l'intersection entre deux intervalles
     * bidimensionnels.
     * 
     * @param that
     *            L'autre intervalle bidimensionnel.
     * 
     * @return La taille de l'intersection entre les deux intervalles
     *         bidimensionnels.
     */
    public int sizeOfIntersectionWith(Interval2D that) {
        return this.iX().sizeOfIntersectionWith(that.iX())
                * this.iY().sizeOfIntersectionWith(that.iY());
    }

    /**
     * Construit l'union englobante de deux intervalles bidimensionnels.
     * 
     * @param that
     *            L'autre intervalle bidimensionnel.
     * 
     * @return L'union englobante de deux intervalles bidimensionnels.
     */
    public Interval2D boundingUnion(Interval2D that) {
        return new Interval2D(this.iX().boundingUnion(that.iX()),
                this.iY().boundingUnion(that.iY()));
    }

    /**
     * Indique si deux intervalles bidimensionnels sont unionables, i.e. si leur
     * union produit un autre intervalle bidimensionnel.
     * 
     * @param that
     *            L'autre intervalle bidimensionnel.
     * 
     * @return Vrai si deux intervalles sont unionables.
     */
    public boolean isUnionableWith(Interval2D that) {
        return this.size() + that.size()
                - this.sizeOfIntersectionWith(that) == this.boundingUnion(that)
                        .size();
    }

    /**
     * Construit l'union de deux intervalles bidimensionnels.
     * 
     * @param that
     *            L'autre intervalle bidimensionnel.
     * 
     * @return L'union de deux intervalles bidimensionnels.
     * 
     * @throws IllegalArgumentException
     *             si les deux intervalles bidimensionnel ne sont pas
     *             unionables.
     */
    public Interval2D union(Interval2D that) {
        checkArgument(this.isUnionableWith(that),
                "The union of the given Interval2Ds does not produce an Interval2D.");
        return this.boundingUnion(that);
    }

    @Override
    public boolean equals(Object thatO) {
        return thatO instanceof Interval2D
                && this.iX().equals(((Interval2D) thatO).iX())
                && this.iY().equals(((Interval2D) thatO).iY());
    }

    @Override
    public int hashCode() {
        return hash(iX(), iY());
    }

    @Override
    public String toString() {
        return iX() + "×" + iY();
    }

}
