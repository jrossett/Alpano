package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Contains all the view informations of a {@link Panorama}
 */
public final class PanoramaParameters {

    private final GeoPoint observerPosition;
    private final int observerElevation;
    private final double centerAzimuth;
    private final double horizontalCenterIndex;
    private final double verticalCenterIndex;
    private final double horizontalFieldOfView;
    private final double verticalFieldOfView;
    private final int maxDistance;
    private final int width;
    private final int height;
    private final double anglePerPixel;
    private final double pixelPerRadians;

    /**
     * Constructs a new set of parameters to draw a {@link Panorama}
     * 
     * @param observerPosition
     *            position of the observer as a {@link GeoPoint}
     * @param observerElevation
     *            elevation of the observer in meters
     * @param centerAzimuth
     *            center direction of the view in radians. Must be canonical
     * @param horizontalFieldOfView
     *            horizontal field of view. Angle between 0 and
     *            {@link Math2.PI2}
     * @param maxDistance
     *            maximum non-negative view distance of the observer in meters
     * @param width
     *            non-negative width in pixels of the output image
     * @param height
     *            non-negative height in pixels of the output image
     */
    public PanoramaParameters(GeoPoint observerPosition, int observerElevation,
            double centerAzimuth, double horizontalFieldOfView, int maxDistance,
            int width, int height) {
        checkArgument(isCanonical(centerAzimuth));
        checkArgument(horizontalFieldOfView > 0
                && horizontalFieldOfView <= Math2.PI2);
        checkArgument(width > 0 && height > 0);
        checkArgument(maxDistance > 0);
        this.observerPosition = requireNonNull(observerPosition);
        this.observerElevation = observerElevation;
        this.centerAzimuth = centerAzimuth;
        this.horizontalFieldOfView = horizontalFieldOfView;
        this.verticalFieldOfView = horizontalFieldOfView
                * ((height - 1) / (double) (width - 1));
        this.maxDistance = maxDistance;
        this.width = width;
        this.height = height;
        this.anglePerPixel = verticalFieldOfView / (height - 1);
        this.pixelPerRadians = (height - 1) / verticalFieldOfView;
        this.horizontalCenterIndex = ((width - 1) / 2.0d);
        this.verticalCenterIndex = ((height - 1) / 2.0d);
    }

    /**
     * Returns the azimuth corresponding to a specific pixel on the
     * {@link Panorama} image. Throws an {@link IllegalArgumentException} if the
     * pixel index is not in range.
     * 
     * @param x
     *            horizontal pixel index
     * @return canonical azimuth in radians
     */
    public double azimuthForX(double x) {
        checkArgument(x >= 0 && x <= width() - 1);
        double pixelDelta = x - horizontalCenterIndex;
        return canonicalize(centerAzimuth() + (pixelDelta * anglePerPixel));
    }

    /**
     * Returns the horizontal pixel index of a given azimuth. Throws an
     * {@link IllegalArgumentException} if the angle is not in range.
     * 
     * @param a
     *            azimuth in radians
     * @return horizontal pixel index for a given azimuth
     */
    public double xForAzimuth(double a) {
        double angleDelta = Math2.angularDistance(centerAzimuth(), a);
        checkArgument(Math.abs(angleDelta) <= horizontalFieldOfView() / 2d);
        return angleDelta * pixelPerRadians + horizontalCenterIndex;
    }

    /**
     * Returns the pitch angle of a given vertical pixel index. Throws an
     * {@link IllegalArgumentException} if the pixel index is not in range.
     * 
     * @param y
     *            vertical pixel index
     * @return pitch angle in radians
     */
    public double altitudeForY(double y) {
        checkArgument(y >= 0 && y <= height() - 1);
        double pixelDelta = verticalCenterIndex - y;
        return pixelDelta * anglePerPixel;
    }

    /**
     * Returns the pixel index of a given pitch angle. Throws an
     * {@link IllegalArgumentException} if the pitch angle is not in range
     * 
     * @param a
     *            pitch angle in radians
     * @return vertical pixel index for the pitch angle
     */
    public double yForAltitude(double a) {
        checkArgument(Math.abs(a) <= verticalFieldOfView() / 2d);
        return verticalCenterIndex - a * pixelPerRadians;
    }

    /**
     * Checks if a given pixel index is in range of the image or not
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return true if the pixel is in range, false otherwise
     */
    protected boolean isValidSampleIndex(int x, int y) {
        return y >= 0 && y < height() && x >= 0 && x < width();
    }

    /**
     * Returns the linear pixel index of a given valid pixel index
     * 
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the linear index of the given pixel
     */
    protected int linearSampleIndex(int x, int y) {
        checkArgument(isValidSampleIndex(x, y));
        return y * width + x;
    }

    /**
     * Gets the position of the observer
     * 
     * @return the position of the observer as a {@link GeoPoint}
     */
    public GeoPoint observerPosition() {
        return observerPosition;
    }

    /**
     * Gets the elevation of the observer
     * 
     * @return the elevation of the observer in meters
     */
    public int observerElevation() {
        return observerElevation;
    }

    /**
     * Gets the center azimuth of the {@link Panorama}
     * 
     * @return the azimuth in radians
     */
    public double centerAzimuth() {
        return centerAzimuth;
    }

    /**
     * Gets the horizontal field of view of the {@link Panorama}
     * 
     * @return the horizontal field of view in radians
     */
    public double horizontalFieldOfView() {
        return horizontalFieldOfView;
    }

    /**
     * Gets the vertical field of view of the {@link Panorama}
     * 
     * @return the vertical field of view in radians
     */
    public double verticalFieldOfView() {
        return verticalFieldOfView;
    }

    /**
     * Gets the maximum distance at which the rays can go
     * 
     * @return the maximum distance in meters
     */
    public int maxDistance() {
        return maxDistance;
    }

    /**
     * Gets the width of the image in pixel
     * 
     * @return the width of the panorama image in pixel
     */
    public int width() {
        return width;
    }

    /**
     * Gets the height of the image in pixel
     * 
     * @return the height of the panorama image in pixel
     */
    public int height() {
        return height;
    }

}
