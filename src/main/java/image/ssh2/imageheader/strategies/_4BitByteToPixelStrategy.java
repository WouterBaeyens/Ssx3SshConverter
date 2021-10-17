package image.ssh2.imageheader.strategies;

import com.mycompany.sshtobpmconverter.IPixel;
import image.ColorTable;
import image.ssh2.Ssh2ColorTable;
import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * A pixel is stored in only 4 bits or a nibble.
 * As such instead of just using the byte; we retrieve the appropriate nibble from the byte and use this to determine the colour instead.
 * As we need to read the same byte twice from the buffer (once for each nibble), we revert the buffer position the first time to reread this byte.
 */
public class _4BitByteToPixelStrategy implements ByteToPixelStrategy {

    @Override
    public IPixel readNextPixel(final ByteBuffer imageByteBuffer, final ColorTable colorTable, int pixelXPos) {
        final byte imageByte = imageByteBuffer.get();
        final byte imageNibble;
        final boolean pixelNumberIsEven = (pixelXPos % 2 == 0);
        if(pixelNumberIsEven){
            imageNibble = ByteUtil.getRightNibble(imageByte);
            revertBufferPosition(imageByteBuffer);
        } else {
            imageNibble = ByteUtil.getLeftNibble(imageByte);
        }
        return colorTable.getPixelFromByte(imageNibble);
    }

    private void revertBufferPosition(final ByteBuffer imageByteBuffer){
        imageByteBuffer.position(imageByteBuffer.position() - 1);
    }
}
