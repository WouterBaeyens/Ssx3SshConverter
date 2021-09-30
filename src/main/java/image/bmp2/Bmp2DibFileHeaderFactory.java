package image.bmp2;

import image.bmp2.dibheader.DibHeader;
import image.bmp2.dibheader.DibInfoHeader;
import image.bmp2.dibheader.tags.DibHeaderSizeTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class Bmp2DibFileHeaderFactory {

    public DibHeader create(final RandomAccessFile bmpFile, final long filePosition) throws IOException {
        final DibHeaderSizeTag.DibType dibType = new DibHeaderSizeTag(bmpFile, filePosition).getDibType();
        switch (dibType) {
            case BITMAPINFOHEADER:
                return new DibInfoHeader(bmpFile, filePosition);
            default:
                throw new UnsupportedEncodingException(dibType.name() + "bmp format is currently not supported, feel free to bug the developer about this.");
        }

    }
}
