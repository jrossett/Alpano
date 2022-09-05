package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.imageio.ImageIO;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.CompositeDiscreteElevationModel;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class used to define the graphic interface
 */
public final class Alpano extends Application {

    private PanoramaComputerBean panoramaComputer;
    private PanoramaParametersBean panoramaParameters;
    private PredefinedPanoramasListHandler predefPanorama;
    private Labelizer labelizer;
    private static final PanoramaUserParameters DEFAULT_PARAMETERS = PredefinedPanoramas.ALPES_DU_JURA;

    /**
     * Main launch method
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        ContinuousElevationModel MNT = loadHGT(6, 10, 45, 47);
        panoramaComputer = new PanoramaComputerBean(MNT);
        predefPanorama = new PredefinedPanoramasListHandler();
        panoramaParameters = new PanoramaParametersBean(DEFAULT_PARAMETERS);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	primaryStage.setMaximized(true);
    	
        primaryStage.setOnHiding(event -> {
            if (predefPanorama.hasChanged()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Sauvegarder");
                alert.setHeaderText("Voulez-vous sauvegarder la liste de panoramas ?");
                ButtonType yes = new ButtonType("Oui");
                ButtonType no = new ButtonType("Non");
                alert.getButtonTypes().setAll(yes, no);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == yes) {
                    saveConfigAs(primaryStage);
                }
            }
        });

        TextArea liveParams = new TextArea();
        liveParams.setEditable(false);
        liveParams.setPrefRowCount(2);
        liveParams.setPrefColumnCount(1);

        ImageView panoView = new ImageView();
        panoView.setPreserveRatio(true);
        panoView.setSmooth(true);
        panoView.imageProperty().bind(panoramaComputer.imageProperty());
        panoView.fitWidthProperty().bind(panoramaParameters.widthProperty());

        panoView.setOnMouseMoved(event -> {
            Panorama panorama = panoramaComputer.getPanorama();
            PanoramaParameters parameters = panoramaParameters.parametersProperty().get()
                    .panoramaParameters();

            int xPos = (int) Math.scalb(event.getX(),
                    panoramaParameters.superSamplingExponentProperty().get());
            int yPos = (int) Math.scalb(event.getY(),
                    panoramaParameters.superSamplingExponentProperty().get());

            liveParams.setText(createTextDescription(panorama, parameters, xPos, yPos));
        });

        panoView.setOnMouseClicked(event -> {
            int xPos = (int) Math.scalb(event.getX(),
                    panoramaParameters.superSamplingExponentProperty().get());
            int yPos = (int) Math.scalb(event.getY(),
                    panoramaParameters.superSamplingExponentProperty().get());

            Panorama panorama = panoramaComputer.getPanorama();

            URI osmURI;
            try {
                osmURI = generateURI(panorama, xPos, yPos);
                java.awt.Desktop.getDesktop().browse(osmURI);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Error: Could not generate URI");
            } catch (IOException e) {
                throw new IllegalArgumentException("Error: Could not open browser");
            }
        });

        Pane labelsPane = new Pane();
        Bindings.bindContent(labelsPane.getChildren(), panoramaComputer.getLabels());
        labelsPane.prefWidthProperty().bind(panoramaParameters.widthProperty());
        labelsPane.prefHeightProperty().bind(panoramaParameters.heightProperty());
        labelsPane.setMouseTransparent(true);

        StackPane panoGroup = new StackPane(labelsPane, panoView);
        labelsPane.toFront();

        ScrollPane panoScrollPane = new ScrollPane(panoGroup);

        Text updateText = new Text(
                "Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.");
        updateText.setFont(new Font(40));
        updateText.setTextAlignment(TextAlignment.CENTER);

        Pane updatePane = new Pane();
        updatePane.setBackground(new Background(
                new BackgroundFill(new Color(1, 1, 1, 0.9), new CornerRadii(0), new Insets(0))));
        updatePane.prefWidthProperty().bind(panoramaParameters.widthProperty());
        updatePane.prefHeightProperty().bind(panoramaParameters.heightProperty());

        StackPane updateNotice = new StackPane(updateText, updatePane);
        updateText.toFront();
        updateNotice.visibleProperty().bind(panoramaComputer.parametersProperty()
                .isNotEqualTo(panoramaParameters.parametersProperty()));

        updateNotice.setOnMouseClicked(event -> {
            panoramaComputer.setParameters(panoramaParameters.parametersProperty().get());
        });

        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);
        updateNotice.toFront();

        GridPane paramsGrid = new GridPane();

        List<String> parametersLabels = new ArrayList<>();
        parametersLabels.add("Latitude :");
        parametersLabels.add("Longitude :");
        parametersLabels.add("Altitude :");
        parametersLabels.add("Azimuth :");
        parametersLabels.add("Angle de vue :");
        parametersLabels.add("Visibilité (km) :");
        parametersLabels.add("Largeur (px) :");
        parametersLabels.add("Hauteur (px) :");
        parametersLabels.add("Suréchantillonnage :");

        for (int i = 0; i < parametersLabels.size(); i++) {
            Label label = new Label(parametersLabels.get(i));
            paramsGrid.add(label, (i % 3) * 2, i / 3);
            GridPane.setHalignment(label, HPos.RIGHT);
        }

        FixedPointStringConverter fourDecimalConverter = new FixedPointStringConverter(4);
        FixedPointStringConverter zeroDecimalConverter = new FixedPointStringConverter(0);

        List<Node> parametersFields = new ArrayList<>();
        parametersFields.add(createTextField(fourDecimalConverter, 7, Pos.CENTER_RIGHT,
                panoramaParameters.observerLatitudeProperty()));

        parametersFields.add(createTextField(fourDecimalConverter, 7, Pos.CENTER_RIGHT,
                panoramaParameters.observerLongitudeProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 4, Pos.CENTER_RIGHT,
                panoramaParameters.observerElevationProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 3, Pos.CENTER_RIGHT,
                panoramaParameters.centerAzimuthProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 3, Pos.CENTER_RIGHT,
                panoramaParameters.horizontalFieldOfViewProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 3, Pos.CENTER_RIGHT,
                panoramaParameters.maxDistanceProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 4, Pos.CENTER_RIGHT,
                panoramaParameters.widthProperty()));

        parametersFields.add(createTextField(zeroDecimalConverter, 4, Pos.CENTER_RIGHT,
                panoramaParameters.heightProperty()));

        LabeledListStringConverter superSamplingExponentConverter = new LabeledListStringConverter(
                "non", "2x", "4x");
        ChoiceBox<Integer> superSamplingExponentField = new ChoiceBox<>();
        superSamplingExponentField.getItems().addAll(0, 1, 2);
        superSamplingExponentField.valueProperty()
                .bindBidirectional(panoramaParameters.superSamplingExponentProperty());
        superSamplingExponentField.setConverter(superSamplingExponentConverter);
        parametersFields.add(superSamplingExponentField);

        for (int i = 0; i < parametersFields.size(); i++) {
            paramsGrid.add(parametersFields.get(i), (i % 3) * 2 + 1, i / 3);
        }
        paramsGrid.add(liveParams, 6, 0);
        GridPane.setRowSpan(liveParams, 3);
        GridPane.setHalignment(liveParams, HPos.LEFT);
        GridPane.setHgrow(liveParams, Priority.ALWAYS);

        GridPane predefParamGrid = new GridPane();
        GridPane.setHgrow(predefParamGrid, Priority.ALWAYS);

        ListView<String> predefParamList = new ListView<String>();
        GridPane.setHgrow(predefParamList, Priority.ALWAYS);
        predefParamList.prefHeightProperty().bind(liveParams.heightProperty());
        Bindings.bindContent(predefParamList.getItems(), predefPanorama.names().get());
        predefParamGrid.add(predefParamList, 0, 0);
        GridPane.setRowSpan(predefParamList, 2);

        paramsGrid.add(predefParamGrid, 7, 0);
        GridPane.setRowSpan(predefParamGrid, 3);

        Button loadParam = new Button("Charger");
        predefParamGrid.add(loadParam, 1, 0);
        loadParam.setPrefSize(150, 60);
        GridPane.setValignment(loadParam, VPos.CENTER);
        GridPane.setHalignment(loadParam, HPos.CENTER);
        GridPane.setMargin(loadParam, new Insets(2, 4, 2, 4));

        loadParam.setOnAction(event -> {
            String selection = predefParamList.getSelectionModel().getSelectedItem();
            if (selection != null) {
                loadPanoramaUserParameters(predefPanorama.get(selection));
            }
        });

        Button editParam = new Button("Gérer...");
        predefParamGrid.add(editParam, 1, 1);
        editParam.setPrefWidth(150);
        GridPane.setValignment(editParam, VPos.CENTER);
        GridPane.setHalignment(editParam, HPos.CENTER);
        GridPane.setMargin(editParam, new Insets(2, 4, 2, 4));

        editParam.setOnAction(event -> {
            final Stage dialog = new Stage();
            BorderPane box = new BorderPane();
            box.setPrefSize(400, 200);

            GridPane grid = new GridPane();
            box.setCenter(grid);
            grid.setPrefSize(400, 200);

            ListView<String> paramListView = new ListView<String>();
            Bindings.bindContent(paramListView.getItems(), predefPanorama.names().get());
            paramListView.setPrefWidth(450);
            grid.add(paramListView, 0, 0);
            GridPane.setColumnSpan(paramListView, 2);
            GridPane.setHgrow(paramListView, Priority.ALWAYS);

            Button load = new Button("Charger");
            GridPane.setHalignment(load, HPos.CENTER);
            load.setOnAction(e -> {
                String selected = paramListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    loadPanoramaUserParameters(predefPanorama.get(selected));
                    dialog.close();
                }
            });

            Button remove = new Button("Supprimer");
            GridPane.setHalignment(remove, HPos.LEFT);

            remove.setOnAction(e -> {
                String selected = paramListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (predefPanorama.isRemovable(selected)) {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Voulez-vous vraiment supprimer: " + selected + " ?");
                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.get() == ButtonType.OK) {
                            predefPanorama.remove(selected);
                            predefParamList.setItems(null);
                            paramListView.setItems(null);
                            predefParamList.setItems(predefPanorama.names().get());
                            paramListView.setItems(predefPanorama.names().get());
                        }
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Attention");
                        alert.setHeaderText(selected
                                + " est un panorama par défaut et ne peut pas être supprimé");
                        alert.showAndWait();
                    }
                }
            });

            Button add = new Button("Ajouter");

            TextField panoName = new TextField();

            add.setOnAction(e -> {
                String selected = panoName.getText();
                if (selected != null && !selected.isEmpty()
                        && !predefPanorama.names().get().contains(selected)) {
                    predefPanorama.add(selected, panoramaParameters.parametersProperty().get());
                }
                paramListView.setItems(null);
                predefParamList.setItems(null);
                paramListView.setItems(predefPanorama.names().get());
                predefParamList.setItems(predefPanorama.names().get());
                panoName.setText("");
            });

            grid.add(load, 0, 1);
            grid.add(remove, 1, 1);
            grid.add(add, 0, 2);
            grid.add(panoName, 1, 2);
            GridPane.setMargin(load, new Insets(2, 4, 2, 4));
            GridPane.setMargin(remove, new Insets(2, 4, 2, 4));
            GridPane.setMargin(panoName, new Insets(2, 4, 2, 4));
            GridPane.setMargin(add, new Insets(2, 4, 2, 4));

            Scene dialogScene = new Scene(box, 400, 200);
            dialog.setResizable(false);
            dialog.setScene(dialogScene);
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
        });

        for (Node node : paramsGrid.getChildren()) {
            GridPane.setMargin(node, new Insets(2, 4, 2, 4));
        }

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Fichier");
        Menu menuEdit = new Menu("Edition");
        Menu menuDisplay = new Menu("Affichage");

        MenuItem openConfig = new MenuItem("Ouvrir une liste de panoramas");
        openConfig.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openConfig.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir une liste de panorama");
            fileChooser.getExtensionFilters()
                    .add(new ExtensionFilter("Fichier texte (*.txt)", "*.txt"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                boolean overwrite = true;
                if (!predefPanorama.isDefault()) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Ecraser");
                    alert.setHeaderText("Voulez-vous écraser la liste actuelle ?");
                    Optional<ButtonType> result = alert.showAndWait();
                    overwrite = result.get() == ButtonType.OK;
                }
                if (!predefPanorama.loadFromFile(file, overwrite)) {
                    Alert error = new Alert(AlertType.WARNING);
                    error.setTitle("Attention");
                    error.setHeaderText(
                            "Le programme à rencontré un problème au chargement de la liste.\nVeuillez sélectionner un fichier valide");
                    error.showAndWait();
                }
                predefParamList.setItems(null);
                predefParamList.setItems(predefPanorama.names().get());
            }
        });

        MenuItem saveImageAs = new MenuItem("Enregistrer l'image sous...");
        saveImageAs.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveImageAs.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer l'image");
            fileChooser.getExtensionFilters()
                    .add(new ExtensionFilter("PNG Image (*.png)", "*.png"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    WritableImage image = panoGroup.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        MenuItem saveConfigAs = new MenuItem("Enregistrer la liste de panoramas sous...");
        menuFile.setOnShowing(event -> {
            saveConfigAs.setDisable(!predefPanorama.hasChanged());
        });
        saveConfigAs.setOnAction(event -> {
            saveConfigAs(primaryStage);
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem exit = new MenuItem("Quitter");
        exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exit.setOnAction(event -> {
            primaryStage.close();
        });

        MenuItem defaultItem = new MenuItem("Charger les paramètres par défaut");
        defaultItem.setOnAction(event -> {
            loadPanoramaUserParameters(DEFAULT_PARAMETERS);
            predefPanorama.clearList();
            predefParamList.setItems(null);
            predefParamList.setItems(predefPanorama.names().get());
        });

        Menu painterItem = new Menu("Profil de couleur");
        final ToggleGroup groupPainter = new ToggleGroup();
        RadioMenuItem rainbowEffect = addPainterProfile(groupPainter, painterItem, "Arc-en-Ciel",
                "rainbow");
        addPainterProfile(groupPainter, painterItem, "Noir et Blanc", "bw");
        addPainterProfile(groupPainter, painterItem, "Réaliste", "realistic");
        addPainterProfile(groupPainter, painterItem, "Contour", "contour");
        addPainterProfile(groupPainter, painterItem, "Mars", "mars");
        addPainterProfile(groupPainter, painterItem, "Borderlands", "borderlands");
        groupPainter.selectToggle(rainbowEffect);

        groupPainter.selectedToggleProperty().addListener((b, o, n) -> {
            if (groupPainter.getSelectedToggle() != null) {
                panoramaComputer
                        .setPainter((String) groupPainter.getSelectedToggle().getUserData());
            }
        });

        menuFile.getItems().addAll(openConfig, saveImageAs, saveConfigAs, separator, exit);
        menuEdit.getItems().addAll(defaultItem);
        menuDisplay.getItems().addAll(painterItem);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuDisplay);

        BorderPane root = new BorderPane(panoPane, menuBar, null, paramsGrid, null);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Loads in a {@link PanoramaUserParameters}
     * 
     * @param parameters
     *            a PanoramaUserParameters
     */
    private void loadPanoramaUserParameters(PanoramaUserParameters parameters) {
        panoramaParameters.observerLatitudeProperty().set(parameters.observerLatitude());
        panoramaParameters.observerLongitudeProperty().set(parameters.observerLongitude());
        panoramaParameters.observerElevationProperty().set(parameters.observerElevation());
        panoramaParameters.centerAzimuthProperty().set(parameters.centerAzimuth());
        panoramaParameters.horizontalFieldOfViewProperty().set(parameters.horizontalFieldOfView());
        panoramaParameters.maxDistanceProperty().set(parameters.maxDistance());
        panoramaParameters.widthProperty().set(parameters.width());
        panoramaParameters.heightProperty().set(parameters.height());
        panoramaParameters.superSamplingExponentProperty().set(parameters.superSamplingExponent());
    }

