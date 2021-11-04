package image.bmp2.dibheader.tags;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;
import util.PrintUtil;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * BI_RGB: The bitmap is in uncompressed red green blue (RGB) format that is not compressed and does not use color masks.
 * BI_RLE8: An RGB format that uses run-length encoding (RLE) compression for bitmaps with 8 bits per pixel. The compression uses a 2-byte format consisting of a count byte followed by a byte containing a color index.
 * BI_RLE4: An RGB format that uses RLE compression for bitmaps with 4 bits per pixel. The compression uses a 2-byte format consisting of a count byte followed by two word-length color indexes.
 * BI_BITFIELDS: The bitmap is not compressed, and the color table consists of three DWORD (defined in [MS-DTYP] section 2.2.9) color masks that specify the red, green, and blue components, respectively, of each pixel. This is valid when used with 16 and 32-bits per pixel bitmaps.
 * BI_JPEG: The image is a JPEG image, as specified in [JFIF]. This value SHOULD only be used in certain bitmap operations, such as JPEG pass-through. The application MUST query for the pass-through support, since not all devices support JPEG pass-through. Using non-RGB bitmaps MAY limit the portability of the metafile to other devices. For instance, display device contexts generally do not support this pass-through.
 * BI_PNG: The image is a PNG image, as specified in [RFC2083]. This value SHOULD only be used certain bitmap operations, such as JPEG/PNG pass-through. The application MUST query for the pass-through support, because not all devices support JPEG/PNG pass-through. Using non-RGB bitmaps MAY limit the portability of the metafile to other devices. For instance, display device contexts generally do not support this pass-through.
 * BI_CMYK: The image is an uncompressed CMYK format.
 * BI_CMYKRLE8: A CMYK format that uses RLE compression for bitmaps with 8 bits per pixel. The compression uses a 2-byte format consisting of a count byte followed by a byte containing a color index.
 * BI_CMYKRLE4: A CMYK format that uses RLE compression for bitmaps with 4 bits per pixel. The compression uses a 2-byte format consisting of a count byte followed by two word-length color indexes.
 * Note  A bottom-up bitmap can be compressed, but a top-down bitmap cannot.
 */
public class CompressionTypeTag extends ImgSubComponent implements TypeComponent<CompressionTypeTag.CompressionType> {

    private static final long DEFAULT_SIZE = 4;

    @Override
    public Class<CompressionType> getTypeClass() {
        return CompressionType.class;
    }

    public CompressionTypeTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "Compression type: " + getTypeInfo();
    }

    public enum CompressionType implements ComponentType {
        BI_RGB("00000000"),
        BI_RLE8("01000000"),
        BI_RLE4("02000000"),
        BI_BITFIELDS("03000000"),
        BI_JPEG("04000000"),
        BI_PNG("05000000"),
        BI_CMYK("0B000000"),
        BI_CMYKRLE8("0C000000"),
        BI_CMYKRLE4("0D000000");

        final String value;

        CompressionType(String value) {
            this.value = value;
        }

        @Override
        public String getReadableValue() {
            return value;
        }

        @Override
        public Function<byte[], String> toReadable() {
            return ByteUtil::bytesToHex;
        }
    }
}
