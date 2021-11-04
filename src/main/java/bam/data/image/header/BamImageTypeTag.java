package bam.data.image.header;

import bam.ssb.SsbComponentTypeTag;
import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.FileTypeTag;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * This could be a way more specific identifier, for now I just use it as "is image"
 * Which could be an error as the other files don't even contain a header for some reason.
 */
public class BamImageTypeTag extends ImgSubComponent implements TypeComponent<BamImageTypeTag.BamImageType> {

    private static final long DEFAULT_SIZE = 1;

    @Override
    public Class<BamImageType> getTypeClass() {
        return BamImageType.class;
    }

    public BamImageTypeTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    public BamImageType getImageType(){
        return getType().orElse(BamImageType.UNKNOWN);
    }

    @Override
    public String getInfo() {
        return "bam component type: " + getTypeInfo();
    }

    public enum BamImageType implements ComponentType {

        DEFAULT("09"),
        UNKNOWN("XX");

        final String value;

        BamImageType(String value){
            this.value = value.replaceAll(" ", "");
        };

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
