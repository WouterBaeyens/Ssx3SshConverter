package image.ssh2.colortableheader.strategies;

import com.mycompany.sshtobpmconverter.IPixel;
import com.mycompany.sshtobpmconverter.Pixel2;
import image.ssh2.Ssh2ColorTable;

import java.nio.ByteBuffer;

public interface ByteToPixelStrategy {

    /**
     * - Reads data from the buffer
     * - Use the table to create the correct Pixel from this data
     * - Update the buffer postion accordingly:
     *
     * Note: pixelXPos is needed as a workaround in case of 4-bit colors.
     *       The buffer position is insufficient as it can only jump in increments of 1 byte while the data needed is only 4 bits (or 1/2 bytes) in size.
     */
    IPixel readNextPixel(final ByteBuffer imageByteBuffer, final Ssh2ColorTable colorTable, final int pixelXPos);

    default boolean requiresPalette(){
        return true;
    }
}
