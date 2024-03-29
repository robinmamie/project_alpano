package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.PanoramaComputer.INTERVAL;
import static ch.epfl.alpano.PanoramaComputer.rayToGroundDistance;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Integer.compare;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.round;
import static java.lang.Math.tan;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Labelizable;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Étiquette les sommets visibles sur les paramètres d'un Panorama donné.
 *
 * @author Robin Mamié
 */
public final class Labelizer {

	/**
	 * Rayon de définition d'un sommet.
	 */
	private static final int TOLERANCE = 200;

	/**
	 * Nombre de pixels requis au minimum pour tracer une ligne, i.e. une ligne
	 * ne sera jamais plus courte que ça.
	 */
	private static final int PIXELS_NEEDED = 20;

	/**
	 * Espace laissé entre les étiquettes et la ligne.
	 */
	private static final int PIXELS_ROOM = 2;

	/**
	 * Les étiquettes peuvent être dessinées à partir de ce pixel vertical.
	 * Avant, l'espace laissé n'est pas suffisant.
	 */
	private static final int PIXEL_THRESHOLD = 170;

	/**
	 * Angle du texte par rapport à l'horizontale.
	 */
	private static final int TEXT_ANGLE = -60;

	/**
	 * Index du tableau de la map pour l'index x.
	 */
	private static final int INDEX_X = 0;

	/**
	 * Index du tableau de la map pour l'index y.
	 */
	private static final int INDEX_Y = 1;

	/**
	 * MNT continu.
	 */
	private final ContinuousElevationModel cem;

	/**
	 * Liste des sommets initialisée dans le constructeur.
	 */
	private final List<Labelizable> labels;

	/**
	 * Table associative utilisée pour sauvegarder les coordonées d'un sommet
	 * sur l'image.
	 */
	private final Map<Labelizable, Integer[]> values = new HashMap<>();

	private final boolean hideNonSummits;

	/**
	 * Construit un Labelizer, qui servira à étiquetter les sommets visibles
	 * d'un Panorama.
	 * 
	 * @param cem     Un MNT continu.
	 * @param summits Une liste complètes de tous les sommets.
	 */
	public Labelizer(final ContinuousElevationModel cem, final List<Labelizable> summits,
			final boolean hideNonSummits) {
		this.cem = requireNonNull(cem);
		this.labels = unmodifiableList(new ArrayList<>(requireNonNull(summits)));
		this.hideNonSummits = hideNonSummits;
	}

	/**
	 * Calcule la distance entre l'observateur et le sommet.
	 * 
	 * @param s          Le sommet.
	 * @param parameters Les paramètres du Panorama.
	 * 
	 * @return La distance entre l'observateur et le sommet ou POSITIVE_INFINITY si
	 *         le sommet se trouve trop loin.
	 */
	private double distanceToSummit(final Labelizable s, final PanoramaParameters parameters) {
		final var distance = parameters.observerPosition().distanceTo(s.position());
		return distance > parameters.maxDistance() ? POSITIVE_INFINITY : distance;
	}

	/**
	 * Calcule l'azimuth vers le sommet depuis l'observateur.
	 * 
	 * @param s          Le sommet.
	 * @param parameters Les paramètres du Panorama.
	 * 
	 * @return L'azimuth vers le sommet depuis l'observateur ou POSITIVE_INFINITY si
	 *         le sommet n'apparaît pas dans cet angle de vue horizontal.
	 */
	private double azimuthToSummit(final Labelizable s, final PanoramaParameters parameters) {
		final var azimuth = parameters.observerPosition().azimuthTo(s.position());
		return abs(angularDistance(azimuth, parameters.centerAzimuth())) > parameters.horizontalFieldOfView() / 2.
				? POSITIVE_INFINITY
				: azimuth;
	}

	/**
	 * Calcule l'altitude vers le sommet depuis l'observateur.
	 * 
	 * @param parameters Les paramètres du Panorama.
	 * @param profile    Le profil altimétrique dirigé vers le sommet.
	 * @param distance   La distance entre l'observateur et le sommet.
	 * 
	 * @return L'altitude vers le sommet depuis l'observateur ou POSITIVE_INFINITY
	 *         si le sommet n'apparaît pas dans cet angle de vue vertical.
	 */
	private double altitudeToSummit(final PanoramaParameters parameters,
			final ElevationProfile profile, final double distance) {
		final var altitude = atan2(
				-rayToGroundDistance(profile, parameters.observerElevation(), 0).applyAsDouble(distance),
				distance);
		return abs(altitude) > parameters.verticalFieldOfView() / 2d ? POSITIVE_INFINITY : altitude;
	}

