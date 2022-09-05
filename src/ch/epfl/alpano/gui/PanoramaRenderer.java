package ch.epfl.alpano.gui;


import ch.epfl.alpano.Panorama;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Interface that is used to create {@link Image}
 */
public interface PanoramaRenderer {

    /**
     * Creates an {@link Image} from a {@link Panorama}
     * @param panorama {@link Panorama} to draw
     * @param painter {@link ImagePainter} that defines the color of every pixel 
     * @return an {@link Image}
     */
    public static Image renderPanorama(Panorama panorama,
            ImagePainter painter) {
        WritableImage image = new WritableImage(panorama.parameters().width(), panorama.parameters().height());
        PixelWriter writer = image.getPixelWriter();
        
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                writer.setColor(x, y, painter.colorAt(x, y));
            }
        }
        return image;
    }
}
