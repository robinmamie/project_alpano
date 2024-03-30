package ch.epfl.alpano.gui;

/**
 * Interface fournissant quelques paramètres utilisateurs de Panoramas
 * prédifinis.
 *
 * @author Robin Mamié
 */
public enum PredefinedPanoramas {

	/**
	 * Crée les paramètres utilisateurs du Panorama du Niesen.
	 */
	NIESEN(7_6500, 46_7300, 600, 180, 110),

	/**
	 * Crée les paramètres utilisateurs du Panorama des Alpes vues du Jura.
	 */
	JURA(6_8087, 47_0085, 1380, 162, 27),

	/**
	 * Crée les paramètres utilisateurs du Panorama des Alpes vues du Mont
	 * Racine.
	 */
	RACINE(6_8200, 47_0200, 1500, 135, 45),

	/**
	 * Crée les paramètres utilisateurs du Panorama du Cervin vu du
	 * Finsteraarhorn.
	 */
	FINSTER(8_1260, 46_5374, 4300, 205, 20),

	/**
	 * Crée les paramètres utilisateurs du Panorama des Alpes vues de la Tour de
	 * Sauvabelin.
	 */
	SAUVABELIN(6_6385, 46_5353, 700, 135, 100),

	/**
	 * Crée les paramètres utilisateurs du Panorama des Alpes vues de la plage
	 * du Pélican.
	 */
	PELICAN(6_5728, 46_5132, 380, 135, 60);

	/**
	 * Quelques paramètres par défaut des panoramas définis.
	 */
	private static final int MAX_DISTANCE = 300;
	private static final int WIDTH = 2500;
	private static final int HEIGHT = 800;
	private static final int SUPER_SAMPLING_EX = 0;

	private final PanoramaUserParameters parameters;

	PredefinedPanoramas(final int longitude, final int latitude, final int elevation,
			final int centerAzimuth, final int horizontalFov) {
		this.parameters = new PanoramaUserParameters(longitude, latitude, elevation, centerAzimuth,
				horizontalFov, MAX_DISTANCE, new ImageParameters(WIDTH, HEIGHT, SUPER_SAMPLING_EX));
	}

	public PanoramaUserParameters parameters() {
		return parameters;
	}
}