	/**
	 * Vérifie toutes les conditions nécessaires pour qu'un sommet soit visible sur
	 * le Panorama.
	 * 
	 * @param summit Un sommet.
	 * 
	 * @return vrai si le sommet est visible sur le Panorama.
	 */
	private boolean summitIsVisible(final Labelizable summit, final PanoramaParameters parameters) {

		final var distance = distanceToSummit(summit, parameters);
		if (distance == POSITIVE_INFINITY) {
			return false;
		}

		final var azimuth = azimuthToSummit(summit, parameters);
		if (azimuth == POSITIVE_INFINITY) {
			return false;
		}

		final var profile = new ElevationProfile(cem, parameters.observerPosition(), azimuth, distance);
		final var altitude = altitudeToSummit(parameters, profile, distance);
		if (altitude == POSITIVE_INFINITY) {
			return false;
		}

		final var distanceUntilGround = firstIntervalContainingRoot(
				rayToGroundDistance(profile, parameters.observerElevation(), tan(altitude)), 0, distance, INTERVAL);

		if (distanceUntilGround != POSITIVE_INFINITY && distanceUntilGround < distance - TOLERANCE) {
			return false;
		}

		values.put(summit, new Integer[] { (int) round(parameters.xForAzimuth(azimuth)),
				(int) round(parameters.yForAltitude(altitude)) });

		return true;
	}

	/**
	 * Calcule tous les sommets visibles depuis un position donnée et les trie.
	 * 
	 * @param parameters Les paramètres du Panorama en question.
	 * 
	 * @return La liste triée de tous les sommets visibles.
	 */
	private List<Labelizable> visibleSummits(final PanoramaParameters parameters) {
		final var visible = new ArrayList<Labelizable>();

		for (final Labelizable l : labels) {
			if (summitIsVisible(l, parameters) && (l.priority() == 0 || !hideNonSummits)) {
				visible.add(l);
			}
		}

		visible.sort((a, b) -> {
			final var typeOfLabel = compare(b.priority(), a.priority());
			if (typeOfLabel != 0) {
				return typeOfLabel;
			}
			final var higher = compare(values.get(a)[INDEX_Y], values.get(b)[INDEX_Y]);
			return higher == 0 ? compare(b.elevation(), a.elevation()) : higher;
		});

		return unmodifiableList(visible);
	}

	/**
	 * Étiquette le plus de sommets possibles avec leur nom et leur altitude en
	 * retournant une liste de nœuds JavaFX.
	 * 
	 * @param parameters Les paramètres du Panorama.
	 * 
	 * @return Une liste de nœuds JavaFX.
	 */
	public List<Node> labels(PanoramaParameters parameters) {

		final var visible = visibleSummits(parameters);
		final var nodes = new ArrayList<Node>();

		final var positions = new BitSet(parameters.width() + PIXELS_NEEDED);
		positions.set(0, PIXELS_NEEDED);
		positions.set(parameters.width(), parameters.width() + PIXELS_NEEDED);

		var labelPlace = Integer.MAX_VALUE;
		for (final Labelizable l : visible) {
			final var y = values.get(l)[INDEX_Y];
			if (y > PIXEL_THRESHOLD && y < labelPlace) {
				labelPlace = y - PIXELS_NEEDED - PIXELS_ROOM;
			}
		}

		for (final var l : visible) {
			final var x = values.get(l)[INDEX_X];
			final var y = values.get(l)[INDEX_Y];

			if (y > PIXEL_THRESHOLD && positions.get(x, x + PIXELS_NEEDED).isEmpty()) {
				positions.set(x, x + PIXELS_NEEDED);

				final var text = new Text(l.name() + " (" + l.elevation() + " m)");
				text.getTransforms().addAll(new Translate(x, labelPlace), new Rotate(TEXT_ANGLE, 0, 0));
				final var line = new Line(x, (double) labelPlace + PIXELS_ROOM, x, y);
				if (l.priority() > 0) {
					text.setFill(Color.RED);
				} else if (l.priority() < 0) {
					text.setFill(Color.BLUE);
				}
				nodes.addAll(Arrays.asList(text, line));
			}
		}
		return unmodifiableList(nodes);
	}

}
