package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.Interval2D;

/**
 * Elevation model composed by two {@link DiscreteElevationModel}
 */
public final class CompositeDiscreteElevationModel implements DiscreteElevationModel {

    private final DiscreteElevationModel dem1;
    private final DiscreteElevationModel dem2;
    private final Interval2D union;

    /**
     * Construct a discrete elevation model composed by two {@link DiscreteElevationModel}
     * 
     * @param dem1
     *            first {@link DiscreteElevationModel}
     * @param dem2
     *            second {@link DiscreteElevationModel}
     */
    public CompositeDiscreteElevationModel(DiscreteElevationModel dem1,
            DiscreteElevationModel dem2) {
        requireNonNull(dem1);
        requireNonNull(dem2);
        this.dem1 = dem1;
        this.dem2 = dem2;
        this.union = dem1.extent().union(dem2.extent());
    }

    @Override
    public void close() throws Exception {
        dem1.close();
        dem2.close();
    }

    @Override
    public Interval2D extent() {
        return union;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y));

        if (dem1.extent().contains(x, y)) {
            return dem1.elevationSample(x, y);
        }   
        if (dem2.extent().contains(x, y)) {
            return dem2.elevationSample(x, y);
        }
        return 0;
    }
}
