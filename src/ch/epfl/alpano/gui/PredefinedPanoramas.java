package ch.epfl.alpano.gui;

/**
 * Interface fournissant quelques paramètres utilisateurs de Panoramas
 * prédifinis.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface PredefinedPanoramas {

    /**
     * Quelques paramètres par défaut des panoramas définis.
     */
    int MAX_DISTANCE = 300, WIDTH = 2500, HEIGHT = 800, SUPER_SAMPLING_EX = 0;

    /**
     * Crée les paramètres utilisateurs du Panorama du Niesen.
     * 
     * @return Les paramètres utilisateurs du Panorama du Niesen.
     */
    static PanoramaUserParameters niesen() {
        return new PanoramaUserParameters(7_6500, 46_7300, 600, 180, 110,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }

    /**
     * Crée les paramètres utilisateurs du Panorama des Alpes vues du Jura.
     * 
     * @return Les paramètres utilisateurs du Panorama des Alpes vues du Jura.
     */
    static PanoramaUserParameters alpsFromJura() {
        return new PanoramaUserParameters(6_8087, 47_0085, 1380, 162, 27,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }

    /**
     * Crée les paramètres utilisateurs du Panorama des Alpes vues du Mont
     * Racine.
     * 
     * @return Les paramètres utilisateurs du Panorama des Alpes vues du Mont
     *         Racine.
     */
    static PanoramaUserParameters racine() {
        return new PanoramaUserParameters(6_8200, 47_0200, 1500, 135, 45,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }

    /**
     * Crée les paramètres utilisateurs du Panorama du Cervin vu du
     * Finsteraarhorn.
     * 
     * @return Les paramètres utilisateurs du Panorama du Cervin vu du
     *         Finsteraarhorn.
     */
    static PanoramaUserParameters finsteraarhorn() {
        return new PanoramaUserParameters(8_1260, 46_5374, 4300, 205, 20,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }

    /**
     * Crée les paramètres utilisateurs du Panorama des Alpes vues de la Tour de
     * Sauvabelin.
     * 
     * @return Les paramètres utilisateurs du Panorama des Alpes vues de la Tour
     *         de Sauvabelin.
     */
    static PanoramaUserParameters sauvabelin() {
        return new PanoramaUserParameters(6_6385, 46_5353, 700, 135, 100,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }

    /**
     * Crée les paramètres utilisateurs du Panorama des Alpes vues de la plage
     * du Pélican.
     * 
     * @return Les paramètres utilisateurs du Panorama des Alpes vues de la
     *         plage du Pélican.
     */
    static PanoramaUserParameters pelican() {
        return new PanoramaUserParameters(6_5728, 46_5132, 380, 135, 60,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EX);
    }
}
