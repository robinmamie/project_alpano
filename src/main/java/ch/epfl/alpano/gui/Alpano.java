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

import java.awt.Desktop;
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
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HilbertDiscreteElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.draw.GazetteerParserTest;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Labelizable;
import ch.epfl.alpano.summit.Place;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
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

/**
 * Classe principale du projet. Lance le programme graphique.
 *
 * @author Robin Mamié
 */
public final class Alpano extends Application {

	private static final Logger logger = LogManager.getLogger(Alpano.class);

	/**
	 * Les paramètres de panorama à précharger.
	 */
	private static final PanoramaUserParameters PRELOAD = PredefinedPanoramas.JURA.parameters();

	/**
	 * Valeur préférée de prefRowCount pour l'aire de texte des infos sous le
	 * curseur.
	 */
	private static final int TEXT_AREA_PREF_ROW_COUNT = 2;

	/**
	 * Opacité de la fenêtre de mise à jour.
	 */
	private static final float OPACITY_UPDATE_NOTICE = 0.9f;

	/**
	 * Taille du texte de la mise à jour des paramètres.
	 */
	private static final double UDPATE_TEXT_FONT_SIZE = 40;

	/**
	 * Espacement honrizontal des éléments de la grille du bas.
	 */
	private static final double BOTTOM_GRID_HGAP = 10;

	/**
	 * Espacement vertical des éléments de la grille du bas.
	 */
	private static final double BOTTOM_GRID_VGAP = 3;

	/**
	 * Taille de la marge de la grille du bas.
	 */
	private static final Insets BOTTOM_GRID_PADDING = new Insets(7, 5, 5, 5);

	/**
	 * Largeur préférentielle de la fenêtre au démarrage.
	 */
	private static final double WINDOW_PREF_WIDTH = 1500;

	/**
	 * Hauteur préférentielle de la fenêtre au démarrage.
	 */
	private static final double WINDOW_PREF_HEIGHT = 700;

	/**
	 * MNT continu chargé.
	 */
	private static final ContinuousElevationModel CEM;

	/**
	 * Bean des paramètres de panorama.
	 */
	private static final PanoramaParametersBean PARAMETERS_B;

	/**
	 * Bean du calculateur de panorama.
	 */
	private static final PanoramaComputerBean COMPUTER_B;

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

	private static final String SAVE_LABEL = "Sauvegarder";

	private static final String SAVE_PATH = "save/";

	private static final String CANCEL_LABEL = "Annuler";

	/**
	 * Constructeur statique de la classe.
	 */
	static {
		logger.info("Alpano launching...");

		final var labels = new ArrayList<Labelizable>();
		try {
			final var file = new File(GazetteerParserTest.class.getResource("/alps.txt").getFile());
			labels.addAll(GazetteerParser.readSummitsFrom(file));
		} catch (final IOException e) {
			throw new IllegalArgumentException("The file 'alps.txt' does not exist or is corrupted.");
		}

		final var places = new File("plc/");
		if (places.exists()) {
			final var files = places.listFiles();
			for (final var f : files) {
				try (final var in = new ObjectInputStream(new FileInputStream(f))) {
					labels.add((Place) in.readObject());
				} catch (final Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		logger.info(" - Summits and labels loaded.");

		final var dem = SuperHgtDiscreteElevationModel.FULL;

		logger.info(" - DEMs loaded.");

		CEM = new ContinuousElevationModel(dem);
		PARAMETERS_B = new PanoramaParametersBean(PRELOAD);
		COMPUTER_B = new PanoramaComputerBean(CEM, labels);

		logger.info(" - Beans created.");
	}

	/**
	 * Méthode main du programme.
	 * 
	 * @param args Arguments externes.
	 */
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		final var areaInfo = setAreaInfo();
		final var panoView = setPanoView(areaInfo);
		final var labelsPane = setLabelsPane();
		final var panoGroup = new StackPane(panoView, labelsPane);
		final var panoScrollPane = new ScrollPane(panoGroup);
		final var updateNotice = setUpdateNotice();
		final var panoPane = new StackPane(panoScrollPane, updateNotice);
		final var paramsGrid = setParamsGrid(areaInfo);
		final var menuBar = setMenuBar(panoGroup);
		final var root = setRoot(panoPane, paramsGrid, menuBar);
		final var scene = new Scene(root);

		primaryStage.setTitle("Alpano - Panorama Painter");
		primaryStage.setScene(scene);
		primaryStage.setWidth(WINDOW_PREF_WIDTH);
		primaryStage.setHeight(WINDOW_PREF_HEIGHT);
		primaryStage.getIcons().add(new Image(new FileInputStream(new File("res/mainIcon.png"))));
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			logger.info("Goodbye.");
			System.exit(0);
		});

		logger.info(" - Graphical interface loaded.");

		logger.info("Welcome!");
	}

