package ch.epfl.alpano;

import java.util.function.DoubleUnaryOperator;
import static ch.epfl.alpano.Preconditions.checkArgument;

public interface Math2 {

    double PI2 = 2*Math.PI;
    
    static double sq(double x) {
        return x*x;
    }
    
    static double floorMod(double x, double y) {
        return x - y * Math.floor(x / y);
    }
    
    static double haversin(double x) {
        return Math.pow(Math.sin(x / 2), 2);
    }
    
    static double angularDistance(double a1, double a2) {
        return floorMod(a2 - a1 + Math.PI, PI2) - Math.PI;
    }
    
    static double lerp(double y0, double y1, double x) {
        return y0 + (y1 - y0) * x;
    }
    
    static double bilerp(double z00, double z10, double z01, double z11, double x, double y) {
        return lerp(lerp(z00, z10, x), lerp(z01, z11, x), y);
    }
    
    static double firstIntervalContainingRoot(DoubleUnaryOperator f, double minX, double maxX, double dX) {
        while(minX <= maxX - dX) {
            if(f.applyAsDouble(minX)*f.applyAsDouble(minX + dX) <= 0)
                return minX;
            minX += dX;
        }
        return Double.POSITIVE_INFINITY;
    }
    
    static double improveRoot(DoubleUnaryOperator f, double x1, double x2, double epsilon) {
        double limit = 0.01;
        double inf = firstIntervalContainingRoot(f, x1, x2, limit);
        checkArgument(inf != Double.POSITIVE_INFINITY);
        double sup = inf + limit;
        while(sup-inf > epsilon) {
            double mid = (inf+sup)/2.0;
            double value = f.applyAsDouble((inf+sup)/2.0);
            if(value * f.applyAsDouble(inf) < 0)
                sup = mid;
            else
                inf = mid;
        }
        return inf;
    }
}
