package ch.epfl.alpano.gui;

import javafx.scene.paint.Color;

@FunctionalInterface
public interface ImagePainter {

    abstract Color colorAt(int x, int y);

    static ImagePainter hsb(ChannelPainter h, ChannelPainter s,
            ChannelPainter b, ChannelPainter o) {
        return (x, y) -> Color.hsb(h.valueAt(x, y), s.valueAt(x, y),
                b.valueAt(x, y), o.valueAt(x, y));
    }

    static ImagePainter gray(ChannelPainter g, ChannelPainter o) {
        return (x, y) -> Color.gray(g.valueAt(x, y), o.valueAt(x, y));
    }
}
