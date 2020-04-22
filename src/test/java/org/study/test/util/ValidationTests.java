package org.study.test.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationTests {
    public static String checkPassword(String passwordStr) {
        // 参考 https://www.cnblogs.com/ggjucheng/p/3423731.html
        // Z-字母、S-数字、T-特殊字符
        String regexZ = "[A-Za-z]+";
        String regexS = "^\\d+$";
        String regexT = "[~!@#$%^&*.]+";
        String regexZT = "[a-zA-Z~!@#$%^&*.]+";
        String regexZS = "[0-9A-Za-z]+";
        String regexST = "[\\d~!@#$%^&*.]*";
        String regexZST = "[\\da-zA-Z~!@#$%^&*.]+";

        if (passwordStr == null || "".equals(passwordStr)) {
            return "密码不能为空!";
        }
        if (passwordStr.length() < 6 || passwordStr.length() > 12) {
            return "密码为 6-12 位字母、数字或英文字符!";
        }
        if (passwordStr.matches(regexZ)) {
            return "纯字母，弱";
        }
        if (passwordStr.matches(regexS)) {
            return "纯数字，弱";
        }
        if (passwordStr.matches(regexT)) {
            return "纯字符，弱";
        }
        if (passwordStr.matches(regexZT)) {
            return "字母字符，中";
        }
        if (passwordStr.matches(regexZS)) {
            return "字母数字，中";
        }
        if (passwordStr.matches(regexST)) {
            return "数字字符，中";
        }
        if (passwordStr.matches(regexZST)) {
            return "强";
        }
        return "不知道是啥";
    }

    public static void main(String[] args) {
        System.out.println(checkPassword("qqqqqq"));
        System.out.println(checkPassword("111111"));
        System.out.println(checkPassword("......"));

        System.out.println(checkPassword("qqq..."));
        System.out.println(checkPassword("111qqq"));
        System.out.println(checkPassword("111..."));

        System.out.println(checkPassword("11qq.."));
        System.out.println(checkPassword("11..qq"));
        System.out.println(checkPassword("qq..11"));
        System.out.println(checkPassword("qq11.."));
        System.out.println(checkPassword("..11qq"));
        System.out.println(checkPassword("..qq11"));
    }

    /**
     * 判断是不是数字
     */
    @Test
    public void isNumeric() {
        String str = "1y";
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(str);
        boolean isNumeric = isNum.matches();
        System.out.println(isNumeric);
    }
}
