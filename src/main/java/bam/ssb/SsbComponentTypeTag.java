package bam.ssb;

import image.ImgSubComponent;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class SsbComponentTypeTag extends ImgSubComponent implements TypeComponent<SsbComponentTypeTag.SsbComponentType> {

    private static final long DEFAULT_SIZE = 4;

    @Override
    public Class<SsbComponentType> getTypeClass() {
        return SsbComponentType.class;
    }

    public SsbComponentTypeTag(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    public SsbComponentType getComponentType(){
        return getType()
                .orElseThrow(() -> new NoSuchElementException(getInfo()));
    }

    @Override
    public String getInfo() {
        return "ssb component type: " + getTypeInfo();
    }

    public enum SsbComponentType implements ComponentType {
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

        @Override
        public String getReadableValue() {
            return value;
        }

        @Override
        public Function<byte[], String> toReadable() {
            return String::new;
        }
    }

}