	/**
	 * Crée la zone de texte qui contient les informations du point sous le curseur.
	 * 
	 * @return Un TextArea paramétré pour contenir les informations du point sous le
	 *         curseur.
	 */
	private TextArea setAreaInfo() {
		final var areaInfo = new TextArea();
		areaInfo.setEditable(false);
		areaInfo.setPrefRowCount(TEXT_AREA_PREF_ROW_COUNT);
		return areaInfo;
	}

	/**
	 * Crée la vue du Panorama.
	 * 
	 * @param areaInfo La zone de texte montrant les informations du point sous le
	 *                 curseur.
	 * 
	 * @return Une ImageView contenant le Panorama.
	 */
	private ImageView setPanoView(final TextArea areaInfo) {
		final var panoView = new ImageView();
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
	 * @param panoView Le Pane dans lequel le curseur se déplace.
	 * 
	 * @param areaInfo La zone de texte à paramétrer.
	 */
	private void setMouseMove(final ImageView panoView, final TextArea areaInfo) {
		panoView.setOnMouseMoved(e -> {
			final var x = getSampledIndex(e.getX());
			final var y = getSampledIndex(e.getY());

			final var lat = toDegrees(COMPUTER_B.getPanorama().latitudeAt(x, y));
			final var lon = toDegrees(COMPUTER_B.getPanorama().longitudeAt(x, y));

			final var azimuth = COMPUTER_B.getParameters().panoramaParameters().azimuthForX(x);
			final var northOrSouth = lat >= 0.0 ? 'N' : 'S';
			final var eastOrWest = lon >= 0.0 ? 'E' : 'W';

			final var sb = new StringBuilder();
			sb.append(format("Position : %.4f°%c %.4f°%c%n", abs(lat), northOrSouth, abs(lon), eastOrWest));
			sb.append(format("Distance : %.1f km%n", COMPUTER_B.getPanorama().distanceAt(x, y) / M_PER_KM));
			sb.append(format("Altitude : %d m%n", (int) COMPUTER_B.getPanorama().elevationAt(x, y)));
			sb.append(format("Azimut : %.1f° (%s)  Élévation : %.1f°", toDegrees(azimuth),
					toOctantString(azimuth, "N", "E", "S", "W"),
					toDegrees(COMPUTER_B.getParameters().panoramaParameters().altitudeForY(y))));
			areaInfo.setText(sb.toString());
		});
	}

	/**
	 * Paramétrise les actions à entreprendre lorsque l'utilisateur clique dans
	 * l'ImageView du Panorama.
	 * 
	 * @param panoView Le Pane dans lequel la souris clique.
	 */
	private void setMouseClick(final ImageView panoView) {
		panoView.setOnMouseClicked(e -> {
			final var x = getSampledIndex(e.getX());
			final var y = getSampledIndex(e.getY());
			if (e.getButton() == MouseButton.PRIMARY) {
				final var lat = toDegrees(COMPUTER_B.getPanorama().latitudeAt(x, y));
				final var lon = toDegrees(COMPUTER_B.getPanorama().longitudeAt(x, y));
				final var qy = format((Locale) null, "mlat=%.4f&mlon=%.4f", lat, lon);
				final var fg = format((Locale) null, "map=15/%.4f/%.4f", lat, lon);
				try {
					final var osmURI = new URI("http", "www.openstreetmap.org", "/", qy, fg);
					logger.info("Opening {}", osmURI);
					openBrowser(osmURI);
				} catch (final URISyntaxException ex) {
					logger.error("Could not parse URI.", ex);
				} catch (final IOException ey) {
					logger.error("Could not get to browser.", ey);
				}
			} else if (e.getButton() == MouseButton.SECONDARY) {
				final var p = COMPUTER_B.getPanorama();
				PARAMETERS_B.observerLongitudeProperty().set((int) (10_000 * toDegrees(p.longitudeAt(x, y))));
				PARAMETERS_B.observerLatitudeProperty().set((int) (10_000 * toDegrees(p.latitudeAt(x, y))));
				PARAMETERS_B.observerElevationProperty().set((int) p.elevationAt(x, y) + 20);
				PARAMETERS_B.centerAzimuthProperty().set((int) toDegrees(Azimuth
						.canonicalize(COMPUTER_B.getParameters().panoramaParameters().azimuthForX(x) + Math.PI)));
			}
		});
	}

	private void openBrowser(final URI osmURI) throws IOException {
		final var os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") >= 0 || os.indexOf("win") >= 0) {
			Desktop.getDesktop().browse(osmURI);
		} else if (os.indexOf("nux") >= 0 || os.indexOf("nix") >= 0) {
			final var rt = Runtime.getRuntime();
			final var browsers = new String[] { "firefox", "google-chrome", "mozilla", "epiphany", "konqueror",
					"netscape", "opera", "links", "lynx" };
			final var cmd = new StringBuilder();
			for (var i = 0; i < browsers.length; i++) {
				if (i == 0) {
					cmd.append(String.format("%s \"%s\"", browsers[i], osmURI));
				} else {
					cmd.append(String.format(" || %s \"%s\"", browsers[i], osmURI));
				}
			}
			rt.exec(new String[] { "sh", "-c", cmd.toString() });
		} else {
			logger.warn("Could not open broswer on this platform.");
		}
	}

