package ch.epfl.alpano.gui;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;

import javafx.util.StringConverter;

/**
 * Convertit entre des nombres décimaux sous forme de chaînes de caractère et
 * des entiers.
 *
 * @author Robin Mamié
 */
public final class FixedPointStringConverter extends StringConverter<Integer> {

	/**
	 * Le nombre de décimales sauvegardées.
	 */
	private final int decimals;

	/**
	 * Construit un convertisseur entre nombres décimaux sous forme de chaînes
	 * de caractère et des entiers.
	 * 
	 * @param decimals La valeur décimale à laquelle il faut transformer les nombres
	 *                 à virgule sous forme de chaînes de caractères en entiers.
	 */
	public FixedPointStringConverter(final int decimals) {
		this.decimals = decimals;
	}

	@Override
	public Integer fromString(final String string) {
		return new BigDecimal(string).movePointRight(decimals).setScale(0, HALF_UP).intValueExact();
	}

	@Override
	public String toString(final Integer object) {
		return object == null ? ""
				: new BigDecimal(object).movePointLeft(decimals).stripTrailingZeros().toPlainString();
	}

}
