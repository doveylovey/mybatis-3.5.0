package org.study.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidationUtils {
    /**
     * integer (-MAX, MAX)
     */
    public final static String REGEX_INTEGER = "^[-\\+]?\\d+$";
    /**
     * integer [1, MAX)
     */
    public final static String REGEX_POSITIVE_INTEGER = "^\\+?[1-9]\\d*$";
    /**
     * integer (-MAX, -1]
     */
    public final static String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    /**
     * integer [0, MAX), only numeric
     */
    public final static String REGEX_NUMERIC = "^\\d+$";
    /**
     * decimal (-MAX, MAX)
     */
    public final static String REGEX_DECIMAL = "^[-\\+]?\\d+\\.\\d+$";
    /**
     * decimal (0.0, MAX)
     */
    public final static String REGEX_POSITIVE_DECIMAL = "^\\+?([1-9]+\\.\\d+|0\\.\\d*[1-9])$";
    /**
     * decimal (-MAX, -0.0)
     */
    public final static String REGEX_NEGATIVE_DECIMAL = "^-([1-9]+\\.\\d+|0\\.\\d*[1-9])$";
    /**
     * decimal + integer (-MAX, MAX)
     */
    public final static String REGEX_REAL_NUMBER = "^[-\\+]?(\\d+|\\d+\\.\\d+)$";
    /**
     * decimal + integer [0, MAX)
     */
    public final static String REGEX_NON_NEGATIVE_REAL_NUMBER = "^\\+?(\\d+|\\d+\\.\\d+)$";

    private static boolean isMatch(String regex, String original) {
        if (original == null || "".equals(original.trim())) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(original);
        return isNum.matches();
    }

    /**
     * 非负整数[0,MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isNumeric(String original) {
        return isMatch(REGEX_NUMERIC, original);
    }

    /**
     * 正整数[1,MAX)
     *
     * @param original
     * @return boolean
     * @Description: 是否为正整数
     */
    public static boolean isPositiveInteger(String original) {
        return isMatch(REGEX_POSITIVE_INTEGER, original);
    }

    /**
     * 负整数 (-MAX,-1]
     *
     * @param original
     * @return boolean
     */
    public static boolean isNegativeInteger(String original) {
        return isMatch(REGEX_NEGATIVE_INTEGER, original);
    }

    /**
     * 整数 (-MAX,MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isInteger(String original) {
        return isMatch(REGEX_INTEGER, original);
    }

    /**
     * 正小数 (0.0, MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isPositiveDecimal(String original) {
        return isMatch(REGEX_POSITIVE_DECIMAL, original);
    }

    /**
     * 负小数 (-MAX, -0.0)
     *
     * @param original
     * @return boolean
     */
    public static boolean isNegativeDecimal(String original) {
        return isMatch(REGEX_NEGATIVE_DECIMAL, original);
    }

    /**
     * 小数 (-MAX, MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isDecimal(String original) {
        return isMatch(REGEX_DECIMAL, original);
    }

    /**
     * 实数，包括所有的整数与小数 (-MAX, MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isRealNumber(String original) {
        return isMatch(REGEX_REAL_NUMBER, original);
    }

    /**
     * 非负实数 [0, MAX)
     *
     * @param original
     * @return boolean
     */
    public static boolean isNonNegativeRealNumber(String original) {
        return isMatch(REGEX_NON_NEGATIVE_REAL_NUMBER, original);
    }

    /**
     * 正实数
     *
     * @param original
     * @return boolean
     */
    public static boolean isPositiveRealNumber(String original) {
        return isPositiveDecimal(original) || isPositiveInteger(original);
    }
}
