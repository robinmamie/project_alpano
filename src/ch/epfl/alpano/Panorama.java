package ch.epfl.alpano;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

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

    public PanoramaParameters parameters() {
        return parameters;
    }

    private void checkIndex(int x, int y, String m) {
        if (!parameters.isValidSampleIndex(x, y))
            throw new IndexOutOfBoundsException(m);
    }

    public float distanceAt(int x, int y) throws IndexOutOfBoundsException {
        checkIndex(x, y, "Index not valid for this distance.");
        return distance[parameters().linearSampleIndex(x, y)];
    }

    public float distanceAt(int x, int y, float d) {
        if (!parameters.isValidSampleIndex(x, y))
            return d;
        return distanceAt(x, y);
    }

    public float longitudeAt(int x, int y) throws IndexOutOfBoundsException {
        checkIndex(x, y, "Index not valid for this longitude.");
        return longitude[parameters().linearSampleIndex(x, y)];
    }

    public float latitudeAt(int x, int y) throws IndexOutOfBoundsException {
        checkIndex(x, y, "Index not valid for this latitude.");
        return latitude[parameters().linearSampleIndex(x, y)];
    }

    public float elevationAt(int x, int y) throws IndexOutOfBoundsException {
        checkIndex(x, y, "Index not valid for this altitude.");
        return elevation[parameters().linearSampleIndex(x, y)];
    }

    public float slopeAt(int x, int y) throws IndexOutOfBoundsException {
        checkIndex(x, y, "Index not valid for this slope.");
        return slope[parameters().linearSampleIndex(x, y)];
    }

    public static final class Builder {
        private final PanoramaParameters parameters;
        private float[] distance, longitude, latitude, elevation, slope;
        private boolean built;

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

        public Builder setDistanceAt(int x, int y, float distance)
                throws IllegalStateException {
            checkIfBuilt();
            this.distance[parameters.linearSampleIndex(x, y)] = distance;
            return this;
        }

        public Builder setLongitudeAt(int x, int y, float longitude)
                throws IllegalStateException {
            checkIfBuilt();
            this.longitude[parameters.linearSampleIndex(x, y)] = longitude;
            return this;

        }

        public Builder setLatitudeAt(int x, int y, float latitude)
                throws IllegalStateException {
            checkIfBuilt();
            this.latitude[parameters.linearSampleIndex(x, y)] = latitude;
            return this;

        }

        public Builder setElevationAt(int x, int y, float elevation)
                throws IllegalStateException {
            checkIfBuilt();
            this.elevation[parameters.linearSampleIndex(x, y)] = elevation;
            return this;

        }

        public Builder setSlopeAt(int x, int y, float slope)
                throws IllegalStateException {
            checkIfBuilt();
            this.slope[parameters.linearSampleIndex(x, y)] = slope;
            return this;
        }

        public Panorama build()
                throws IllegalStateException {
            checkIfBuilt();
            this.built = true;
            return new Panorama(parameters, distance, longitude, latitude,
                    elevation, slope);
        }

    }

}
