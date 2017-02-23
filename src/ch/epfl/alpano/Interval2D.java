package ch.epfl.alpano;
import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

/**
 * 
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Interval2D {

    private final Interval1D iX;
    private final Interval1D iY;
    
    public Interval2D(Interval1D iX, Interval1D iY){
       checkArgument(iX != null && iY != null, "null instance of Interval1D given");
        
        this.iX = iX;
        this.iY = iY;
    }
    
    public Interval1D iX() { return iX; }
    
    public Interval1D iY() { return iY; }
    
    public boolean contains(int x, int y) {
        return iX.contains(x) && iY.contains(y);
    }
    
    public int size () {
        return iX.size() * iY.size();
    }
    
    public int sizeOfIntersectionWith(Interval2D that) {
        return this.iX.sizeOfIntersectionWith(that.iX) * this.iY.sizeOfIntersectionWith(that.iY);
    }
    
   public Interval2D boundingUnion(Interval2D that) {
       return new Interval2D(this.iX.boundingUnion(that.iX), this.iY.boundingUnion(that.iY));
   }
   
   public boolean isUnionableWith(Interval2D that) {
       return 0 < sizeOfIntersectionWith(that);
   }
   
   public Interval2D union (Interval2D that) {
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
       return Objects.hash(iX.hashCode(), iY.hashCode());
   }
   
   @Override
   public String toString() {
     return iX.toString() + "×" + iY.toString();
   }
   
}
