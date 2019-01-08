package com.just.jfilegenerator.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 文字列に関するユーティリティクラス。
 * @author QuadJust
 */
public class StringUtil {
    /**
     * 文字列の改行とインデントを取り除く。
     * @param str
     * @return 改行とインデントを取り除いた文字列。
     */
    public static String clean(String str) {
        String[] lines = str.split("\n");
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            line = line.replaceAll("\t", "");
            builder.append(line);
        }
        return builder.toString();
    }
    
    /**
     * ファイル名から拡張子を取得する。
     * @param fileName
     * @return 拡張子。
     */
    public static String getFileExtension(String fileName) {
        // 拡張子を取得する。
        String ret = "";
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            ret = parts[parts.length - 1].toLowerCase();
        }
        return ret;
    }
    
    /**
     * 文字列の先頭を大文字に変換する。
     * @see http://coglayblog.com/2017/03/19/java_format_touppercase/
     * @param str 対象文字列。
     * @return 変換後文字列。
     */
    public static String upperCaseFirst(String o) {
        String ret = o.toUpperCase().substring(0, 1) + o.substring(1);
        return ret;
    }
    
    /**
     * 文字列の先頭を大文字に変換する。
     * @see http://coglayblog.com/2017/03/19/java_format_touppercase/
     * @param str 対象文字列。
     * @return 変換後文字列。
     */
    public static String lowerCaseFirst(String o) {
        String ret = o.toLowerCase().substring(0, 1) + o.substring(1);
        return ret;
    }
    
    /**
     * Integer型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isInteger(String o) {
        try {
            Integer.parseInt(o);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Long型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isLong(String o) {
        try {
            Long.parseLong(o);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Float型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isFloat(String o) {
        try {
            Float.parseFloat(o);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Double型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isDouble(String o) {
        try {
            Double.parseDouble(o);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Date型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isDate(String o) {
        try {
            LocalDate.parse(o);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Boolean型かどうか調べる。
     * @param o
     * @return
     */
    public static boolean isBoolean(String o) {
        return o != null && 
                (o.toLowerCase().equals("true") || o.toLowerCase().equals("false"));
    }
}
