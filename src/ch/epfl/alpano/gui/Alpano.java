package ch.epfl.alpano.gui;

import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static java.lang.Math.toDegrees;
import static java.lang.String.format;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private final static PanoramaUserParameters PRELOAD = PredefinedPanoramas.PELICAN;

    private ContinuousElevationModel cem;
    private PanoramaParametersBean parametersB;
    private PanoramaComputerBean computerB;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // *** PANORAMA INITIALIZATION ***

        initPanorama();

        // *** STRUCTRURE ***

        ImageView panoView = new ImageView();
        TextArea areaInfo = new TextArea();
        Pane labelsPane = new Pane();
        StackPane panoGroup = new StackPane(panoView, labelsPane);
        ScrollPane panoScrollPane = new ScrollPane(panoGroup);
        Text updateText = new Text();
        StackPane updateNotice = new StackPane(updateText);
        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);
        GridPane paramsGrid = new GridPane();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);

        // *** PANE 1 ***

        setPanoView(panoView);
        setMouseMove(panoView, areaInfo);
        setMouseClick(panoView);
        setLabels(labelsPane);
        setUpdateText(updateText);
        setUpdateNotice(updateNotice);

        // *** PANE 2 & 3 ***

        setDynamicParameters(paramsGrid, areaInfo);
        setAreaInfo(areaInfo);

        // *** ROOT ***

        setRoot(root, panoPane, paramsGrid);

        // *** PROGRAMM ***

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initPanorama() throws Exception {
        File alps = new File("alps.txt");
        List<Summit> summits = GazetteerParser.readSummitsFrom(alps);
        
        /*
         * List<DiscreteElevationModel> dems = new ArrayList<>(); for (int i =
         * 45; i <= 47; ++i) for (int j = 6; j <= 10; ++j) dems.add(new
         * HgtDiscreteElevationModel( new File("N" + i + "E" + format("%03d", j)
         * + ".hgt")));
         * 
         * DiscreteElevationModel dem1 = dems.get(0).union(dems.get(1));
         * DiscreteElevationModel dem2 = dems.get(2).union(dems.get(3));
         * DiscreteElevationModel dem3 = dems.get(5).union(dems.get(6));
         * DiscreteElevationModel dem4 = dems.get(7).union(dems.get(8));
         * DiscreteElevationModel dem5 = dems.get(10).union(dems.get(11));
         * DiscreteElevationModel dem6 = dems.get(12).union(dems.get(13));
         * DiscreteElevationModel dem7 = dems.get(4)
         * .union(dems.get(9).union(dems.get(14)));
         * 
         * DiscreteElevationModel dem = dem1.union(dem2).union(dem3.union(dem4))
         * .union(dem5.union(dem6)).union(dem7);
         */

        DiscreteElevationModel dem = new SuperHgtDiscreteElevationModel();

        cem = new ContinuousElevationModel(dem);

        parametersB = new PanoramaParametersBean(PRELOAD);
        computerB = new PanoramaComputerBean(cem, summits);
    }

    private void setPanoView(ImageView panoView) {
        panoView.imageProperty().bind(computerB.imageProperty());
        panoView.fitWidthProperty().bind(parametersB.widthProperty());
        panoView.setPreserveRatio(true);
        panoView.setSmooth(true);
    }

    private void setMouseMove(ImageView panoView, TextArea areaInfo) {
        panoView.setOnMouseMoved(e -> {
            int[] index = getIndices(e);
            int x = index[0], y = index[1];

            String[] lonAndLat = getFormattedLongitudeAndLatitude(x, y);
            float distance = computerB.getPanorama().distanceAt(x, y);
            int altitude = (int) computerB.getPanorama().elevationAt(x, y);
            double azimuth = computerB.getParameters()
                    .panoramaDisplayParameters().azimuthForX(x);
            double elevation = computerB.getParameters()
                    .panoramaDisplayParameters().altitudeForY(y);

            StringBuilder sb = new StringBuilder();
            sb.append("Position : ");
            sb.append(lonAndLat[1]);
            sb.append("°N ");
            sb.append(lonAndLat[0]);
            sb.append("°E\n");
            sb.append(format("Distance : %.1f km%n", distance / 1000));
            sb.append(format("Altitude : %d m%n", altitude));
            sb.append(format("Azimut : %.1f° (", toDegrees(azimuth)));
            sb.append(Azimuth.toOctantString(azimuth, "N", "E", "S", "W"));
            sb.append(format(")  Élévation : %.1f°%n", toDegrees(elevation)));
            areaInfo.setText(sb.toString());
        });
    }

    private void setMouseClick(ImageView panoView) {
        panoView.setOnMouseClicked(e -> {
            int[] index = getIndices(e);
            int x = index[0], y = index[1];

            String[] lonAndLat = getFormattedLongitudeAndLatitude(x, y);

            String qy = "?mlat=" + lonAndLat[1] + "&mlon=" + lonAndLat[0];
            String fg = "map=15/" + lonAndLat[1] + "/" + lonAndLat[0];
            URI osmURI;
            try {
                osmURI = new URI("http", "www.openstreetmap.org", "/", qy, fg);
                java.awt.Desktop.getDesktop().browse(osmURI);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    private int[] getIndices(MouseEvent e) {
        int x = (int) e.getX(), y = (int) e.getY();

        int superSE = 2 * computerB.getParameters().superSamplingExponent();
        if (superSE > 0) {
            x *= superSE;
            y *= superSE;
        }

        return new int[] { x, y };
    }

    private String[] getFormattedLongitudeAndLatitude(int x, int y) {
        float longitude = computerB.getPanorama().longitudeAt(x, y);
        float latitude = computerB.getPanorama().latitudeAt(x, y);
        String lambda = format((Locale) null, "%.4f", toDegrees(longitude));
        String phi = format((Locale) null, "%.4f", toDegrees(latitude));
        return new String[] { lambda, phi };
    }

    private void setLabels(Pane labelsPane) {
        Bindings.bindContent(labelsPane.getChildren(), computerB.getLabels());
        labelsPane.prefWidthProperty().bind(parametersB.widthProperty());
        labelsPane.prefHeightProperty().bind(parametersB.heightProperty());
        labelsPane.setMouseTransparent(true);
    }

    private void setUpdateText(Text updateText) {
        updateText.setText(
                "Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.");
        updateText.setFont(new Font(40));
        updateText.setTextAlignment(TextAlignment.CENTER);
    }

    private void setUpdateNotice(StackPane updateNotice) {
        updateNotice.setBackground(
                new Background(new BackgroundFill(Color.WHITE, null, null)));
        updateNotice.setOpacity(0.9);

        updateNotice.visibleProperty().bind(parametersB.parametersProperty()
                .isNotEqualTo(computerB.parametersProperty()));
        updateNotice.setOnMouseClicked(e -> computerB
                .setParameters(parametersB.parametersProperty().get()));
    }

    private void setDynamicParameters(GridPane paramsGrid, TextArea areaInfo) {

        Label latitudeL = new Label("Latitude (°) :");
        TextField latitudeF = new TextField();
        setLabelAndField(OBSERVER_LATITUDE, latitudeL, latitudeF, 7, 4);

        Label longitudeL = new Label("Longitude (°) :");
        TextField longitudeF = new TextField();
        setLabelAndField(OBSERVER_LONGITUDE, longitudeL, longitudeF, 7, 4);

        Label elevationL = new Label("Altitude (m) :");
        TextField elevationF = new TextField();
        setLabelAndField(OBSERVER_ELEVATION, elevationL, elevationF, 4, 0);

        Label azimuthL = new Label("Azimut (°) :");
        TextField azimuthF = new TextField();
        setLabelAndField(CENTER_AZIMUTH, azimuthL, azimuthF, 3, 0);

        Label fieldOfViewL = new Label("Angle de vue (°) :");
        TextField fieldOfViewF = new TextField();
        setLabelAndField(HORIZONTAL_FIELD_OF_VIEW, fieldOfViewL, fieldOfViewF,
                3, 0);

        Label maxDistanceL = new Label("Visibilité (km) :");
        TextField maxDistanceF = new TextField();
        setLabelAndField(MAX_DISTANCE, maxDistanceL, maxDistanceF, 3, 0);

        Label widthL = new Label("Largeur (px) :");
        TextField widthF = new TextField();
        setLabelAndField(WIDTH, widthL, widthF, 4, 0);

        Label heightL = new Label("Hauteur (px) :");
        TextField heightF = new TextField();
        setLabelAndField(HEIGHT, heightL, heightF, 4, 0);

        Label superSamplingExL = new Label("Suréchantillonage :");
        ChoiceBox<Integer> superSamplingExF = new ChoiceBox<>();
        superSamplingExF.getItems().addAll(0, 1, 2);
        StringConverter<Integer> supFor = new LabeledListStringConverter("non",
                "2×", "4×");
        superSamplingExF.valueProperty()
                .bindBidirectional(parametersB.superSamplingExponentProperty());
        superSamplingExF.setConverter(supFor);
        superSamplingExL.setAlignment(Pos.CENTER_RIGHT);

        paramsGrid.addRow(0, latitudeL, latitudeF, longitudeL, longitudeF,
                elevationL, elevationF);
        paramsGrid.addRow(1, azimuthL, azimuthF, fieldOfViewL, fieldOfViewF,
                maxDistanceL, maxDistanceF);
        paramsGrid.addRow(2, widthL, widthF, heightL, heightF, superSamplingExL,
                superSamplingExF);
        paramsGrid.add(areaInfo, 6, 0, 1, 3);

        // FIXME Spacing between columns and spanning in the window.
    }

    private void setLabelAndField(UserParameter uP, Label label,
            TextField field, int i, int j) {
        TextFormatter<Integer> formatter = new TextFormatter<>(
                new FixedPointStringConverter(j));
        formatter.valueProperty()
                .bindBidirectional(parametersB.getProperty(uP));
        field.setTextFormatter(formatter);
        label.setAlignment(Pos.CENTER_RIGHT);
        field.setAlignment(Pos.CENTER_RIGHT);
        field.setPrefColumnCount(i);
    }

    private void setAreaInfo(TextArea areaInfo) {
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(2);
    }

    private void setRoot(BorderPane root, StackPane panoPane,
            GridPane paramsGrid) {
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);
        root.setPrefWidth(1260);
        root.setPrefHeight(700);
    }

}