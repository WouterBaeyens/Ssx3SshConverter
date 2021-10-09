package image.ssh2.imageheader.strategies;

import com.mycompany.sshtobpmconverter.IPixel;
import com.mycompany.sshtobpmconverter.Pixel2;
import image.ssh2.Ssh2ColorTable;

import java.nio.ByteBuffer;

public class _32BitByteToPixelWithoutPaletteStrategy implements ByteToPixelStrategy{
    @Override
    public IPixel readNextPixel(ByteBuffer imageByteBuffer, Ssh2ColorTable colorTable, int pixelXPos) {
        final byte[] rgba = new byte[4];
        imageByteBuffer.get(rgba);
        return new Pixel2(rgba);
    }

    @Override
    public boolean requiresPalette() {
        return false;
    }
}
