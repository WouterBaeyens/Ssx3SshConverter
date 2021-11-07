package image.ssh2.colortableheader;

import image.ImgSubComponent;
import image.ssh2.colortableheader.lookuptrategies.IndexLookupStrategy;
import image.ssh2.colortableheader.lookuptrategies.IndexWithMiddleBitsSwitchedLookupStrategy;
import image.ssh2.colortableheader.lookuptrategies.LookupStrategy;
import image.ssh2.fileheader.ComponentType;
import image.ssh2.fileheader.TypeComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * The purpose and info within this tag are completely unknown.
 *
 * <p>
 * Note: even the name TableType2Tag is just a wild guess
 */
public class ColorTableLookupType extends ImgSubComponent implements TypeComponent<ColorTableLookupType.LookupType> {

    private static final long DEFAULT_SIZE = 6;

    @Override
    public Class<LookupType> getTypeClass() {
        return LookupType.class;
    }

    public ColorTableLookupType(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?TableType2?: " + getTypeInfo();
    }

    public LookupType getLookupType(){
        return getType().orElse(LookupType.DEFAULT);
    }

    public enum LookupType implements ComponentType {
        /**
         * Chooses the color
         */
        LOW_RES("000000000000", new IndexLookupStrategy(), false),

        /**
         * No idea what this is supposed to do
         */
        LOW_RES_BAM("000000100000", new IndexLookupStrategy(), true),


        /**
         */
        DEFAULT("000000200000", new IndexWithMiddleBitsSwitchedLookupStrategy(), false),

        /**
         */
        DEFAULT_BAM("000000300000", new IndexWithMiddleBitsSwitchedLookupStrategy(), true);

        final String value;
        final LookupStrategy lookupStrategy;
        final boolean hasPadding;

        LookupType(final String value, final LookupStrategy lookupStrategy, final boolean hasPadding) {
            this.value = value;
            this.lookupStrategy = lookupStrategy;
            this.hasPadding = hasPadding;
        }

        public LookupStrategy getLookupStrategy(){
            return lookupStrategy;
        }

        public boolean hasPadding(){
            return hasPadding;
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
