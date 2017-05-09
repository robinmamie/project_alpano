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
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javax.imageio.ImageIO.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
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

            if (e.getButton() == MouseButton.PRIMARY) {
                String[] lonAndLat = getFormattedLongitudeAndLatitude(x, y);

                String qy = "?mlat=" + lonAndLat[1] + "&mlon=" + lonAndLat[0];
                String fg = "map=15/" + lonAndLat[1] + "/" + lonAndLat[0];
                URI osmURI;
                try {
                    osmURI = new URI("http", "www.openstreetmap.org", "/", qy,
                            fg);
                    java.awt.Desktop.getDesktop().browse(osmURI);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                Panorama p = computerB.getPanorama();
                int longitude = (int) (10_000 * toDegrees(p.longitudeAt(x, y)));
                int latitude = (int) (10_000 * toDegrees(p.latitudeAt(x, y)));
                int elevation = (int) p.elevationAt(x, y) + 2;
                int azimuth = (int) toDegrees(Azimuth.canonicalize(
                        computerB.getParameters().panoramaDisplayParameters()
                                .azimuthForX(x) + Math.PI));
                parametersB.observerLongitudeProperty().set(longitude);
                parametersB.observerLatitudeProperty().set(latitude);
                parametersB.observerElevationProperty().set(elevation);
                parametersB.centerAzimuthProperty().set(azimuth);
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

        Label superSamplingExL = new Label("  Suréchantillonage : ");
        ChoiceBox<Integer> superSamplingExF = new ChoiceBox<>();
        superSamplingExF.getItems().addAll(0, 1, 2);
        StringConverter<Integer> supFor = new LabeledListStringConverter("non",
                "2×", "4×");
        superSamplingExF.valueProperty()
                .bindBidirectional(parametersB.superSamplingExponentProperty());
        superSamplingExF.setConverter(supFor);
        superSamplingExL.setAlignment(Pos.CENTER_RIGHT);
        labelsAndField.add(superSamplingExL);
        labelsAndField.add(superSamplingExF);

        for (int i = 0; i < 18; ++i)
            paramsGrid.add(labelsAndField.get(i), i % 6, i / 6);

        Button computeElevation = new Button("Auto-altitude");
        computeElevation
                .setOnAction(e -> parametersB.observerElevationProperty()
                        .set((int) cem.elevationAt(parametersB
                                .parametersProperty().get().panoramaParameters()
                                .observerPosition()) + 2));

        Button saveImageAndPanorama = new Button("Save");
        saveImageAndPanorama.setOnAction(e -> openSaveWindow(e));

        Button loadPanorama = new Button("Load");
        loadPanorama.setOnAction(e -> openLoadWindow(e));

        paramsGrid.add(computeElevation, 6, 0);
        paramsGrid.add(saveImageAndPanorama, 6, 1);
        paramsGrid.add(loadPanorama, 6, 2);

        paramsGrid.add(areaInfo, 7, 0, 1, 3);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment(HPos.RIGHT);
        for (int i = 0; i < 6; ++i)
            paramsGrid.getColumnConstraints().add(cc);

        // FIXME Spacing between columns and spanning in the window.
    }

    private void openLoadWindow(ActionEvent e) {
        Stage loadStage = new Stage();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        File saveFolder = new File("save/");
        File[] files = saveFolder.listFiles();

        ChoiceBox<File> choices = new ChoiceBox<>();
        choices.getItems().addAll(files);
        StringConverter<File> loadConv = new FileStringConverter(
                saveFolder.getPath());
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
            parametersB.observerLongitudeProperty().set(p.observerLongitude());
            parametersB.observerLatitudeProperty().set(p.observerLatitude());
            parametersB.observerElevationProperty().set(p.observerElevation());
            parametersB.centerAzimuthProperty().set(p.centerAzimuth());
            parametersB.maxDistanceProperty().set(p.maxDistance());
            parametersB.horizontalFieldOfViewProperty()
                    .set(p.horizontalFieldOfView());
            parametersB.widthProperty().set(p.width());
            parametersB.heightProperty().set(p.height());
            parametersB.superSamplingExponentProperty()
                    .set(p.superSamplingExponent());
            loadStage.close();
        });

        // for(File f: files)

        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> loadStage.close());

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPrefWidth(300);
        grid.setPrefHeight(150);

        grid.add(choices, 0, 0, 2, 1);
        grid.add(loadButton, 0, 1);
        grid.add(quitButton, 1, 1);

        loadStage.setTitle("Alpano - Load File");
        loadStage.setScene(scene);
        loadStage.show();

    }

    private void openSaveWindow(ActionEvent e) {
        if (computerB.getImage() == null)
            return;

        Stage saveStage = new Stage();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        Label queryL = new Label("Veuillez entrer le nom de la sauvegarde :");
        queryL.setPadding(new Insets(5));
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
                    write(fromFXImage(computerB.getImage(), null), "png",
                            new File("img/" + queryF.getText() + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(
                                "save/" + queryF.getText() + ".ser"))) {
                    out.writeObject(computerB.getParameters());
                } catch (IOException i) {
                    i.printStackTrace();
                }
                saveStage.close();
            }
        });
        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> saveStage.close());

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPrefWidth(300);
        grid.setPrefHeight(150);

        grid.add(queryL, 0, 0, 2, 1);
        grid.add(queryF, 0, 1, 2, 1);
        grid.add(saveButton, 0, 3);
        grid.add(quitButton, 1, 3);

        saveStage.setTitle("Alpano - Save File");
        saveStage.setScene(scene);
        saveStage.show();
    }

    private List<Control> setLabelAndField(String name, UserParameter uP, int i,
            int j) {
        Label label = new Label("  " + name + " : ");
        label.setPadding(new Insets(2));
        TextField field = new TextField();
        field.setPadding(new Insets(2));
        TextFormatter<Integer> formatter = new TextFormatter<>(
                new FixedPointStringConverter(j));
        formatter.valueProperty()
                .bindBidirectional(parametersB.getProperty(uP));
        field.setTextFormatter(formatter);
        field.setAlignment(Pos.CENTER_RIGHT);
        field.setPrefColumnCount(i);
        return Arrays.asList(label, field);
    }

    private void setAreaInfo(TextArea areaInfo) {
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(2);
    }

    private void setRoot(BorderPane root, StackPane panoPane,
            GridPane paramsGrid) {
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);
        root.setPrefWidth(1400);
        root.setPrefHeight(700);
    }

}