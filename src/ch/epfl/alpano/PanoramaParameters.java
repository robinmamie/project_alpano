package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

/**
 * Définit les paramètres utiles à la création d'un Panorama. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class PanoramaParameters {

    /**
     * Petite erreur nécessaire au calcul d'appartenance d'un azimut ou d'une
     * altitude..
     */
    private static final double EPSILON = 1e-10;

    /**
     * Les différents paramètres du Panorama.
     */
    private final GeoPoint observerPosition;
    private final int observerElevation, maxDistance, width, height;
    private final double centerAzimuth, horizontalFieldOfView;

    private final double verticalFieldOfView;
    private final double delta;
    private final double centerHorizontalPixel;
    private final double centerVerticalPixel;

    /**
     * Construit les paramètres utiles au Panorama.
     * 
     * @param observerPosition
     *            La position de l'observateur.
     * @param observerElevation
     *            L'altitude de l'observateur.
     * @param centerAzimuth
     *            L'azimut central du Panorama sous forme canonique.
     * @param horizontalFieldOfView
     *            Le champ de vue horizontal. Entre 0 (exclu) et 2*Pi (inclu).
     * @param maxDistance
     *            La profondeur maximale du Panorama. Strictement positive.
     * @param width
     *            La largeur du Panorama, strictement supérieure à 1.
     * @param height
     *            La hauteur du Panorama, strictement positive.
     * 
     * @throws IllegalArgumentException
     *             si l'une des conditions précitées n'est pas remplie.
     * @throws NullPointerException
     *             si la position l'observateur est null.
     */
    public PanoramaParameters(GeoPoint observerPosition, int observerElevation,
            double centerAzimuth, double horizontalFieldOfView, int maxDistance,
            int width, int height) {
        this.observerPosition = requireNonNull(observerPosition,
                "The given observer position is null.");
        this.observerElevation = observerElevation;

        checkArgument(isCanonical(centerAzimuth),
                "The given azimuth is not in canonical form.");
        this.centerAzimuth = centerAzimuth;

        checkArgument(0 < horizontalFieldOfView && horizontalFieldOfView <= PI2,
                "The given horizontal field of view is not defined between 0 (excluded) and 2 Pi (included).");
        this.horizontalFieldOfView = horizontalFieldOfView;

        checkArgument(0 < maxDistance,
                "The given maxDistance is not stricly positive.");
        this.maxDistance = maxDistance;

        checkArgument(1 < width,
                "The given width is not stricly superior to 1.");
        this.width = width;

        checkArgument(0 < height, "The given height is not stricly positive.");
        this.height = height;

        this.verticalFieldOfView = (horizontalFieldOfView() * (height() - 1))
                / (width() - 1);
        
        this.delta = horizontalFieldOfView() / (width() - 1);
        this.centerHorizontalPixel = (width() - 1) / 2.0;
        this.centerVerticalPixel = (height() - 1) / 2.0;
    }

    /**
     * Retourne la position de l'observateur.
     * 
     * @return La position de l'observateur.
     */
    public GeoPoint observerPosition() {
        return observerPosition;
    }

    /**
     * Retourne l'altitude de l'observateur.
     * 
     * @return L'altitude de l'observateur.
     */
    public int observerElevation() {
        return observerElevation;
    }

    /**
     * Retourne l'azimut central du Panorama.
     * 
     * @return L'azimut central du Panorama.
     */
    public double centerAzimuth() {
        return centerAzimuth;
    }

    /**
     * Retourne le champ de vue horizonal du Panorama.
     * 
     * @return Le champ de vue horizonal du Panorama.
     */
    public double horizontalFieldOfView() {
        return horizontalFieldOfView;
    }

    /**
     * Retourne le champ de vue vertical du Panorama.
     * 
     * @return Le champ de vue vertical du Panorama.
     */
    public double verticalFieldOfView() {
        return verticalFieldOfView;
    }

    /**
     * Retourne la profondeur maximale du Panorama.
     * 
     * @return La profondeur maximale du Panorama.
     */
    public int maxDistance() {
        return maxDistance;
    }

    /**
     * Retourne la largeur du Panorama.
     * 
     * @return La largeur du Panorama.
     */
    public int width() {
        return width;
    }

    /**
     * Retourne la hauteur du Panorama.
     * 
     * @return La hauteur du Panorama.
     */
    public int height() {
        return height;
    }

    /**
     * Calcule la valeur de conversion pixel - angle.
     * 
     * @return La valeur de conversion pixel - angle.
     */
    private double delta() {
        return delta;
    }

    /**
     * Calcule l'index horizontal central.
     * 
     * @return L'index horizontal central.
     */
    private double centerHorizontalPixel() {
        return centerHorizontalPixel;
    }

    /**
     * Calcule l'index vertical central.
     * 
     * @return L'index vertical central.
     */
    private double centerVerticalPixel() {
        return centerVerticalPixel;
    }

    /**
     * Calcule l'azimut correspondant à l'index de pixel horizontal x.
     * 
     * @param x
     *            L'index de pixel horizontal (valide pour le Panorama).
     * 
     * @return L'azimut correspondant à l'index donné.
     * 
     * @throws IllegalArgumentException
     *             si le pixel donné n'est pas défini dans la largeur du
     *             Panorama.
     */
    public double azimuthForX(double x) {
        checkArgument(0 <= x && x <= width() - 1,
                "The given index 'x' is invalid.");
        return canonicalize(
                centerAzimuth() + (x - centerHorizontalPixel()) * delta());
    }

    /**
     * Calcule l'index du pixel horizontal correspondant à l'azimut donné.
     * 
     * @param a
     *            L'azimut (sous forme canonique et appartenant à la zone
     *            visible).
     * 
     * @return L'index du pixel horizontal correspondant à l'azimut donné.
     * 
     * @throws IllegalArgumentException
     *             si l'azimut donné n'est pas sous forme canonique ou pas
     *             défini dans le champ de vue horizontal du panorma.
     */
    public double xForAzimuth(double a) {
        double dist = angularDistance(centerAzimuth(), a);
        checkArgument(abs(dist) <= horizontalFieldOfView() / 2.0 + EPSILON,
                "The given azimuth is not defined in the Panorama.");
        return centerHorizontalPixel() + dist / delta();
    }

    /**
     * Calcule l'altitude correspondant à l'index de pixel vertical y.
     * 
     * @param y
     *            L'index de pixel vertical (valide pour le Panorama).
     * 
     * @return L'altitude en radians correspondant à l'index donné.
     * 
     * @throws IllegalArgumentException
     *             si le pixel donné n'est pas défini dans la hauteur du
     *             Panorama.
     */
    public double altitudeForY(double y) {
        checkArgument(0 <= y && y <= height() - 1,
                "The given index 'y' is invalid.");
        return (centerVerticalPixel() - y) * delta();
    }

    /**
     * Calcule l'index du pixel horizontal correspondant à l'altitude donnée.
     * 
     * @param a
     *            L'altitude en radians (appartenant à la zone visible)
     * 
     * @return L'index du pixel vertical correspondant à l'altitude donnée.
     * 
     * @throws IllegalArgumentException
     *             si l'altitude n'est pas définie dans le champ de vue vertical
     *             du panorma.
     */
    public double yForAltitude(double a) {
        checkArgument(abs(a) <= verticalFieldOfView() / 2.0 + EPSILON,
                "The given altitude is not defined in the Panorama.");
        return centerVerticalPixel() - a / delta();
    }

    /**
     * Vérifie si les index donnés sont valides pour le Panorama courant.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return L'appartenance ou non de la paire d'index au Panorama.
     */
    protected boolean isValidSampleIndex(int x, int y) {
        return 0 <= x && x < width() && 0 <= y && y < height();
    }

    /**
     * Calcule l'index linéaire du Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return L'index linéaire du Panorama.
     */
    protected int linearSampleIndex(int x, int y) {
        assert isValidSampleIndex(x, y);
        return y * width() + x;
    }
}
