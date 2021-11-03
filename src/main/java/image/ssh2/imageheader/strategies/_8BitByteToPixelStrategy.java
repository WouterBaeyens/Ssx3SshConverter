package image.ssh2.imageheader.strategies;

import com.mycompany.sshtobpmconverter.IPixel;
import image.ColorTable;
import image.ssh2.Ssh2ColorTable;

import java.nio.ByteBuffer;

public class _8BitByteToPixelStrategy implements ByteToPixelStrategy {

    @Override
    public IPixel readNextPixel(ByteBuffer imageByteBuffer, ColorTable colorTable, int pixelXPos) {
        return colorTable.getPixelFromByte(imageByteBuffer.get());
    }
}
