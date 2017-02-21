package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

public interface Azimuth {
    
    static boolean isCanonical(double azimuth) {
        return (0 <= azimuth) && (azimuth < Math2.PI2);
    }
    
    static double canonicalize(double azimuth) {
        return Math2.floorMod(azimuth, Math2.PI2);
    }
    
    static double toMath(double azimuth) {
        checkArgument(isCanonical(azimuth), "azimuth non canonical");
        return canonicalize(Math2.PI2 - azimuth);
    }
    
    static double fromMath(double azimuth) {
        return toMath(azimuth);
    }
    
    static String toOctantString(double azimuth, String n, String e, String s, String w) {
        checkArgument(isCanonical(azimuth), "azimuth non canonical");
        String answer = "";
        final double distance = 3*Math.PI/8;
        if(Math.abs(Math2.angularDistance(azimuth, 0)) < distance)
            answer += n;
        if(Math.abs(Math2.angularDistance(azimuth, Math.PI)) < distance)
            answer += s;
        if(Math.abs(Math2.angularDistance(azimuth, Math.PI/2)) < distance)
            answer += e;
        if(Math.abs(Math2.angularDistance(azimuth, 3*Math.PI/2)) < distance)
            answer += w;
        return answer;
    }
    
}
