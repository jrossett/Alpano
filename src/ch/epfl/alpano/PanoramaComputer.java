package ch.epfl.alpano;

import static ch.epfl.alpano.Distance.EARTH_RADIUS;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.improveRoot;
import static ch.epfl.alpano.Math2.sq;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;

/**
 * Stores a {@link ContinuousElevationModel} to calculate values of a
 * {@link Panorama} given by a set of parameters
 */
public final class PanoramaComputer {

    private final static double UNPRECIZE_INTERVAL_SIZE = 64.0;
    private final static double PRECIZE_INTERVAL_SIZE = 4.0;
    private final static double REFRAC_COEFF = 0.13;
    private final static double DELTA = (1 - REFRAC_COEFF) / (2 * EARTH_RADIUS);
    private final ContinuousElevationModel dem;

    /**
     * Constructs a panorama computer
     * 
     * @param dem
     *            {@link ContinuousElevationModel} that contains the information
     *            about the terrain
     */
    public PanoramaComputer(ContinuousElevationModel dem) {
        this.dem = requireNonNull(dem);
    }

    /**
     * Calculates the value of each parameters of the {@link Panorama} for every pixels
     * on the image
     * 
     * @param parameters
     *            parameters for the panorama as a {@link PanoramaParameters}
     * @return a new initialized {@link Panorama} that can be drawn
     */
    public Panorama computePanorama(PanoramaParameters parameters) {
        Panorama.Builder builder = new Panorama.Builder(parameters);
        ElevationProfile profile;
        DoubleUnaryOperator distanceToGround;
        double firstInterval;
        double intersectionPoint;
        GeoPoint intersect;
        double minDistance;
        double altitudeForY;

        for (int x = 0; x < parameters.width(); x++) {
            profile = new ElevationProfile(dem, parameters.observerPosition(),
                    parameters.azimuthForX(x), parameters.maxDistance());
            minDistance = 0;

            for (int y = parameters.height() - 1; y >= 0; y--) {
                altitudeForY = parameters.altitudeForY(y);
                distanceToGround = rayToGroundDistance(profile,
                        parameters.observerElevation(),
                        Math.tan(altitudeForY));
                firstInterval = firstIntervalContainingRoot(distanceToGround,
                        minDistance, parameters.maxDistance(),
                        UNPRECIZE_INTERVAL_SIZE);

                if (firstInterval <= parameters.maxDistance()) {

                    intersectionPoint = improveRoot(distanceToGround,
                            firstInterval,
                            firstInterval + UNPRECIZE_INTERVAL_SIZE > parameters
                                    .maxDistance()
                                            ? parameters.maxDistance()
                                            : firstInterval
                                                    + UNPRECIZE_INTERVAL_SIZE,
                            PRECIZE_INTERVAL_SIZE);
                    intersect = profile.positionAt(intersectionPoint);

                    builder.setDistanceAt(x, y,
                            (float) (intersectionPoint
                                    / Math.cos(altitudeForY)))
                            .setElevationAt(x, y,
                                    (float) profile
                                            .elevationAt(intersectionPoint))
                            .setLatitudeAt(x, y, (float) intersect.latitude())
                            .setLongitudeAt(x, y, (float) intersect.longitude())
                            .setSlopeAt(x, y,
                                    (float) profile.slopeAt(intersectionPoint));

                    minDistance = intersectionPoint;
                } else {
                    break;
                }
            }
        }
        return builder.build();
    }

    /**
     * Return the function that gives the distance between a given {@link ElevationProfile} and a ray
     * 
     * @param profile
     *            an {@link ElevationProfile}
     * @param ray0
     *            starting elevation of the ray in meters
     * @param raySlope
     *            slope of the ray in radians
     * @return a function that gives the distance between the ray and the
     *         {@link ElevationProfile}
     */
    public static DoubleUnaryOperator rayToGroundDistance(
            ElevationProfile profile, double ray0, double raySlope) {
        return (x) -> ray0 + x * raySlope - profile.elevationAt(x)
                + DELTA * sq(x);

    }
}
