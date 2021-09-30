/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.google.common.io.Files;
import com.mycompany.sshtobpmconverter.IPixel;
import image.bmp.BmpHeader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wouter
 */
public class BmpImageFileWrapper implements Image {

    private RandomAccessFile file;
    private BmpHeader header;
    private Image wrapper;

    public BmpImageFileWrapper(File bmpFile) throws IOException {
        if (!isBMPFile(bmpFile)) {
            throw new IllegalArgumentException("only bmp files allowed!");
        }
        String filePermissions = "r";
        file = new RandomAccessFile(bmpFile, filePermissions);
        header = new BmpHeader(file);
    }

    public BmpImageFileWrapper(Image wrapper) {
        this.wrapper = wrapper;
        header = new BmpHeader(wrapper.getImgWidth(), wrapper.getImgHeight());
    }

    public Map<Integer, List<IPixel>> getImageRow(int rowNr) throws IOException {
        Map<Integer, List<IPixel>> result = new HashMap<>();
        List<IPixel> pixelList = new ArrayList<>();
        if (rowNr >= getImgHeight()) {
            throw new IllegalArgumentException("given rowNr is bigger than the height of the img");
        }
        long rowOffset = header.getImgRowLength() * ((getImgHeight() - 1) - rowNr);
        int imgWidth = getImgWidth();
        file.seek(rowOffset);
        byte[] pixelRGB = new byte[(int) header.getBytesPerPixel()];
        for (int i = 0; i < imgWidth; i++) {
            file.read(pixelRGB);
            pixelList.add(Pixel.createPixelFromRGB(pixelRGB));
        }
        result.put(rowNr, pixelList);
        return result;
    }

    @Override
    public List<List<IPixel>> getImage() throws IOException {
        List<List<IPixel>> image = new ArrayList<>();
        int imgHeight = (int) getImgHeight();
        int imgWidth = (int) getImgWidth();
        for (int rowNr = 0; rowNr < imgHeight; rowNr++) {
            List<IPixel> pixelList = new ArrayList();
            long rowOffset = getImgOffset() + header.getImgRowLength() * ((getImgHeight() - 1) - rowNr);
            file.seek(rowOffset);
            byte[] pixelRGB = new byte[(int) header.getBytesPerPixel()];
            for (int j = 0; j < imgWidth; j++) {
                file.read(pixelRGB);
                pixelList.add(Pixel.createPixelFromRGB(pixelRGB));
            }
            image.add(pixelList);
        }
        return image;
    }

    private long getImgOffset(){
        return header.getSize();
    }
    
    @Override
    public int getImgWidth() {
        return (int) header.getImgWidth();
    }

    @Override
    public int getImgHeight() {
        return (int) header.getImgHeight();
    }

    private boolean isBMPFile(File file) {
        String extension = Files.getFileExtension(file.getPath());
        return extension.equals("bmp");
    }

    public void writeToFile(OutputStream os) throws IOException {
        if (file != null) {
            throw new FileAlreadyExistsException("No wrapper file to write from found. Are trying to write the same file you just read? (as in just copy-paste?)");
        }
        header.writeToFile(os);
        //New code usign getImage()
        List<List<IPixel>> image = wrapper.getImage();
        for (int rowNr = getImgHeight() - 1; rowNr >= 0; rowNr--) {
            List<IPixel> row = image.get(rowNr);
            for (IPixel pixel : row) {
                os.write(pixel.getRGBValue());
            }
        }
        os.close();
        
    }

    @Override
    public void printFormatted() {
        header.printFormatted();
    }

}
