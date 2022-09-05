package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Represents a summit that has a name, a position and an altitude
 */
public final class Summit {
    private final String name;
    private final GeoPoint position;
    private final int elevation;

    /**
     * Constructs a summit with a name, a position and an elevation
     * 
     * @param name
     *            name of the summit
     * @param position
     *            position of the summit
     * @param elevation
     *            elevation of the summit in meters
     */
    public Summit(String name, GeoPoint position, int elevation) {
        this.name = requireNonNull(name);
        this.position = requireNonNull(position);
        this.elevation = elevation;
    }

    /**
     * Gets the name of the summit
     * 
     * @return the string representation of the name
     */
    public String name() {
        return name;
    }

    /**
     * Gets the position of the summit
     * 
     * @return the GeoPoint representation of the position
     */
    public GeoPoint position() {
        return position;
    }

    /**
     * Gets the elevation of the summit
     * 
     * @return the elevation of the summit in meters
     */
    public int elevation() {
        return elevation;
    }

    @Override
    public String toString() {
        return String.format("%1$s %2$s %3$d", name, position, elevation);
    }

}
