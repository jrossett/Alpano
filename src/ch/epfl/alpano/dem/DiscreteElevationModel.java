package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import ch.epfl.alpano.Interval2D;

/**
 * Represents a square of elevation data
 */
public interface DiscreteElevationModel extends AutoCloseable {

    public static int SAMPLES_PER_DEGREE = 3600;
    public static double SAMPLES_PER_RADIAN = SAMPLES_PER_DEGREE
            / Math.toRadians(1);

    /**
     * Give the index corresponding to an angle in radian
     * 
     * @param angle
     *            an angle in radians
     * @return the number of sample covering an arc of a circle
     */
    public static double sampleIndex(double angle) {
        return angle * SAMPLES_PER_RADIAN;
    }

    /**
     * Return the extent of the elevation model
     * 
     * @return an {@link Interval2D}
     */
    public abstract Interval2D extent();

    /**
     * Give the elevation in meters of a {@link DiscreteElevationModel} at the
     * indexes x, y
     * 
     * @param x
     *            index in the x axis
     * @param y
     *            index on the y axis
     * @return the elevation at the position (x, y) in meters
     */
    public abstract double elevationSample(int x, int y);

    /**
     * Construct the union of two {@link DiscreteElevationModel}. Throws an
     * {@link IllegalArgumentException} if the {@link DiscreteElevationModel}
     * are not unionable
     * 
     * @param that
     *            another {@link DiscreteElevationModel}
     * @return a {@link CompositeElevationModel}
     */
    public default DiscreteElevationModel union(DiscreteElevationModel that) {
        checkArgument(that.extent().isUnionableWith(this.extent()));
        return new CompositeDiscreteElevationModel(this, that);
    }
}
