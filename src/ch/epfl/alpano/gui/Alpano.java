package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Azimuth.toOctantString;
import static ch.epfl.alpano.gui.UserParameter.*;
import static java.lang.Math.toDegrees;
import static java.lang.String.format;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javax.imageio.ImageIO.write;
import static java.lang.Math.abs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public final class Alpano extends Application {

    private final static PanoramaUserParameters PRELOAD = PredefinedPanoramas.JURA;

    private final static float OPACITY_UPDATE_NOTICE = 0.9f;
    private final static double UDPATE_TEXT_FONT_SIZE = 40;
    private final static double BOTTOM_GRID_HGAP = 10;
    private final static double BOTTOM_GRID_VGAP = 3;
    private final static Insets BOTTOM_GRID_PADDING = new Insets(7, 5, 5, 5);
    private final static double WINDOW_PREF_WIDTH = 1500;
    private final static double WINDOW_PREF_HEIGHT = 700;

    private final static ContinuousElevationModel CEM;
    private final static PanoramaParametersBean PARAMETERS_B;
    private final static PanoramaComputerBean COMPUTER_B;

    static {
        List<Summit> summits;
        try {
            summits = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        // DiscreteElevationModel dem = new SuperHgtDiscreteElevationModel();
        // DiscreteElevationModel dem = new HilbertDiscreteElevationModel(0, 0);

        DiscreteElevationModel dem = new HgtDiscreteElevationModel(
                new File("N45E006.hgt")).union(
                        new HgtDiscreteElevationModel(new File("N45E007.hgt")))
                        .union(new HgtDiscreteElevationModel(
                                new File("N45E008.hgt"))
                                        .union(new HgtDiscreteElevationModel(
                                                new File("N45E009.hgt"))))
                        .union(new HgtDiscreteElevationModel(
                                new File("N46E006.hgt"))
                                        .union(new HgtDiscreteElevationModel(
                                                new File("N46E007.hgt")))
                                        .union(new HgtDiscreteElevationModel(
                                                new File("N46E008.hgt"))
                                                        .union(new HgtDiscreteElevationModel(
                                                                new File(
                                                                        "N46E009.hgt")))));

        CEM = new ContinuousElevationModel(dem);

        PARAMETERS_B = new PanoramaParametersBean(PRELOAD);
        COMPUTER_B = new PanoramaComputerBean(CEM, summits);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        TextArea areaInfo = setAreaInfo();
        ImageView panoView = setPanoView(areaInfo);
        Pane labelsPane = setLabels();
        StackPane panoGroup = new StackPane(panoView, labelsPane);
        ScrollPane panoScrollPane = new ScrollPane(panoGroup);
        Text updateText = setUpdateText();
        ProgressBar computeBar = new ProgressBar();
        GridPane progressGrid = new GridPane();
        StackPane updateNotice = setUpdateNotice(updateText);
        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);
        GridPane paramsGrid = setDynamicParameters(areaInfo);
        BorderPane root = setRoot(panoPane, paramsGrid, progressGrid);
        Scene scene = new Scene(root);

        setComputeBar(computeBar, progressGrid);

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ImageView setPanoView(TextArea areaInfo) {
        ImageView panoView = new ImageView();
        panoView.imageProperty().bind(COMPUTER_B.imageProperty());
        panoView.fitWidthProperty().bind(PARAMETERS_B.widthProperty());
        panoView.setPreserveRatio(true);
        panoView.setSmooth(true);
        setMouseMove(panoView, areaInfo);
        setMouseClick(panoView);
        return panoView;
    }

    private TextArea setAreaInfo() {
        TextArea areaInfo = new TextArea();
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(2);
        return areaInfo;
    }

    private void setMouseMove(ImageView panoView, TextArea areaInfo) {
        panoView.setOnMouseMoved(e -> {
            int x = getSampledIndex(e.getX());
            int y = getSampledIndex(e.getY());

            double lat = toDegrees(COMPUTER_B.getPanorama().latitudeAt(x, y));
            double lon = toDegrees(COMPUTER_B.getPanorama().longitudeAt(x, y));

            double azimuth = COMPUTER_B.getParameters().panoramaParameters()
                    .azimuthForX(x);
            char northOrSouth = lat >= 0 ? 'N' : 'S';
            char eastOrWest = lon >= 0 ? 'E' : 'W';

            StringBuilder sb = new StringBuilder();
            sb.append(format("Position : %.4f°%c %.4f°%c%n", abs(lat),
                    northOrSouth, abs(lon), eastOrWest));
            sb.append(format("Distance : %.1f km%n",
                    COMPUTER_B.getPanorama().distanceAt(x, y) / 1000));
            sb.append(format("Altitude : %d m%n",
                    (int) COMPUTER_B.getPanorama().elevationAt(x, y)));
            sb.append(format("Azimut : %.1f° (%s) Elévation : %.1f°",
                    toDegrees(azimuth),
                    toOctantString(azimuth, "N", "E", "S", "W"),
                    toDegrees(COMPUTER_B.getParameters().panoramaParameters()
                            .altitudeForY(y))));
            areaInfo.setText(sb.toString());
        });
    }

    private void setMouseClick(ImageView panoView) {
        panoView.setOnMouseClicked(e -> {
            int x = getSampledIndex(e.getX());
            int y = getSampledIndex(e.getY());

            if (e.getButton() == MouseButton.PRIMARY) {
                double lat = toDegrees(
                        COMPUTER_B.getPanorama().latitudeAt(x, y));
                double lon = toDegrees(
                        COMPUTER_B.getPanorama().longitudeAt(x, y));
                String qy = format((Locale) null, "mlat=%.4f&mlon=%.4f", lat,
                        lon);
                String fg = format((Locale) null, "map=15/%.4f/%.4f", lat, lon);
                URI osmURI;
                try {
                    osmURI = new URI("http", "www.openstreetmap.org", "/", qy,
                            fg);
                    java.awt.Desktop.getDesktop().browse(osmURI);
                } catch (URISyntaxException e1) {
                    System.err.println("Could not parse URI.");
                } catch (IOException e1) {
                    System.err.println("Could not get browser.");
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                Panorama p = COMPUTER_B.getPanorama();
                PARAMETERS_B.observerLongitudeProperty()
                        .set((int) (10_000 * toDegrees(p.longitudeAt(x, y))));
                PARAMETERS_B.observerLatitudeProperty()
                        .set((int) (10_000 * toDegrees(p.latitudeAt(x, y))));
                PARAMETERS_B.observerElevationProperty()
                        .set((int) p.elevationAt(x, y) + 20);
                PARAMETERS_B.centerAzimuthProperty()
                        .set((int) toDegrees(Azimuth.canonicalize(
                                COMPUTER_B.getParameters().panoramaParameters()
                                        .azimuthForX(x) + Math.PI)));
            }
        });
    }

    private int getSampledIndex(double index) {
        return (int) Math.scalb(index,
                COMPUTER_B.getParameters().superSamplingExponent());
    }

    private Pane setLabels() {
        Pane labelsPane = new Pane();
        Bindings.bindContent(labelsPane.getChildren(), COMPUTER_B.getLabels());
        labelsPane.prefWidthProperty().bind(PARAMETERS_B.widthProperty());
        labelsPane.prefHeightProperty().bind(PARAMETERS_B.heightProperty());
        labelsPane.setMouseTransparent(true);
        return labelsPane;
    }

    private Text setUpdateText() {
        Text updateText = new Text();
        String notice = "Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.";
        updateText.setText(notice);
        updateText.setFont(new Font(UDPATE_TEXT_FONT_SIZE));
        updateText.setTextAlignment(TextAlignment.CENTER);
        return updateText;
    }

    private void setComputeBar(ProgressBar computeBar, GridPane progressGrid) {
        computeBar.progressProperty().bind(COMPUTER_B.statusProperty());
        Text textProgress = new Text("0 %");
        computeBar.progressProperty().addListener((p, o, n) -> textProgress
                .setText(format("%.0f %%", n.doubleValue() * 100)));
        GridPane.setHalignment(textProgress, HPos.CENTER);
        progressGrid.add(computeBar, 0, 0);
        progressGrid.add(textProgress, 0, 0);
        progressGrid.setPadding(new Insets(5));
    }

    private StackPane setUpdateNotice(Text updateText) {
        StackPane updateNotice = new StackPane(updateText);
        updateNotice.setBackground(
                new Background(new BackgroundFill(Color.WHITE, null, null)));
        updateNotice.setOpacity(OPACITY_UPDATE_NOTICE);
        updateNotice.visibleProperty().bind(PARAMETERS_B.parametersProperty()
                .isNotEqualTo(COMPUTER_B.parametersProperty()));
        updateNotice.setOnMouseClicked(e -> COMPUTER_B
                .setParameters(PARAMETERS_B.parametersProperty().get()));
        return updateNotice;
    }

    private GridPane setDynamicParameters(TextArea areaInfo) {

        GridPane paramsGrid = new GridPane();

        List<Control> labelsAndField = new ArrayList<>();
        labelsAndField.addAll(
                setLabelAndField("Latitude (°)", OBSERVER_LATITUDE, 7, 4));
        labelsAndField.addAll(
                setLabelAndField("Longitude (°)", OBSERVER_LONGITUDE, 7, 4));
        labelsAndField.addAll(
                setLabelAndField("Altitude (m)", OBSERVER_ELEVATION, 4, 0));
        labelsAndField
                .addAll(setLabelAndField("Azimut (°)", CENTER_AZIMUTH, 3, 0));
        labelsAndField.addAll(setLabelAndField("Angle de vue (°)",
                HORIZONTAL_FIELD_OF_VIEW, 3, 0));
        labelsAndField.addAll(
                setLabelAndField("Visibilité (km)", MAX_DISTANCE, 3, 0));
        labelsAndField.addAll(setLabelAndField("Largeur (px)", WIDTH, 4, 0));
        labelsAndField.addAll(setLabelAndField("Hauteur (px)", HEIGHT, 4, 0));
        labelsAndField.addAll(
                setLabelAndField("Suréchantillonage", SUPER_SAMPLING_EXPONENT));

        for (int i = 0; i < 18; ++i)
            paramsGrid.add(labelsAndField.get(i), i % 6, i / 6);

        Button computeElevation = new Button("Auto-altitude");
        computeElevation.setOnAction(e -> PARAMETERS_B
                .observerElevationProperty()
                .set((int) CEM.elevationAt(PARAMETERS_B.parametersProperty()
                        .get().panoramaDisplayParameters().observerPosition())
                        + 2));
        GridPane.setHalignment(computeElevation, HPos.CENTER);

        Button saveImageAndPanorama = new Button("Save");
        saveImageAndPanorama.setOnAction(e -> openSaveWindow(e));
        GridPane.setFillWidth(saveImageAndPanorama, true);
        GridPane.setHalignment(saveImageAndPanorama, HPos.CENTER);

        Button loadPanorama = new Button("Load");
        loadPanorama.setOnAction(e -> openLoadWindow(e));
        GridPane.setHalignment(loadPanorama, HPos.CENTER);

        paramsGrid.setAlignment(Pos.CENTER);
        paramsGrid.setHgap(BOTTOM_GRID_HGAP);
        paramsGrid.setVgap(BOTTOM_GRID_VGAP);
        paramsGrid.setPadding(BOTTOM_GRID_PADDING);

        paramsGrid.add(computeElevation, 6, 0);
        paramsGrid.add(saveImageAndPanorama, 6, 1);
        paramsGrid.add(loadPanorama, 6, 2);

        paramsGrid.add(areaInfo, 7, 0, 1, 3);

        return paramsGrid;
    }

    private Collection<? extends Control> setLabelAndField(String string,
            UserParameter superSamplingExponent) {
        Label label = new Label("Suréchantillonage :");
        ChoiceBox<Integer> field = new ChoiceBox<>();
        field.getItems().addAll(0, 1, 2);
        StringConverter<Integer> supFor = new LabeledListStringConverter("non",
                "2×", "4×");
        field.valueProperty().bindBidirectional(
                PARAMETERS_B.superSamplingExponentProperty());
        field.setConverter(supFor);
        label.setAlignment(Pos.CENTER_RIGHT);
        return Arrays.asList(label, field);
    }

    private Collection<? extends Control> setLabelAndField(String name,
            UserParameter uP, int prefColumnCount, int decimals) {
        Label label = new Label(name + " :");
        GridPane.setHalignment(label, HPos.RIGHT);
        TextField field = new TextField();
        TextFormatter<Integer> formatter = new TextFormatter<>(
                new FixedPointStringConverter(decimals));
        formatter.valueProperty()
                .bindBidirectional(PARAMETERS_B.getProperty(uP));
        field.setTextFormatter(formatter);
        field.setAlignment(Pos.CENTER_RIGHT);
        field.setPrefColumnCount(prefColumnCount);
        return Arrays.asList(label, field);
    }

    private void openSaveWindow(ActionEvent e) {
        if (COMPUTER_B.getImage() == null)
            return;

        Stage saveStage = new Stage();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        Label queryL = new Label("Veuillez entrer le nom de la sauvegarde :");
        TextField queryF = new TextField();
        queryF.setPromptText("Exemple : mon_super_panorama");

        Button saveButton = new Button("Sauvegarder");
        saveButton.setOnAction(f -> {
            File imgFolder = new File("img/");
            File saveFolder = new File("save/");
            if (!imgFolder.exists())
                imgFolder.mkdirs();
            if (!saveFolder.exists())
                saveFolder.mkdirs();
            if (queryF.getText() != null && !queryF.getText().isEmpty()) {
                try {
                    write(fromFXImage(COMPUTER_B.getImage(), null), "png",
                            new File("img/" + queryF.getText() + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(
                                "save/" + queryF.getText() + ".ser"))) {
                    out.writeObject(COMPUTER_B.getParameters());
                } catch (IOException i) {
                    i.printStackTrace();
                }
                saveStage.close();
            }
        });
        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> saveStage.close());

        GridPane.setHalignment(quitButton, HPos.RIGHT);

        grid.setVgap(20);
        grid.setHgap(20);
        grid.setPrefWidth(350);
        grid.setPrefHeight(150);

        grid.add(queryL, 0, 0, 2, 1);
        grid.add(queryF, 0, 1, 2, 1);
        grid.add(saveButton, 0, 2);
        grid.add(quitButton, 1, 2);

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        saveStage.setTitle("Alpano - Save File");
        saveStage.setScene(scene);
        saveStage.setResizable(false);
        saveStage.show();
    }

    private void openLoadWindow(ActionEvent e) {

        // FIXME don't open if no save
        // FIXME create folders if don't exist

        Stage loadStage = new Stage();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        File saveFolder = new File("save/");
        File[] files = saveFolder.listFiles();

        ChoiceBox<File> choices = new ChoiceBox<>();
        GridPane.setHalignment(choices, HPos.CENTER);
        choices.getItems().addAll(files);
        StringConverter<File> loadConv = new FileStringConverter(
                saveFolder.getPath(), 3);
        choices.setConverter(loadConv);

        Button loadButton = new Button("Load");

        loadButton.setOnAction(f -> {
            File load = choices.getValue();
            PanoramaUserParameters p = null;
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(load))) {
                p = (PanoramaUserParameters) in.readObject();
            } catch (Exception i) {
                i.printStackTrace();
                return;
            }
            PARAMETERS_B.observerLongitudeProperty().set(p.observerLongitude());
            PARAMETERS_B.observerLatitudeProperty().set(p.observerLatitude());
            PARAMETERS_B.observerElevationProperty().set(p.observerElevation());
            PARAMETERS_B.centerAzimuthProperty().set(p.centerAzimuth());
            PARAMETERS_B.maxDistanceProperty().set(p.maxDistance());
            PARAMETERS_B.horizontalFieldOfViewProperty()
                    .set(p.horizontalFieldOfView());
            PARAMETERS_B.widthProperty().set(p.width());
            PARAMETERS_B.heightProperty().set(p.height());
            PARAMETERS_B.superSamplingExponentProperty()
                    .set(p.superSamplingExponent());
            loadStage.close();
        });

        // for(File f: files)

        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> loadStage.close());

        grid.setPadding(new Insets(20));
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setPrefWidth(200);
        grid.setPrefHeight(75);

        grid.add(choices, 0, 0, 2, 1);
        grid.add(loadButton, 0, 1);
        grid.add(quitButton, 1, 1);

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        loadStage.setTitle("Alpano - Load File");
        loadStage.setScene(scene);
        loadStage.setResizable(false);
        loadStage.show();

    }

    private BorderPane setRoot(StackPane panoPane, GridPane paramsGrid,
            GridPane progressGrid) {
        BorderPane root = new BorderPane();
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);
        root.setTop(progressGrid);
        root.setPrefWidth(WINDOW_PREF_WIDTH);
        root.setPrefHeight(WINDOW_PREF_HEIGHT);
        return root;
    }

}