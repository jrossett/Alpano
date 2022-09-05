package ch.epfl.alpano.gui;

import javafx.scene.paint.Color;

@FunctionalInterface
/**
 * Functional interface that is used to calculate the color of every pixels on
 * an image
 */
public interface ImagePainter {

    /**
     * Gets the color value of an ImagePainter at the index x and y
     * 
     * @param x
     *            horizontal index
     * @param y
     *            vertical index
     * 
     * @return color value at the index x, y
     */
    public abstract Color colorAt(int x, int y);

    /**
     * Creates an ImagePainter based on the hsb color format
     * 
     * @param hue
     *            {@link ChannelPainter} that defines the hue of every pixel
     * @param saturation
     *            {@link ChannelPainter} that defines the saturation of every
     *            pixel
     * @param brightness
     *            {@link ChannelPainter} that defines the brightness of every
     *            pixel
     * @param opacity
     *            {@link ChannelPainter} that defines the opacity of every pixel
     * @return an ImagePainter based on the hsb color format
     */
    public static ImagePainter hsb(ChannelPainter hue,
            ChannelPainter saturation, ChannelPainter brightness,
            ChannelPainter opacity) {
        return (x, y) -> Color.hsb(hue.valueAt(x, y), saturation.valueAt(x, y),
                brightness.valueAt(x, y), opacity.valueAt(x, y));
    }

    /**
     * Creates a grayscale ImagePainter
     * 
     * @param gray
     *            {@link ChannelPainter} that defines the gray rgb color of
     *            every pixel
     * @param opacity
     *            {@link ChannelPainter} that defines the opacity of every pixel
     * @return an grayscale ImagePainter
     */
    public static ImagePainter gray(ChannelPainter gray,
            ChannelPainter opacity) {
        return (x, y) -> Color.color(gray.valueAt(x, y), gray.valueAt(x, y),
                gray.valueAt(x, y), opacity.valueAt(x, y));
    }
}
