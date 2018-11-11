/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.common.primitives.Longs;

/**
 *
 * @author Wouter
 */
public class ByteUtil {
    
    private ByteUtil() throws Exception{
        throw new Exception("This is a utility class and should not be initialized!");
    }
    
    /**
     * Converts a long into an array of bytes (using Little Endian format)
     * @param nrOfBytes this defines the length of the array that will be returned
     * @param value the long value that will be converted
     * @return the value as a string of bytes
     *          ex: 2 bytes; value 5 -> {05 00}
     */
    public static byte[] convertToBytesLE(int nrOfBytes, long value){
        byte[] bigEndian = Longs.toByteArray(value);
        byte[] littleEndian = new byte[nrOfBytes];
        for(int i = bigEndian.length; 0 < i & bigEndian.length - i < littleEndian.length; i--){
            littleEndian[bigEndian.length - i] = bigEndian[i-1];
        }
        return littleEndian;
    }
    
    
    /**
     * Converts array of bytes to a long (using Little Endian format)
     * @param by byte array that will be converted 
     * @return long, with the calculated value of the byte array
     */
    public static long convertToLongLE(byte[] by){
        long value = 0;
        for (int i = 0; i < by.length; i++)
        {
            value += ((long) by[i] & 0xffL) << (8 * i);
        }
        return value;
    }
    
    public static byte[] combineByteArrays(byte[]... byteArrays){
        int totalLength= 0;
        for(byte[] byteArray: byteArrays){
            totalLength += byteArray.length;
        }
        byte[] result = new byte[totalLength];
        int currentPosition = 0;
        for(byte[] byteArray: byteArrays){
            System.arraycopy(byteArray, 0, result, currentPosition, byteArray.length);
            currentPosition += byteArray.length;
        }
        return result;
    }
    
    public static int simulateSwitching4th5thBit(int nr) {
        boolean bit4 = (nr % 16) / 8 >= 1;
        boolean bit5 = (nr % 32) / 16 >= 1;
        if (bit4 & !bit5) {
            return nr + 8;
        }
        if (!bit4 & bit5) {
            return nr - 8;
        } else {
            return nr;
        }
    }
}
