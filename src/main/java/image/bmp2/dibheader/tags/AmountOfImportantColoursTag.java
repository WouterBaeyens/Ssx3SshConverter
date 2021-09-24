package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * the number of colors in the color palette, or 0 to default to 2n
 */
public class AmountOfImportantColoursTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public AmountOfImportantColoursTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "# important colours: " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public int getConvertedValue() {
        return (int) ByteUtil.convertToLongLE(getBytes());
    }

}
