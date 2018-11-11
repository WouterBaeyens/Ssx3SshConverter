/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

//import com.mycompany.sshtobpmconverter.*;
import image.ImgComponent;
import util.ByteUtil;
import util.PrintUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author Wouter
 */
public class SshHeader implements ImgComponent{
    
    
    private long positionOffset;
    /*SSH HEADER*/
    
    //this is the hex value of "SHPS"
    private static final byte[] SHPS = {0x53, 0x48, 0x50, 0x53};
    //the total file size
    private byte[] fileSize = new byte[4];
    //mostly unknown, standard: 01 00 .. archive: 10 00 ..
    private byte[] archive = {0x01, 0x00, 0x00, 0x00};
    //this is the hex value of g357
    private byte[] g357 = {0x47, 0x33, 0x35, 0x37};
    
    //the name of the file
    private byte[] fileName = new byte[4];
    //header size (this seems to not include the last/fist 16 bytes of information (?)
    private byte[] headerSize = new byte[4];
    //this is the hex value of "Buy ERTS"
    private static final byte [] buy_erts = {0x42, 0x75, 0x79, 0x20, 0x45, 0x52, 0x54, 0x53};
    
    //all 0's all the time, maybe used in case of archive
    private byte [] unused1 = new byte[80];
    
    
    
    public SshHeader(SshImage image, String name){
        //fileSize
        //(archive)
        setFileName(name);
        headerSize = ByteUtil.convertToBytesLE(headerSize.length, getSize());
        fileSize = ByteUtil.convertToBytesLE(fileSize.length, image.getSize() + getSize());
    }
    
    public SshHeader(RandomAccessFile file) throws IOException{
        this(file, 0);
    }
    
    public Map<String, byte[]> getMetaData(){
        Map<String, byte[]> metaData = new HashMap<>();
        metaData.put("sshHeader.fileName", fileName);
        return metaData;
    }
    
    public void updateMetaData(Map<String, byte[]> metaData){
        if(metaData.containsKey("sshHeader.fileName"))
            fileName = metaData.get("sshHeader.fileName");
    }
    
    public SshHeader(RandomAccessFile file, long position) throws IOException{
        
        file.seek(position);
        byte[] buffer = new byte[SHPS.length];
        file.read(SHPS);
        /*if(! Arrays.equals(SHPS, buffer)){
            throw new IOException("expected SHPH in header, but recieved " + Arrays.toString(buffer));
        }*/
        file.read(fileSize);
        file.read(archive);
        file.read(g357);
        /*buffer = new byte[g357.length];
        file.read(buffer);
        if(! Arrays.equals(g357, buffer)){
            throw new IOException("expected g357 in header, but recieved " + Arrays.toString(buffer));
        }*/
        file.read(fileName);
        file.read(headerSize);
        buffer = new byte[buy_erts.length];
        file.read(buy_erts);
        /*if(! Arrays.equals(buy_erts, buffer)){
            throw new IOException("expected buy erts in header, but recieved " + Arrays.toString(buffer));
        }*/
        file.read(unused1);
    }
    
    public byte[] toByteArray(){
        byte[] header = ByteUtil.combineByteArrays(SHPS, fileSize, archive, g357, 
                fileName, headerSize, buy_erts, unused1);
        return header;
    }
    
    @Override
    public void writeToFile(OutputStream os) throws IOException{
        os.write(toByteArray());
    }
    
    public String getFileName(){
        return new String(fileName);
    }
    
    public void setFileName(String name){
        for(int i = 0; i < fileName.length && i < name.length(); i++){
            fileName[i] = (byte) name.charAt(i);
        }
    }
    
    public void setFileSize(long size){
        fileSize = ByteUtil.convertToBytesLE(fileSize.length, size);
    }
   
    
    @Override
    public long getSize(){
        return SHPS.length + fileSize.length+ archive.length+ g357.length +
                fileName.length +headerSize.length+ buy_erts.length +unused1.length;
    }

    
    @Override
    public void printFormatted(){
        System.out.println("--SSH HEADER--");
        System.out.println(PrintUtil.toRainbow("SHPS: \""+ new String(SHPS) + "\"", " fileSize: " + ByteUtil.convertToLongLE(fileSize), 
                " archive (01 = standard)", " g357: \"" + new String(g357) + "\""));
        System.out.println(PrintUtil.toHexString(true, SHPS, fileSize, archive, g357));
        System.out.println(PrintUtil.toRainbow("fileName: \"" + new String(fileName) + "\"", " headerSize: " + ByteUtil.convertToLongLE(headerSize),
                " buy erts: \"" + new String(buy_erts) + "\""));
        System.out.println(PrintUtil.toHexString(true, fileName, headerSize, buy_erts));
        System.out.println("unused1: all 0's so far");
        System.out.println(PrintUtil.insert(PrintUtil.toHexString(false, unused1), "\n", 16*3));
    }

    @Override
    public long getPositionOffset() {
        return positionOffset;
    }
    
}
