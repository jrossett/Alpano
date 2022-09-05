package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.util.Objects;

/**
 * Represents an interval of integer between two bounds
 */
public final class Interval1D {

    private final int includedFrom;
    private final int includedTo;

    /**
     * Constructs an integer interval between two values (included in the
     * interval)
     * 
     * @param includedFrom
     *            the lower bound of an interval
     * @param includedTo
     *            the upper bound of an interval
     */
    public Interval1D(int includedFrom, int includedTo) {
        checkArgument(includedFrom <= includedTo);
        this.includedFrom = includedFrom;
        this.includedTo = includedTo;
    }

    /**
     * Give the lower bound of an interval
     * 
     * @return the lower bound of the interval
     */
    public int includedFrom() {
        return includedFrom;
    }

    /**
     * Give the upper bound of an interval
     * 
     * @return upper bound of the interval
     */
    public int includedTo() {
        return includedTo;
    }

    /**
     * Test if a value belongs to an interval
     * 
     * @param v
     *            a value
     * @return true if v belongs in the interval
     */
    public boolean contains(int v) {
        return v >= this.includedFrom() && v <= this.includedTo();
    }

    /**
     * Calculate the size of an interval
     * 
     * @return the size of the interval
     */
    public int size() {
        return this.includedTo() - this.includedFrom() + 1;
    }

    /**
     * Calculate the size of the intersection of two interval
     * 
     * @param that
     *            an interval
     * @return the size of the intersection
     */
    public int sizeOfIntersectionWith(Interval1D that) {
        if (that.includedFrom() > this.includedTo()
                || that.includedTo() < this.includedFrom()) {
            return 0;
        } else {
            return Math.min(this.includedTo(), that.includedTo())
                    - Math.max(this.includedFrom(), that.includedFrom()) + 1;
        }
    }

    /**
     * Create a bounding union of two interval
     * 
     * @param that
     *            an interval
     * @return a new interval made from the bounding union of that and this
     *         interval
     */
    public Interval1D boundingUnion(Interval1D that) {
        return new Interval1D(
                Math.min(this.includedFrom(), that.includedFrom()),
                Math.max(this.includedTo(), that.includedTo()));
    }

    /**
     * Check if the intervals don't have blanks between them
     * 
     * @param that
     *            an interval
     * @return true if there is no blank
     */
    public boolean isUnionableWith(Interval1D that) {
        return this.size() + that.size()
                - this.sizeOfIntersectionWith(that) == this.boundingUnion(that)
                        .size();
    }

    /**
     * Make a union between two intervals
     * 
     * @param that
     *            an interval
     * @return an interval if they are unionable, throw an
     *         {@link IllegalArgumentException} otherwise
     */
    public Interval1D union(Interval1D that) {
        checkArgument(this.isUnionableWith(that));
        return this.boundingUnion(that);
    }

    @Override
    public boolean equals(Object thatO) {
        return thatO instanceof Interval1D
                && this.includedFrom() == ((Interval1D) thatO).includedFrom()
                && this.includedTo() == ((Interval1D) thatO).includedTo();

    }

    @Override
    public int hashCode() {
        return Objects.hash(includedFrom(), includedTo());
    }

    @Override
    public String toString() {
        return String.format("[%1$d..%2$d]", includedFrom(), includedTo());
    }
}