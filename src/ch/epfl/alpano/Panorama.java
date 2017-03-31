package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;
import static java.lang.Float.POSITIVE_INFINITY;

import java.util.Arrays;

/**
 * Classe contenant toutes les informations utiles à la représentation graphique
 * ou textuelle d'un panorama. Elle donne plusieurs informations à propos de
 * chaque point le composant: sa distance par rapport à l'observateur, sa
 * position, son élévation et sa pente.
 * 
 * <p>
 * Classe immuable ne pouvant être construite qu'à l'aide de son Builder.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Panorama {

    private final PanoramaParameters parameters;
    private final float[] distance, longitude, latitude, elevation, slope;

    private Panorama(PanoramaParameters parameters, float[] distance,
            float[] longitude, float[] latitude, float[] elevation,
            float[] slope) {
        this.parameters = parameters;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.slope = slope;
    }

    /**
     * Retourne les paramètres du Panorama.
     * 
     * @return Les paramètres du Panorama.
     */
    public PanoramaParameters parameters() {
        return parameters;
    }

    private float getParameter(int x, int y, float[] parameter) {
        if (!parameters.isValidSampleIndex(x, y))
            throw new IndexOutOfBoundsException();
        return parameter[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Retourne la distance du point <i>(x,y)</i> du Panorama par rapport à
     * l'observateur ou lève l'exception IndexOutOfBoundsException si le point
     * n'est pas défini dans le Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return La distance du point <i>(x,y)</i> du Panorama par rapport à
     *         l'observateur.
     * 
     * @throws IndexOutOfBoundsException
     *             si le point n'est pas défini dans le Panorama.
     */
    public float distanceAt(int x, int y) {
        return getParameter(x, y, distance);
    }

    /**
     * Retourne la distance du point <i>(x,y)</i> du Panorama par rapport à
     * l'observateur si le point est défini dans le Panorama. Sinon, retourne la
     * valeur par défaut passée en arguement.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * @param d
     *            Valeur par défaut.
     * 
     * @return La distance du point <i>(x,y)</i> du Panorama par rapport à
     *         l'observateur ou la valeur par défaut <i>d</i> passée en argument
     *         si le point n'est pas défini dans le Panorama.
     */
    public float distanceAt(int x, int y, float d) {
        if (!parameters.isValidSampleIndex(x, y))
            return d;
        return distanceAt(x, y);
    }

    /**
     * Retourne la longitude du point <i>(x,y)</i> du Panorama ou lève
     * l'exception IndexOutOfBoundsException si le point n'est pas défini dans
     * le Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return La longitude du point <i>(x,y)</i> du Panorama.
     * 
     * @throws IndexOutOfBoundsException
     *             si le point n'est pas défini dans le Panorama.
     */
    public float longitudeAt(int x, int y) {
        return getParameter(x, y, longitude);
    }

    /**
     * Retourne la latitude du point <i>(x,y)</i> du Panorama ou lève
     * l'exception IndexOutOfBoundsException si le point n'est pas défini dans
     * le Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return La latitude du point <i>(x,y)</i> du Panorama.
     * 
     * @throws IndexOutOfBoundsException
     *             si le point n'est pas défini dans le Panorama.
     */
    public float latitudeAt(int x, int y) {
        return getParameter(x, y, latitude);
    }

    /**
     * Retourne l'élévation du point <i>(x,y)</i> du Panorama ou lève
     * l'exception IndexOutOfBoundsException si le point n'est pas défini dans
     * le Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return L'élévation du point <i>(x,y)</i> du Panorama.
     * 
     * @throws IndexOutOfBoundsException
     *             si le point n'est pas défini dans le Panorama.
     */
    public float elevationAt(int x, int y) {
        return getParameter(x, y, elevation);
    }

    /**
     * Retourne la pente du point <i>(x,y)</i> du Panorama ou lève l'exception
     * IndexOutOfBoundsException si le point n'est pas défini dans le Panorama.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return La pente du point <i>(x,y)</i> du Panorama.
     * 
     * @throws IndexOutOfBoundsException
     *             si le point n'est pas défini dans le Panorama.
     */
    public float slopeAt(int x, int y) {
        return getParameter(x, y, slope);
    }

    /**
     * Classe utilitaire permettant de construire un panorama qui lui sera
     * immuable.
     *
     * @author Robin Mamie (257234)
     * @author Maxence Jouve (269716)
     */
    public static final class Builder {
        private final PanoramaParameters parameters;
        private float[] distance, longitude, latitude, elevation, slope;
        private boolean built;

        /**
         * Constructeur du Builder de Panorama. Demande les paramètres du
         * Panorama en argument.
         * 
         * @param parameters
         *            Les paramètres du Panorama.
         * 
         * @throws NullPointerException
         *             si le PanoramaParameters passé en argument est null.
         */
        public Builder(PanoramaParameters parameters) {
            this.parameters = requireNonNull(parameters,
                    "The given parameters are null.");
            final int size = parameters.width() * parameters.height();

            this.distance = new float[size];
            this.longitude = new float[size];
            this.latitude = new float[size];
            this.elevation = new float[size];
            this.slope = new float[size];

            Arrays.fill(this.distance, POSITIVE_INFINITY);

            this.built = false;
        }

        private Builder setParameter(int x, int y, float[] parameter,
                float value) {
            if (built)
                throw new IllegalStateException(
                        "The Panorama Builder was already built, cannot add elements.");
            if (!parameters.isValidSampleIndex(x, y))
                throw new IndexOutOfBoundsException();
            parameter[parameters.linearSampleIndex(x, y)] = value;
            return this;
        }

        /**
         * Redéfinit la distance du point <i>(x,y)</i> du Panorama par rapport à
         * l'obervateur.
         * 
         * @param x
         *            L'index horizontal.
         * @param y
         *            L'index vertical.
         * @param distance
         *            La distance qui va redéfinir le point.
         * 
         * @return L'instance actualisée du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         * @throws IndexOutOfBoundsException
         *             si le point n'est pas défini dans le Panorama.
         */
        public Builder setDistanceAt(int x, int y, float distance) {
            return setParameter(x, y, this.distance, distance);
        }

        /**
         * Redéfinit la longitude du point <i>(x,y)</i> du Panorama.
         * 
         * @param x
         *            L'index horizontal.
         * @param y
         *            L'index vertical.
         * @param longitude
         *            La longitude qui va redéfinir le point.
         * 
         * @return L'instance actualisée du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         * @throws IndexOutOfBoundsException
         *             si le point n'est pas défini dans le Panorama.
         */
        public Builder setLongitudeAt(int x, int y, float longitude) {
            return setParameter(x, y, this.longitude, longitude);
        }

        /**
         * Redéfinit la latitude du point <i>(x,y)</i> du Panorama.
         * 
         * @param x
         *            L'index horizontal.
         * @param y
         *            L'index vertical.
         * @param latitude
         *            La latitude qui va redéfinir le point.
         * 
         * @return L'instance actualisée du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         * @throws IndexOutOfBoundsException
         *             si le point n'est pas défini dans le Panorama.
         */
        public Builder setLatitudeAt(int x, int y, float latitude) {
            return setParameter(x, y, this.latitude, latitude);
        }

        /**
         * Redéfinit l'élévation du point <i>(x,y)</i> du Panorama.
         * 
         * @param x
         *            L'index horizontal.
         * @param y
         *            L'index vertical.
         * @param elevation
         *            L'élévation qui va redéfinir le point.
         * 
         * @return L'instance actualisée du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         * @throws IndexOutOfBoundsException
         *             si le point n'est pas défini dans le Panorama.
         */
        public Builder setElevationAt(int x, int y, float elevation) {
            return setParameter(x, y, this.elevation, elevation);
        }

        /**
         * Redéfinit la pente du point <i>(x,y)</i> du Panorama.
         * 
         * @param x
         *            L'index horizontal.
         * @param y
         *            L'index vertical.
         * @param slope
         *            La pente qui va redéfinir le point.
         * 
         * @return L'instance actualisée du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         * @throws IndexOutOfBoundsException
         *             si le point n'est pas défini dans le Panorama.
         */
        public Builder setSlopeAt(int x, int y, float slope) {
            return setParameter(x, y, this.slope, slope);
        }

        /**
         * Construit un Panorama comportant les éléments courants du Builder.
         * 
         * @return Le Panorama comportant les éléments courants du Builder.
         * 
         * @throws IllegalStateException
         *             si un Panorama a déjà été construit à l'aide de ce
         *             Builder.
         */
        public Panorama build() {
            if (built)
                throw new IllegalStateException(
                        "The Panorama Builder was already built.");
            this.built = true;
            return new Panorama(parameters, distance, longitude, latitude,
                    elevation, slope);
        }

    }

}
