/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.common.primitives.Longs;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Wouter
 */
public class ByteUtil {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private ByteUtil() throws AssertionError {
        throw new AssertionError("This is a utility class and should not be initialized!");
    }

    /**
     * Credits to https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Converts a long into an array of bytes (using Little Endian format)
     *
     * @param nrOfBytes this defines the length of the array that will be returned
     * @param value     the long value that will be converted
     * @return the value as a string of bytes
     * ex: 2 bytes; value 5 -> {05 00}
     */
    public static byte[] convertToBytesLE(int nrOfBytes, long value) {
        byte[] bigEndian = Longs.toByteArray(value);
        byte[] littleEndian = new byte[nrOfBytes];
        for (int i = bigEndian.length; 0 < i && bigEndian.length - i < littleEndian.length; i--) {
            littleEndian[bigEndian.length - i] = bigEndian[i - 1];
        }
        return littleEndian;
    }

    public static boolean isArrayZeroFilled(final byte[] bytes) {
        return Arrays.equals(bytes, new byte[bytes.length]);
    }


    /**
     * Converts array of bytes to a long (using Little Endian format)
     *
     * @param bytes byte array that will be converted
     * @return long, with the calculated value of the byte array
     */
    public static long convertToLongLE(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value += ((long) bytes[i] & 0xffL) << (8 * i);
        }
        return value;
    }

    public static long convertToLongBE(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value += ((long) bytes[(bytes.length - 1) - i] & 0xffL) << (8 * i);
        }
        return value;
    }

    public static void swap(byte[] a, int i, int j) {
        byte t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * Returns a string that also includes the hex representation as it can often help deciphering ssh files.
     *
     * @return a string representation that includes the value as hex
     * ex: 17584 -> "17584/0x44b0"
     */
    public static String printLongWithHex(long value) {
        return value + "/0x " + Long.toHexString(value);
    }

    public static byte[] combineByteArrays(byte[]... byteArrays) {
        int totalLength = 0;
        for (byte[] byteArray : byteArrays) {
            totalLength += byteArray.length;
        }
        byte[] result = new byte[totalLength];
        int currentPosition = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, result, currentPosition, byteArray.length);
            currentPosition += byteArray.length;
        }
        return result;
    }

    public static int simulateSwitching4th5thBit(int nr) {
        boolean bit4 = (nr % 16) / 8 >= 1;
        boolean bit5 = (nr % 32) / 16 >= 1;
        if (bit4 && !bit5) {
            return nr + 8;
        }
        if (!bit4 && bit5) {
            return nr - 8;
        } else {
            return nr;
        }
    }

    public static int swapBits(final int input, final int bitIndex1, int bitIndex2){
        if(getBit(input, bitIndex1) == getBit(input, bitIndex2)){
            return input;
        } else {
            int toggleMask = (1 << bitIndex1) | (1 << bitIndex2);
            return input ^ toggleMask;
        }
    }

    public static int swapBits(final int input, final int bitIndex1, int bitIndex2, int length){
        int result = input;
        for(int offset = 0; offset < length; offset ++) {
            int bi1 = bitIndex1 + offset;
            int bi2 = bitIndex2 + offset;
            if (getBit(result, bi1) != getBit(result, bi2)) {
                int toggleMask = (1 << bi1) | (1 << bi2);
                result = result ^ toggleMask;
            }
        }
        return result;
    }

    public static int getBit(final int input, int bitIndex){
        return  getBits(input, bitIndex, bitIndex + 1);
    }

    /**
     * ex: input b00110100, 1, 5 -> ..000 1010 (split like so: 001 1010 00)
     */
    public static int getBits(final int input, int bitIndexStart, int bitIndexEnd){
        return  (input >>> bitIndexStart) & ((1 << (bitIndexEnd - bitIndexStart)) - 1);
    }

    /**
     * To clarify (since off by 1 is often a problem here)
     * 0001 1000 -> the bits that have a one, are bits 4 and 5
     *
     */
    public static byte switchBit4And5(final byte byte_) {
        if (getBitFromByte(byte_, 4) == getBitFromByte(byte_, 5)) {
            return byte_;
        } else {
            return toggleBit4And5(byte_);
        }
    }

    public static int getBitFromByte(final byte byte_, final int position) {
        byte result = byte_;
        result >>>= (position - 1);
        result &= 1;
        return result;
    }

    private static byte toggleBit4And5(final byte byte_) {
        byte result = byte_;
        result ^= 0x18; // binary: 0001 1000
        return result;
    }

    public static byte getLeftNibble(final byte _byte){
        byte tmpByte = _byte;
        return (byte) ((tmpByte >>> 4) & 0x0F);
    }

    public static byte getRightNibble(final byte _byte){
        byte tmpByte = _byte;
        return tmpByte &= 0x0F;
    }
}
