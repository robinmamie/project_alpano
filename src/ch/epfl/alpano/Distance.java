package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

public interface Distance {
    
    double EARTH_RADIUS = 6_371_000;
    
    static double toRadians(double distanceInMeters) {
        checkArgument(0 <= distanceInMeters, "invalid distance");
        return distanceInMeters / EARTH_RADIUS;
    }
    
    static double toMeters(double distanceInRadians) {
        return distanceInRadians * EARTH_RADIUS;
    }
    
}
