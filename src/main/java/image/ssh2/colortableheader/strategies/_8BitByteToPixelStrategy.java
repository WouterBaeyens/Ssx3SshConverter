package image.ssh2.colortableheader.strategies;

import com.mycompany.sshtobpmconverter.IPixel;
import image.ssh2.Ssh2ColorTable;

import java.nio.ByteBuffer;

public class _8BitByteToPixelStrategy implements ByteToPixelStrategy {

    @Override
    public IPixel readNextPixel(ByteBuffer imageByteBuffer, Ssh2ColorTable colorTable, int pixelXPos) {
        return colorTable.getPixelFromByte(imageByteBuffer.get());
    }
}
