package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
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
import javafx.stage.Stage;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;

public final class Alpano extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Computing the Panorama
        File alps = new File("alps.txt");
        List<Summit> summits = GazetteerParser.readSummitsFrom(alps);

        DiscreteElevationModel dem = new SuperHgtDiscreteElevationModel();
        ContinuousElevationModel cem = new ContinuousElevationModel(dem);

        PanoramaUserParameters predef = PredefinedPanoramas.JURA;
        PanoramaParametersBean parametersB = new PanoramaParametersBean(predef);
        PanoramaComputerBean computerB = new PanoramaComputerBean(cem, summits);

        // *** PANE 1 ***

        // IMAGE
        ImageView panoView = new ImageView();
        panoView.imageProperty().bind(computerB.imageProperty());
        panoView.fitWidthProperty().bind(parametersB.widthProperty());
        panoView.setPreserveRatio(true);
        panoView.setSmooth(true);
        Text mouseInfo = new Text();
        panoView.setOnMouseMoved(e -> {
            int x = (int) e.getX();
            int y = (int) e.getY();
            float longitude = computerB.getPanorama().longitudeAt(x, y);
            float latitude = computerB.getPanorama().latitudeAt(x, y);
            GeoPoint point = new GeoPoint(longitude, latitude);
            float distance = computerB.getPanorama().distanceAt(x, y);
            int altitude = (int) cem.elevationAt(point);
            double azimuth = computerB.getParameters().panoramaParameters()
                    .observerPosition().azimuthTo(point);
            float elevation = computerB.getPanorama().elevationAt(x, y);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Position : %.4f°N %.4fE%n",
                    Math.toDegrees(latitude), Math.toDegrees(longitude)));
            sb.append(String.format("Distance : %.1f km%n", distance));
            sb.append(String.format("Altitude : %d m%n", altitude));
            sb.append(String.format("Azimut : %.1f (", azimuth));
            sb.append(Azimuth.toOctantString(azimuth, "N", "E", "S", "W"));
            sb.append(String.format(")  Elévation : %.1f°%n", elevation));
            mouseInfo.setText(sb.toString());
        });
        panoView.setOnMouseClicked(e -> {
            int x = (int) e.getX();
            int y = (int) e.getY();
            float longitude = computerB.getPanorama().longitudeAt(x, y);
            float latitude = computerB.getPanorama().latitudeAt(x, y);
            String lambda = String.format((Locale) null, "%.4f",
                    Math.toDegrees(longitude));
            String phi = String.format((Locale) null, "%.4f",
                    Math.toDegrees(latitude));
            String qy = "?mlat=" + phi + "&mlon=" + lambda;
            String fg = "map=15/" + phi + "/" + lambda;
            URI osmURI;
            try {
                osmURI = new URI("http", "www.openstreetmap.org", "/", qy, fg);
                java.awt.Desktop.getDesktop().browse(osmURI);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        // LABELS

        Pane labelsPane = new Pane();
        Bindings.bindContent(labelsPane.getChildren(), computerB.getLabels());
        labelsPane.prefWidthProperty().bind(parametersB.widthProperty());
        labelsPane.prefHeightProperty().bind(parametersB.heightProperty());
        labelsPane.setMouseTransparent(true);

        StackPane panoGroup = new StackPane(panoView, labelsPane);
        ScrollPane panoScrollPane = new ScrollPane(panoGroup);

        Text updateText = new Text(
                "Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.");
        updateText.setFont(new Font(40));
        updateText.setTextAlignment(TextAlignment.CENTER);

        StackPane updateNotice = new StackPane(updateText);
        updateNotice.setBackground(
                new Background(new BackgroundFill(Color.WHITE, null, null)));
        updateNotice.setOpacity(0.9);

        updateNotice.visibleProperty().bind(parametersB.parametersProperty()
                .isNotEqualTo(computerB.parametersProperty()));
        updateNotice.setOnMouseClicked(e -> computerB
                .setParameters(parametersB.parametersProperty().get()));

        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);

        // *** PANE 2 & 3 ***

        // ELEMENTS OF PANELS 2 & 3 (labales, textfield, ...)

        StringConverter<Integer> fixedPointConv4 = new FixedPointStringConverter(
                4);
        StringConverter<Integer> fixedPointConv0 = new FixedPointStringConverter(
                0);

        Label latL = new Label("Latitude (°) :");
        TextField latF = new TextField();
        TextFormatter<Integer> latFor = new TextFormatter<>(fixedPointConv4);
        latF.setTextFormatter(latFor);
        latFor.valueProperty()
                .bindBidirectional(parametersB.observerLatitudeProperty());
        latL.setAlignment(Pos.CENTER_RIGHT);
        latF.setAlignment(Pos.CENTER_RIGHT);
        latF.setPrefColumnCount(7);

        Label lonL = new Label("Longitude (°) :");
        TextField lonF = new TextField();
        TextFormatter<Integer> lonFor = new TextFormatter<>(fixedPointConv4);
        lonF.setTextFormatter(lonFor);
        lonFor.valueProperty()
                .bindBidirectional(parametersB.observerLongitudeProperty());
        lonL.setAlignment(Pos.CENTER_RIGHT);
        lonF.setAlignment(Pos.CENTER_RIGHT);
        lonF.setPrefColumnCount(7);
        
        Label altL = new Label("Altitude (m) :");
        TextField altF = new TextField();
        TextFormatter<Integer> altFor = new TextFormatter<>(fixedPointConv0);
        altF.setTextFormatter(altFor);
        altFor.valueProperty()
        .bindBidirectional(parametersB.observerElevationProperty());
        altL.setAlignment(Pos.CENTER_RIGHT);
        altF.setAlignment(Pos.CENTER_RIGHT);
        altF.setPrefColumnCount(4);
        
        Label aziL = new Label("Azimut (°) :");
        TextField aziF = new TextField();
        TextFormatter<Integer> aziFor = new TextFormatter<>(fixedPointConv0);
        aziF.setTextFormatter(aziFor);
        aziFor.valueProperty()
        .bindBidirectional(parametersB.centerAzimuthProperty());
        aziL.setAlignment(Pos.CENTER_RIGHT);
        aziF.setAlignment(Pos.CENTER_RIGHT);
        aziF.setPrefColumnCount(3);
        
        Label angL = new Label("Angle de vue (°) :");
        TextField angF = new TextField();
        TextFormatter<Integer> angFor = new TextFormatter<>(fixedPointConv0);
        angF.setTextFormatter(angFor);
        angFor.valueProperty()
        .bindBidirectional(parametersB.horizontalFieldOfViewProperty());
        angL.setAlignment(Pos.CENTER_RIGHT);
        angF.setAlignment(Pos.CENTER_RIGHT);
        angF.setPrefColumnCount(3);
        
        Label visL = new Label("Visibilité (km) :");
        TextField visF = new TextField();
        TextFormatter<Integer> visFor = new TextFormatter<>(fixedPointConv0);
        visF.setTextFormatter(visFor);
        visFor.valueProperty()
        .bindBidirectional(parametersB.maxDistanceProperty());
        visL.setAlignment(Pos.CENTER_RIGHT);
        visF.setAlignment(Pos.CENTER_RIGHT);
        visF.setPrefColumnCount(3);
        
        Label widL = new Label("Largeur (px) :");
        TextField widF = new TextField();
        TextFormatter<Integer> widFor = new TextFormatter<>(fixedPointConv0);
        widF.setTextFormatter(widFor);
        widFor.valueProperty()
        .bindBidirectional(parametersB.widthProperty());
        widL.setAlignment(Pos.CENTER_RIGHT);
        widF.setAlignment(Pos.CENTER_RIGHT);
        widF.setPrefColumnCount(4);
        
        Label heiL = new Label("Hauteur (px) :");
        TextField heiF = new TextField();
        TextFormatter<Integer> heiFor = new TextFormatter<>(fixedPointConv0);
        heiF.setTextFormatter(heiFor);
        heiFor.valueProperty()
        .bindBidirectional(parametersB.heightProperty());
        heiL.setAlignment(Pos.CENTER_RIGHT);
        heiF.setAlignment(Pos.CENTER_RIGHT);
        heiF.setPrefColumnCount(4);
        
        Label supL = new Label("");
        ChoiceBox<Integer> supF = new ChoiceBox<>();
        StringConverter<Integer> supFor =
                new LabeledListStringConverter("non", "2×", "4×");
        supF.setConverter(supFor);
        supF.valueProperty()
        .bindBidirectional(parametersB.superSamplingExponentProperty());
        supL.setAlignment(Pos.CENTER_RIGHT);
        
        TextArea areaInfo = new TextArea();
        areaInfo.textProperty().bind(mouseInfo.textProperty());
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(2);

        GridPane paramsGrid = new GridPane();
        paramsGrid.addRow(0, latL, latF, lonL, lonF, altL, altF);
        paramsGrid.addRow(1, aziL, aziF, angL, angF, visL, visF);
        paramsGrid.addRow(2, widL, widF, heiL, heiF, supL, supF);
        paramsGrid.add(areaInfo, 6, 0, 1, 3);

        // *** ROOT ***
        BorderPane root = new BorderPane();
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}