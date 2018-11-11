/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.google.common.io.Files;
import image.ssh.SshHeader;
import image.ssh.SshImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wouter
 */
public class SshImageFileWrapper implements ImageFileWrapper{

    private RandomAccessFile file;
    private SshHeader header;
    private SshImage image;
    
    public SshImageFileWrapper(File sshFile) throws FileNotFoundException, IOException{
        if(!isSshFile(sshFile))
            throw new IllegalArgumentException("only ssh files allowed!");
        String filePermissions = "r";
        file = new RandomAccessFile(sshFile, filePermissions);
        header = new SshHeader(file);
        long imageOffset = header.getSize();
        image = new SshImage(file, imageOffset);
    }
    
    public SshImageFileWrapper(ImageFileWrapper wrapper) throws IOException{
        image = new SshImage(wrapper);
        header = new SshHeader(image, "");
    }
    
    @Override
    public Map<Integer, List<Pixel>> getImageRow(int rowNr) throws IOException {
        return image.getImageRow(rowNr);
    }
    
    @Override
    public List<List<Pixel>> getImage() throws IOException{
        return image.getImage();
    }


    @Override
    public int getImgWidth() {
        return image.getImgWidth();
    }

    @Override
    public int getImgHeight() {
        return image.getImgHeight();
    }
    
    private boolean isSshFile(File file){
        String extension = Files.getFileExtension(file.getPath());
        return extension.equals("ssh");
    }

    @Override
    public void writeToFile(OutputStream os) throws IOException {
        header.writeToFile(os);
        image.writeToFile(os);
        os.close();
    }

    @Override
    public void printFormatted() {
        header.printFormatted();
        image.printFormatted();
    }
    
    /**
     * this is used to conserve data that gets lost in the conversion process in case it's essential
     * a bit ugly, but it should work
     * @return 
     */
    public Map<String, byte[]> getMetaData(){
        Map<String, byte[]> metaData = new HashMap<>();
        metaData.putAll(header.getMetaData());
        metaData.putAll(image.getMetaData());
        return metaData;
    }
    
    public void updateMetaData(Map<String, byte[]> metaData){
        image.updateMetaData(metaData);
        header.updateMetaData(metaData);
    }
}
