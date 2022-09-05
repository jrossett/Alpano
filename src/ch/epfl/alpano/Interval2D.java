package ch.epfl.alpano;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * Describes an interval in two dimension defined by two {@link Interval1D}
 */
public final class Interval2D {

    private final Interval1D iX;
    private final Interval1D iY;

    /**
     * Constructs an Interval2D based on two {@link Interval1D}
     * 
     * @param iX
     *            an {@link Interval1D}
     * @param iY
     *            an {@link Interval1D}
     */
    public Interval2D(Interval1D iX, Interval1D iY) {
        this.iX = requireNonNull(iX);
        this.iY = requireNonNull(iY);
    }

    /**
     * Gets the first interval of the Interval2D
     * 
     * @return the {@link Interval1D} on X
     */
    public Interval1D iX() {
        return iX;
    }

    /**
     * Gets the second interval of the Interval2D
     * 
     * @return the {@link Interval1D} on Y
     */
    public Interval1D iY() {
        return iY;
    }

    /**
     * Checks if a set of values belongs to the interval
     * 
     * @param x
     *            a value
     * @param y
     *            a value
     * @return true if x belongs to iX and y to iY
     */
    public boolean contains(int x, int y) {
        return iX().contains(x) && iY().contains(y);
    }

    /**
     * Calculates the size of the interval
     * 
     * @return size of the space created by the interval
     */
    public int size() {
        return iX().size() * iY().size();
    }

    /**
     * Calculates the size of the intersection between two interval2D
     * 
     * @param that
     *            an {@link Interval2D}
     * @return the size of the intersection
     */
    public int sizeOfIntersectionWith(Interval2D that) {
        return iX().sizeOfIntersectionWith(that.iX())
                * iY().sizeOfIntersectionWith(that.iY());
    }

    /**
     * Create the bounding union between two {@link Interval2D}
     * 
     * @param that
     *            an {@link Interval2D}
     * @return the bounding union of two interval
     */
    public Interval2D boundingUnion(Interval2D that) {
        return new Interval2D(iX().boundingUnion(that.iX()),
                iY().boundingUnion(that.iY()));
    }

    /**
     * Check if the two {@link Interval2D} are not entirely distinct
     * 
     * @param that
     *            an {@link Interval2D}
     * @return true if they are not distinct
     */
    public boolean isUnionableWith(Interval2D that) {
        int unionSize = size() + that.size() - sizeOfIntersectionWith(that);

        return unionSize == boundingUnion(that).size();
    }

    /**
     * Gets the union between this two Interval2D
     * 
     * @param that
     *            an {@link Interval2D}
     * @return an interval if they are unionable, throw an
     *         {@link IllegalArgumentException} otherwise
     */
    public Interval2D union(Interval2D that) {
        return new Interval2D(iX().union(that.iX()), iY().union(that.iY()));
    }

    @Override
    public boolean equals(Object thatO) {
        return thatO instanceof Interval2D
                && iX().equals(((Interval2D) thatO).iX())
                && iY().equals(((Interval2D) thatO).iY());
    }

    @Override
    public int hashCode() {
        return hash(iX(), iY());
    }

    @Override
    public String toString() {
        return String.format("%sx%s", iX().toString(), iY().toString());
    }
}
