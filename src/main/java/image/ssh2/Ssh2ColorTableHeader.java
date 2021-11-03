package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableEntriesTag;
import image.ssh2.colortableheader.ColorTableWidthTag;
import image.ssh2.colortableheader.ColorTableSizeTag;
import image.ssh2.colortableheader.ColorTableHeightTag;
import image.ssh2.colortableheader.ColorTableLookupType;
import image.ssh2.colortableheader.ColorTableTypeTag;
import image.ssh2.colortableheader.lookuptrategies.LookupStrategy;
import image.ssh2.fileheader.FillerTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

public class Ssh2ColorTableHeader {

    //these components (in this order) make up the complete tableHeader
    private final ColorTableTypeTag typeTag;
    private final ColorTableSizeTag sizeTag;
    private final ColorTableWidthTag amountOfEntriesTag;
    private final ColorTableHeightTag actualTableLocation;
    private final ColorTableEntriesTag amountOfEntriesCopyTag;
    private final ColorTableLookupType colorTableLookupType;
    private final FillerTag padding;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ColorTableHeader(final ByteBuffer sshFileBuffer) {
        this.typeTag = new ColorTableTypeTag(sshFileBuffer);
        this.sizeTag = new ColorTableSizeTag(sshFileBuffer);
        this.amountOfEntriesTag = new ColorTableWidthTag(sshFileBuffer);
        this.actualTableLocation = new ColorTableHeightTag(sshFileBuffer);
        this.amountOfEntriesCopyTag = new ColorTableEntriesTag(sshFileBuffer, amountOfEntriesTag.getConvertedValue());
        this.colorTableLookupType = new ColorTableLookupType(sshFileBuffer);
        if (colorTableLookupType.getLookupType().hasPadding()) {
            this.padding = new FillerTag.Reader().withPrefix(new byte[]{(byte)0x80}).withDesiredStartAddress(0x080).withAddressIncrement(0x080).read(sshFileBuffer);
        } else {
            this.padding = new FillerTag.Reader().withFillerSize(0).read(sshFileBuffer);
        }
        this.componentsOrdered = List.of(typeTag, sizeTag, amountOfEntriesTag, actualTableLocation, amountOfEntriesCopyTag, colorTableLookupType, padding);

    }

    public void printFormatted() {
        System.out.println("--COLOR TABLE HEADER--");
        System.out.println("table_header_start(" + ByteUtil.printLongWithHex(getStartPosition()) + ") | table_header_end/table_start(" + ByteUtil.printLongWithHex(getEndPosition()) + ") | table_end(" + ByteUtil.printLongWithHex(getTableEndPosition()) + ")");

        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

    public long getStartPosition() {
        return componentsOrdered.get(0).getStartPos();
    }

    public long getEndPosition() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }

    public long getTableStartPosition() {
        return getEndPosition();
    }

    public long getTableSize() {
        return getTableEndPosition() - getTableStartPosition();
    }

    public LookupStrategy getLookupStrategy(){
        return colorTableLookupType.getLookupType().getLookupStrategy();
    }

    public long getTableEndPosition() {
        long fullColorTableComponentSize = sizeTag.getConvertedValue();
        if(fullColorTableComponentSize == 0){ // content is missing - we need to calculate it ourselves (was only the case on G268 dir type)
            fullColorTableComponentSize = calculateFullTableComponentSize();
        }
        return getStartPosition() + fullColorTableComponentSize;
    }

    public int getTableHeaderSize(){
        return Math.toIntExact(componentsOrdered.get(componentsOrdered.size() - 1).getEndPos() - componentsOrdered.get(0).getStartPos());
    }

    private long calculateFullTableComponentSize(){
        final long calculatedHeaderSize = getTableHeaderSize();
        final long calculatedTableSize = getCalculatedTableSize();
        final long fullColorTableComponentSize = calculatedHeaderSize + calculatedTableSize;
        final long fullSizeWithBuffer = (long) Math.ceil((double)fullColorTableComponentSize/16) * 16; // lenght increases in groups of 16, 0x00 filled
        return fullSizeWithBuffer;
    }

    private long getCalculatedTableSize(){
        final int bytesPerEntry = 4;
        final boolean switchesIndex = Set.of(ColorTableLookupType.LookupType.DEFAULT_BAM, ColorTableLookupType.LookupType.DEFAULT).contains(colorTableLookupType.getLookupType());
        int amountOfEntries = amountOfEntriesTag.getConvertedValue();
        int calculatedAmount;
        if(switchesIndex){
            int bit3 = ByteUtil.getBit(amountOfEntries, 3);
            int bit4 = ByteUtil.getBit(amountOfEntries, 4);
            if(bit4 == bit3){
                calculatedAmount = roundUpToNextMultiple(amountOfEntries, 4);
            } else if(bit4 == 1 && bit3 == 0) {
                calculatedAmount = roundUpToNextMultiple(amountOfEntries + 1, 8);
            } else {
                int amountOfEntriesSwapped = ByteUtil.swapBits(amountOfEntries, 3,4);
                calculatedAmount = roundUpToNextMultiple(amountOfEntriesSwapped, 4);
            }
        } else {
            calculatedAmount = amountOfEntriesTag.getConvertedValue();
        }
        return calculatedAmount * bytesPerEntry;
    }

    private int roundUpToNextMultiple(int number, int multiple){
        return (int) Math.ceil((double) number/multiple)*multiple;
    }

    private int roundDownToPreviousMultiple(int number, int multiple){
        return (int) Math.floor((double) number/multiple)*multiple;
    }
}
