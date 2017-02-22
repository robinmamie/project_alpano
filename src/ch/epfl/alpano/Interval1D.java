package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

public final class Interval1D {

    private int includedFrom;
    private int includedTo;
     
    Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedTo > includedFrom, "invalid interval");
        this.includedFrom = includedFrom;
        this.includedTo = includedTo;
    }
    
    public int includedFrom (){ return includedFrom; }
    
    public int includedTo(){return includedTo; }
    
    public boolean contains (int v) {return includedFrom <= v && v <=includedTo;}
    
    public int size(){
        return includedTo-includedFrom+1;
    }
    
    public int sizeOfIntersectionWith(Interval1D that){
        if (this.includedTo < that.includedFrom || that.includedTo < this.includedFrom){
            return 0;
        }
        return Math.min(this.size(), that.size());
    }
    
    public Interval1D boundingUnion(Interval1D that){
        return new Interval1D(Math.min(this.includedFrom, that.includedFrom), Math.max(this.includedTo, that.includedTo));
    }
    
    public boolean isUnionableWith (Interval1D that){
       return (this.contains(that.includedFrom) || this.contains(that.includedTo)); 
    }
    
    public Interval1D union (Interval1D that){
            checkArgument(!isUnionableWith(that), "invalid union");
        return boundingUnion(that);
    }
    
    @Override
    public boolean equals(Object thatO){
        if (thatO == null)
            return false;
        else
            if (thatO.getClass() != getClass())
                return false;
        
            else 
                return (this.includedFrom == (int)thatO.includedFrom) && this.includedTo == (int)thatO.includedTo);
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(includedFrom(), includedTo());
    }
    
    @Override
    public String toString (){
      String chaine = "[" + includedFrom + "..." + includedTo + "]";
       return chaine;
    }
    
}
