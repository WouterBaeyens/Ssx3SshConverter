/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import converter.ImageFileWrapper;
import converter.Pixel;
import image.ImgComponent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;

/**
 *
 * @author Wouter
 */
public class SshImage implements ImgComponent {

    //used to read image data from (instead of storing it all)
    private RandomAccessFile sshDataFile;
    private long offset;
    
    ImageFileWrapper wrapper;

    private long imageDataOffset;
    
    private SshImageHeader header;
    private SshPostImageData postImage;
    private SshImageDecoderStrategy decoderStrategy;
    private SshColorTable colorTable;
    private SshFooter footer;

    public SshImage(RandomAccessFile sshDataFile, long offset) throws IOException {
        this.sshDataFile = sshDataFile;
        header = new SshImageHeader(sshDataFile, offset);
        determineDecoderStrategy(header.getEncodingType());
        imageDataOffset = header.getPositionOffset() + header.getSize();
        long postImageOffset = header.getImgSize() + imageDataOffset;
        postImage = new SshPostImageData(sshDataFile, postImageOffset);
        long colorTableOffset = postImage.getPositionOffset() + postImage.getSize();
        colorTable = new SshColorTable(sshDataFile, colorTableOffset);
        long footerOffset = colorTable.getPositionOffset() + colorTable.getSize();
        footer = new SshFooter(sshDataFile, footerOffset);
    }
    
    public SshImage(ImageFileWrapper wrapper) throws IOException{
        this.wrapper = wrapper;
        header = new SshImageHeader(wrapper.getImgWidth(), wrapper.getImgHeight());
        postImage = new SshPostImageData();
        colorTable = new SshColorTable(wrapper);
        footer = new SshFooter("");
    }

        public Map<String, byte[]> getMetaData(){
        Map<String, byte[]> metaData = new HashMap<>();
        metaData.putAll(footer.getMetaData());
        metaData.putAll(header.getMetaData());
        return metaData;
    }
    
    public void updateMetaData(Map<String, byte[]> metaData){
        footer.updateMetaData(metaData);
        header.updateMetaData(metaData);
        determineDecoderStrategy(header.getEncodingType());
    }
    
    @Override
    public void writeToFile(OutputStream os) throws IOException {
        if (sshDataFile != null) {
            throw new FileAlreadyExistsException("No wrapper file to write from found. Are trying to write the same file you just read? (as in just copy-paste?)");
        }
        header.writeToFile(os);
        //os.flush();
        //IMAGE writing
        int imgHeight = wrapper.getImgHeight();
        List<List<Pixel>> image = decoderStrategy.encodeImage(wrapper.getImage());
        for(List<Pixel> row: image){
            for(Pixel pixel: row){
                os.write(colorTable.getByteFromPixel(pixel));
            }
        }
        //os.flush();
        postImage.writeToFile(os);
        //os.flush();
        colorTable.writeToFile(os);
        //os.flush();
        footer.writeToFile(os);
        //os.flush();
    }

    /**
     * Returns a chunk of rows (as List<Pixel> with the row inside it.
     * Note: the row originally asked can be retrieved by using [result].get(rowNr)
     * @param rowNr
     * @return 
     * @throws java.io.IOException 
     */
    public Map<Integer, List<Pixel>> getImageRow(int rowNr) throws IOException {
        Map<Integer, List<Pixel>> result = new HashMap<>();
        List<Pixel> pixelList = new ArrayList<>();
        if(rowNr >= getImgHeight())
            throw new IllegalArgumentException("given rowNr is bigger than the height of the img");
        int imgWidth = (int) getImgWidth();
        long rowOffset = imageDataOffset + imgWidth * rowNr;
        sshDataFile.seek(rowOffset);
        int pixelCode;
        for(int i = 0; i < imgWidth;i++){
            pixelCode = sshDataFile.read();
            pixelList.add(colorTable.getPixelfromPixelCode(pixelCode));
        }
        result.put(rowNr, pixelList);
        return result;
    }

    private void determineDecoderStrategy(SshImageHeader.EncodingType encoding){
        switch(encoding){
            case NONE:
                decoderStrategy = new NoneDecoderStrategy();
                break;
            case INTERLEAFED:
                decoderStrategy = new InterleafedDecoderStrategy();
                break;
            default:
                decoderStrategy = new NoneDecoderStrategy();
                
        }
    }
    
    public List<List<Pixel>> getImage() throws IOException {
        List<List<Pixel>> image = new ArrayList<>();
        int imgHeight = (int) getImgHeight();
        int imgWidth = (int) getImgWidth();
        List<Pixel> imageRow;
        for(int rowNr = 0; rowNr < imgHeight; rowNr++){
            imageRow = new ArrayList<>();
            long rowOffset = imageDataOffset + imgWidth * rowNr;
            sshDataFile.seek(rowOffset);
            int pixelCode;
            for(int i = 0; i < imgWidth;i++){
                pixelCode = sshDataFile.read();
                imageRow.add(colorTable.getPixelfromPixelCode(pixelCode));
            }
            image.add(imageRow);
        }
        return decoderStrategy.decodeImage(image);
    }

    public int getImgWidth(){
        return (int) header.getImgWidth();
    }
    
    public int getImgHeight(){
        return (int) header.getImgHeight();
    }
    
    @Override
    public void printFormatted() {
        System.out.println("---IMG HEADER---");
        header.printFormatted();
        colorTable.printFormatted();
        footer.printFormatted();
    }

    @Override
    public long getPositionOffset() {
        return offset;
    }

    @Override
    public long getSize() {
        return header.getSize() + header.getImgSize() + postImage.getSize() + colorTable.getSize() + footer.getSize();
    }

}
