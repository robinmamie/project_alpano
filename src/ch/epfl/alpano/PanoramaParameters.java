package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;

/**
 * Définit les paramètres utiles à la création
 * d'un panorama. Classe immuable.
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
    
    /**
     * Construit les paramètres utiles au panorama.
     * 
     * @param observerPosition
     *          Position de l'observateur.
     * @param observerElevation
     *          Altitude de l'observateur.
     * @param centerAzimuth
     *          Azimut central du panorama. Sous forme canonique.
     * @param horizontalFieldOfView
     *          Champ de vue horizontal. Entre 0 (exclus) et 2Pi (inclus).
     * @param maxDistance
     *          Profondeur maximale du panorama. Strictement positif.
     * @param width
     *          Largeur du panorama. Strictement positif.
     * @param height
     *          Hauteur du panorama. Strictement psotitif.
     */
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

    /**
     * Retourne la position de l'observateur.
     * 
     * @return la position de l'observateur
     */
    public GeoPoint observerPosition() {
        return observerPosition;
    }

    /**
     * Retourne l'altitude de l'observateur.
     * 
     * @return l'altitude de l'observateur
     */
    public int observerElevation() {
        return observerElevation;
    }

    /**
     * Retourne l'azimut central du panorama.
     * 
     * @return l'azimut central du panorama
     */
    public double centerAzimuth() {
        return centerAzimuth;
    }

    /**
     * Retourne le champ de vue horizonal du panorama.
     * 
     * @return le champ de vue horizonal du panorama
     */
    public double horizontalFieldOfView() {
        return horizontalFieldOfView;
    }
    
    /**
     * Retourne le champ de vue vertical du panorama.
     * 
     * @return le champ de vue vertical du panorama
     */
    public double verticalFieldOfView() {
        return (horizontalFieldOfView() * (height()-1))/(width()-1);
    }

    /**
     * Retourne la profondeur maximale du panorama.
     * 
     * @return la profondeur maximale du panorama
     */
    public int maxDistance() {
        return maxDistance;
    }

    /**
     * Retourne la largeur du panorama.
     * 
     * @return la largeur du panorama
     */
    public int width() {
        return width;
    }

    /**
     * Retourne la hauteur du panorama.
     * 
     * @return la hauteur du panorama
     */
    public int height() {
        return height;
    }
    
    private double delta() {
        return horizontalFieldOfView() / (width()-1);
    }
    
    private double centerPixel() {
        return width()/2.0;
    }

    /**
     * Calcule l'azimut correspondant à l'index de pixel
     * horizontal x.
     * 
     * @param x
     *          index de pixel horizontal (valide pour
     *          le panorama)
     * 
     * @return l'azimut correspondant à l'index donné
     */
    public double azimuthForX(double x) {
        checkArgument(0 <= x && x <= width()-1
                , "The given index 'x' is invalid.");
        return canonicalize(centerAzimuth() + (x - centerPixel()) * delta());
    }
    
    /**
     * Calcule l'index du pixel horizontal correspondant à
     * l'azimut donné.
     * 
     * @param a 
     *          azimut (sous forme canonique et appartenant
     *          à la zone visible)
     * 
     * @return l'index du pixel horizontal correspondant
     *          à l'azimut donné
     */
    public double xForAzimuth(double a) {
        checkArgument(isCanonical(a)
                , "The given azimuth is not in canonical form.");
        double dist = angularDistance(centerAzimuth(), a);
        checkArgument(Math.abs(dist) <= horizontalFieldOfView()/2.0
                , "The given azimuth is not defined in the panorama.");
        return centerPixel() + dist/delta();
    }
    
    /**
     * Calcule l'altitude correspondant à l'index de pixel
     * vertical y.
     * 
     * @param y
     *          index de pixel vertical (valide pour
     *          le panorama)
     * 
     * @return l'altitude correspondant à l'index donné
     */
    public double altitudeForY(double y) {
        checkArgument(0 <= y && y <= height()-1
                , "The given index 'y' is invalid.");
        return (y - observerElevation()) * delta();
    }

    /**
     * Calcule l'index du pixel horizontal correspondant à
     * l'altitude donnée.
     * 
     * @param a
     *          altitude (appartenant à la zone visible)
     * 
     * @return l'index du pixel vertical correspondant
     *          à l'azimut donné
     */
    public double yForAltitude(double a) {
        double dist = a - observerElevation();
        checkArgument(Math.abs(dist) <= verticalFieldOfView()/2.0
                , "The given altitude is not defined in the panorama.");
        return dist / delta();
    }
    
    /**
     * Vérifie si les index donnés sont valides pour
     * le panorama courant.
     * 
     * @param x
     *          index horizontal
     * @param y
     *          index vertical
     * 
     * @return L'appartenance ou non de la paire d'index
     *          au panorama.
     */
    public boolean isValidSampleIndex(int x, int y) {
        return x < width() && y < height();
    }
    
    /**
     * Calcule l'index linéaire du panorama.
     * 
     * @param x
     *          index horizontal
     * @param y
     *          index vertical
     * 
     * @return L'index linéaire du panorama.
     */
    public int linearSampleIndex(int x, int y) {
        return y * width() + x;
    }
}
