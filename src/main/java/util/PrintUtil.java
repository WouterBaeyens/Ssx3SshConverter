/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

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
 * Like standard splitting with delimiter, but ignoring special chars (for example the invisible colour information{@code [30;1m } )
 *
 * @param text          the original text
 * @param delimiter     the delimiter to be inserted every {@code groupSize} characters
 * @param groupSize     the size of each text-segment that is cut by the delimiter
 * @return 
 */
public static String insertForColouredString(
        String text, String delimiter, int groupSize) {
        int amountOfAnsi = StringUtils.countMatches(text, "\033");
        int ansiLength = 7;
        StringBuilder builder = new StringBuilder(
                text.length() + delimiter.length() * ((text.length() - amountOfAnsi * ansiLength) / groupSize) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the delimiter in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = delimiter;
            //determine Length
            int accountedFor = 0;
            String substr = text.substring(index, Math.min(index + groupSize, text.length()));
            int found = StringUtils.countMatches(substr, "\033");
            while (accountedFor < found){
                accountedFor = found;
                substr = text.substring(index, Math.min(index + groupSize + found * ansiLength, text.length()));
                //if no new ansi is found as a result of extending the string, the loop finishes
                found =  StringUtils.countMatches(substr, "\033");
            }
            
            builder.append(text.substring(index,
                    Math.min(index + groupSize + found * ansiLength, text.length())));
            index += groupSize + found * ansiLength;
        }
        return builder.toString();
    }

    public static String toHexString(byte[]... bytes){
        return toHexString(false, bytes);
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
            return String.join("", hexString);
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
        final StringBuilder result = new StringBuilder();
        final String[] colors = {"\033[30;1m", "\033[31;1m", "\033[32;1m", "\033[34;1m", "\033[35;1m"};
        final String noColor = "\033[0m";

        int i = 0;
        for (String listElement : list) {
            result.append(colors[i++ % colors.length]);
            result.append(listElement);
        }
        result.append(noColor);
        return result.toString();
    }
}
