package image.ssh2.colortableheader.lookuptrategies;

import util.ByteUtil;

public class IndexLookupStrategy implements LookupStrategy{

    /**
     * Each table entry/index is linked to a certain byte or "PixelCode"
     * This method finds this byte based on the entry number or index
     * <p>
     * PixelCode refers to the byte, whose pixel rgb value can be found at [index]
     * <p>
     * ex: - entry 0 is linked to byte 0x00
     * - entry 1 is linked to byte 0x01
     * - entry 9 is linked to byte 0x09
     * - entry 16 is linked to byte 0x10
     * ...
     *
     * @param index this is the position in the table
     * @return the Hex-code that is linked to the value in this table
     */
    public byte getByteLinkedToTableEntryNr(int index) {
        return (byte) index;
    }

}
