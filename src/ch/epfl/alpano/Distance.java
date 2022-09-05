package ch.epfl.alpano;

/**
 * Interface providing methods to make conversion between angle and meters
 */
public interface Distance {

    static double EARTH_RADIUS = 6371000; // Earth radius in meters

    /**
     * Converts a distance at the surface of the Earth to the corresponding
     * angle in radians
     * 
     * @param distanceInMeters
     *            distance in meters
     * @return the corresponding angle
     */
    public static double toRadians(double distanceInMeters) {
        return distanceInMeters / EARTH_RADIUS;
    }

    /**
     * Converts an angle in radians to the corresponding distance at the surface
     * of the Earth
     * 
     * @param distanceInRadians
     *            angle in radians
     * @return the corresponding distance in meters
     */
    public static double toMeters(double distanceInRadians) {
        return distanceInRadians * EARTH_RADIUS;
    }
}