    /**
     * Creates a {@link TextField} with a linked {@link TextFormatter}
     * 
     * @param decimalFormat
     *            {@link FixedPointStringConverter}
     * @param prefColumnCount
     *            column count property of the TextField
     * @param pos
     *            alignment position of the text in the TextField
     * @param linkProperty
     *            integer value linked to the TextField
     * @return the built {@link TextField}
     */
    private static TextField createTextField(FixedPointStringConverter decimalFormat,
            int prefColumnCount, Pos pos, ObjectProperty<Integer> linkProperty) {
        TextFormatter<Integer> formatter = new TextFormatter<>(decimalFormat);
        TextField textField = new TextField();
        textField.setAlignment(pos);
        textField.setPrefColumnCount(prefColumnCount);
        formatter.valueProperty().bindBidirectional(linkProperty);
        textField.setTextFormatter(formatter);
        return textField;
    }

    /**
     * Constructs a {@link RadioMenuItem} that can be displayed in the menu
     * 
     * @param group
     *            {@link ToggleGroup} group of values that this object belongs
     *            to
     * @param menu
     *            {@link Menu} parent of the menu item
     * @param label
     *            String displayed on the interface
     * @param userData
     *            String identifier for the parameter
     * @return the built {@link RadioMenuItem}
     */
    private static RadioMenuItem addPainterProfile(ToggleGroup group, Menu menu, String label,
            String userData) {
        RadioMenuItem item = new RadioMenuItem(label);
        item.setUserData(userData);
        item.setToggleGroup(group);
        menu.getItems().add(item);
        return item;
    }

