package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

/**
 * Représente un intervalle unidimensionnel.
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
     * Construit un intervalle.
     * 
     * @param includedFrom
     *          Borne inférieure de l'intervalle.
     * @param includedTo
     *          Borne supérieure de l'intervalle.
     *          
     * @throws IllegalArgumentException
     *          si la borne supérieure est strictement inférieure à la borne inférieure
     */
    public Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedFrom <= includedTo, "invalid interval");
        
        this.includedFrom = includedFrom;
        this.includedTo   = includedTo;
    }

    
    /**
     * Retourne la borne inférieure de l'intervalle.
     * 
     * @return la borne inférieure de l'intervalle.
     */
    public int includedFrom() { return includedFrom; }

    
    /**
     * Retourne la borne supérieure de l'intervalle.
     * 
     * @return la borne supérieure de l'intervalle.
     */
    public int includedTo() { return includedTo; }

    
    /**
     * Permet d'indiquer si l'intervalle contient une certaine valeur.
     * 
     * @param v
     *          un entier
     *          
     * @return si l'entier appartient à l'intervalle
     */
    public boolean contains(int v) {
        return includedFrom <= v && v <= includedTo;
    }

    
    /**
     * Calcule la taille de l'intervalle.
     * 
     * @return la taille de l'intervalle
     */
    public int size() {
        return includedTo - includedFrom + 1;
    }

    
    /**
     * Calcule la taille de l'intersection entre deux intervalles.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return la taille de l'intersection entre deux intervalles
     * 
     * @throws IllegalArgumentException
     *          si l'argument donné est null.
     */
    public int sizeOfIntersectionWith(Interval1D that) {
        checkArgument(that != null, "null argument given");
        int size = Math.min(this.includedTo, that.includedTo) - Math.max(this.includedFrom, that.includedFrom) + 1;
        return size < 0 ? 0 : size;
    }

    
    /**
     * Construit l'union englobante de deux intervalles.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return l'union englobante de deux intervalles
     * 
     * @throws IllegalArgumentException
     *          si l'argument donné est null.
     */
    public Interval1D boundingUnion(Interval1D that) {
        checkArgument(that != null, "null argument given");
        return new Interval1D(Math.min(this.includedFrom, that.includedFrom), Math.max(this.includedTo, that.includedTo));
    }

    
    /**
     * Indique si deux intervalles sont unionables.
     * 
     * @param that
     *          autre intervalle
     *          
     * @return si deux intervalles sont unionables
     * 
     * @throws IllegalArgumentException
     *          si l'argument donné est null.
     */
    public boolean isUnionableWith(Interval1D that) {
        return  0 < sizeOfIntersectionWith(that); 
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
     *          si l'argument donné est null ou si les deux intervalles
     *          ne sont pas unionables.
     */
    public Interval1D union(Interval1D that) {
        checkArgument(isUnionableWith(that), "union not possible");
        return boundingUnion(that);
    }

    
    @Override
    public boolean equals(Object thatO) {
        if(thatO == null || getClass() != thatO.getClass())
            return false;
        
        Interval1D that = (Interval1D)thatO;

        return this.includedFrom == that.includedFrom && this.includedTo == that.includedTo;
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(includedFrom(), includedTo());
    }

    
    @Override
    public String toString() {
        return "[" + includedFrom + ".." + includedTo + "]";
    }
    
}
