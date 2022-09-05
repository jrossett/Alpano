package ch.epfl.alpano.gui;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Panorama;

@FunctionalInterface

/**
 * Functional interface that is used to define matrices of data and provides
 * operations on these tables
 */
public interface ChannelPainter {

    /**
     * Give the value of the ChannelPainter at a point
     * 
     * @param x
     *            horizontal coordinate
     * @param y
     *            vertical coordinate
     * @return the value of the ChannelPainter at the point (x,y)
     */
    public abstract float valueAt(int x, int y);

    /**
     * Give the maximum difference of distance between a point of a panorama and
     * the points next to it
     * 
     * @param panorama
     *            the panorama used
     * @return a ChannelPainter that represents the maximum distance between a
     *         point and it's neighbors or between the point and zero if no
     *         neighbors exist
     */
    public static ChannelPainter maxDistanceToNeighbors(Panorama panorama) {
        return (x,
                y) -> max(max(panorama.distanceAt(x - 1, y, 0), panorama.distanceAt(x + 1, y, 0)),
                        max(panorama.distanceAt(x, y - 1, 0), panorama.distanceAt(x, y + 1, 0)))
                        - panorama.distanceAt(x, y, 0);
    }

    /**
     * Adds a constant to a ChannelPainter
     * 
     * @param cst
     *            constant value to add to the ChannelPainter
     * @return a ChannelPainter with the constant added
     */
    default ChannelPainter add(float cst) {
        return (x, y) -> valueAt(x, y) + cst;
    }

    /**
     * Adds a ChannelPainter to another ChannelPainter
     * 
     * @param other
     *            ChannelPainter to add to the other ChannelPainter
     * @return the sum of the two ChannelPainter
     */
    default ChannelPainter add(ChannelPainter other) {
        return (x, y) -> valueAt(x, y) + other.valueAt(x, y);
    }

    /**
     * Subtracts a constant to a ChannelPainter
     * 
     * @param cst
     *            constant value to subtract to the ChannelPainter
     * @return the ChannelPainter with the constant subtracted
     */
    default ChannelPainter sub(float cst) {
        return add(-cst);
    }

    /**
     * Subtracts a ChannelPainter to another ChannelPainter
     * 
     * @param other
     *            ChannelPainter to subtract to the other ChannelPainter
     * @return the subtraction of the two ChannelPainter
     */
    default ChannelPainter sub(ChannelPainter other) {
        return (x, y) -> valueAt(x, y) - other.valueAt(x, y);
    }

    /**
     * Multiplies the value of a ChannelPainter by a constant
     * 
     * @param cst
     *            constant value that multiplies the ChannelPainter
     * @return the ChannelPainter multiplied by the constant
     */
    default ChannelPainter mul(float cst) {
        return (x, y) -> valueAt(x, y) * cst;
    }

    /**
     * Multiplies two ChannelPainter together
     * 
     * @param other
     *            ChannelPainter that multiplies the other ChannelPainter
     * @return the two ChannelPainter multiplied together
     */
    default ChannelPainter mul(ChannelPainter other) {
        return (x, y) -> valueAt(x, y) * other.valueAt(x, y);
    }

    /**
     * Divides the value of a ChannelPainter by a constant
     * 
     * @param cst
     *            constant value that divides the ChannelPainter
     * @return the ChannelPainter divided by the constant
     */
    default ChannelPainter div(float cst) {
        return (x, y) -> valueAt(x, y) / cst;
    }

    /**
     * Applies a {@linked DoubleUnaryOperator} to a ChannelPainter
     * 
     * @param operator
     *            the {@linked DoubleUnaryOperator} to apply
     * @return a ChannelPainter after applying the operator
     */
    default ChannelPainter map(DoubleUnaryOperator operator) {
        return (x, y) -> (float) operator.applyAsDouble(valueAt(x, y));
    }

    /**
     * Clamps the values of a ChannelPainter between 0 and 1
     * 
     * @return a clamped ChannelPainter
     */
    default ChannelPainter clamped() {
        return (x, y) -> max(0, min(valueAt(x, y), 1));
    }

    /**
     * Extracts the fractional part of the values of a ChannelPainter
     * 
     * @return a ChannelPainter with all its values reduced to their fractional
     *         parts
     */
    default ChannelPainter cycle() {
        return (x, y) -> valueAt(x, y) % 1;
    }

    /**
     * Returns a ChannelPainter with the one-complement of its values
     * 
     * @return the one-complement of the values of the ChannelPainter
     */
    default ChannelPainter inverted() {
        return (x, y) -> 1 - valueAt(x, y);
    }
}
