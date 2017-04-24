package ch.epfl.alpano.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.util.StringConverter;

public final class FixedPointStringConverter extends StringConverter<Integer> {

    private final int decimals;

    public FixedPointStringConverter(int decimals) {
        this.decimals = decimals;
    }

    @Override
    public Integer fromString(String string) {
        return new BigDecimal(string).movePointRight(decimals)
                .setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    @Override
    public String toString(Integer object) {
        return new BigDecimal(object).movePointLeft(decimals).toString();
    }

}
