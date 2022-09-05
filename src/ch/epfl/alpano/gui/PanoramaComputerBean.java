package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Class use to compute an image represented by a panorama
 */
public final class PanoramaComputerBean {

	private ObjectProperty<Panorama> panorama = new SimpleObjectProperty<Panorama>();
	private ObjectProperty<PanoramaUserParameters> parameters = new SimpleObjectProperty<PanoramaUserParameters>();
	private ObjectProperty<Image> image = new SimpleObjectProperty<Image>();
	private ObjectProperty<ObservableList<Node>> nodes = new SimpleObjectProperty<>(
			FXCollections.observableArrayList());
	private ObjectProperty<String> painter = new SimpleObjectProperty<String>();
	private ContinuousElevationModel cDem;
	private Labelizer labelizer;

	/**
	 * Constructs a PanoramaComputerBean with a {@link DiscreteElevationModel}
	 * that will compute an image representing the panorama with labels
	 * 
	 * @param cDem
	 *            The {@link DiscreteElevationModel} where the panorama is
	 * @throws IOException
	 *             if the file used for the labels is not in the right format
	 */
	public PanoramaComputerBean(ContinuousElevationModel cDem) throws IOException {
		this.cDem = cDem;
		labelizer = new Labelizer(cDem, GazetteerParser.readSummitsFrom(new File("data/alps.txt")));

		parameters.addListener((b, o, n) -> Platform.runLater(this::syncData));
		painter.addListener((b, o, n) -> Platform.runLater(this::renderImage));
	}

	/**
	 * Synchronize the data of the {@link PanoramaComputerBean} with which who
	 * were changed when they are changed
	 */
	private void syncData() {
		panorama.set(new PanoramaComputer(cDem).computePanorama(getParameters().panoramaParameters()));
		renderImage();
		getLabels().setAll(labelizer.labelize(getParameters().panoramaDisplayParameters()));
	}

	/**
	 * Switch the painter to get different filters and by default is the rainbow
	 * filter
	 */
	private void renderImage() {
		String selectedPainter = painter.get();
		ImagePainter currentPainter;
		if (selectedPainter != null) {
			switch (selectedPainter) {
			case "rainbow":
				currentPainter = PredefinedImagePainter.rainbow(getPanorama());
				break;
			case "contour":
				currentPainter = PredefinedImagePainter.contour(getPanorama());
				break;
			case "realistic":
				currentPainter = PredefinedImagePainter.realistic(getPanorama());
				break;
			case "mars":
				currentPainter = PredefinedImagePainter.mars(getPanorama());
				break;
			case "borderlands":
				currentPainter = PredefinedImagePainter.borderlands(getPanorama());
				break;
			case "bw":
				currentPainter = PredefinedImagePainter.bw(getPanorama());
				break;
			default:
				currentPainter = PredefinedImagePainter.rainbow(getPanorama());
				break;
			}
		} else {
			currentPainter = PredefinedImagePainter.rainbow(getPanorama());
		}
		image.set(PanoramaRenderer.renderPanorama(getPanorama(), currentPainter));
	}

	/**
	 * Gets the {@link PanoramaUserParameters} property of the computer
	 * 
	 * @return the corresponding {@link PanoramaUserParameters} in
	 *         ObjectProperty
	 */
	public ObjectProperty<PanoramaUserParameters> parametersProperty() {
		return parameters;
	}

	/**
	 * Gets the {@link PanoramaUserParameters} of the computer
	 * 
	 * @return the corresponding {@link PanoramaUserParameters}
	 */
	public PanoramaUserParameters getParameters() {
		return parameters.get();
	}

	/**
	 * Set the {@link PanoramaUserParameters} of the computer
	 * 
	 * @param newParameters
	 *            the new {@link PanoramaUserParameters} of the computer
	 */
	public void setParameters(PanoramaUserParameters newParameters) {
		parameters.set(newParameters);
	}

	/**
	 * Gets the {@link Panorama} computed in ReadOnlyObjectProperty
	 * 
	 * @return the corresponding {@link Panorama} in ReadOnlyObjectProperty
	 */
	public ReadOnlyObjectProperty<Panorama> panoramaProperty() {
		return panorama;
	}

	/**
	 * Gets the {@link Panorama} computed
	 * 
	 * @return the corresponding {@link Panorama}
	 */
	public Panorama getPanorama() {
		return panorama.get();
	}

	/**
	 * Gets the {@link Image} of the panorama computed in ReadOnlyProperty
	 * 
	 * @return the corresponding {@link Image} in ReadOnlyProperty
	 */
	public ReadOnlyObjectProperty<Image> imageProperty() {
		return image;
	}

	/**
	 * Gets the {@link Image} of the panorama computed
	 * 
	 * @return the corresponding {@link Image}
	 */
	public Image getImage() {
		return image.get();
	}

	/**
	 * Gets the labels of the panorama computed in ReadOnlyProperty
	 * 
	 * @return the labels in ReadOnlyProperty
	 */
	public ReadOnlyObjectProperty<ObservableList<Node>> labelsProperty() {
		return nodes;
	}

	/**
	 * Gets the labels of the panorama computed
	 * 
	 * @return the corresponding labels
	 */
	public ObservableList<Node> getLabels() {
		return nodes.get();
	}

	/**
	 * Sets the {@link ChannelPainter} to draw the image
	 * 
	 * @param imagePainter
	 *            the name of the {@link ChannelPainter} to use
	 */
	public void setPainter(String imagePainter) {
		painter.set(imagePainter);
	}
}
