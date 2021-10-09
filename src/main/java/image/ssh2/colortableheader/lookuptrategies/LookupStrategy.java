package image.ssh2.colortableheader.lookuptrategies;

import util.ByteUtil;

public interface LookupStrategy {

    /**
     * Each table entry/index is linked to a certain byte or "PixelCode"
     * This method finds this byte based on the entry number or index
     * <p>
     * PixelCode refers to the byte, whose pixel rgb value can be found at [index]
     * <p>
     * @param index this is the position in the table
     * @return the Hex-code that is linked to the value in this table
     */
    byte getByteLinkedToTableEntryNr(int index);

}
