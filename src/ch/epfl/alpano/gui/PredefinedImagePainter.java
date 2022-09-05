package ch.epfl.alpano.gui;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.Panorama;

/**
 * Interface used to define different {@link ImagePainter}
 */
public interface PredefinedImagePainter {

    /**
     * ImagePainter that draws a rainbow effect of the terrain
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter rainbow(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;

        ChannelPainter hue = distance.div(100_000).cycle().mul(360);
        ChannelPainter saturation = distance.div(200_000).clamped().inverted();
        ChannelPainter brightness = slope.mul(2).div((float) Math.PI).inverted().mul(0.7f)
                .add(0.3f);
        ChannelPainter opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return ImagePainter.hsb(hue, saturation, brightness, opacity);
    }

    /**
     * ImagePainter that draws a black and white effect of the terrain
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter bw(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;

        ChannelPainter gray = slope.mul(2).div((float) Math.PI).inverted().mul(0.7f).add(0.3f);
        ChannelPainter opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        return ImagePainter.gray(gray, opacity);
    }

    /**
     * ImagePainter that draws the contour of the terrain
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter contour(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter gray = ChannelPainter.maxDistanceToNeighbors(panorama).sub(500).div(4500)
                .clamped().inverted();

        ChannelPainter opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);
        return ImagePainter.gray(gray, opacity);
    }

    /**
     * ImagePainter that draws an approximately accurate representation of the
     * terrain
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter realistic(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter altitude = panorama::elevationAt;

        ChannelPainter hue = altitude.div(4000).cycle().mul(50).add(90)
                .add(distance.map(d -> d == Float.POSITIVE_INFINITY ? 95 : 0));
        ChannelPainter saturation = slope.map(d -> d > Math.toRadians(45) ? 0.1 : 0.8)
                .mul(altitude.map(a -> a < 2000 ? 1.0 : Math2.lerp(1.0, 0.0, -4 + a / 500)))
                .sub(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.5 : 0.0)).clamped();
        ChannelPainter brightness = slope.mul(2).div((float) Math.PI).inverted().mul(0.5f)
                .mul(altitude.map(a -> a < 2000 ? 1.0 : Math2.lerp(1.0, 2.0, -4 + a / 500)))
                .mul(slope.mul(2).div((float) Math.PI).inverted().mul(0.7f).add(0.3f)).clamped()
                .add(distance.map(d -> d == Float.POSITIVE_INFINITY ? 1.0 : 0.0)).clamped();
        ChannelPainter opacity = distance.div(200_000).clamped().inverted()
                .add(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.8 : 0.0)).clamped();
        return ImagePainter.hsb(hue, saturation, brightness, opacity);
    }

    /**
     * ImagePainter that draws an approximately accurate representation of the
     * terrain as if it was Mars
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter mars(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter altitude = panorama::elevationAt;

        ChannelPainter hue = altitude.div(5000).cycle().mul(20).add(15);
        ChannelPainter saturation = slope.map(d -> d > Math.toRadians(45) ? 0.7 : 1.0)
                .mul(altitude.map(a -> a < 4000 ? 1.0 : Math2.lerp(1.0, 0.0, -4 + a / 1000)))
                .sub(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.5 : 0.0)).clamped();
        ChannelPainter brightness = slope.mul(2).div((float) Math.PI).inverted().mul(0.5f)
                .mul(altitude.map(a -> a < 4000 ? 1.0 : Math2.lerp(1.0, 2.0, -4 + a / 1000)))
                .clamped();
        ChannelPainter opacity = distance.div(250_000).clamped().inverted()
                .add(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.5 : 0.0)).clamped();
        return ImagePainter.hsb(hue, saturation, brightness, opacity);
    }

    /**
     * ImagePainter that draws an approximately accurate representation of the
     * terrain as if it was Pandora
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @return the {@link ImagePainter}
     */
    public static ImagePainter borderlands(Panorama panorama) {
        ChannelPainter distance = panorama::distanceAt;
        ChannelPainter slope = panorama::slopeAt;
        ChannelPainter altitude = panorama::elevationAt;

        ChannelPainter gray = ChannelPainter.maxDistanceToNeighbors(panorama).sub(100).div(1000)
                .clamped();

        ChannelPainter hue = altitude.div(5000).cycle().mul(20).add(25);
        ChannelPainter saturation = slope.map(d -> d > Math.toRadians(45) ? 0.4 : 0.8)
                .mul(altitude.map(a -> a < 4000 ? 1.0 : Math2.lerp(1.0, 0.0, -4 + a / 1000)))
                .sub(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.5 : 0.0)).clamped();
        ChannelPainter brightness = slope.mul(2).div((float) Math.PI).inverted().mul(0.9f)
                .mul(altitude.map(a -> a < 4000 ? 1.0 : Math2.lerp(1.0, 2.0, -4 + a / 1000)))
                .clamped().sub(gray.map(v -> v >= 0 && v <= 1 ? v : 0)).clamped();
        ChannelPainter opacity = distance.div(250_000).clamped().inverted()
                .add(distance.map(d -> d == Float.POSITIVE_INFINITY ? 0.5 : 0.0)).clamped();
        return ImagePainter.hsb(hue, saturation, brightness, opacity);
    }
}
