package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.function.DoubleUnaryOperator;

/**
 * Interface containing useful math functions used in the panorama calculations
 */
public interface Math2 {

    public static double PI2 = 2 * Math.PI;

    /**
     * Squares a number
     * 
     * @param x
     *            a double
     * @return the square of x
     */
    public static double sq(double x) {
        return x * x;
    }

    /**
     * Calculates the rest of the integer division of two numbers
     * 
     * @param x
     *            a double
     * @param y
     *            a double
     * @return the rest of the integer division by default of x by y
     */
    public static double floorMod(double x, double y) {
        return x - y * Math.floor(x / y);
    }

    /**
     * Calculates the haversine of a number
     * 
     * @param x
     *            a double
     * @return the half of the versine of x
     */
    public static double haversin(double x) {
        return sq(Math.sin(x / 2));
    }

    /**
     * Calculates the difference between two angles
     * 
     * @param a1
     *            an angle in radians
     * @param a2
     *            an angle in radians
     * @return the difference between a1 and a2
     */
    public static double angularDistance(double a1, double a2) {
        return (floorMod((a2 - a1 + Math.PI), PI2) - Math.PI);
    }

    /**
     * Calculates the value of a point between two boundaries by linearly
     * interpolating between them
     * 
     * @param y0
     *            lower boundary
     * @param y1
     *            upper boundary
     * @param x
     *            value between 0 and 1 at which to get the value from the
     *            linear interpolant (linear function made from two points)
     * @return the value on the linear interpolant (linear function made from
     *         two points) given by x
     */
    public static double lerp(double y0, double y1, double x) {
        return y0 + x * (y1 - y0);
    }

    /**
     * Calculates the value of a point between four points on a plane using
     * bilinear interpolation
     * 
     * @param z00
     *            lower left corner of the plane
     * @param z10
     *            lower right corner of the plane
     * @param z01
     *            upper left corner of the plane
     * @param z11
     *            upper right corner of the plane
     * @param x
     *            value between 0 and 1 at which to get the value from the
     *            linear interpolant (linear function made from two points) on
     *            the x axis
     * @param y
     *            value between 0 and 1 at which to get the value from the
     *            linear interpolant (linear function made from two points) on
     *            the y axis
     * @return value on the bilinear interpolant (linear function made from two
     *         points) given by x and y
     */
    public static double bilerp(double z00, double z10, double z01, double z11,
            double x, double y) {
        return lerp(lerp(z00, z10, x), lerp(z01, z11, x), y);
    }

    /**
     * Search the first interval contained between two boundaries of a function
     * containing a root. Throws an {@link IllegalArgumentException} if minX is
     * greater than maxX or if dX is smaller than 0.
     * 
     * @param f
     *            a function represented as a {@link DoubleUnaryOperator}
     * @param minX
     *            lower bound of the sample space
     * @param maxX
     *            upper bound of the sample space
     * @param dX
     *            size of the interval we are searching the root in
     * @return the lower bound of the interval dX if a root is found. Return
     *         {@link Double.POSITIVE_INFINITY} if none was found in the sample
     *         space
     */
    public static double firstIntervalContainingRoot(DoubleUnaryOperator f,
            double minX, double maxX, double dX) {
        checkArgument(minX <= maxX || dX > 0);
        double f1, f2, currentMin, currentMax;
        currentMin = minX;
        f1 = f.applyAsDouble(currentMin);

        while (currentMin < maxX) {
            currentMax = currentMin + dX;
            
            if(currentMax > maxX){
                break;
            }
            
            f2 = f.applyAsDouble(currentMax);

            if (f1 * f2 <= 0) {
                return currentMin;
            }

            currentMin = currentMax;
            f1 = f2;
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Search an interval of size epsilon containing a root between two
     * boundaries of a function. Throws an {@link IllegalArgumentException} if
     * there are no roots in the interval.
     * 
     * @param f
     *            a function represented as a {@link DoubleUnaryOperator}
     * @param x1
     *            lower bound of the sample space
     * @param x2
     *            upper bound of the sample space
     * @param epsilon
     *            size of the interval containing the root. We stop the
     *            iterations if the interval found is smaller
     * @return the lower bound of the interval of size epsilon containing a root
     *         of the function
     */
    public static double improveRoot(DoubleUnaryOperator f, double x1,
            double x2, double epsilon) {
        checkArgument(f.applyAsDouble(x1) * f.applyAsDouble(x2) <= 1e-10);
        // To counter any imprecision on the values of the function

        double half;

        while (x2 - x1 > epsilon) {
            half = (x1 + x2) / 2;

            if (f.applyAsDouble(half) * f.applyAsDouble(x1) <= 0) {
                x2 = half;
            } else {
                x1 = half;
            }
        }
        return x1;
    }
}