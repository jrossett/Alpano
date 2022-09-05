package ch.epfl.alpano;

import static java.util.Arrays.fill;

/**
 * Describes a {@link Panorama} with arrays of values for each pixel of the
 * image. The indexes of the values are linear.
 */
public final class Panorama {
    private final PanoramaParameters parameters;
    private final float[] distance;
    private final float[] longitude;
    private final float[] latitude;
    private final float[] elevation;
    private final float[] slope;

    /**
     * Constructs a {@link Panorama} with a set of values for each pixel of the
     * image
     * 
     * @param parameters
     *            parameters of the panorama as a {@link PanoramaParameters}
     * @param distance
     *            array of distances from the observer for each pixels
     * @param longitude
     *            array of longitude of the intersections with the terrain for
     *            each pixels
     * @param latitude
     *            array of latitude of the intersections with the terrain for
     *            each pixels
     * @param elevation
     *            array of the elevation of each intersections with the terrain
     *            for each pixels
     * @param slope
     *            array of the slope of each intersections with the terrain for
     *            each pixels
     */
    private Panorama(PanoramaParameters parameters, float[] distance, float[] longitude,
            float[] latitude, float[] elevation, float[] slope) {
        this.parameters = parameters;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.slope = slope;
    }

    /**
     * Gets the parameters of the panorama
     * 
     * @return the {@link PanoramaParameters}
     */
    public PanoramaParameters parameters() {
        return parameters;
    }

    /**
     * Gets the distance value at a given pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the distance value at the pixel index in meters
     */
    public float distanceAt(int x, int y) {
        checkIndex(x, y);
        return distance[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Gets the distance value at a given pixel index or return a default value
     * if the index is not in range
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the distance value at the pixel index in meters or the default
     *         value if the index is not in range
     */
    public float distanceAt(int x, int y, float d) {
        return !parameters.isValidSampleIndex(x, y) ? d
                : distance[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Gets the longitude value at a given pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the longitude value at the pixel index in radians
     */
    public float longitudeAt(int x, int y) {
        checkIndex(x, y);
        return longitude[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Gets the latitude value at a given pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the latitude value at the pixel index in radians
     */
    public float latitudeAt(int x, int y) {
        checkIndex(x, y);
        return latitude[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Gets the elevation value at a given pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the elevation value at the pixel index in meters
     */
    public float elevationAt(int x, int y) {
        checkIndex(x, y);
        return elevation[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Gets the slope value at a given pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the slope value at the pixel index in radians
     */
    public float slopeAt(int x, int y) {
        checkIndex(x, y);
        return slope[parameters.linearSampleIndex(x, y)];
    }

    /**
     * Checks if the given index is in range. Throws an
     * {@link IllegalArgumentException} if it is not.
     * 
     * @param x
     *            x index
     * @param y
     *            y index
     */
    private void checkIndex(int x, int y) {
        if (!parameters.isValidSampleIndex(x, y)) {
            throw new IndexOutOfBoundsException("Error: Illegal pixel index");
        }
    }

    /**
     * Builder for the Panorama class. Incrementally constructs a
     * {@link Panorama} by storing values in float arrays.
     */
    public static final class Builder {

        private final PanoramaParameters parameters;
        private float[] distance;
        private float[] longitude;
        private float[] latitude;
        private float[] elevation;
        private float[] slope;
        private boolean built = false;

        /**
         * Constructs a new {@link Panorama.Builder} with the given
         * {@link PanoramaParameters}
         * 
         * @param parameters
         *            parameters of the panorama as a {@link PanoramaParameters}
         */
        public Builder(PanoramaParameters parameters) {
            this.parameters = parameters;
            int size = parameters.height() * parameters.width();
            distance = new float[size];
            fill(distance, Float.POSITIVE_INFINITY);
            longitude = new float[size];
            latitude = new float[size];
            elevation = new float[size];
            slope = new float[size];
        }

        /**
         * Sets the distance value at a given pixel index
         * 
         * @param x
         *            horizontal pixel index
         * @param y
         *            vertical pixel index
         * @param distance
         *            distance value in meters
         * @return the {@link Panorama.Builder}
         */
        public Builder setDistanceAt(int x, int y, float distance) {
            checkBuild();
            checkIndex(x, y);
            this.distance[parameters.linearSampleIndex(x, y)] = distance;
            return this;
        }

        /**
         * Sets the longitude value at a given pixel index
         * 
         * @param x
         *            horizontal pixel index
         * @param y
         *            vertical pixel index
         * @param longitude
         *            longitude value in radians
         * @return the {@link Panorama.Builder}
         */
        public Builder setLongitudeAt(int x, int y, float longitude) {
            checkBuild();
            checkIndex(x, y);
            this.longitude[parameters.linearSampleIndex(x, y)] = longitude;
            return this;
        }

        /**
         * Sets the latitude value at a given pixel index
         * 
         * @param x
         *            horizontal pixel index
         * @param y
         *            vertical pixel index
         * @param latitude
         *            latitude value in radians
         * @return the {@link Panorama.Builder}
         */
        public Builder setLatitudeAt(int x, int y, float latitude) {
            checkBuild();
            checkIndex(x, y);
            this.latitude[parameters.linearSampleIndex(x, y)] = latitude;
            return this;
        }

        /**
         * Sets the elevation value at a given pixel index
         * 
         * @param x
         *            horizontal pixel index
         * @param y
         *            vertical pixel index
         * @param elevation
         *            elevation value in meters
         * @return the {@link Panorama.Builder}
         */
        public Builder setElevationAt(int x, int y, float elevation) {
            checkBuild();
            checkIndex(x, y);
            this.elevation[parameters.linearSampleIndex(x, y)] = elevation;
            return this;
        }

        /**
         * Sets the slope value at a given pixel index
         * 
         * @param x
         *            horizontal pixel index
         * @param y
         *            vertical pixel index
         * @param slope
         *            slope value in radians
         * @return the {@link Panorama.Builder}
         */
        public Builder setSlopeAt(int x, int y, float slope) {
            checkBuild();
            checkIndex(x, y);
            this.slope[parameters.linearSampleIndex(x, y)] = slope;
            return this;
        }

        /**
         * Builds the {@link Panorama} with the actual values stored in the
         * arrays
         * 
         * @return a fully built {@link Panorama}
         */
        public Panorama build() {
            checkBuild();
            built = true;
            Panorama panorama = new Panorama(parameters, distance, longitude, latitude, elevation,
                    slope);
            clearArrays();
            return panorama;
        }

        /**
         * Checks if the given index is in range. Throws an
         * {@link IllegalArgumentException} if it is not.
         * 
         * @param x
         *            x index
         * @param y
         *            y index
         */
        private void checkIndex(int x, int y) {
            if (!parameters.isValidSampleIndex(x, y)) {
                throw new IndexOutOfBoundsException(
                        "Error: Illegal pixel index");
            }
        }

        /**
         * Checks if the panorama has already been built. Throws an
         * {@link IllegalStateException} if it was.
         */
        private void checkBuild() {
            if (built) {
                throw new IllegalStateException("Error: Panorama already built");
            }
        }
        
        /**
         * Clear all the arrays of the builder used when the panorama is built
         */
        private void clearArrays() {
            distance = null;
            longitude = null;
            latitude = null;
            elevation = null;
            slope = null;
        }
    }
}
