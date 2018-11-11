/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wouter
 */
public interface ImageFileWrapper {
    
    /**
     * NOTE: I've come to the conclusion that for now it's not worth the extra dev time 
     * to create and implement a getImageRow.
     * 
     * Possible uses: really big image files, that can't be fitted in memory all at once
     * @param rowNr
     * @return 
     */
    @Deprecated
    public Map<Integer, List<Pixel>> getImageRow(int rowNr) throws IOException ;
    
    /**
     * Returns the complete image as a list of rows
     * I'm a bit paranoid about big files and the space it would consume,
     * but the positives (faster, easier to use) probably outweigh the negatives
     * @return A list of list, representing a list of rows containing a list of pixels.
     * @throws IOException 
     */
    public List<List<Pixel>> getImage() throws IOException;
    
    /**
     * Get the width (in pixels) of the image.
     * @return 
     */
    public int getImgWidth();
    
    /**
     * Get the height (in pixels) of the image
     * @return 
     */
    public int getImgHeight();
    
    /**
     * 
     * @param os the stream with the fileName to which will be written
     * @throws IOException 
     */
    public void writeToFile(OutputStream os)throws IOException;

    public void printFormatted();
}
