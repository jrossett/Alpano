package ch.epfl.alpano.gui;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Enumeration used to store parameters and control their validity
 */
public enum UserParameter {
    OBSERVER_LONGITUDE("degrees", 60000, 120000),
    OBSERVER_LATITUDE("degrees", 450000, 480000),
    OBSERVER_ELEVATION("meters", 300, 10000),
    CENTER_AZIMUTH("degrees", 0, 359),
    HORIZONTAL_FIELD_OF_VIEW("degrees", 1, 360),
    MAX_DISTANCE("kilometers", 10, 600),
    WIDTH("samples", 30, 16000),
    HEIGHT("samples", 10, 4000),
    SUPER_SAMPLING_EXPONENT("-", 0, 2);

    private String unit;
    private int max, min;

    /**
     * Constructs a UserParameter
     * 
     * @param unit
     *            unit of the integer parameter
     * @param min
     *            minimum possible value of the parameter
     * @param max
     *            maximum possible value of the parameter
     */
    private UserParameter(String unit, int min, int max) {
        this.unit = unit;
        this.max = max;
        this.min = min;
    }

    /**
     * Clamps the value of the parameter between its predefindes bounds 
     * @param value current value
     * @return sanitized value
     */
    public int sanitize(int value) {
        return max(min(value, this.max), this.min);
    }
}
