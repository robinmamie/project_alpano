package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Azimuth.toOctantString;
import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.SUPER_SAMPLING_EXPONENT;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
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

/**
 * Classe principale du projet. Lance le programme graphique.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
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

    // Constructeur static de la classe.
    static {
        List<Summit> summits;
        try {
            summits = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }

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

    /**
     * Méthode main du programme.
     * 
     * @param args
     *            Arguments externes.
     */
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
        StackPane updateNotice = setUpdateNotice(updateText);
        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);
        GridPane paramsGrid = setDynamicParameters(areaInfo);
        BorderPane root = setRoot(panoPane, paramsGrid);
        Scene scene = new Scene(root);

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
            double lat = toDegrees(COMPUTER_B.getPanorama().latitudeAt(x, y));
            double lon = toDegrees(COMPUTER_B.getPanorama().longitudeAt(x, y));
            String qy = format((Locale) null, "mlat=%.4f&mlon=%.4f", lat, lon);
            String fg = format((Locale) null, "map=15/%.4f/%.4f", lat, lon);
            URI osmURI;
            try {
                osmURI = new URI("http", "www.openstreetmap.org", "/", qy, fg);
                java.awt.Desktop.getDesktop().browse(osmURI);
            } catch (URISyntaxException e1) {
                System.err.println("Could not parse URI.");
            } catch (IOException e1) {
                System.err.println("Could not get browser.");
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

        paramsGrid.add(areaInfo, 6, 0, 1, 3);

        paramsGrid.setAlignment(Pos.CENTER);
        paramsGrid.setHgap(BOTTOM_GRID_HGAP);
        paramsGrid.setVgap(BOTTOM_GRID_VGAP);
        paramsGrid.setPadding(BOTTOM_GRID_PADDING);

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

    private BorderPane setRoot(StackPane panoPane, GridPane paramsGrid) {
        BorderPane root = new BorderPane();
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);
        root.setPrefWidth(WINDOW_PREF_WIDTH);
        root.setPrefHeight(WINDOW_PREF_HEIGHT);
        return root;
    }

}