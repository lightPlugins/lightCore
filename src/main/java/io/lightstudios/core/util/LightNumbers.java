package io.lightstudios.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class LightNumbers {

    public static BigDecimal formatBigDecimal(BigDecimal bd) {
        return bd.setScale(2, RoundingMode.HALF_UP);
    }

    public static double formatDouble(BigDecimal bd) {
        return formatBigDecimal(bd).doubleValue();
    }

    public static BigDecimal convertToBigDecimal(double d) {
        return formatBigDecimal(BigDecimal.valueOf(d));
    }

    public static String formatForMessages(BigDecimal number) {
        Locale locale = Locale.getDefault();
        NumberFormat formatter = NumberFormat.getInstance(locale);
        return formatter.format(number);
    }

    public static String formatForMessages(BigDecimal number, int decimalPlaces) {
        Locale locale = Locale.getDefault();
        NumberFormat formatter = NumberFormat.getInstance(locale);
        formatter.setMaximumFractionDigits(decimalPlaces);
        formatter.setMinimumFractionDigits(decimalPlaces);
        formatter.setGroupingUsed(true);
        number = number.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return formatter.format(number);
    }

    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShortNumber(String s) {
        return s.endsWith("k") || s.endsWith("m") || s.endsWith("b") || s.endsWith("t");
    }

    public static BigDecimal parseMoney(String amount) {
        try {
            if (amount.matches("^\\d+$")) {
                return formatBigDecimal(new BigDecimal(amount));
            }

            BigDecimal multiplier = BigDecimal.ONE;
            if (amount.endsWith("k")) {
                multiplier = new BigDecimal("1000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("m")) {
                multiplier = new BigDecimal("1000000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("b")) {
                multiplier = new BigDecimal("1000000000");
                amount = amount.substring(0, amount.length() - 1);
            } else if (amount.endsWith("t")) {
                multiplier = new BigDecimal("1000000000000");
                amount = amount.substring(0, amount.length() - 1);
            }
            return formatBigDecimal(new BigDecimal(amount).multiply(multiplier));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isPositiveNumber(double d) {
        return d > 0;
    }
    public static boolean isNumberInRange(double d, double min, double max) {
        return d >= min && d <= max;
    }

    public static boolean isValidInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
