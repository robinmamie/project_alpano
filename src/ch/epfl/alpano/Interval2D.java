package ch.epfl.alpano;
import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

/**
 * Représente un intervalle bidimensionnel.
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
     * Construit un intervalle bidimensionnel ssi deux intervalles unidimensionnels
     * non null sont donnés.
     * 
     * @param iX
     *          Premier intervalle unidimensionnel
     * @param iY
     *          Second intervalle unidimensionnel
     *          
     * @throws IllegalArgumentException
     *          si au moins l'un des arguments donnés est nul.
     */
    public Interval2D(Interval1D iX, Interval1D iY) {
        if(iX == null || iY == null)
            throw new NullPointerException("at least one of the arguments given is null");

        this.iX = iX;
        this.iY = iY;
    }


    /**
     * Retourne le premier intervalle du produit cartésien.
     * 
     * @return le premier intervalle du produit cartésien
     */
    public Interval1D iX() { return iX; }


    /**
     * Retourne le second intervalle du produit cartésien.
     * 
     * @return le second intervalle du produit cartésien
     */
    public Interval1D iY() { return iY; }


    /**
     * Permet d'indiquer si l'intervalle contient un certain couple.
     * 
     * @param x
     *          premier entier
     * @param y
     *          second entier
     *          
     * @return si le couple appartient à l'intervalle
     */
    public boolean contains(int x, int y) {
        return iX.contains(x) && iY.contains(y);
    }


    /**
     * Calcule la taille de l'intervalle.
     * 
     * @return la taille de l'intervalle
     */
    public int size() {
        return iX.size() * iY.size();
    }


    /**
     * Calcule la taille de l'intersection entre deux intervalles.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return la taille de l'intersection entre deux intervalles
     */
    public int sizeOfIntersectionWith(Interval2D that) {
        return this.iX.sizeOfIntersectionWith(that.iX) * this.iY.sizeOfIntersectionWith(that.iY);
    }

    
    /**
     * Construit l'union englobante de deux intervalles.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return l'union englobante de deux intervalles
     */
    public Interval2D boundingUnion(Interval2D that) {
        return new Interval2D(this.iX.boundingUnion(that.iX), this.iY.boundingUnion(that.iY));
    }

    
    /**
     * Indique si deux intervalles sont unionables.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return si deux intervalles sont unionables
     */
    public boolean isUnionableWith(Interval2D that) {
        return 0 < sizeOfIntersectionWith(that);
    }

    
    /**
     * Construit l'union de deux intervalles.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return l'union de deux intervalles
     * 
     * @throws IllegalArgumentException
     *          si les deux intervalles ne sont pas unionables.
     */
    public Interval2D union(Interval2D that) {
        checkArgument(isUnionableWith(that), "union not possible");
        return boundingUnion(that);
    }

    
    @Override
    public boolean equals(Object thatO) {
        if(thatO == null || getClass() != thatO.getClass())
            return false;

        Interval2D that = (Interval2D)thatO;

        return this.iX.equals(that.iX) && this.iY.equals(that.iY);
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(iX, iY);
    }

    
    @Override
    public String toString() {
        return iX.toString() + "×" + iY.toString();
    }
    
}
