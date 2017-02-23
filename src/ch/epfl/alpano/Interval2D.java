package ch.epfl.alpano;
import static ch.epfl.alpano.Preconditions.checkArgument;

public final class Interval2D {

    private Interval1D iX;
    private Interval1D iY;
    
    public Interval2D(Interval1D iX, Interval1D iY){
       checkArgument((iX == null || iY == null), "invalid Interval1D");
        
        this.iX = iX;
        this.iY = iY;
    }
    
    public Interval1D iX() { return iX; }
    
    public Interval1D iY() { return iY; }
    
    public boolean contains(int x, int y) {
        return (iX.contains(x) && iY.contains(y));
    }
    
    public int size () {
        return iX.size()*iY.size();
    }
    
    public int sizeOfIntersectionWith(Interval2D that) {
        return iX.sizeOfIntersectionWith(that.iX)*iY.sizeOfIntersectionWith(that.iY);
    }
    
   public Interval2D boundingUnion(Interval2D that) {
       return new Interval2D(this.iX.boundingUnion(that.iX), this.iY.boundingUnion(that.iY));
   }
   
   public boolean isUnionableWith(Interval2D that) {
       return ( (this.size() + that.size() - this.sizeOfIntersectionWith(that)) == this.boundingUnion(that).size() );
   }
   
   public Interval2D union (Interval2D that) {
       checkArgument(isUnionableWith(that), "invalid union");
       return boundingUnion(that); //leger doute
   }
   
   @Override
   public boolean equals(Object thatO){
       if(thatO == null)
           return false;

       if(getClass() != thatO.getClass())
           return false;
       
       Interval2D that = (Interval2D)thatO;
       return (iX.equals(that.iX) && iY.equals(that.iY));
   }
   
   @Override
   public int hashCode();
   
   @Override
   public String toString() {
     return iX.toString() + "x" + iY.toString();
   }
   
}
