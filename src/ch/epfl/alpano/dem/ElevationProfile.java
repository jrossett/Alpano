package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.toMath;
import static ch.epfl.alpano.Distance.toRadians;
import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.lerp;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.scalb;
import static java.lang.Math.sin;
import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Class containing elevation data on a straight line defined by an origin and an azimuth
 */
public final class ElevationProfile {

    private static final int EXPONENT = 12;
    private final ContinuousElevationModel elevationModel;
    private final double length;
    private final double[] samples;

    /**
     * Constructs an elevation profile
     * 
     * @param elevationModel
     *            a {@link ContinuousElevationModel}
     * @param origin
     *            a {@link GeoPoint} representing the starting point of the elevation
     *            profile
     * @param azimuth
     *            an angle representing the direction of the elevation profile. Must be canonical
     * @param length
     *            a non-negative value representing the length of the elevation profile
     */
    public ElevationProfile(ContinuousElevationModel elevationModel,
            GeoPoint origin, double azimuth, double length) {
        checkArgument(isCanonical(azimuth) && length > 0);
        requireNonNull(origin);
        this.elevationModel = requireNonNull(elevationModel);
        this.length = length;
        samples = new double[2 * ((int) (scalb(length, -EXPONENT)) + 2)];
        initArrays(azimuth, length, origin);
        
    }

    /**
     * Creates an array of uniformly spaced longitudes and latitudes along the elevation profile
     * 
     * @param azimuth
     *            an angle representing the direction of the elevation profile
     * @param length
     *            a value representing the length of the elevation profile
     * @param a
     *            {@link GeoPoint} representing the starting point of the elevation
     *            profile
     */
    public void initArrays(double azimuth, double length, GeoPoint origin) {
        double latitude;
        double longitude;
       
        for (int j = 0; j < samples.length; j += 2) {
            double angle = scalb(scalb(j, -1), EXPONENT);
            latitude = asin(sin(origin.latitude()) * cos(toRadians(angle))
                    + cos(origin.latitude()) * sin(toRadians(angle))
                            * cos(toMath(azimuth)));
            longitude = (origin.longitude() - asin(sin(toRadians(angle))
                    * sin(toMath(azimuth)) / cos(latitude)) + PI) % PI2 - PI;
            samples[j] = longitude;
            samples[j + 1] = latitude;
        }
    }

    /**
     * Give the elevation of a point at a distance to the origin of the
     * elevation profile
     * 
     * @param x
     *            the distance to the origin
     * @return the elevation at the point of distance x from the origin
     */
    public double elevationAt(double x) {
        checkArgument(x >= 0 && x <= length);
        return elevationModel.elevationAt(positionAt(x));
    }

    /**
     * Give the position as a {@link GeoPoint} of a point at a certain distance on the elevation profile
     * 
     * @param x
     *            the distance to the origin
     * @return a GeoPoint of distance x from the origin
     */
    public GeoPoint positionAt(double x) {
        checkArgument(x >= 0 && x <= length);
        double i = scalb(x, -EXPONENT);
        int j = (int) i;
        int index = (int) scalb(j, 1);
        return new GeoPoint(lerp(samples[index], samples[index + 2], i - j),
                lerp(samples[index + 1], samples[index + 3], i - j));
    }

    /**
     * Give the slope at a point of a certain distance from the origin
     * 
     * @param x
     *            the distance to the origin
     * @return the slope at a point of distance x to the origin
     */
    public double slopeAt(double x) {
        checkArgument(x >= 0 && x <= length);
        return elevationModel.slopeAt(positionAt(x));
    }
}
