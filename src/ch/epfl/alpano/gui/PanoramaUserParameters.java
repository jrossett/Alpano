package ch.epfl.alpano.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;

/**
 * Class that represents the parameters of a {@link Panorama}
 */
public final class PanoramaUserParameters {

    private Map<UserParameter, Integer> parameters;

    /**
     * Constructs a PanoramaUserParameters from a map of parameters and values
     * 
     * @param parameters
     *            map of {@link UserParameter} to {@link Integer}
     */
    public PanoramaUserParameters(Map<UserParameter, Integer> parameters) {
        this.parameters = Collections.unmodifiableMap(new EnumMap<>(parameters));
    }

    /**
     * Constructs a PanoramaUserParameters from individual parameters
     * 
     * @param longitude
     *            longitude parameter in thousandth of degrees
     * @param latitude
     *            latitude parameter in thousandth of degrees
     * @param elevation
     *            elevation parameter in meters
     * @param centerAzimuth
     *            center azimuth in thousandth of degrees
     * @param horizontalFieldOfView
     *            horizontal field of view in degrees
     * @param maxDistance
     *            maximum render distance in meters
     * @param width
     *            width of the image in pixel
     * @param height
     *            height of the image in pixel
     * @param superSamplingExponent
     *            super sampling exponent
     */
    public PanoramaUserParameters(int longitude, int latitude, int elevation, int centerAzimuth,
            int horizontalFieldOfView, int maxDistance, int width, int height,
            int superSamplingExponent) {
        this(zipAsMap(Arrays.asList(UserParameter.values()),
                Arrays.asList(longitude, latitude, elevation, centerAzimuth, horizontalFieldOfView,
                        maxDistance, width,
                        (int)Math.min(height, 170 * ((width - 1) / (double)horizontalFieldOfView) + 1),
                        superSamplingExponent)));
    }

    /**
     * Creates a map from {@link UserParameter} to {@link Integer} from two
     * lists
     * 
     * @param param
     *            list of {@link UserParameter}
     * @param values
     *            list of corresponding integer values
     * @return an {@link EnumMap} from {@link UserParameter} to {@link Integer}
     */
    private static EnumMap<UserParameter, Integer> zipAsMap(List<UserParameter> param,
            List<Integer> values) {
        EnumMap<UserParameter, Integer> parameters = new EnumMap<UserParameter, Integer>(
                UserParameter.class);
        for (int i = 0; i < param.size(); i++) {
            parameters.put(param.get(i), param.get(i).sanitize(values.get(i)));
        }
        return parameters;
    }

    /**
     * Gets the {@link PanoramaParameters} of the corresponding
     * PanoramaUserParameters ready to be rendered
     * 
     * @return a corresponding {@link PanoramaParameters}
     */
    public PanoramaParameters panoramaParameters() {
        return new PanoramaParameters(
                new GeoPoint(
                        Math.toRadians(parameters.get(UserParameter.OBSERVER_LONGITUDE) / 10_000d),
                        Math.toRadians(parameters.get(UserParameter.OBSERVER_LATITUDE) / 10_000d)),
                parameters.get(UserParameter.OBSERVER_ELEVATION),
                Math.toRadians(parameters.get(UserParameter.CENTER_AZIMUTH)),
                Math.toRadians(parameters.get(UserParameter.HORIZONTAL_FIELD_OF_VIEW)),
                parameters.get(UserParameter.MAX_DISTANCE) * 1000,
                (int) Math.scalb(parameters.get(UserParameter.WIDTH),
                        parameters.get(UserParameter.SUPER_SAMPLING_EXPONENT)),
                (int) Math.scalb(parameters.get(UserParameter.HEIGHT),
                        parameters.get(UserParameter.SUPER_SAMPLING_EXPONENT)));
    }

    /**
     * Gets the {@link PanoramaParameters} of the corresponding
     * PanoramaUserParameters ready to be displayeds
     * 
     * @return a corresponding {@link PanoramaParameters}
     */
    public PanoramaParameters panoramaDisplayParameters() {
        return new PanoramaParameters(
                new GeoPoint(
                        Math.toRadians(parameters.get(UserParameter.OBSERVER_LONGITUDE) / 10_000d),
                        Math.toRadians(parameters.get(UserParameter.OBSERVER_LATITUDE) / 10_000d)),
                parameters.get(UserParameter.OBSERVER_ELEVATION),
                Math.toRadians(parameters.get(UserParameter.CENTER_AZIMUTH)),
                Math.toRadians(parameters.get(UserParameter.HORIZONTAL_FIELD_OF_VIEW)),
                parameters.get(UserParameter.MAX_DISTANCE) * 1000,
                parameters.get(UserParameter.WIDTH), parameters.get(UserParameter.HEIGHT));
    }

    /**
     * Gets the integer parameter corresponding to a {@link UserParameter}
     * 
     * @param parameter
     *            {@link UserParameter}
     * @return the corresponding integer parameter
     */
    public int get(UserParameter parameter) {
        return parameters.get(parameter);
    }

    /**
     * Gets the longitude parameter
     * 
     * @return the integer longitude parameter
     */
    public int observerLongitude() {
        return get(UserParameter.OBSERVER_LONGITUDE);
    }

    /**
     * Gets the latitude parameter
     * 
     * @return the integer latitude parameter
     */
    public int observerLatitude() {
        return get(UserParameter.OBSERVER_LATITUDE);
    }

    /**
     * Gets the elevation parameter
     * 
     * @return the integer elevation parameter
     */
    public int observerElevation() {
        return get(UserParameter.OBSERVER_ELEVATION);
    }

    /**
     * Gets the center azimuth parameter
     * 
     * @return the integer center azimuth parameter
     */
    public int centerAzimuth() {
        return get(UserParameter.CENTER_AZIMUTH);
    }

    /**
     * Gets the horizontal field of view parameter
     * 
     * @return the integer horizontal field of view parameter
     */
    public int horizontalFieldOfView() {
        return get(UserParameter.HORIZONTAL_FIELD_OF_VIEW);
    }

    /**
     * Gets the maximum distance parameter
     * 
     * @return the integer maximum distance parameter
     */
    public int maxDistance() {
        return get(UserParameter.MAX_DISTANCE);
    }

    /**
     * Gets the width parameter
     * 
     * @return the integer width parameter
     */
    public int width() {
        return get(UserParameter.WIDTH);
    }

    /**
     * Gets the height parameter
     * 
     * @return the integer height parameter
     */
    public int height() {
        return get(UserParameter.HEIGHT);
    }

    /**
     * Gets the super sampling exponent parameter
     * 
     * @return the integer super sampling exponent parameter
     */
    public int superSamplingExponent() {
        return get(UserParameter.SUPER_SAMPLING_EXPONENT);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PanoramaUserParameters)) {
            return false;
        }
        for (UserParameter param : parameters.keySet()) {
            if (((PanoramaUserParameters) other).get(param) != parameters.get(param)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return parameters.hashCode();
    }
}
