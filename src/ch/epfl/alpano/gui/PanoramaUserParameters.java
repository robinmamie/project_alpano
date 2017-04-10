package ch.epfl.alpano.gui;

import static java.lang.Math.toRadians;
import static ch.epfl.alpano.gui.UserParameter.*;

import java.util.EnumMap;
import java.util.Map;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaParameters;

public final class PanoramaUserParameters {

    private final Map<UserParameter, Integer> map;

    public PanoramaUserParameters(Map<UserParameter, Integer> map) {
        this.map = new EnumMap<>(map);
        int limit = (int) (1 + 170 * (width() - 1.) / horizontalFieldOfView());
        map.replaceAll((k, v) -> k.sanitize(v));
        map.put(HEIGHT, limit < height() ? limit : height());
    }

    @SuppressWarnings("serial")
    public PanoramaUserParameters(int observerLongitude, int observerLatitude,
            int observerElevation, int centerAzimuth, int horizontalFieldOfView,
            int maxDistance, int width, int height, int superSamplingExponent) {
        this(new EnumMap<UserParameter, Integer>(UserParameter.class) {
            {
                put(OBSERVER_LONGITUDE, observerLongitude);
                put(OBSERVER_LATITUDE, observerLatitude);
                put(OBSERVER_ELEVATION, observerElevation);
                put(CENTER_AZIMUTH, centerAzimuth);
                put(HORIZONTAL_FIELD_OF_VIEW, horizontalFieldOfView);
                put(MAX_DISTANCE, maxDistance);
                put(WIDTH, width);
                put(HEIGHT, height);
                put(SUPER_SAMPLING_EXPONENT, superSamplingExponent);
            }
        });
    }

    public int get(UserParameter parameter) {
        return map.get(parameter);
    }

    public int observerLongitude() {
        return get(OBSERVER_LONGITUDE);
    }

    public int observerLatitude() {
        return get(OBSERVER_LATITUDE);
    }

    public int observerElevation() {
        return get(OBSERVER_ELEVATION);
    }

    public int centerAzimuth() {
        return get(CENTER_AZIMUTH);
    }

    public int horizontalFieldOfView() {
        return get(HORIZONTAL_FIELD_OF_VIEW);
    }

    public int maxDistance() {
        return get(MAX_DISTANCE);
    }

    public int width() {
        return get(WIDTH);
    }

    public int height() {
        return get(HEIGHT);
    }

    public int superSamplingExponent() {
        return get(SUPER_SAMPLING_EXPONENT);
    }

    private PanoramaParameters panoramaParametersSet(int n) {
        return new PanoramaParameters(
                new GeoPoint(toRadians(observerLongitude() / 10_000.),
                        toRadians(observerLatitude() / 10_000.)),
                observerElevation(), toRadians(centerAzimuth()),
                toRadians(horizontalFieldOfView()), 1_000 * maxDistance(),
                width(), height());
    }

    public PanoramaParameters panoramaParameters() {
        return panoramaParametersSet(superSamplingExponent());
    }

    public PanoramaParameters panoramaDisplayParamters() {
        return panoramaParametersSet(0);
    }
}
