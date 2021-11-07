/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import image.ImgComponent;
import image.ssh2.fileheader.ComponentType;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Wouter
 */
public class SshImageHeader implements ImgComponent{

    long positionOffset;
    
    //It's purpose is not yet found out
    private byte[] unknown = {0x02};
    //img data + some filler (stops right at color table)
    //note: this might be worng, 3 bytes seems very little img size
    private byte[] imgSize = new byte[3];
    //width of the image
    private byte[] imgWidth = new byte[2];
    //height of the image
    private byte[] imgHeight = new byte[2];
    //all 0's
    private byte[] unused = new byte[4];
    private byte[] encodingType = new byte[4];

    public SshImageHeader(RandomAccessFile sshFile, long offset) throws IOException {
        positionOffset = offset;
        sshFile.seek(offset);
        sshFile.read(unknown);
        sshFile.read(imgSize);
        sshFile.read(imgWidth);
        sshFile.read(imgHeight);
        sshFile.read(unused);
        sshFile.read(encodingType);
    }

    public SshImageHeader(int width, int height) {
        //FILLER DATA (until color table) NOT INCLUDED
        imgSize = ByteUtil.convertToBytesLE(imgSize.length, width * height + getSize());
        imgWidth = ByteUtil.convertToBytesLE(imgWidth.length, width);
        imgHeight = ByteUtil.convertToBytesLE(imgHeight.length, height);
    }

        public Map<String, byte[]> getMetaData(){
        Map<String, byte[]> metaData = new HashMap<>();
        metaData.put("sshImageHeader.encodingType", encodingType);
        return metaData;
    }
    
    public void updateMetaData(Map<String, byte[]> metaData){
        if(metaData.containsKey("sshImageHeader.encodingType"))
            encodingType = metaData.get("sshImageHeader.encodingType");
    }
    
    @Override
    public long getSize() {
        return unknown.length + imgSize.length + imgWidth.length + imgHeight.length + unused.length + encodingType.length;
    }

    /**
     * Returns a byte array, representing the component.
     * @return  a byte array, as it would be found in the actual file.
     */
    public byte[] toByteArray() {
        byte[] header = ByteUtil.combineByteArrays(unknown, imgSize, imgWidth, imgHeight, unused, encodingType);
        return header;
    }
    
    @Override
    public void writeToFile(OutputStream os) throws IOException {
       os.write(toByteArray());
    }

    public long getImgHeight() {
        return ByteUtil.convertToLongLE(imgHeight);
    }

    public long getImgWidth() {
        return ByteUtil.convertToLongLE(imgWidth);
    }
    
    /**
     * This calculates the size of the image.
     * This assumes the current encountered encoding where 1 pixel = 1 byte of info.
     * @return 
     */
    public long getImgSize(){
        return getImgHeight() * getImgWidth();
    }

    public void setEncodingType(EncodingType type) throws DecoderException {
        encodingType = Hex.decodeHex(type.getHexCode());
    }

    public EncodingType getEncodingType() {
        String encodingTypeHexValue = Hex.encodeHexString(encodingType);
        for (EncodingType type : EncodingType.values()) {
            if (encodingTypeHexValue.equals(type.getHexCode())) {
                return type;
            }
        }
        return EncodingType.UNKNOWN;
    }

    @Override
    public long getPositionOffset() {
        return positionOffset;
    }
    
    @Override
    public void printFormatted() {
        System.out.println("---IMG HEADER---");
        System.out.println(PrintUtil.toRainbow("? (usually 02)", " imgSize: " + ByteUtil.convertToLongLE(imgSize), 
                " imgWidth: " + ByteUtil.convertToLongLE(imgWidth), " imgHeight: " + ByteUtil.convertToLongLE(imgHeight), " unused2", " encodingType: " + getEncodingType().toString()));
        System.out.println(PrintUtil.toHexString(true, unknown, imgSize, imgWidth, imgHeight, unused, encodingType));
    }

    public enum EncodingType {
        NONE("00000000"),
        INTERLEAFED("00200000"),
        UNKNOWN("????????");

        private final String hexCode;

        private EncodingType(String hexCode) {
            this.hexCode = hexCode;
        }

        public String getHexCode() {
            return hexCode;
        }
    }
}
