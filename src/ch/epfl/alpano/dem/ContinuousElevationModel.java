package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.alpano.dem.DiscreteElevationModel.SAMPLES_PER_RADIAN;
import static ch.epfl.alpano.dem.DiscreteElevationModel.sampleIndex;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Elevation model constructed by a {@link DiscreteElevationModel}
 */
public final class ContinuousElevationModel {

    private final DiscreteElevationModel dem;
    private final static double DNS = toMeters(1 / SAMPLES_PER_RADIAN);

    /**
     * Constructs an continuous elevation model given a
     * {@link DiscreteElevationModel}
     * 
     * @param dem
     *            a {@link DiscreteElevationModel}
     */
    public ContinuousElevationModel(DiscreteElevationModel dem) {
        this.dem = requireNonNull(dem);
    }

    /**
     * Give the altitude of a point belonging to a
     * {@link DiscreteElevationModel}
     * 
     * @param p
     *            a {@link GeoPoint}
     * @return the altitude at a {@link GeoPoint} p
     */
    public double elevationAt(GeoPoint p) {
        double xIndex = sampleIndex(p.longitude());
        double yIndex = sampleIndex(p.latitude());
        int wholeX = (int) Math.floor(xIndex);
        int wholeY = (int) Math.floor(yIndex);
        double elevation00 = elevationSample(wholeX, wholeY);
        double elevation01 = elevationSample(wholeX + 1, wholeY);
        double elevation10 = elevationSample(wholeX, wholeY + 1);
        double elevation11 = elevationSample(wholeX + 1, wholeY + 1);
        double x = xIndex - wholeX;
        double y = yIndex - wholeY;

        return bilerp(elevation00, elevation01, elevation10, elevation11, x, y);
    }

    /**
     * Calculates the slope of a point belonging to a
     * {@link DiscreteElevationModel}
     * 
     * @param p
     *            a {@link GeoPoint}
     * @return the slope of a {@link GeoPoint} p
     */
    public double slopeAt(GeoPoint p) {
        double xIndex = sampleIndex(p.longitude());
        double yIndex = sampleIndex(p.latitude());
        int wholeX = (int) Math.floor(xIndex);
        int wholeY = (int) Math.floor(yIndex);
        double slope00 = slopeSample(wholeX, wholeY);
        double slope01 = slopeSample(wholeX + 1, wholeY);
        double slope10 = slopeSample(wholeX, wholeY + 1);
        double slope11 = slopeSample(wholeX + 1, wholeY + 1);
        double x = xIndex - wholeX;
        double y = yIndex - wholeY;

        return bilerp(slope00, slope01, slope10, slope11, x, y);
    }

    /**
     * Calculates the altitude of a known point. Returns 0 if the point doesn't
     * belong to the {@link DiscreteElevationModel}
     * 
     * @param x
     *            an index of a {@link DiscreteElevationModel}
     * @param y
     *            an index of a {@link DiscreteElevationModel}
     * @return the height at a point of index (x, y) and 0 if it doesn't belong
     *         to the discrete elevation model
     */
    private double elevationSample(int x, int y) {
        return dem.extent().contains(x, y) ? dem.elevationSample(x, y) : 0;
    }

    /**
     * Calculates the slope of a known point based on the elevation of the
     * neighbor points
     * 
     * @param x
     *            an index of a {@link DiscreteElevationModel}
     * @param y
     *            an index of a {@link DiscreteElevationModel}
     * @return the slope of a point at the index (x, y) and 0 if it doesn't
     *         belong to the {@link DiscreteElevationModel}
     */
    private double slopeSample(int x, int y) {
        double elevationAtOrigin = elevationSample(x, y);
        double deltaZa = elevationSample(x + 1, y) - elevationAtOrigin;
        double deltaZb = elevationSample(x, y + 1) - elevationAtOrigin;

        return Math.acos(DNS / Math.sqrt(sq(deltaZa) + sq(deltaZb) + sq(DNS)));
    }

    /**
     * Calculates the slope of a known point accurately by checking which index
     * to choose depending on the current position. Returns 0 if the point
     * doesn't belong to the {@link DiscreteElevationModel}
     * 
     * @param x
     *            an index of a {@link DiscreteElevationModel}
     * @param y
     *            an index of a {@link DiscreteElevationModel}
     * @return the slope of a point at the index (x, y) and 0 if it doesn't
     *         belong to the {@link DiscreteElevationModel}
     */
    private double accurateSlopeSample(int x, int y) {
        double deltaZa;
        double deltaZb;

        if (dem.extent().contains(x + 1, y)
                && dem.extent().contains(x, y + 1)) {
            deltaZa = elevationSample(x + 1, y) - elevationSample(x, y);
            deltaZb = elevationSample(x, y + 1) - elevationSample(x, y);
        } else if (dem.extent().contains(x - 1, y)
                && dem.extent().contains(x, y - 1)) {
            deltaZa = elevationSample(x - 1, y) - elevationSample(x, y);
            deltaZb = elevationSample(x, y - 1) - elevationSample(x, y);
        } else if (dem.extent().contains(x + 1, y)
                && dem.extent().contains(x, y - 1)) {
            deltaZa = elevationSample(x + 1, y) - elevationSample(x, y);
            deltaZb = elevationSample(x, y - 1) - elevationSample(x, y);
        } else if (dem.extent().contains(x - 1, y)
                && dem.extent().contains(x, y + 1)) {
            deltaZa = elevationSample(x - 1, y) - elevationSample(x, y);
            deltaZb = elevationSample(x, y + 1) - elevationSample(x, y);
        } else {
            return 0;
        }

        return Math.acos(DNS / Math.sqrt(sq(deltaZa) + sq(deltaZb) + sq(DNS)));
    }
}
