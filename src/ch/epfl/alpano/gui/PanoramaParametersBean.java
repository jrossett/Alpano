package ch.epfl.alpano.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 * Class representing an observable {@link PanoramaUserParameters}
 */
public final class PanoramaParametersBean {

	private ObjectProperty<PanoramaUserParameters> parameters;
	private ObjectProperty<Integer> observerLatitude;
	private ObjectProperty<Integer> observerLongitude;
	private ObjectProperty<Integer> observerElevation;
	private ObjectProperty<Integer> centerAzimuth;
	private ObjectProperty<Integer> horizontalFieldOfView;
	private ObjectProperty<Integer> maxDistance;
	private ObjectProperty<Integer> width;
	private ObjectProperty<Integer> height;
	private ObjectProperty<Integer> superSamplingExponent;

	/**
	 * Constructs an observable {@link PanoramaUserParameter} with a
	 * {@link PanoramaUserParameter}
	 * 
	 * @param parameters
	 *            the {@link PanoramaUserParameter} from which to get the values
	 */
	public PanoramaParametersBean(PanoramaUserParameters parameters) {
		this.parameters = new SimpleObjectProperty<>(parameters);
		this.observerLatitude = new SimpleObjectProperty<>(parameters.observerLatitude());
		this.observerLongitude = new SimpleObjectProperty<>(parameters.observerLongitude());
		this.observerElevation = new SimpleObjectProperty<>(parameters.observerElevation());
		this.centerAzimuth = new SimpleObjectProperty<>(parameters.centerAzimuth());
		this.horizontalFieldOfView = new SimpleObjectProperty<>(parameters.horizontalFieldOfView());
		this.maxDistance = new SimpleObjectProperty<>(parameters.maxDistance());
		this.width = new SimpleObjectProperty<>(parameters.width());
		this.height = new SimpleObjectProperty<>(parameters.height());
		this.superSamplingExponent = new SimpleObjectProperty<>(parameters.superSamplingExponent());
		ChangeListener<Integer> listener = (b, o, n) -> Platform.runLater(this::syncParameters);
		observerLongitudeProperty().addListener(listener);
		observerLatitudeProperty().addListener(listener);
		observerElevationProperty().addListener(listener);
		centerAzimuthProperty().addListener(listener);
		horizontalFieldOfViewProperty().addListener(listener);
		maxDistanceProperty().addListener(listener);
		widthProperty().addListener(listener);
		heightProperty().addListener(listener);
		superSamplingExponentProperty().addListener(listener);
	}

	/**
	 * Synchronize the attributes of the class with the properties when the
	 * values of the properties are changed
	 */
	private void syncParameters() {
		PanoramaUserParameters newParameters = new PanoramaUserParameters(observerLongitudeProperty().get(),
				observerLatitudeProperty().get(), observerElevationProperty().get(), centerAzimuthProperty().get(),
				horizontalFieldOfViewProperty().get(), maxDistanceProperty().get(), widthProperty().get(),
				heightProperty().get(), superSamplingExponentProperty().get());
		parameters.set(newParameters);
		observerLongitudeProperty().set(parametersProperty().get().observerLongitude());
		observerLatitudeProperty().set(parametersProperty().get().observerLatitude());
		observerElevationProperty().set(parametersProperty().get().observerElevation());
		centerAzimuthProperty().set(parametersProperty().get().centerAzimuth());
		horizontalFieldOfViewProperty().set(parametersProperty().get().horizontalFieldOfView());
		maxDistanceProperty().set(parametersProperty().get().maxDistance());
		widthProperty().set(parametersProperty().get().width());
		heightProperty().set(parametersProperty().get().height());
		superSamplingExponentProperty().set(parametersProperty().get().superSamplingExponent());
	}

	/**
	 * Gets the {@link PanoramaUserParameter} property in ReadOnlyProperty
	 * 
	 * @return The {@link PanoramaUserParameter} property in ReadOnlyProperty
	 */
	public ReadOnlyObjectProperty<PanoramaUserParameters> parametersProperty() {
		return parameters;
	}

	/**
	 * Gets the latitude property
	 * 
	 * @return the latitude property
	 */
	public ObjectProperty<Integer> observerLatitudeProperty() {
		return observerLatitude;
	}

	/**
	 * Gets the longitude property
	 * 
	 * @return the longitude property
	 */
	public ObjectProperty<Integer> observerLongitudeProperty() {
		return observerLongitude;
	}

	/**
	 * Gets the ObserverElevation property
	 * 
	 * @return the ObserverElevation property
	 */
	public ObjectProperty<Integer> observerElevationProperty() {
		return observerElevation;
	}

	/**
	 * Gets the centerAzimuth property
	 * 
	 * @return the centerAzimuth property
	 */
	public ObjectProperty<Integer> centerAzimuthProperty() {
		return centerAzimuth;
	}

	/**
	 * Gets the horizontalFieldOfView property
	 * 
	 * @return the horizontalFieldOfVIew property
	 */
	public ObjectProperty<Integer> horizontalFieldOfViewProperty() {
		return horizontalFieldOfView;
	}

	/**
	 * Gets the maxDistance property
	 * 
	 * @return the maxDistance property
	 */
	public ObjectProperty<Integer> maxDistanceProperty() {
		return maxDistance;
	}

	/**
	 * Gets the width property
	 * 
	 * @return the width property
	 */
	public ObjectProperty<Integer> widthProperty() {
		return width;
	}

	/**
	 * Gets the height property
	 * 
	 * @return the height property
	 */
	public ObjectProperty<Integer> heightProperty() {
		return height;
	}

	/**
	 * Gets the superSampling property
	 * 
	 * @return the superSampling property
	 */
	public ObjectProperty<Integer> superSamplingExponentProperty() {
		return superSamplingExponent;
	}
}
