package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;

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

    private void checkIndex(int x, int y, String m) {
        if (!parameters.isValidSampleIndex(x, y))
            throw new IndexOutOfBoundsException(m);
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
        checkIndex(x, y, "Index not valid for this distance.");
        return distance[parameters().linearSampleIndex(x, y)];
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
        checkIndex(x, y, "Index not valid for this longitude.");
        return longitude[parameters().linearSampleIndex(x, y)];
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
        checkIndex(x, y, "Index not valid for this latitude.");
        return latitude[parameters().linearSampleIndex(x, y)];
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
        checkIndex(x, y, "Index not valid for this altitude.");
        return elevation[parameters().linearSampleIndex(x, y)];
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
        checkIndex(x, y, "Index not valid for this slope.");
        return slope[parameters().linearSampleIndex(x, y)];
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
            this.parameters = requireNonNull(parameters);
            final int size = parameters.width() * parameters.height();

            this.distance = new float[size];
            this.longitude = new float[size];
            this.latitude = new float[size];
            this.elevation = new float[size];
            this.slope = new float[size];

            Arrays.fill(this.distance, Float.POSITIVE_INFINITY);

            this.built = false;
        }

        private void checkIfBuilt() {
            if (built)
                throw new IllegalStateException(
                        "Panorama Builder already built.");
        }

        private void checkIndex(int x, int y, String m) {
            if (!parameters.isValidSampleIndex(x, y))
                throw new IndexOutOfBoundsException(m);
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
            checkIfBuilt();
            checkIndex(x, y, "Index not valid for this distance.");
            this.distance[parameters.linearSampleIndex(x, y)] = distance;
            return this;
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
            checkIfBuilt();
            checkIndex(x, y, "Index not valid for this longitude.");
            this.longitude[parameters.linearSampleIndex(x, y)] = longitude;
            return this;

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
            checkIfBuilt();
            checkIndex(x, y, "Index not valid for this latitude.");
            this.latitude[parameters.linearSampleIndex(x, y)] = latitude;
            return this;

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
            checkIfBuilt();
            checkIndex(x, y, "Index not valid for this elevation.");
            this.elevation[parameters.linearSampleIndex(x, y)] = elevation;
            return this;

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
            checkIfBuilt();
            checkIndex(x, y, "Index not valid for this slope.");
            this.slope[parameters.linearSampleIndex(x, y)] = slope;
            return this;
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
            checkIfBuilt();
            this.built = true;
            return new Panorama(parameters, distance, longitude, latitude,
                    elevation, slope);
        }

    }

}
