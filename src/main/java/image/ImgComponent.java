/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Wouter
 */
public interface ImgComponent {
    
    /**
     * Prints a formatted representation of the component to output.
     */
    public void printFormatted();
    
    /**
     * Get the position of where to find this data in the actual file.
     * @return position in the file
     */
    public long getPositionOffset();
    
    /**
     * Get the size (in bytes) of this component
     * @return the size (in bytes)
     */
    public long getSize();
    
    public void writeToFile(OutputStream os)throws IOException;
}
