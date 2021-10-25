package image.ssh2.colortableheader;

import image.ImgSubComponent;
import image.ssh2.colortableheader.lookuptrategies.IndexLookupStrategy;
import image.ssh2.colortableheader.lookuptrategies.IndexWithMiddleBitsSwitchedLookupStrategy;
import image.ssh2.colortableheader.lookuptrategies.LookupStrategy;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 * The purpose and info within this tag are completely unknown.
 *
 * <p>
 * Note: even the name TableType2Tag is just a wild guess
 */
public class ColorTableLookupType extends ImgSubComponent {

    private static final long DEFAULT_SIZE = 6;

    public ColorTableLookupType(final ByteBuffer buffer) {
        super(buffer, DEFAULT_SIZE);
    }

    @Override
    public String getInfo() {
        return "?TableType2?: " + LookupType.getInfo(getBytes());
    }

    public LookupType getLookupType(){
        return LookupType.getLookupType(getBytes())
                .orElse(LookupType.DEFAULT);
    }

    public enum LookupType {
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

        public static Optional<LookupType> getLookupType(final byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);
            return Arrays.stream(values())
                    .filter(fileType -> fileType.value.equals(dataAsString))
                    .findAny();
        }

        public static String getInfo(final byte[] data) {
            String dataAsString = ByteUtil.bytesToHex(data);
            return getLookupType(data).map(matchingType -> matchingType + "(" + matchingType.value + ")")
                    .orElseGet(() -> "Unknown type (" + dataAsString + ")");
        }
    }
}
