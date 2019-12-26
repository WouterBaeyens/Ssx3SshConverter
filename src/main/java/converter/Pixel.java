/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.mycompany.sshtobpmconverter.IPixel;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 *
 * @author Wouter
 */
public class Pixel implements IPixel {
    
    private byte[] rgb = new byte[3];
    
    public Pixel(byte[] rgb){
        this.rgb = ArrayUtils.clone(rgb);
    }
    
    public static Pixel createPixelFromRGB(byte[] bytes){
        return new Pixel(bytes);
    }
    
    public static Pixel createPixelFromBGR(byte[] bytes){
        byte[] bgr = ArrayUtils.clone(bytes);
        ArrayUtils.reverse(bgr);
        return new Pixel(bgr);
    }
    
    public byte[] getRGBValue(){
        return rgb;
    }

    public byte[] getRGBValueLE() {
        byte[] bgr = ArrayUtils.clone(rgb);
        ArrayUtils.reverse(bgr);
        return bgr;
    }
    
    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pixel other = (Pixel) obj;
        return Arrays.equals(this.getRGBValue(), other.getRGBValue());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode(this.rgb);
        return hash;
    }
}