	/**
	 * Retourne l'index en prenant en compte le suréchantillonage.
	 * 
	 * @param index L'index de base.
	 * 
	 * @return l'index prenant en compte le suréchantillonage.
	 */
	private int getSampledIndex(final double index) {
		return (int) Math.scalb(index, COMPUTER_B.getParameters().superSamplingExponent());
	}

	/**
	 * Paramétrise le Pane contenant les étiquettes des sommets.
	 * 
	 * @return le Pane contenant les étiquettes des sommets.
	 */
	private Pane setLabelsPane() {
		final var labelsPane = new Pane();
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
	private Text setUpdateText(final String text) {
		final var updateText = new Text(text);
		updateText.setFont(new Font(UDPATE_TEXT_FONT_SIZE));
		updateText.setTextAlignment(TextAlignment.CENTER);
		return updateText;
	}

	/**
	 * Paramétrise le Pane contenant le texte de miste à jour et son interactivité.
	 * 
	 * @param updateText Le texte de mise à jour.
	 * 
	 * @return le Pane contenant le texte de miste à jour et son interactivité.
	 */
	private StackPane setUpdateNotice() {
		final var updateText = setUpdateText(
				"Les paramètres du panorama ont changé.\nCliquez ici pour mettre le dessin à jour.");

		final var panoCompute = "Le panorama est en cours de calcul...";
		final var panoCreation = "Le panorama est en cours de création...";
		final var updatingText = setUpdateText(panoCompute);
		final var updatingProcess = new ProgressBar();
		updatingProcess.setPrefWidth(500);
		updatingProcess.setPrefHeight(25);
		updatingProcess.progressProperty().bind(COMPUTER_B.statusProperty());
		COMPUTER_B.statusProperty().addListener((p, o, n) -> {
			if (1 - o.doubleValue() < 1e-10 && updatingText.getText().equals(panoCompute)) {
				updatingText.setText(panoCreation);
			} else if (1 - o.doubleValue() < 1e-10 && updatingText.getText().equals(panoCreation)) {
				updatingText.setText(panoCompute);
			}
		});
		final var updatingGrid = new GridPane();
		updatingGrid.addRow(0, updatingText);
		updatingGrid.addRow(1, updatingProcess);
		updatingGrid.setAlignment(Pos.CENTER);
		updatingGrid.setVgap(10);
		GridPane.setHalignment(updatingProcess, HPos.CENTER);
		GridPane.setHalignment(updatingText, HPos.CENTER);

		final var updateNotice = new StackPane(updateText);
		updateNotice.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		updateNotice.setOpacity(OPACITY_UPDATE_NOTICE);
		updateNotice.visibleProperty().bind(PARAMETERS_B.parametersProperty()
				.isNotEqualTo(COMPUTER_B.parametersProperty()).or(COMPUTER_B.imageProperty().isNull()));
		updateNotice.visibleProperty().addListener(e -> {
			if (!updateNotice.isVisible()) {
				updateNotice.getChildren().setAll(updateText);
			}
		});
		COMPUTER_B.parametersProperty().addListener((p, o, n) -> {
			if (!updateNotice.getChildren().contains(updatingGrid)) {
				updateNotice.getChildren().setAll(updatingGrid);
			}
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
	 * @param areaInfo La zone de texte contenant les informations du point se
	 *                 situant sous le pointeur de la souris.
	 * 
	 * @return la grille des paramètres au bas de la fenêtre principale.
	 */
	private GridPane setParamsGrid(final TextArea areaInfo) {

		final var paramsGrid = new GridPane();

		final var labelsAndField = new ArrayList<Node>();
		labelsAndField.addAll(setLabelAndField("Latitude (°)", OBSERVER_LATITUDE, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
		labelsAndField
				.addAll(setLabelAndField("Longitude (°)", OBSERVER_LONGITUDE, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
		labelsAndField.addAll(setLabelAndField("Altitude (m)", OBSERVER_ELEVATION, MIDI_COL_COUNT, NO_DECIMAL_NUMBER));
		labelsAndField.addAll(setLabelAndField("Azimut (°)", CENTER_AZIMUTH, MINI_COL_COUNT, 0));
		labelsAndField.addAll(setLabelAndField("Angle de vue (°)", HORIZONTAL_FIELD_OF_VIEW, MINI_COL_COUNT, 0));
		labelsAndField.addAll(setLabelAndField("Visibilité (km)", MAX_DISTANCE, MINI_COL_COUNT, 0));
		labelsAndField.addAll(setLabelAndField("Largeur (px)", WIDTH, MIDI_COL_COUNT, 0));
		labelsAndField.addAll(setLabelAndField("Hauteur (px)", HEIGHT, MIDI_COL_COUNT, 0));
		labelsAndField.addAll(setSuperSamplingOption());

		for (var i = 0; i < labelsAndField.size(); ++i) {
			paramsGrid.add(labelsAndField.get(i), i % 6, i / 6);
		}

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
		final var label = new Label("Suréchantillonage :");
		final var field = new ChoiceBox<Integer>();
		field.getItems().addAll(0, 1, 2);
		final var supFor = new LabeledListStringConverter("non", "2×", "4×");
		field.valueProperty().bindBidirectional(PARAMETERS_B.superSamplingExponentProperty());
		field.setConverter(supFor);
		label.setAlignment(Pos.CENTER_RIGHT);
		return Arrays.asList(label, field);
	}

	/**
	 * Paramétrise les options concernant les différents paramètres du Panorama.
	 * 
	 * @param name            Le nom de l'option.
	 * @param uP              Le paramètre utilisateur concerné.
	 * @param prefColumnCount Le nombre de colonnes de texte dans le Field.
	 * @param decimals        Le nombre de chiffres après la virgule désiré.
	 * 
	 * @return Le Label et le TextField correspondant aux paramètres donnés sous
	 *         forme de tableau.
	 */
	private Collection<? extends Control> setLabelAndField(final String name, final UserParameter uP,
			final int prefColumnCount, final int decimals) {
		final var label = new Label(name + " :");
		GridPane.setHalignment(label, HPos.RIGHT);
		final var field = new TextField();
		final var formatter = new TextFormatter<Integer>(new FixedPointStringConverter(decimals));
		if (uP != null) {
			formatter.valueProperty().bindBidirectional(PARAMETERS_B.getProperty(uP));
		}
		field.setTextFormatter(formatter);
		if (uP != null) {
			field.setAlignment(Pos.CENTER_RIGHT);
		} else {
			field.setAlignment(Pos.CENTER_LEFT);
		}
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
	private MenuBar setMenuBar(final StackPane panoGroup) {
		final var menuBar = new MenuBar();
		final var menuFile = setMenuFile(panoGroup);
		final var menuParameters = setMenuParameters();
		final var menuHelp = setMenuHelp();
		menuBar.getMenus().addAll(menuFile, menuParameters, menuHelp);
		return menuBar;
	}

	private Menu setMenuFile(final StackPane panoGroup) {
		final var menuFile = new Menu("Fichier");
		final var refresh = new MenuItem("Actualiser");
		refresh.setOnAction(e -> {
			COMPUTER_B.setParameters(null);
			COMPUTER_B.setParameters(PARAMETERS_B.parametersProperty().get());
		});
		final var save = new MenuItem(SAVE_LABEL);
		save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		save.setOnAction(e -> openSaveWindow(panoGroup));
		final var load = new MenuItem("Charger");
		load.setOnAction(e -> openLoadWindow());
		load.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
		final var exit = new MenuItem("Quitter");
		exit.setOnAction(e -> {
			logger.info("Goodbye.");
			System.exit(0);
		});
		exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));

		try {
			refresh.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/refresh.png")))));
			save.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/save.png")))));
			load.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/load.png")))));
			exit.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/close.png")))));
		} catch (final FileNotFoundException e1) {
			logger.error(e1.getMessage(), e1);
		}

		menuFile.getItems().addAll(refresh, save, load, new SeparatorMenuItem(), exit);
		return menuFile;
	}

	private void openSaveWindow(final StackPane panoGroup) {
		if (COMPUTER_B.getImage() == null) {
			return;
		}

		final var saveStage = new Stage();
		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var queryL = new Label("Veuillez entrer le nom de la sauvegarde :");
		final var queryF = new TextField();
		queryF.setPromptText("Exemple : mon_super_panorama");

		final var saveButton = new Button(SAVE_LABEL);
		saveButton.setOnAction(f -> {
			final var imgFile = new File("img/" + queryF.getText() + ".png");
			final var saveFile = new File(SAVE_PATH + queryF.getText() + ".png");
			final var save = (Runnable) () -> saveImageAndParameters(saveStage, queryF, panoGroup);
			if (imgFile.exists() || saveFile.exists()) {
				alreadyExistsPrompt(queryF.getText(), save);
			} else {
				save.run();
			}
		});

		final var quitButton = new Button(CANCEL_LABEL);
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
		saveStage.setWidth(300);
		try {
			saveStage.getIcons().add(new Image(new FileInputStream(new File("res/save.png"))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		saveStage.show();
	}

	private void saveImageAndParameters(final Stage saveStage, final TextField queryF, final StackPane panoGroup) {
		final var imgFolder = new File("img/");
		final var saveFolder = new File(SAVE_PATH);
		if (!imgFolder.exists()) {
			imgFolder.mkdirs();
		}
		if (!saveFolder.exists()) {
			saveFolder.mkdirs();
		}
		final var imgFile = new File("img/" + queryF.getText() + ".png");
		final var saveFile = new File(SAVE_PATH + queryF.getText() + ".ser");

		if (queryF.getText() != null && !queryF.getText().isEmpty()) {
			try {
				final var image = new WritableImage((int) panoGroup.getWidth(), (int) panoGroup.getHeight());
				panoGroup.snapshot(null, image);
				write(fromFXImage(image, null), "png", imgFile);
				logger.info("Image saved in:       {}", imgFile.getPath());
			} catch (final IOException e) {
				logger.error(e.getMessage(), e);
			}
			try (final var out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
				out.writeObject(COMPUTER_B.getParameters());
				logger.info("Parameters saved in:  {}", saveFile.getPath());
			} catch (final IOException e) {
				logger.error(e.getMessage(), e);
			}
			saveStage.close();
		}
	}

	private void alreadyExistsPrompt(final String name, final Runnable action) {
		final var promptStage = new Stage();
		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var queryL = new Label("Le fichier " + name + " existe déjà.\nQue souhaitez-vous faire ?");

		final var saveButton = new Button("Continuer");
		saveButton.setOnAction(f -> {
			promptStage.close();
			action.run();
		});

		final var quitButton = new Button(CANCEL_LABEL);
		quitButton.setOnAction(f -> promptStage.close());
		GridPane.setHalignment(quitButton, HPos.RIGHT);

		grid.add(queryL, 0, 0, 2, 1);
		grid.add(saveButton, 0, 1);
		grid.add(quitButton, 1, 1);

		grid.setVgap(BOTTOM_GRID_VGAP);
		grid.setHgap(BOTTOM_GRID_HGAP);
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(BOTTOM_GRID_PADDING);

		promptStage.setTitle("Overwrite file?");
		promptStage.setScene(scene);
		promptStage.setResizable(false);
		promptStage.setAlwaysOnTop(true);
		promptStage.setWidth(250);
		promptStage.show();

	}

	private void openLoadWindow() {
		final var loadStage = new Stage();

		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var saveFolder = new File(SAVE_PATH);
		if (!saveFolder.exists()) {
			return;
		}

		final var files = saveFolder.listFiles();

		final var choices = new ChoiceBox<File>();
		GridPane.setHalignment(choices, HPos.CENTER);
		choices.getItems().addAll(files);
		final var loadConv = new FileStringConverter(saveFolder.getPath(), 3);
		choices.setConverter(loadConv);

		final var parametersInfo = setAreaInfo();
		parametersInfo.setPrefWidth(280);
		parametersInfo.setPrefHeight(120);

		choices.setOnAction(e -> parametersInfo.setText(loadParameters(choices).toString()));

		final var loadButton = new Button("Load");

		loadButton.setOnAction(e -> {
			final var p = loadParameters(choices);
			PARAMETERS_B.observerLongitudeProperty().set(p.observerLongitude());
			PARAMETERS_B.observerLatitudeProperty().set(p.observerLatitude());
			PARAMETERS_B.observerElevationProperty().set(p.observerElevation());
			PARAMETERS_B.centerAzimuthProperty().set(p.centerAzimuth());
			PARAMETERS_B.maxDistanceProperty().set(p.maxDistance());
			PARAMETERS_B.horizontalFieldOfViewProperty().set(p.horizontalFieldOfView());
			PARAMETERS_B.widthProperty().set(p.width());
			PARAMETERS_B.heightProperty().set(p.height());
			PARAMETERS_B.superSamplingExponentProperty().set(p.superSamplingExponent());
			loadStage.close();
		});

		final var quitButton = new Button(CANCEL_LABEL);
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
		try {
			loadStage.getIcons().add(new Image(new FileInputStream(new File("res/load.png"))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		loadStage.show();

	}

	private PanoramaUserParameters loadParameters(final ChoiceBox<File> choices) {
		final var load = choices.getValue();
		try (final var in = new ObjectInputStream(new FileInputStream(load))) {
			return (PanoramaUserParameters) in.readObject();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private Menu setMenuParameters() {
		final var menuParameters = new Menu("Paramètres");
		final var addLabel = new MenuItem("Ajouter lieu");
		addLabel.setOnAction(e -> openAddPlaceWindow());
		final var changeCem = new MenuItem("Changer de MNT");
		changeCem.setOnAction(e -> changeCem());
		final var autoAltitude = setAutoAltitude();
		final var hideNonSummits = new RadioMenuItem("Masquer les étiquettes supplémentaires");
		COMPUTER_B.hideNonSummitsProperty().bind(hideNonSummits.selectedProperty());
		final var lightweight = new RadioMenuItem("Graphismes allégés");
		COMPUTER_B.slopeNecessaryProperty().bind(lightweight.selectedProperty().not());
		menuParameters.getItems().addAll(addLabel, changeCem, new SeparatorMenuItem(), autoAltitude, hideNonSummits,
				lightweight);
		try {
			addLabel.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/globe.png")))));
			changeCem.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/dem.png")))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return menuParameters;
	}

	private void configueAndSpawnStage(final Stage placeStage, final GridPane grid, final Scene scene,
			final String title, final String iconFilepath) {
		grid.setVgap(BOTTOM_GRID_VGAP);
		grid.setHgap(BOTTOM_GRID_HGAP);
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(BOTTOM_GRID_PADDING);

		placeStage.setTitle(title);
		placeStage.setScene(scene);
		placeStage.setResizable(false);
		placeStage.setAlwaysOnTop(true);
		try {
			placeStage.getIcons().add(new Image(new FileInputStream(new File(iconFilepath))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		placeStage.show();
	}

	private void openAddPlaceWindow() {
		final var placeStage = new Stage();
		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var mainRequest = new Label("Veuillez entrer les données du point :");
		final var labelsAndField = new ArrayList<Control>();
		final var nameL = new Label("Nom :");
		GridPane.setHalignment(nameL, HPos.RIGHT);
		labelsAndField.add(nameL);
		TextField nameF = new TextField();
		nameF.setPromptText("Fribourg");
		labelsAndField.add(nameF);
		labelsAndField.addAll(setLabelAndField("Latitude (°)", null, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
		((TextField) labelsAndField.get(labelsAndField.size() - 1)).setPromptText("46.8062");
		labelsAndField.addAll(setLabelAndField("Longitude (°)", null, MAXI_COL_COUNT, MAX_DECIMAL_NUMBER));
		((TextField) labelsAndField.get(labelsAndField.size() - 1)).setPromptText("7.1628");

		final var sliderName = new Label("Priorité :");
		GridPane.setHalignment(sliderName, HPos.RIGHT);
		labelsAndField.add(sliderName);
		final var slider = new Slider();
		slider.setMin(-Labelizable.PRIORITY_RANGE);
		slider.setMax(Labelizable.PRIORITY_RANGE);
		slider.setValue(Labelizable.PRIORITY_RANGE / 2d);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(Labelizable.PRIORITY_RANGE);
		slider.setMinorTickCount(Labelizable.PRIORITY_RANGE - 1);
		slider.valueProperty().addListener((p, o, n) -> slider.setValue(n.intValue()));
		labelsAndField.add(slider);

		final var infoPriority = new Label("Les sommets ont une priorité de 0.");
		GridPane.setHalignment(infoPriority, HPos.CENTER);

		final var saveButton = new Button(SAVE_LABEL);
		saveButton.setOnAction(f -> {
			final var plcFile = new File("plc/" + nameF.getText() + ".ser");
			final var save = (Runnable) () -> savePlace(placeStage, nameF, (TextField) labelsAndField.get(3),
					(TextField) labelsAndField.get(5), slider);
			if (plcFile.exists()) {
				alreadyExistsPrompt(nameF.getText(), save);
			} else {
				save.run();
			}
		});

		final var quitButton = new Button(CANCEL_LABEL);
		quitButton.setOnAction(f -> placeStage.close());
		GridPane.setHalignment(quitButton, HPos.RIGHT);

		grid.add(mainRequest, 0, 0, 2, 1);
		for (int i = 0; i < labelsAndField.size(); ++i) {
			grid.add(labelsAndField.get(i), i % 2, 1 + i / 2);
		}
		grid.add(infoPriority, 0, 5, 2, 1);
		grid.add(saveButton, 0, 6);
		grid.add(quitButton, 1, 6);

		configueAndSpawnStage(placeStage, grid, scene, "Add place", "res/globe.png");
	}

	private void savePlace(final Stage placeStage, final TextField name, final TextField lat, final TextField lon,
			final Slider slider) {
		final var plcFolder = new File("plc/");
		if (!plcFolder.exists()) {
			plcFolder.mkdirs();
		}
		final var plcFile = new File("plc/" + name.getText() + ".ser");
		if (name.getText() != null && !name.getText().isEmpty() && lat.getText() != null && !lat.getText().isEmpty()
				&& lon.getText() != null && !lon.getText().isEmpty()) {
			try (final var out = new ObjectOutputStream(new FileOutputStream(plcFile))) {
				final var position = new GeoPoint(Math.toRadians(Double.parseDouble(lon.getText())),
						Math.toRadians(Double.parseDouble(lat.getText())));
				out.writeObject(
						new Place(name.getText(), position, (int) CEM.elevationAt(position), (int) slider.getValue()));
				logger.info("Label saved in:       {}", plcFile.getPath());
			} catch (final IOException e) {
				logger.error(e.getMessage(), e);
			}
			placeStage.close();
		}
	}

	private void changeCem() {
		final var placeStage = new Stage();
		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var mainRequest = new Label("Choisissez le MNT désiré :");

		final var group = new ToggleGroup();
		final var superHGT = new RadioButton("Option standard");
		superHGT.setUserData(SuperHgtDiscreteElevationModel.FULL);
		superHGT.setToggleGroup(group);
		superHGT.setSelected(false);
		final var hilbertHGT = new RadioButton("Option Hilbert");
		hilbertHGT.setUserData(new HilbertDiscreteElevationModel(0, 0));
		hilbertHGT.setToggleGroup(group);
		hilbertHGT.setSelected(false);

		group.selectedToggleProperty().addListener((p, o, n) -> {
			if (group.getSelectedToggle() != null) {
				COMPUTER_B.cemProperty().set(
						new ContinuousElevationModel((DiscreteElevationModel) group.getSelectedToggle().getUserData()));
				logger.info("DEM changed.");
			}
		});

		final var okButton = new Button("OK");
		okButton.setOnAction(e -> placeStage.close());
		GridPane.setHalignment(okButton, HPos.CENTER);

		grid.add(mainRequest, 0, 0);
		grid.add(superHGT, 0, 1);
		grid.add(hilbertHGT, 0, 2);
		grid.add(okButton, 0, 3);

		configueAndSpawnStage(placeStage, grid, scene, "Change DEM", "res/dem.png");
	}

	private RadioMenuItem setAutoAltitude() {
		final var autoAltitude = new RadioMenuItem("Auto-altitude");
		final ChangeListener<? super Integer> listener = (p, o,
				n) -> runLater(() -> PARAMETERS_B.observerElevationProperty()
						.set((int) CEM.elevationAt(
								PARAMETERS_B.parametersProperty().get().panoramaDisplayParameters().observerPosition())
								+ 2));
		autoAltitude.setOnAction(e -> {
			if (autoAltitude.isSelected()) {
				PARAMETERS_B.observerLongitudeProperty().addListener(listener);
				PARAMETERS_B.observerLatitudeProperty().addListener(listener);
			} else {
				PARAMETERS_B.observerLongitudeProperty().removeListener(listener);
				PARAMETERS_B.observerLatitudeProperty().removeListener(listener);
			}
		});
		return autoAltitude;
	}

	private Menu setMenuHelp() {
		final var menuHelp = new Menu("Aide");
		final var about = new MenuItem("À propos");
		about.setOnAction(e -> setMenuAbout());

		try {
			about.setGraphic(new ImageView(new Image(new FileInputStream(new File("res/info.png")))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}

		menuHelp.getItems().addAll(about);
		return menuHelp;
	}

	private void setMenuAbout() {
		final var aboutStage = new Stage();
		final var grid = new GridPane();
		final var scene = new Scene(grid);

		final var mainRequest = new Label("Version 1.0.0\nProgrammé par R. Mamié.\n2017-2024, EPFL.\nrobin@mamie.one");
		mainRequest.setTextAlignment(TextAlignment.CENTER);

		grid.add(mainRequest, 0, 0);

		grid.setVgap(BOTTOM_GRID_VGAP);
		grid.setHgap(BOTTOM_GRID_HGAP);
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(BOTTOM_GRID_PADDING);

		aboutStage.setTitle("About");
		aboutStage.setScene(scene);
		aboutStage.setResizable(false);
		aboutStage.setAlwaysOnTop(true);
		try {
			aboutStage.getIcons().add(new Image(new FileInputStream(new File("res/info.png"))));
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		aboutStage.show();
	}

	/**
	 * Paramétrise la racine de la fenêtre.
	 * 
	 * @param panoPane   Le Pane composé du Panorama, des Labels et de la notice
	 *                   d'update.
	 * @param paramsGrid Le Pane contenant toutes les options paramétrables du
	 *                   Panorama ainsi que le texte contenant les informations du
	 *                   point situé sous le curseur.
	 * 
	 * @return le BorderPane à la base de l'interface graphique.
	 */
	private BorderPane setRoot(final StackPane panoPane, final GridPane paramsGrid, final MenuBar menuBar) {
		final var root = new BorderPane();
		root.setCenter(panoPane);
		root.setBottom(paramsGrid);
		root.setTop(menuBar);
		return root;
	}

}