    /**
     * Builds the String description of a pixel of index x, y
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @param parameters
     *            current {@link PanoramaParameters}
     * @param x
     *            horizontal position on the panorama
     * @param y
     *            vertical position on the panorama
     * @return a String description of the hovered pixel
     */
    private static String createTextDescription(Panorama panorama, PanoramaParameters parameters,
            int x, int y) {
        StringBuilder builder = new StringBuilder();
        builder.append("Position : \t\t")
                .append(String.format("%.4f", Math.toDegrees(panorama.latitudeAt(x, y))))
                .append("N \t")
                .append(String.format("%.4f", Math.toDegrees(panorama.longitudeAt(x, y))))
                .append("E\nDistance : \t")
                .append(String.format("%.1f", panorama.distanceAt(x, y) / 1000d))
                .append(" km\nAltitude : \t\t")
                .append(String.format("%.0f", panorama.elevationAt(x, y)))
                .append(" m\nAzimut : \t\t")
                .append(String.format("%.1f", Math.toDegrees(parameters.azimuthForX(x))))
                .append(" ").append("(")
                .append(Azimuth.toOctantString(parameters.azimuthForX(x), "N", "E", "S", "W"))
                .append(")\t").append("\tElévation : \t")
                .append(String.format("%.1f", Math.toDegrees(parameters.altitudeForY(y))))
                .append("");
        return builder.toString();
    }

