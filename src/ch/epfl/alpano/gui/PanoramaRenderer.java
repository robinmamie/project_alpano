package ch.epfl.alpano.gui;

import ch.epfl.alpano.Panorama;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public interface PanoramaRenderer {

    static Image renderPanorama(Panorama p, ImagePainter iP) {
        WritableImage i = new WritableImage(p.parameters().width(),
                p.parameters().height());
        PixelWriter pW = i.getPixelWriter();
        for (int y = 0; y < i.getHeight(); ++y)
            for (int x = 0; x < i.getWidth(); ++x)
                pW.setColor(x, y, iP.colorAt(x, y));
        return i;
    }
}
