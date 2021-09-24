package image.ssh2;

import com.mycompany.sshtobpmconverter.Pixel2;
import util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Optional;

public class Ssh2ColorTableTable {

    private static final int DEFAULT_BYTES_PER_TABLE_ENTRY = 4;

    private int amountOfEntries;
    private int amountOfZeroEntries;
    //private BiMap<Byte, Pixel2> colorMap = HashBiMap.create();
    private HashMap<Byte, Pixel2> colorMap = new HashMap<>();

    public Ssh2ColorTableTable(final ByteBuffer buffer, final long size) {
        readTable(buffer, size);
    }

    private void readTable(final ByteBuffer buffer, final long size) {
        this.amountOfEntries = (int) (size / DEFAULT_BYTES_PER_TABLE_ENTRY);
        byte[] entry = new byte[DEFAULT_BYTES_PER_TABLE_ENTRY];
        for (int entryNr = 0; entryNr < amountOfEntries; entryNr++) {
            buffer.get(entry);
            Pixel2 pixel = Pixel2.createPixelFromLE(entry);
            if (pixel.isPixelEmpty()) {
                amountOfZeroEntries++;
            } else {
                colorMap.put(getByteLinkedToTableEntryNr(entryNr), Pixel2.createPixelFromLE(entry));
            }
        }
    }

    public long getAmountOfNullEntries() {
        return amountOfZeroEntries;
    }

    public long getAmountOfStoredEntries() {
        return colorMap.values().size();
    }

    public long getAmountOfSpecialEntries() {
        return colorMap.values().stream().filter(Pixel2::isSpecialAlpha).count();
    }

    public Pixel2 getPixelFromByte(byte byte_) {
        return Optional.ofNullable(colorMap.get(byte_)).orElse(Pixel2.getBlackPixel());
    }

    /**
     * Each table entry/index is linked to a certain byte or "PixelCode"
     * This method finds this byte based on the entry number or index
     * <p>
     * PixelCode refers to the byte, whose pixel rgb value can be found at [index]
     * <p>
     * ex: - entry 0 is linked to byte 0x00
     * - entry 1 is linked to byte 0x01
     * - entry 9 is linked to byte 0x11
     * - entry 16 is linked to byte 0x08
     * ...
     *
     * @param index this is the position in the table
     * @return the Hex-code that is linked to the value in this table
     */
    private byte getByteLinkedToTableEntryNr(int index) {
        return ByteUtil.switchBit4And5((byte) index);
    }

    public void printFormatted() {
        System.out.println("--COLOR TABLE--");
        System.out.print("colours: " + amountOfEntries);
        System.out.print(" | Empty colours: " + amountOfZeroEntries);
        System.out.println(" | Alpha colours: " + getAmountOfSpecialEntries());
    }
}
