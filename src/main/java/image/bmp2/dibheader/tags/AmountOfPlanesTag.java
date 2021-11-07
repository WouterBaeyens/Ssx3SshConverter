package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Number of color planes being used
 */
public class AmountOfPlanesTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 2;

    public AmountOfPlanesTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "# Planes: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getRawBytes());
    }

}
