package image.ssh2;

import com.mycompany.sshtobpmconverter.Pixel2;
import image.ssh2.colortableheader.lookuptrategies.LookupStrategy;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Optional;

public class Ssh2ColorTableTable {

    private static final int DEFAULT_BYTES_PER_TABLE_ENTRY = 4;

    private int amountOfEntries;
    private int amountOfZeroEntries;
    //private BiMap<Byte, Pixel2> colorMap = HashBiMap.create();
    private HashMap<Byte, Pixel2> colorMap = new HashMap<>();

    public Ssh2ColorTableTable(final ByteBuffer buffer, final long size, final LookupStrategy lookupStrategy) {
        readTable(buffer, size, lookupStrategy);
    }

    private void readTable(final ByteBuffer buffer, final long size, final LookupStrategy lookupStrategy) {
        this.amountOfEntries = (int) (size / DEFAULT_BYTES_PER_TABLE_ENTRY);
        byte[] entry = new byte[DEFAULT_BYTES_PER_TABLE_ENTRY];
        for (int entryNr = 0; entryNr < amountOfEntries; entryNr++) {
            buffer.get(entry);
            Pixel2 pixel = Pixel2.createPixelFromLE(entry);
            if (pixel.isPixelEmpty()) {
                amountOfZeroEntries++;
            } else {
                colorMap.put(lookupStrategy.getByteLinkedToTableEntryNr(entryNr), Pixel2.createPixelFromLE(entry));
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
        return Optional.ofNullable(colorMap.get(byte_)).orElse(Pixel2.getDefaultPixel());
    }

    public void printFormatted() {
        System.out.println("--COLOR TABLE--");
        System.out.print("colours: " + amountOfEntries);
        System.out.print(" | Empty colours: " + getAmountOfNullEntries());
        System.out.println(" | Alpha colours: " + getAmountOfSpecialEntries());
        System.out.println();
    }
}
