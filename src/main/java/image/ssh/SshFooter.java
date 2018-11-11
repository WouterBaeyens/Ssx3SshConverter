/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import com.mycompany.sshtobpmconverter.*;
import image.ImgComponent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import util.ByteUtil;
import util.PrintUtil;

/**
 *
 * @author Wouter
 */
public class SshFooter implements ImgComponent{
    
    private long positionOffset;
    
    private final byte[] default1 = {0x70, 0x00, 0x00, 0x00};
    
    private byte[] unknown1 = default1;
    //probably just the first few bytes are for the name
    //the rest might be for something else
    //so far at least the first 4-5 bytes are used to define a name
    private byte[] name = new byte[12];

    public SshFooter(RandomAccessFile sshFile, long offset) throws IOException{
        positionOffset = offset;
        sshFile.read(unknown1);
        sshFile.read(name);
    }
    
    public SshFooter(String name){
        setName(name);
    }
    
        public Map<String, byte[]> getMetaData(){
        Map<String, byte[]> metaData = new HashMap<>();
        metaData.put("sshFooter.name", name);
        return metaData;
    }
    
    public void updateMetaData(Map<String, byte[]> metaData){
        if(metaData.containsKey("sshFooter.name"))
            name = metaData.get("sshFooter.name");
    }
    

    
    public String getName(){
        return new String(name);
    }
    
    private void setName(String name){
        for(int i = 0; i < name.length(); i++){
            this.name[i] = (byte) name.charAt(i);
        }
    }
    
    @Override
    public long getSize(){
        return unknown1.length + name.length;
    }
    
    public byte[] createByteArray(){
        byte[] footer = ByteUtil.combineByteArrays(unknown1, name);
        return footer;
    }

    @Override
    public void printFormatted() {
        System.out.println("---FOOTER---");
        System.out.println(PrintUtil.toRainbow("default1", " name: " + ByteUtil.convertToLongLE(name)));
        System.out.println("default1 expected:");
        System.out.println(PrintUtil.toHexString(true, default1));
        System.out.println(PrintUtil.toHexString(true, unknown1, name));    }

    @Override
    public long getPositionOffset() {
        return positionOffset;
    }

    @Override
    public void writeToFile(OutputStream os) throws IOException {
        os.write(createByteArray());
    }
}
