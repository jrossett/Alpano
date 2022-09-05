package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.fromMath;
import static ch.epfl.alpano.Distance.toMeters;
import static ch.epfl.alpano.Math2.haversin;
import static ch.epfl.alpano.Preconditions.checkArgument;

/**
 * Describes a point on the globe defined by a latitude and a longitude
 */
public final class GeoPoint {

    private final double longitude;
    private final double latitude;

    /**
     * Constructs a GeoPoint represented by a longitude and a latitude. Throw an
     * {@link IllegalArgumentException} if longitude is not between [-PI, PI] or
     * longitude is not between [-PI/2, PI/2]
     * 
     * @param longitude
     *            a lateral coordinate in radians
     * @param latitude
     *            a vertical coordinate in radians
     */
    public GeoPoint(double longitude, double latitude) {
        checkArgument(longitude >= -Math.PI && longitude <= Math.PI
                && latitude >= -Math.PI / 2d && latitude <= Math.PI / 2d);

        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the GeoPoint
     * 
     * @return the longitude of the GeoPoint
     */
    public double longitude() {
        return longitude;
    }

    /**
     * Gets the latitude of the GeoPoint
     * 
     * @return the latitude of the GeoPoint
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Calculates the distance between two points on the globe
     * 
     * @param that
     *            a GeoPoint
     * @return the distance between two GeoPoint in meters
     */
    public double distanceTo(GeoPoint that) {
        double lat1 = this.latitude();
        double lat2 = that.latitude();
        double lon1 = this.longitude();
        double lon2 = that.longitude();

        double angle = 2 * Math.asin(Math.sqrt(haversin(lat1 - lat2)
                + Math.cos(lat1) * Math.cos(lat2) * haversin(lon1 - lon2)));
        return toMeters(angle);
    }

    /**
     * Calculates the angle between the horizon and another point on the globe
     * 
     * @param that
     *            a GeoPoint
     * @return an angle in radians
     */
    public double azimuthTo(GeoPoint that) {
        double lat1 = this.latitude();
        double lat2 = that.latitude();
        double lon1 = this.longitude();
        double lon2 = that.longitude();

        double azimuth = Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                        * Math.cos(lat2) * Math.cos(lon1 - lon2));
        return fromMath(canonicalize(azimuth));
    }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f)", Math.toDegrees(longitude()),
                Math.toDegrees(latitude()));
    }
}
