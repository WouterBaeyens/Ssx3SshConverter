package com.mycompany.sshtobpmconverter;

import org.apache.commons.lang3.ArrayUtils;
import util.ByteUtil;

import java.util.Arrays;

public class Pixel2 implements IPixel {

    private static final Pixel2 BLACK_PIXEL = new Pixel2(new byte[4]);
    public static final Pixel2 WHITE_PIXEL = new Pixel2(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
    public static final Pixel2 GREY_PIXEL = new Pixel2(new byte[]{(byte) 0x78, (byte) 0x78, (byte) 0x78, (byte) 0x78});
    public static final Pixel2 DARK_GREY_PIXEL = new Pixel2(new byte[]{(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30});

    /**
     * This is the value used when the "alpha-level" is completely neutral.
     */
    private static final byte DEFAULT_ALPHA_VALUE = (byte) 0x80;

    /**
     * for this class to correctly function I assume the given byteArray will be 4 bytes long
     */
    private final byte[] rgba;

    public Pixel2(byte[] rgba) {
        this.rgba = ArrayUtils.clone(rgba);
    }

    public static Pixel2 createPixelFromLE(final byte[] bytes) {
        final byte[] rgba = Arrays.copyOf(bytes, 4);
        ByteUtil.swap(rgba, 0, 2);
        return new Pixel2(rgba);
    }

    public static byte[] tintGreenForSpecialAlpha(final byte[] rgba) {
        byte[] rgbaGreenTinted = ArrayUtils.clone(rgba);
        rgbaGreenTinted[0] = rgba[3];
        rgbaGreenTinted[1] = rgba[3];
        rgbaGreenTinted[2] = rgba[3];
        return rgbaGreenTinted;
    }

    public boolean isPixelEmpty() {
        return ByteUtil.isArrayZeroFilled(rgba);
    }

    public static Pixel2 createPixelFromRGB(byte[] bytes) {
        final byte[] rgba = Arrays.copyOf(bytes, 4);
        rgba[3] = DEFAULT_ALPHA_VALUE;
        return new Pixel2(rgba);
    }

    @Override
    public byte[] getRGBValue() {
        return Arrays.copyOf(rgba, 3);
    }

    public boolean isSpecialAlpha() {
        return rgba[3] != DEFAULT_ALPHA_VALUE;
    }

    public static Pixel2 getDefaultPixel() {
        return BLACK_PIXEL;
    }

    public byte[] getRGBAValue() {
        return Arrays.copyOf(rgba, 4);
    }

    @Override
    public byte[] getRGBValueLE() {
        byte[] bytesLE = getRGBValue();
        ByteUtil.swap(bytesLE, 0, 2);
        return bytesLE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pixel2 other = (Pixel2) obj;
        return Arrays.equals(this.getRGBAValue(), other.getRGBAValue());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode(this.rgba);
        return hash;
    }

    @Override
    public String toString() {
        return "Pixel2{" +
                "rgba=" + Arrays.toString(rgba) +
                '}';
    }
}
