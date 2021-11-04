package image.ssh2.imageheader;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableLookupType;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import image.ssh2.imageheader.strategies._32BitByteToPixelWithoutPaletteStrategy;
import image.ssh2.imageheader.strategies._4BitByteToPixelStrategy;
import image.ssh2.imageheader.strategies._8BitByteToPixelStrategy;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This tag describes the type of image (bits per pixel etc.) that the data contains.
 */
public class ImageTypeTag extends ImgSubComponent implements TypeComponent<ImageTypeTag.ImageType> {

    private static final long DEFAULT_SIZE = 1;

    public ImageTypeTag(final ByteBuffer sshFileBuffer) {
        super(sshFileBuffer, DEFAULT_SIZE);
    }

    public ImageType getImageType() {
        return getType().orElse(ImageType.DEFAULT_8BPP);
    }

    @Override
    public String getInfo() {
        return "ImageType: " + getTypeInfo();
    }

    @Override
    public Class<ImageType> getTypeClass() {
        return ImageType.class;
    }

    public enum ImageType implements ComponentType {

        /**
         * Only 16 colours are used, that way each byte contains the info for 2 pixels (each described by 4 bits).
         * An example image of this type would be "crwd.ssh" (crowd textures)
         *
         * Note: for now I'm just guessing it's this value and not {@link ColorTableLookupType} describing this property
         *          as I have not found an example where they change independently
         */
        LOW_RES_4BPP("01", new _4BitByteToPixelStrategy(), 0.5),

        /**
         * 256 colours are used, each byte contains the info for exactly 1 pixel.
         */
        DEFAULT_8BPP("02", new _8BitByteToPixelStrategy(), 1),

        /**
         * Each 4 bytes contains the info for exactly 1 pixel. No Palette is used
         */
        NO_PALETTE_32BPP("05", new _32BitByteToPixelWithoutPaletteStrategy(), 4),

        /**
         * Not much is known; only used by "backs.ssh"
         */
        HIGH_REZ("82", new _8BitByteToPixelStrategy(), 4);


        final String value;
        final ByteToPixelStrategy byteToPixelStrategy;
        final double bytesPerPixel;

        ImageType(final String value, final ByteToPixelStrategy byteToPixelStrategy, final double bytesPerPixel) {
            this.value = value;
            this.byteToPixelStrategy = byteToPixelStrategy;
            this.bytesPerPixel = bytesPerPixel;
        }

        public ByteToPixelStrategy getByteToPixelStrategy(){
            return byteToPixelStrategy;
        }

        public double getBytesPerPixel(){
            return bytesPerPixel;
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
