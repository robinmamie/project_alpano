package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;

/**
 * Définit les paramètres utiles à la création d'un panorama. Classe immuable.
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
     *            La position de l'observateur.
     * @param observerElevation
     *            L'altitude de l'observateur.
     * @param centerAzimuth
     *            L'azimut central du panorama. Sous forme canonique.
     * @param horizontalFieldOfView
     *            Le champ de vue horizontal. Entre 0 (exclus) et 2Pi (inclus).
     * @param maxDistance
     *            La profondeur maximale du panorama. Strictement positive.
     * @param width
     *            La largeur du panorama. Strictement supérieure à 1.
     * @param height
     *            La hauteur du panorama. Strictement positive.
     * 
     * @throws IllegalArgumentException
     *             si l'une des conditions précitées n'est pas remplie.
     * @throws NullPointerException
     *             si la position ou l'altitude de l'observateur sont null.
     */
    public PanoramaParameters(GeoPoint observerPosition, int observerElevation,
            double centerAzimuth, double horizontalFieldOfView, int maxDistance,
            int width, int height) {
        this.observerPosition = requireNonNull(observerPosition);
        this.observerElevation = observerElevation;

        checkArgument(isCanonical(centerAzimuth),
                "The given azimuth is not in canonical form.");
        this.centerAzimuth = centerAzimuth;

        checkArgument(0 < horizontalFieldOfView && horizontalFieldOfView <= PI2,
                "The horizontal field of view is not defined between 0 (excluded) and 2 Pi (included).");
        this.horizontalFieldOfView = horizontalFieldOfView;

        checkArgument(0 < maxDistance,
                "The given maxDistance is not stricly positive.");
        this.maxDistance = maxDistance;

        checkArgument(1 < width,
                "The given width is not stricly superior to 1.");
        this.width = width;

        checkArgument(0 < height,
                "The given height is not stricly positive.");
        this.height = height;
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
     * Retourne l'azimut central du panorama.
     * 
     * @return L'azimut central du panorama.
     */
    public double centerAzimuth() {
        return centerAzimuth;
    }

    /**
     * Retourne le champ de vue horizonal du panorama.
     * 
     * @return Le champ de vue horizonal du panorama.
     */
    public double horizontalFieldOfView() {
        return horizontalFieldOfView;
    }

    /**
     * Retourne le champ de vue vertical du panorama.
     * 
     * @return Le champ de vue vertical du panorama.
     */
    public double verticalFieldOfView() {
        return (horizontalFieldOfView() * (height() - 1)) / (width() - 1);
    }

    /**
     * Retourne la profondeur maximale du panorama.
     * 
     * @return La profondeur maximale du panorama.
     */
    public int maxDistance() {
        return maxDistance;
    }

    /**
     * Retourne la largeur du panorama.
     * 
     * @return La largeur du panorama.
     */
    public int width() {
        return width;
    }

    /**
     * Retourne la hauteur du panorama.
     * 
     * @return La hauteur du panorama.
     */
    public int height() {
        return height;
    }

    private double delta() {
        return horizontalFieldOfView() / (width() - 1);
    }

    private double centerPixelHor() {
        return width() / 2.0;
    }
    
    private double centerPixelVer() {
        return height() / 2.0;
    }

    /**
     * Calcule l'azimut correspondant à l'index de pixel horizontal x.
     * 
     * @param x
     *            L'index de pixel horizontal (valide pour le panorama).
     * 
     * @return L'azimut correspondant à l'index donné.
     * 
     * @throws IllegalArgumentException
     *             si le pixel donné n'est pas défini dans la largeur du
     *             panorama.
     */
    public double azimuthForX(double x) {
        checkArgument(0 <= x && x <= width() - 1,
                "The given index 'x' is invalid.");
        return canonicalize(centerAzimuth() + (x - centerPixelHor()) * delta());
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
        checkArgument(isCanonical(a),
                "The given azimuth is not in canonical form.");
        double dist = angularDistance(centerAzimuth(), a);
        checkArgument(Math.abs(dist) <= horizontalFieldOfView() / 2.0,
                "The given azimuth is not defined in the panorama.");
        return centerPixelHor() + dist / delta();
    }

    /**
     * Calcule l'altitude correspondant à l'index de pixel vertical y.
     * 
     * @param y
     *            L'index de pixel vertical (valide pour le panorama).
     * 
     * @return L'altitude en radians correspondant à l'index donné.
     * 
     * @throws IllegalArgumentException
     *             si le pixel donné n'est pas défini dans la hauteur du
     *             panorama.
     */
    public double altitudeForY(double y) {
        checkArgument(0 <= y && y <= height() - 1,
                "The given index 'y' is invalid.");
        return (centerPixelVer() - y) * delta();
    }

    /**
     * Calcule l'index du pixel horizontal correspondant à l'altitude donnée.
     * 
     * @param a
     *            altitude en radians (appartenant à la zone visible)
     * 
     * @return L'index du pixel vertical correspondant à l'altitude donnée.
     * 
     * @throws IllegalArgumentException
     *             si l'altitude n'est pas définie dans le champ de vue vertical
     *             du panorma.
     */
    public double yForAltitude(double a) {
        checkArgument(a <= verticalFieldOfView() / 2.0,
                "The given altitude is not defined in the panorama.");
        return centerPixelVer() - a / delta();
    }

    /**
     * Vérifie si les index donnés sont valides pour le panorama courant.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return L'appartenance ou non de la paire d'index au panorama.
     */
    public boolean isValidSampleIndex(int x, int y) {
        return 0 <= x && x < width() && 0 <= y && y < height();
    }

    /**
     * Calcule l'index linéaire du panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return L'index linéaire du panorama.
     * 
     * @throws IllegalArgumentException
     *             si les index passés en arguments ne sont pas valides pour le
     *             panorama.
     */
    public int linearSampleIndex(int x, int y) {
        checkArgument(isValidSampleIndex(x, y),
                "The given indices are not valid for this panorama.");
        return y * width() + x;
    }
}
