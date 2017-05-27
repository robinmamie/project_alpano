package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Azimuth.toOctantString;
import static ch.epfl.alpano.gui.PanoramaUserParameters.M_PER_KM;
import static ch.epfl.alpano.gui.UserParameter.CENTER_AZIMUTH;
import static ch.epfl.alpano.gui.UserParameter.HEIGHT;
import static ch.epfl.alpano.gui.UserParameter.HORIZONTAL_FIELD_OF_VIEW;
import static ch.epfl.alpano.gui.UserParameter.MAX_DISTANCE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_ELEVATION;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LATITUDE;
import static ch.epfl.alpano.gui.UserParameter.OBSERVER_LONGITUDE;
import static ch.epfl.alpano.gui.UserParameter.WIDTH;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;
import static java.lang.String.format;
import static javafx.application.Platform.runLater;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javax.imageio.ImageIO.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
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

/**
 * Classe principale du projet. Lance le programme graphique.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Alpano extends Application {

    /**
     * Les paramètres de panorama à précharger.
     */
    private final static PanoramaUserParameters PRELOAD = PredefinedPanoramas.JURA;

    /**
     * Valeur préférée de prefRowCount pour l'aire de texte des infos sous le
     * curseur.
     */
    private final static int TEXT_AREA_PREF_ROW_COUNT = 2;

    /**
     * Opacité de la fenêtre de mise à jour.
     */
    private final static float OPACITY_UPDATE_NOTICE = 0.9f;

    /**
     * Taille du texte de la mise à jour des paramètres.
     */
    private final static double UDPATE_TEXT_FONT_SIZE = 40;

    /**
     * Espacement honrizontal des éléments de la grille du bas.
     */
    private final static double BOTTOM_GRID_HGAP = 10;

    /**
     * Espacement vertical des éléments de la grille du bas.
     */
    private final static double BOTTOM_GRID_VGAP = 3;

    /**
     * Taille de la marge de la grille du bas.
     */
    private final static Insets BOTTOM_GRID_PADDING = new Insets(7, 5, 5, 5);

    /**
     * Largeur préférentielle de la fenêtre au démarrage.
     */
    private final static double WINDOW_PREF_WIDTH = 1500;

    /**
     * Hauteur préférentielle de la fenêtre au démarrage.
     */
    private final static double WINDOW_PREF_HEIGHT = 700;

    /**
     * MNT continu chargé.
     */
    private final static ContinuousElevationModel CEM;

    /**
     * Bean des paramètres de panorama.
     */
    private final static PanoramaParametersBean PARAMETERS_B;

    /**
     * Bean du calculateur de panorama.
     */
    private final static PanoramaComputerBean COMPUTER_B;

    /**
     * Nombre de décimales maximum après la latitude et la longitude dans les
     * paramètres.
     */
    private static final int MAX_DECIMAL_NUMBER = 4;

    /**
     * Pas de décimales.
     */
    private static final int NO_DECIMAL_NUMBER = 0;

    /**
     * Nombre maximal de colonnes dans les parmètres.
     */
    private static final int MAXI_COL_COUNT = 7;

    /**
     * Nombre moyen de colonnes dans les paramètres.
     */
    private static final int MIDI_COL_COUNT = 4;

    /**
     * Nombre minimal de colonnes dans les paramètres.
     */
    private static final int MINI_COL_COUNT = 3;

    /**
     * Constructeur statique de la classe.
     */
    static {
        System.out.println("\nAlpano launching...");

        List<Summit> summits;
        try {
            summits = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The file 'alps.txt' does not exist or is corrupted.");
        }

        System.out.println(" - Summits loaded.");

        DiscreteElevationModel dem = new SuperHgtDiscreteElevationModel();

        System.out.println(" - DEMs loaded.");

        CEM = new ContinuousElevationModel(dem);
        PARAMETERS_B = new PanoramaParametersBean(PRELOAD);
        COMPUTER_B = new PanoramaComputerBean(CEM, summits);

        System.out.println(" - Beans created.");
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
        Pane labelsPane = setLabelsPane();
        StackPane panoGroup = new StackPane(panoView, labelsPane);
        ScrollPane panoScrollPane = new ScrollPane(panoGroup);
        StackPane updateNotice = setUpdateNotice();
        StackPane panoPane = new StackPane(panoScrollPane, updateNotice);
        GridPane paramsGrid = setParamsGrid(areaInfo);
        MenuBar menuBar = setMenuBar(panoGroup);
        BorderPane root = setRoot(panoPane, paramsGrid, menuBar);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Alpano");
        primaryStage.setScene(scene);
        primaryStage.setWidth(WINDOW_PREF_WIDTH);
        primaryStage.setHeight(WINDOW_PREF_HEIGHT);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("\nGoodbye.");
            System.exit(0);
        });

        System.out.println(" - Graphical interface loaded.");

        System.out.println("\nWelcome!\n");
    }

    /**
     * Crée la zone de texte qui contient les informations du point sous le
     * curseur.
     * 
     * @return Un TextArea paramétré pour contenir les informations du point
     *         sous le curseur.
     */
    private TextArea setAreaInfo() {
        TextArea areaInfo = new TextArea();
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(TEXT_AREA_PREF_ROW_COUNT);
        return areaInfo;
    }

    /**
     * Crée la vue du Panorama.
     * 
     * @param areaInfo
     *            La zone de texte montrant les informations du point sous le
     *            curseur.
     * 
     * @return Une ImageView contenant le Panorama.
     */
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

    /**
     * Paramétrise le comportement de l'aire de texte lorsque le curseur bouge.
     * 
     * @param panoView
     *            Le Pane dans lequel le curseur se déplace.
     * 
     * @param areaInfo
     *            La zone de texte à paramétrer.
     */
    private void setMouseMove(ImageView panoView, TextArea areaInfo) {
        panoView.setOnMouseMoved(e -> {
            int x = getSampledIndex(e.getX());
            int y = getSampledIndex(e.getY());

            double lat = toDegrees(COMPUTER_B.getPanorama().latitudeAt(x, y));
            double lon = toDegrees(COMPUTER_B.getPanorama().longitudeAt(x, y));

            double azimuth = COMPUTER_B.getParameters().panoramaParameters()
                    .azimuthForX(x);
            char northOrSouth = lat >= 0.0 ? 'N' : 'S';
            char eastOrWest = lon >= 0.0 ? 'E' : 'W';

            StringBuilder sb = new StringBuilder();
            sb.append(format("Position : %.4f°%c %.4f°%c%n", abs(lat),
                    northOrSouth, abs(lon), eastOrWest));
            sb.append(format("Distance : %.1f km%n",
                    COMPUTER_B.getPanorama().distanceAt(x, y) / M_PER_KM));
            sb.append(format("Altitude : %d m%n",
                    (int) COMPUTER_B.getPanorama().elevationAt(x, y)));
            sb.append(format("Azimut : %.1f° (%s)  Élévation : %.1f°",
                    toDegrees(azimuth),
                    toOctantString(azimuth, "N", "E", "S", "W"),
                    toDegrees(COMPUTER_B.getParameters().panoramaParameters()
                            .altitudeForY(y))));
            areaInfo.setText(sb.toString());
        });
    }

    /**
     * Paramétrise les actions à entreprendre lorsque l'utilisateur clique dans
     * l'ImageView du Panorama.
     * 
     * @param panoView
     *            Le Pane dans lequel la souris clique.
     */
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
                    System.err.println("Could not get to browser.");
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

    /**
     * Retourne l'index en prenant en compte le suréchantillonage.
     * 
     * @param index
     *            L'index de base.
     * 
     * @return l'index prenant en compte le suréchantillonage.
     */
    private int getSampledIndex(double index) {
        return (int) Math.scalb(index,
                COMPUTER_B.getParameters().superSamplingExponent());
    }

    /**
     * Paramétrise le Pane contenant les étiquettes des sommets.
     * 
     * @return le Pane contenant les étiquettes des sommets.
     */
    private Pane setLabelsPane() {
        Pane labelsPane = new Pane();
        Bindings.bindContent(labelsPane.getChildren(), COMPUTER_B.getLabels());
        labelsPane.prefWidthProperty().bind(PARAMETERS_B.widthProperty());
        labelsPane.prefHeightProperty().bind(PARAMETERS_B.heightProperty());
        labelsPane.setMouseTransparent(true);
        return labelsPane;
    }

    /**
     * Paramétrise le texte de mise à jour.
     * 
     * @return le texte de mise à jour.
     */
    private Text setUpdateText(String text) {
        Text updateText = new Text(text);
        updateText.setFont(new Font(UDPATE_TEXT_FONT_SIZE));
        updateText.setTextAlignment(TextAlignment.CENTER);
        return updateText;
    }

    /**
     * Paramétrise le Pane contenant le texte de miste à jour et son
     * interactivité.
     * 
     * @param updateText
     *            Le texte de mise à jour.
     * 
     * @return le Pane contenant le texte de miste à jour et son interactivité.
     */
    private StackPane setUpdateNotice() {
        Text updateText = setUpdateText(
                "Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.");
        
        Text updatingText = setUpdateText("");
        ProgressBar updatingProcess = new ProgressBar();
        updatingProcess.setPrefWidth(500);
        updatingProcess.setPrefHeight(25);
        updatingProcess.progressProperty().bind(COMPUTER_B.statusProperty());
        COMPUTER_B.statusProperty().addListener((p, o, n) -> {
            if(1 - n.doubleValue() < 1e-10)
                updatingText.setText("Le panorama est en cours de création...");
            else
                updatingText.setText("Le panorama est en cours de calcul...");
        });
        GridPane updatingGrid = new GridPane();
        updatingGrid.addRow(0, updatingText);
        updatingGrid.addRow(1, updatingProcess);
        updatingGrid.setAlignment(Pos.CENTER);
        updatingGrid.setVgap(10);
        GridPane.setHalignment(updatingProcess, HPos.CENTER);
        GridPane.setHalignment(updatingText, HPos.CENTER);
        
        StackPane updateNotice = new StackPane(updateText);
        updateNotice.setBackground(
                new Background(new BackgroundFill(Color.WHITE, null, null)));
        updateNotice.setOpacity(OPACITY_UPDATE_NOTICE);
        updateNotice.visibleProperty()
                .bind(PARAMETERS_B.parametersProperty()
                        .isNotEqualTo(COMPUTER_B.parametersProperty())
                        .or(COMPUTER_B.imageProperty().isNull()));
        updateNotice.visibleProperty().addListener(e -> {
            if (!updateNotice.isVisible())
                updateNotice.getChildren().setAll(updateText);
        });
        updateNotice.setOnMouseClicked(e -> {
            COMPUTER_B.setParameters(PARAMETERS_B.parametersProperty().get());
            updateNotice.getChildren().setAll(updatingGrid);
        });
        return updateNotice;
    }

    /**
     * Paramétrise la grille des paramètres au bas de la fenêtre principale.
     * 
     * @param areaInfo
     *            La zone de texte contenant les informations du point se
     *            situant sous le pointeur de la souris.
     * 
     * @return la grille des paramètres au bas de la fenêtre principale.
     */
    private GridPane setParamsGrid(TextArea areaInfo) {

        GridPane paramsGrid = new GridPane();

        List<Control> labelsAndField = new ArrayList<>();
        labelsAndField.addAll(setLabelAndField("Latitude (°)",
                OBSERVER_LATITUDE, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
        labelsAndField.addAll(setLabelAndField("Longitude (°)",
                OBSERVER_LONGITUDE, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
        labelsAndField.addAll(setLabelAndField("Altitude (m)",
                OBSERVER_ELEVATION, MIDI_COL_COUNT, NO_DECIMAL_NUMBER));
        labelsAndField.addAll(setLabelAndField("Azimut (°)", CENTER_AZIMUTH,
                MINI_COL_COUNT, 0));
        labelsAndField.addAll(setLabelAndField("Angle de vue (°)",
                HORIZONTAL_FIELD_OF_VIEW, MINI_COL_COUNT, 0));
        labelsAndField.addAll(setLabelAndField("Visibilité (km)", MAX_DISTANCE,
                MINI_COL_COUNT, 0));
        labelsAndField.addAll(
                setLabelAndField("Largeur (px)", WIDTH, MIDI_COL_COUNT, 0));
        labelsAndField.addAll(
                setLabelAndField("Hauteur (px)", HEIGHT, MIDI_COL_COUNT, 0));
        labelsAndField.addAll(setSuperSamplingOption());

        for (int i = 0; i < labelsAndField.size(); ++i)
            paramsGrid.add(labelsAndField.get(i), i % 6, i / 6);

        paramsGrid.add(areaInfo, 6, 0, 1, 3);

        paramsGrid.setAlignment(Pos.CENTER);
        paramsGrid.setHgap(BOTTOM_GRID_HGAP);
        paramsGrid.setVgap(BOTTOM_GRID_VGAP);
        paramsGrid.setPadding(BOTTOM_GRID_PADDING);

        return paramsGrid;
    }

    /**
     * Paramétrise les options concernant le suréchantillonage.
     * 
     * @return le Label et la ChoiceBox du suréchantillonage.
     */
    private Collection<? extends Control> setSuperSamplingOption() {
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

    /**
     * Paramétrise les options concernant les différents paramètres du Panorama.
     * 
     * @param name
     *            Le nom de l'option.
     * @param uP
     *            Le paramètre utilisateur concerné.
     * @param prefColumnCount
     *            Le nombre de colonnes de texte dans le Field.
     * @param decimals
     *            Le nombre de chiffres après la virgule désiré.
     * 
     * @return Le Label et le TextField correspondant aux paramètres donnés sous
     *         forme de tableau.
     */
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

    /**
     * Crée la barre de menu.
     * 
     * @param panoGroup
     * 
     * @return la barre de menu.
     */
    private MenuBar setMenuBar(StackPane panoGroup) {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = setMenuFile(panoGroup);
        Menu menuParameters = setMenuParameters();
        menuBar.getMenus().addAll(menuFile, menuParameters);
        return menuBar;
    }

    private Menu setMenuFile(StackPane panoGroup) {
        Menu menuFile = new Menu("Fichier");
        MenuItem save = new MenuItem("Sauvegarder");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        save.setOnAction(e -> openSaveWindow(panoGroup));
        MenuItem load = new MenuItem("Charger", null);
        load.setOnAction(e -> openLoadWindow());
        load.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        MenuItem exit = new MenuItem("Quitter", null);
        exit.setOnAction(e -> {
            System.out.println("Goodbye.");
            System.exit(0);
        });
        exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

        try {
            save.setGraphic(new ImageView(
                    new Image(new FileInputStream(new File("res/save.png")))));
            load.setGraphic(new ImageView(
                    new Image(new FileInputStream(new File("res/load.png")))));
            exit.setGraphic(new ImageView(
                    new Image(new FileInputStream(new File("res/close.png")))));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        menuFile.getItems().addAll(save, load, new SeparatorMenuItem(), exit);
        return menuFile;
    }

    private void openSaveWindow(StackPane panoGroup) {
        if (COMPUTER_B.getImage() == null)
            return;

        Stage saveStage = new Stage();
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        Label queryL = new Label("Veuillez entrer le nom de la sauvegarde :");
        TextField queryF = new TextField();
        queryF.setPromptText("Exemple : mon_super_panorama");

        Button saveButton = new Button("Sauvegarder");
        saveButton.setOnAction(
                f -> saveImageAndParameters(saveStage, queryF, panoGroup));

        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> saveStage.close());
        GridPane.setHalignment(quitButton, HPos.RIGHT);

        grid.add(queryL, 0, 0, 2, 1);
        grid.add(queryF, 0, 1, 2, 1);
        grid.add(saveButton, 0, 2);
        grid.add(quitButton, 1, 2);

        grid.setVgap(BOTTOM_GRID_VGAP);
        grid.setHgap(BOTTOM_GRID_HGAP);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(BOTTOM_GRID_PADDING);

        saveStage.setTitle("Save Panorama");
        saveStage.setScene(scene);
        saveStage.setResizable(false);
        saveStage.setAlwaysOnTop(true);
        saveStage.setWidth(250);
        saveStage.show();
    }

    private void saveImageAndParameters(Stage saveStage, TextField queryF,
            StackPane panoGroup) {
        File imgFolder = new File("img/");
        File saveFolder = new File("save/");
        if (!imgFolder.exists())
            imgFolder.mkdirs();
        if (!saveFolder.exists())
            saveFolder.mkdirs();
        if (queryF.getText() != null && !queryF.getText().isEmpty()) {
            try {
                WritableImage image = new WritableImage(
                        (int) panoGroup.getWidth(),
                        (int) panoGroup.getHeight());
                panoGroup.snapshot(null, image);
                write(fromFXImage(image, null), "png",
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
    }

    private void openLoadWindow() {
        Stage loadStage = new Stage();

        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        File saveFolder = new File("save/");
        if (!saveFolder.exists())
            return;

        File[] files = saveFolder.listFiles();

        ChoiceBox<File> choices = new ChoiceBox<>();
        GridPane.setHalignment(choices, HPos.CENTER);
        choices.getItems().addAll(files);
        StringConverter<File> loadConv = new FileStringConverter(
                saveFolder.getPath(), 3);
        choices.setConverter(loadConv);

        TextArea parametersInfo = setAreaInfo();
        parametersInfo.setPrefWidth(230);
        parametersInfo.setPrefHeight(95);

        choices.setOnAction(e -> parametersInfo
                .setText(loadParameters(choices).toString()));

        Button loadButton = new Button("Load");

        loadButton.setOnAction(e -> {
            PanoramaUserParameters p = loadParameters(choices);
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

        Button quitButton = new Button("Annuler");
        quitButton.setOnAction(f -> loadStage.close());

        grid.add(choices, 0, 0, 2, 1);
        grid.add(loadButton, 0, 1);
        grid.add(quitButton, 1, 1);
        grid.add(parametersInfo, 2, 0, 1, 2);

        grid.setVgap(BOTTOM_GRID_VGAP);
        grid.setHgap(BOTTOM_GRID_HGAP);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(BOTTOM_GRID_PADDING);

        loadStage.setTitle("Load Panorama");
        loadStage.setScene(scene);
        loadStage.setResizable(false);
        loadStage.setAlwaysOnTop(true);
        loadStage.show();

    }

    private PanoramaUserParameters loadParameters(ChoiceBox<File> choices) {
        File load = choices.getValue();
        PanoramaUserParameters p = null;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(load))) {
            p = (PanoramaUserParameters) in.readObject();
        } catch (Exception i) {
            i.printStackTrace();
            return null;
        }
        return p;
    }

    private Menu setMenuParameters() {
        Menu menuParameters = new Menu("Paramètres");
        RadioMenuItem autoAltitude = setAutoAltitude();
        autoAltitude.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        menuParameters.getItems().addAll(autoAltitude);
        return menuParameters;
    }

    private RadioMenuItem setAutoAltitude() {
        RadioMenuItem autoAltitude = new RadioMenuItem("Auto-altitude");
        ChangeListener<? super Integer> listener = (p, o,
                n) -> runLater(() -> PARAMETERS_B.observerElevationProperty()
                        .set((int) CEM.elevationAt(PARAMETERS_B
                                .parametersProperty().get()
                                .panoramaDisplayParameters().observerPosition())
                                + 2));
        autoAltitude.setOnAction(e -> {
            if (autoAltitude.isSelected()) {
                PARAMETERS_B.observerLongitudeProperty().addListener(listener);
                PARAMETERS_B.observerLatitudeProperty().addListener(listener);
            } else {
                PARAMETERS_B.observerLongitudeProperty()
                        .removeListener(listener);
                PARAMETERS_B.observerLatitudeProperty()
                        .removeListener(listener);
            }
        });
        return autoAltitude;
    }

    /**
     * Paramétrise la racine de la fenêtre.
     * 
     * @param panoPane
     *            Le Pane composé du Panorama, des Labels et de la notice
     *            d'update.
     * @param paramsGrid
     *            Le Pane contenant toutes les options paramétrables du Panorama
     *            ainsi que le texte contenant les informations du point situé
     *            sous le curseur.
     * 
     * @return le BorderPane à la base de l'interface graphique.
     */
    private BorderPane setRoot(StackPane panoPane, GridPane paramsGrid,
            MenuBar menuBar) {
        BorderPane root = new BorderPane();
        root.setCenter(panoPane);
        root.setBottom(paramsGrid);
        root.setTop(menuBar);
        return root;
    }

}