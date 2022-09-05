package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.Preconditions.checkArgument;

/**
 * Interface providing methods to work with azimuth 
 */
public interface Azimuth {

    /**
     * Checks if an azimuth value is in the range [0; 360[ in degree
     * 
     * @param azimuth
     *            angle in radians
     * @return true if the value is in range, false otherwise
     */
    public static boolean isCanonical(double azimuth) {
        return azimuth >= 0 && azimuth < Math2.PI2;
    }

    /**
     * Constrains an angle in the range [0; 360[ in degree
     * 
     * @param azimuth
     *            angle in radians
     * @return the azimuth value in the canonical range
     */
    public static double canonicalize(double azimuth) {
        if (!isCanonical(azimuth)) {
            azimuth %= Math2.PI2;

            if (azimuth < 0) {
                azimuth += Math2.PI2;
            }
        }
        return azimuth;
    }

    /**
     * Transforms a clockwise increasing angle to a mathematical counter
     * clockwise increasing angle
     * 
     * @param azimuth
     *            angle in radians
     * @return the opposite of the angle in radians, throw an
     *         {@link IllegalArgumentException} if azimuth is not canonical
     */
    public static double toMath(double azimuth) {
        checkArgument(isCanonical(azimuth));
        return canonicalize(-azimuth);
    }

    /**
     * Transforms a counter clockwise mathematical increasing angle to a
     * clockwise increasing angle
     * 
     * @param azimuth
     *            angle in radians (must be canonical)
     * @return the opposite of the angle in radians, throw an
     *         {@link IllegalArgumentException} if azimuth is not canonical
     */
    public static double fromMath(double azimuth) {
        checkArgument(isCanonical(azimuth));
        return canonicalize(-azimuth);
    }

    /**
     * Creates a string that describes the octant in which the azimuth is
     * pointing at
     * 
     * @param azimuth
     *            canonical angle in radians
     * @param n
     *            string that represents the north direction
     * @param e
     *            string that represents the east direction
     * @param s
     *            string that represents the south direction
     * @param w
     *            string that represents the west direction
     * @return the string that represents the direction the angle is pointing
     *         at, throw an {@link IllegalArgumentException} if azimuth is not
     *         canonical
     */
    public static String toOctantString(double azimuth, String n, String e,
            String s, String w) {
        checkArgument(isCanonical(azimuth));
        String[] cardinalPoints = { n, n + e, e, s + e, s, s + w, w, n + w };

        azimuth += Math2.PI2 / 16;
        azimuth = floorMod(azimuth, Math2.PI2);
        int cadran = (int) Math.floor(azimuth / (Math2.PI2 / 8d));
        return cardinalPoints[cadran];
    }
}
