/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

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
public class SshPostImageData implements ImgComponent{
    
        private long positionOffset;
        
        private static final byte[] defaultData = {0x21, 0x10, 0x04, 0x00, 0x00, 0x01, 0x01, 0x00,
                0x00, 0x01, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00,};
    //0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };
        
        //this is the part between the pixel data and the color palette data
    // no idea yet what it is, 
    //it seems to be the same in static images 
    //checked: "posterv_012;kaori_flat;kaori_small_flat;elise_flat
    private byte[] postPixelData = defaultData;
    
    public SshPostImageData(RandomAccessFile sshFile, long offset) throws IOException{
        positionOffset = offset;
        sshFile.seek(offset);
        sshFile.read(postPixelData);
    }
    
    public SshPostImageData(){
        postPixelData = defaultData;
    }
    
    public byte[] createByteArray(){
        return ByteUtil.combineByteArrays(postPixelData);
    }
    
    @Override
    public long getSize(){
        return postPixelData.length;
    }

    @Override
    public void printFormatted() {
        System.out.println("---POST IMAGE DATA---");
        System.out.println("expected:");
        System.out.println(PrintUtil.toHexString(true, defaultData));
        System.out.println(PrintUtil.toHexString(false, postPixelData));
    }

    @Override
    public long getPositionOffset() {
        return positionOffset;
    }

    @Override
    public void writeToFile(OutputStream os) throws IOException {
        os.write(createByteArray());
    }
}
