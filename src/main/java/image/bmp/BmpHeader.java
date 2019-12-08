/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.bmp;

import image.ImgComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author Wouter
 */
public class BmpHeader implements ImgComponent{
    
    long offset;
    
    /*BMP HEADER*/
    //this is the hex value of "bm"
    //if this is not bm, it's not a usable bitmap
    private byte[] BM = {0x42, 0x4d};
    //total size: header (probably 54) + img data
    private byte[] fileSize = new byte[4];
    //application specific, should be all 0's it seems
    private static final byte[] reserved1 = new byte[4];
    //BMP (probably 14) + DIB (probably 40)  = total header size (54 bytes)
    private final byte [] offsetToPixelArray = {0x36,0x00,0x00,0x00};
    
    /*DIB HEADER*/
    //40 bytes total
    private byte[] dibSize = {0x28, 0x00,0x00,0x00};
    private byte[] imgWidth = new byte[4];
    private byte[] imgHeight = new byte[4];
    //nr of color planes being used; 1 in this case
    private byte[] nrOfPlanes = {0x01, 0x00};
    //24 bits per pixel in this case
    private byte[] bitsPerPixel = {0x18, 0x00};
    //compression, no compression here
    private byte[] compression = new byte[4];
    //size (includes padding)
    private byte[] bitmapDataSize = new byte[4];
    //horizontal rez; pixels/metre
    private byte[] horizontalRez = {0x01,0x00,0x00,0x00};
    //vertical rez; pixels/metre
    private byte[] verticalRez = {0x01,0x00,0x00,0x00};
    //nr of colors in palette, 0 default (might change to 256)
    private byte[] colorsInPalette = new byte[4];
    //all colors are equally important here
    private byte[] colorImportance = new byte[4];
    
    public BmpHeader(RandomAccessFile file) throws IOException{
        this(file, 0);
    }
    
    public BmpHeader(RandomAccessFile file, long offset) throws IOException{
        this.offset = offset;
        file.seek(offset);
        file.read(BM);
        file.read(fileSize);
        file.read(reserved1);
        file.read(offsetToPixelArray);
        //DIB HEADER
        file.read(dibSize);
        file.read(imgWidth);
        file.read(imgHeight);
        file.read(nrOfPlanes);
        file.read(bitsPerPixel);
        file.read(compression);
        file.read(bitmapDataSize);
        file.read(horizontalRez);
        file.read(verticalRez);
        file.read(colorsInPalette);
        file.read(colorImportance);
    }
    
    public BmpHeader(long width, long height){
        long totalHeader = ByteUtil.convertToLongLE(offsetToPixelArray);
        long bitMapDataSize = calculateBitmapSize(width, height);
        fileSize = ByteUtil.convertToBytesLE(4, totalHeader + bitMapDataSize);
        imgWidth = ByteUtil.convertToBytesLE(imgWidth.length, width);
        imgHeight = ByteUtil.convertToBytesLE(4, height);
        this.bitmapDataSize = ByteUtil.convertToBytesLE(4, bitMapDataSize);
        
        horizontalRez = ByteUtil.convertToBytesLE(4, 2835);
        verticalRez = ByteUtil.convertToBytesLE(4, 2835);
    }
    
    public long getBitMapDataOffset(){
        return ByteUtil.convertToLongLE(offsetToPixelArray);
    }
    
    /**
     * Reads the size of the dataMap from the header and returns this value
     * This will include padding if applicable
     * 
     * @return 
     */
    public long getBitMapDataSize(){
        return ByteUtil.convertToLongLE(bitmapDataSize);
    }
    
    /**
     * Without padding: Calculates the size based on the height, width, bytes/pixel and padding
     * With padding: default; same as getBitMapDataSize
     * @param includePadding whether to include the padding at the very end
     *      Note: this is not the same as row-padding!
     * @return 
     */
    public long getBitMapDataSize(boolean includeEndPadding){
        if(!includeEndPadding){
            return getImgRowLength() * getImgHeight();
        } else{
            return getBitMapDataSize();
        }
    }
    
    public long getRowPadding(){
        return (getImgWidth()* getBytesPerPixel())%4;
    }
    
    public long getBitsPerPixel(){
        return ByteUtil.convertToLongLE(bitsPerPixel);
    }
    
    public long getBytesPerPixel(){
        return ByteUtil.convertToLongLE(bitsPerPixel) / 8;
    }
    
    public long getImgWidth(){
        return ByteUtil.convertToLongLE(imgWidth);
    }
    
    /**
     * Calculates the amount of bytes used to represent 1 row in the image
     * @return 
     */
    public long getImgRowLength(){
        return (getImgWidth() + getRowPadding()) * getBytesPerPixel();
    }
    
    public long getImgHeight(){
        return ByteUtil.convertToLongLE(imgHeight);
    }
    
    private long calculateBitmapSize(long width, long height){
        long bytesPerPixel = getBytesPerPixel();
        long paddingPerRow = (width * bytesPerPixel / 8) % 8;
        return width * height * bytesPerPixel + height * paddingPerRow;
    }
    
    public byte[] toByteArray(){
        byte[] header = ByteUtil.combineByteArrays(BM, fileSize, reserved1, offsetToPixelArray, dibSize, imgWidth, imgHeight, nrOfPlanes, bitsPerPixel, compression, bitmapDataSize,
                horizontalRez, verticalRez, colorsInPalette, colorImportance);
        return header;
    }
    
    /**
     * A sysout of the bytes to get a color-coded overview of the bytes.
     */
    @Override
    public void printFormatted(){
        System.out.println("--BPM HEADER--");
        System.out.println(PrintUtil.toRainbow("BM: \"" + new String(BM) + "\"", " fileSize: " + ByteUtil.convertToLongLE(fileSize),
                " reserved1: " + new String(reserved1), " offsetToPixelArray: " + ByteUtil.convertToLongLE(offsetToPixelArray)));
        System.out.println(PrintUtil.toHexString(true, BM, fileSize, reserved1, offsetToPixelArray));
        System.out.println("--DIB HEADER--");
        System.out.println(PrintUtil.toRainbow("DibSize: " + ByteUtil.convertToLongLE(dibSize), 
                " imgWidth: " + ByteUtil.convertToLongLE(imgWidth), " imgHeight: " + ByteUtil.convertToLongLE(imgHeight), " nrOfPlanes: " + ByteUtil.convertToLongLE(nrOfPlanes), 
                " bitsPerPixel: " + ByteUtil.convertToLongLE(bitsPerPixel),
                "\ncompression: " + ByteUtil.convertToLongLE(compression),
                " bitmapDataSize: " + ByteUtil.convertToLongLE(bitmapDataSize),
                " horizontalRez (px/m): " + ByteUtil.convertToLongLE(horizontalRez),
                " verticalRez (px/m): " + ByteUtil.convertToLongLE(verticalRez), "\ncolorsInPalette: " + ByteUtil.convertToLongLE(colorsInPalette),
                " colorImportance: " + ByteUtil.convertToLongLE(colorImportance)));

        System.out.println(PrintUtil.insertForColouredString(new String(new char[42]).replace('\0', ' ') +
                PrintUtil.toHexString(true, dibSize, imgWidth, imgHeight, nrOfPlanes, bitsPerPixel, compression,
                        bitmapDataSize, horizontalRez, verticalRez, colorsInPalette, colorImportance), "\n", 16*3));
    }

    @Override
    public long getPositionOffset() {
        return offset;
    }

    @Override
    public long getSize() {
        return getBitMapDataOffset();
    }

    @Override
    public void writeToFile(OutputStream os) throws IOException {
        os.write(toByteArray());
    }
}
