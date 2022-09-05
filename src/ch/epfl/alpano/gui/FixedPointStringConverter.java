package ch.epfl.alpano.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.util.StringConverter;

/**
 * Class used to convert {@link Integer} into {@link String} and vice-versa
 */
public final class FixedPointStringConverter extends StringConverter<Integer> {

    private final int decimal;

    /**
     * Constructs a FixedPointStringConverter
     * 
     * @param decimal int that is used for the conversion
     */
    public FixedPointStringConverter(int decimal) {
        this.decimal = decimal;
    }

    @Override
    public String toString(Integer object) {
        if(object == null){
            return "0";
        }
        BigDecimal number = new BigDecimal(object).movePointLeft(decimal);
        return number.toPlainString();
    }

    @Override
    public Integer fromString(String string) {
        BigDecimal number = new BigDecimal(string)
                .setScale(decimal, RoundingMode.HALF_UP)
                .movePointRight(decimal);
        return number.intValueExact();
    }

}
