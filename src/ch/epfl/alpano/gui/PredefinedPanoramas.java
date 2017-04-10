package ch.epfl.alpano.gui;

public interface PredefinedPanoramas {
    int MAX_DISTANCE = 300;
    int WIDTH = 2500;
    int HEIGHT = 800;
    int SUPER_SAMPLING_EXPONENT = 0;

    static PanoramaUserParameters niesen() {
        return new PanoramaUserParameters(7_6500, 46_7300, 600, 180, 110,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }

    static PanoramaUserParameters alpsFromJura() {
        return new PanoramaUserParameters(6_8087, 47_0085, 1380, 162, 27,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }

    static PanoramaUserParameters racine() {
        return new PanoramaUserParameters(6_8200, 47_0200, 1500, 135, 45,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }

    static PanoramaUserParameters finsteraarhorn() {
        return new PanoramaUserParameters(8_1260, 46_5374, 4300, 205, 20,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }

    static PanoramaUserParameters sauvabelin() {
        return new PanoramaUserParameters(6_6385, 46_5353, 700, 135, 100,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }

    static PanoramaUserParameters pelican() {
        return new PanoramaUserParameters(6_5728, 46_5132, 380, 135, 60,
                MAX_DISTANCE, WIDTH, HEIGHT, SUPER_SAMPLING_EXPONENT);
    }
}
