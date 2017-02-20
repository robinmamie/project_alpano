package ch.epfl.alpano;

public interface Azimuth {
    
    static boolean isCanonical(double azimuth) {
        return (0 <= azimuth) && (azimuth < Math2.PI2);
    }
    
    static double canonicalize(double azimuth) {
        return Math2.floorMod(azimuth, Math2.PI2);
    }
    
    static double toMath(double azimuth) {
        
    }
    
}
