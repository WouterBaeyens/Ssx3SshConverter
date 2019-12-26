/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.mycompany.sshtobpmconverter.IPixel;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Wouter
 */
public interface Image {
    
    /**
     * Returns the complete image as a list of rows
     * I'm a bit paranoid about big files and the space it would consume,
     * but the positives (faster, easier to use) probably outweigh the negatives
     * @return A list of list, representing a list of rows containing a list of pixels.
     * @throws IOException 
     */
    List<List<IPixel>> getImage() throws IOException;
    
    /**
     * Get the width (in pixels) of the image.
     * @return 
     */
    int getImgWidth();
    
    /**
     * Get the height (in pixels) of the image
     * @return 
     */
    int getImgHeight();

    void printFormatted();

    default String getImageName() {
        return "";
    }
}
