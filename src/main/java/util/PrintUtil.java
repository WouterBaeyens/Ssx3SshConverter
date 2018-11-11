/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Wouter
 */
public class PrintUtil {

    private PrintUtil() {
        throw new UnsupportedOperationException("This is a utility class and should not be initialized!");
    }

    /**
     * inserts a string in the text every [period] characters. Useful for making
     * hex strings more readable for example
     *
     * @param text the string you wish to "split"
     * @param insert the string you wish to use to split the text
     * @param period the amount of characters between each split
     * @return the given text with the insert string periodically inserted.
     */
    /*public static String insert(String text, String insert, int period) {
        Pattern p = Pattern.compile("(.{" + period + "})", Pattern.UNIX_LINES);
        Matcher m = p.matcher(text);
        return m.replaceAll("$1" + insert);
    }*/
    public static String insert(
            
            String text, String insert, int period) {
        //int amountOfInserts = 
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }
/**
 * Like standard insert, but with special char's (in this case no "\" signes except for ansi
 * @param text
 * @param insert
 * @param period
 * @return 
 */
    public static String ansiInsert(
            String text, String insert, int period) {
        int amountOfAnsi = StringUtils.countMatches(text, "\033");
        int ansiLength = 7;
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * ((text.length() - amountOfAnsi*ansiLength) / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            //determine Length
            int accountedFor = 0;
            String substr = text.substring(index,Math.min(index + period, text.length()));
            int found = StringUtils.countMatches(substr, "\033");
            while (accountedFor < found){
                accountedFor = found;
                substr = text.substring(index,Math.min(index + period + found * ansiLength, text.length()));
                //if no new ansi is found as a result of extending the string, the loop finishes
                found =  StringUtils.countMatches(substr, "\033");
            }
            
            builder.append(text.substring(index,
                    Math.min(index + period + found * ansiLength, text.length())));
            index += period + found * ansiLength;
        }
        return builder.toString();
    }

    /**
     * Converts an array of bytes to a formatted hex string (seperated by a
     * space every 2 characters)
     *
     * @param colorSwitch notes if you want to color each byte-string
     * differently
     * @param bytes the arrays of bytes you wish to convert to a hex string
     * @return a string representing all the byte arrays
     */
    public static String toHexString(boolean colorSwitch, byte[]... bytes) {
        String[] hexString = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            hexString[i] = insert(Hex.encodeHexString(bytes[i]), " ", 2) + " ";
        }
        if (colorSwitch) {
            return toRainbow(hexString);
        } else {
            return Arrays.toString(hexString);
        }
    }

    /**
     * returns one string, combining the given strings and coloring them each in
     * a different color. Note: This is done by adding ansicodes to the string
     *
     * @param list list of strings
     * @return colored concatenation of the given string list.
     */
    public static String toRainbow(String... list) {
        String result = "";
        String[] colors = {"\033[30;1m", "\033[31;1m", "\033[32;1m", "\033[34;1m", "\033[35;1m"};
        String reset = "\033[0m";
        int i = 0;
        for (String listElement : list) {
            result += colors[i % colors.length];
            i++;
            result += listElement;
        }
        result += reset;
        return result;
    }
}
