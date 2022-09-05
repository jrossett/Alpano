package ch.epfl.alpano;

/**
 * Interface containing pre-condition checking methods
 */
public interface Preconditions {

    /**
     * Throws an {@link IllegalArgumentException} if the boolean input is false
     * 
     * @param b
     *            boolean input
     * @param message
     *            {@link String} to display with the exception
     */
    public static void checkArgument(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the boolean input is false
     * 
     * @param b
     *            boolean input
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

}
