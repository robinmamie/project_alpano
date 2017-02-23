package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

/**
 *
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Interval1D {

    private final int includedFrom;
    private final int includedTo;

    public Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedFrom <= includedTo, "invalid interval");
        
        this.includedFrom = includedFrom;
        this.includedTo   = includedTo;
    }

    public int includedFrom() { return includedFrom; }

    public int includedTo() { return includedTo; }

    public boolean contains(int v) {
        return includedFrom <= v && v <= includedTo;
    }

    public int size() {
        return includedTo - includedFrom + 1;
    }

    public int sizeOfIntersectionWith(Interval1D that) {
        int size = Math.min(this.includedTo, that.includedTo) - Math.max(this.includedFrom, that.includedFrom) + 1;
        return size < 0 ? 0 : size;
    }

    public Interval1D boundingUnion(Interval1D that) {
        return new Interval1D(Math.min(this.includedFrom, that.includedFrom), Math.max(this.includedTo, that.includedTo));
    }

    public boolean isUnionableWith(Interval1D that) {
        return  0 < sizeOfIntersectionWith(that); 
    }

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
