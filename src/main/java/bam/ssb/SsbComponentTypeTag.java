package bam.ssb;

import image.ImgSubComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SsbComponentTypeTag extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 4;

    public SsbComponentTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    public SsbComponentType getComponentType(){
        return SsbComponentType.getSsbComponentType(getBytes())
                .orElseThrow(() -> new NoSuchElementException(getInfo()));
    }

    @Override
    public String getInfo() {
        return "ssb component type: " + SsbComponentType.getInfo(getBytes());
    }

    public enum SsbComponentType {
        /**
         * Component of collection file
         */
        CBXS("CBXS"),

        /**
         * Last component of collection file
         */
        CEND("CEND");

        final String value;

        SsbComponentType(String value) {
            this.value = value;
        }

        public static String getInfo(byte[] data) {
            String dataAsString = new String(data);
            return getSsbComponentType(data)
                    .map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown (" + dataAsString + ")");
        }

        public static Optional<SsbComponentType> getSsbComponentType(byte[] data) {
            String dataAsString = new String(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny();
        }
    }

}
