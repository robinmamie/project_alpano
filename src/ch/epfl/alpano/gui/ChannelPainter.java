package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.floorMod;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Panorama;

@FunctionalInterface
public interface ChannelPainter {

    abstract float valueAt(int x, int y);

    static ChannelPainter maxDistanceToNeighbors(Panorama p) {
        return (x, y) -> max(
                max(p.distanceAt(x - 1, y, 0), p.distanceAt(x + 1, y, 0)),
                max(p.distanceAt(x, y - 1, 0), p.distanceAt(x, y + 1, 0)))
                - p.distanceAt(x, y);
    }

    default ChannelPainter add(float n) {
        return (x, y) -> valueAt(x, y) + n;
    }

    default ChannelPainter sub(float n) {
        return (x, y) -> valueAt(x, y) - n;
    }

    default ChannelPainter mul(float n) {
        return (x, y) -> valueAt(x, y) * n;
    }

    default ChannelPainter div(float n) {
        return (x, y) -> valueAt(x, y) / n;
    }

    default ChannelPainter map(DoubleUnaryOperator f) {
        return (x, y) -> (float) f.applyAsDouble(valueAt(x, y));
    }

    default ChannelPainter invert() {
        return (x, y) -> 1 - valueAt(x, y);
    }

    default ChannelPainter clamp() {
        return (x, y) -> max(0, min(valueAt(x, y), 1));
    }

    default ChannelPainter cycle() {
        return (x, y) -> (float) floorMod(valueAt(x, y), 1);
    }
}