    /**
     * Shows a save dialog for the list of predefined panoramas
     * 
     * @param primaryStage
     *            base {@link Stage} of the user interface
     */
    private void saveConfigAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la liste de panoramas");
        fileChooser.getExtensionFilters()
                .add(new ExtensionFilter("Fichier texte (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            predefPanorama.writeFile(file);
        }
    }

    /**
     * Generates a {@link URI} on openstreetmap.org based a pixel index x, y
     * 
     * @param panorama
     *            currently displayed {@link Panorama}
     * @param x
     *            horizontal pixel index
     * @param y
     *            vertical pixel index
     * @return the corresponding {@link URI}
     * @throws URISyntaxException
     *             if any error occurs during the construction
     */
    private static URI generateURI(Panorama panorama, int x, int y) throws URISyntaxException {
        String latitude = String.format((Locale) null, "%.2f",
                Math.toDegrees(panorama.latitudeAt(x, y)));

        String longitude = String.format((Locale) null, "%.2f",
                Math.toDegrees(panorama.longitudeAt(x, y)));

        String query = "mlat=" + latitude + "&mlon=" + longitude;
        String fragment = "map=15/" + latitude + "/" + longitude;

        return new URI("http", "www.openstreetmap.org", "/", query, fragment);
    }

    /**
     * Loads a {@link ContinuousElevationModel} based on a latitude and
     * longitude interval
     * 
     * @param longitudeFrom
     *            lower bound of the longitude interval
     * @param longitudeTo
     *            upper bound of the longitude interval
     * @param latitudeFrom
     *            lower bound of the latitude interval
     * @param latitudeTo
     *            upper bound of the latitude interval
     * @return a {@link ContinuousElevationModel} containing all the data
     */
    private static ContinuousElevationModel loadHGT(int longitudeFrom, int longitudeTo,
            int latitudeFrom, int latitudeTo) {
        List<File> hgtFiles = new ArrayList<>();
        for (int i = latitudeFrom; i < latitudeTo; i++) {
            for (int j = longitudeFrom; j < longitudeTo; j++) {
                hgtFiles.add(
                        new File("hgt/N" + String.valueOf(i) + "E00" + String.valueOf(j) + ".hgt"));
            }
        }
        if (hgtFiles.size() == 0) {
            return null;
        }
        if (hgtFiles.size() == 1) {
            return new ContinuousElevationModel(new HgtDiscreteElevationModel(hgtFiles.get(0)));
        }
        CompositeDiscreteElevationModel composite = new CompositeDiscreteElevationModel(
                new HgtDiscreteElevationModel(hgtFiles.get(0)),
                new HgtDiscreteElevationModel(hgtFiles.get(1)));
        for (int i = 2; i < hgtFiles.size(); i++) {
            composite = new CompositeDiscreteElevationModel(
                    new HgtDiscreteElevationModel(hgtFiles.get(i)), composite);
        }
        return new ContinuousElevationModel(composite);
    }
}
