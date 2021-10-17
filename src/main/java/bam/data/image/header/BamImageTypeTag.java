package bam.data.image.header;

import bam.ssb.SsbComponentTypeTag;
import image.ImgSubComponent;
import image.ssh2.fileheader.FileTypeTag;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This could be a way more specific identifier, for now I just use it as "is image"
 * Which could be an error as the other files don't even contain a header for some reason.
 */
public class BamImageTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 1;

    public BamImageTypeTag(final ByteBuffer buffer){
        super(buffer, DEFAULT_SIZE);
    }

    public BamImageType getImageType(){
        return BamImageType.getBamComponentType(getBytes())
                .orElse(BamImageType.UNKNOWN);
    }

    @Override
    public String getInfo() {
        return "bam component type: " + BamImageType.getInfo(getBytes());
    }


    public enum BamImageType{

        DEFAULT("09"),
        UNKNOWN("XX");

        final String value;

        BamImageType(String value){
            this.value = value.replaceAll(" ", "");
        };

        public static String getInfo(byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);
            return getBamComponentType(data)
                    .map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown (" + dataAsString + ")");
        }

        public static Optional<BamImageType> getBamComponentType(byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny();
        }
    }
}
