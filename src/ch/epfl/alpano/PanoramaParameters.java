package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;

/**
 * 
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaParameters {
    
    private final GeoPoint observerPosition;
    private final int observerElevation;
    private final double centerAzimuth;
    private final double horizontalFieldOfView;
    private final int maxDistance;
    private final int width;
    private final int height;
    
    public PanoramaParameters(GeoPoint observerPosition
            , int observerElevation
            , double centerAzimuth
            , double horizontalFieldOfView
            , int maxDistance
            , int width
            , int height) {
        this.observerPosition = requireNonNull(observerPosition);
        this.observerElevation = observerElevation;
        
        checkArgument(isCanonical(centerAzimuth)
                , "The given azimuth is not in canonical form.");
        this.centerAzimuth = centerAzimuth;
        
        checkArgument(0 < horizontalFieldOfView && horizontalFieldOfView <= PI2
                , "The horizontal field of view is not defined between 0 (excluded) and 2 Pi (included).");
        this.horizontalFieldOfView = horizontalFieldOfView;
        
        checkArgument(0 < maxDistance
                , "The given maxDistance is not stricly positive.");
        this.maxDistance = maxDistance;
        
        checkArgument(0 < width
                , "The given width is not stricly positive.");
        this.width = width;
        
        checkArgument(0 < height
                , "The given height is not stricly positive.");
        this.height = height;
    }

    public GeoPoint observerPosition() {
        return observerPosition;
    }

    public int observerElevation() {
        return observerElevation;
    }

    public double centerAzimuth() {
        return centerAzimuth;
    }

    public double horizontalFieldOfView() {
        return horizontalFieldOfView;
    }
    
    public double verticalFieldOfView() {
        return (horizontalFieldOfView() * (height()-1))/(width()-1);
    }

    public int maxDistance() {
        return maxDistance;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
    
    private double delta() {
        return horizontalFieldOfView() / (width()-1);
    }
    
    private double centerPixel() {
        return width()/2.0;
    }

    public double azimuthForX(double x) {
        checkArgument(0 <= x && x < width()
                , "The given index 'x' is invalid.");
        return canonicalize(centerAzimuth() + (x - centerPixel()) * delta());
    }
    
    public double xForAzimuth(double a) {
        checkArgument(isCanonical(a)
                , "The given azimuth is not in canonical form.");
        double dist = angularDistance(centerAzimuth(), a);
        checkArgument(Math.abs(dist) <= horizontalFieldOfView()/2.0
                , "The given azimuth is not defined in the panorama.");
        return centerPixel() + dist/delta();
    }
    
    public double altitudeForY(double y) {
        checkArgument(0 <= y && y < width()
                , "The given index 'y' is invalid.");
        return (y - observerElevation()) * delta();
    }

    public double yForAltitude(double a) {
        double dist = a - observerElevation();
        checkArgument(Math.abs(dist) <= verticalFieldOfView()/2.0
                , "The given altitude is not defined in the panorama.");
        
        return dist / delta();
    }
    
    public boolean isValidSampleIndex(int x, int y) {
        return x < width() && y < height();
    }
    
    public int linearSampleIndex(int x, int y) {
        return y * width() + x;
    }
}
