package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableEntriesTag;
import image.ssh2.colortableheader.ColorTableWidthTag;
import image.ssh2.colortableheader.ColorTableSizeTag;
import image.ssh2.colortableheader.ColorTableHeightTag;
import image.ssh2.colortableheader.ColorTableType2;
import image.ssh2.colortableheader.ColorTableTypeTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class Ssh2ColorTableHeader {

    //these components (in this order) make up the complete tableHeader
    private final ColorTableTypeTag typeTag;
    private final ColorTableSizeTag sizeTag;
    private final ColorTableWidthTag amountOfEntriesTag;
    private final ColorTableHeightTag actualTableLocation;
    private final ColorTableEntriesTag amountOfEntriesCopyTag;
    private final ColorTableType2 colorTableType2;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ColorTableHeader(final ByteBuffer sshFileBuffer) throws IOException {
        this.typeTag = new ColorTableTypeTag(sshFileBuffer);
        this.sizeTag = new ColorTableSizeTag(sshFileBuffer);
        this.amountOfEntriesTag = new ColorTableWidthTag(sshFileBuffer);
        this.actualTableLocation = new ColorTableHeightTag(sshFileBuffer);
        this.amountOfEntriesCopyTag = new ColorTableEntriesTag(sshFileBuffer, amountOfEntriesTag.getConvertedValue());
        this.colorTableType2 = new ColorTableType2(sshFileBuffer);

        this.componentsOrdered = List.of(typeTag, sizeTag, amountOfEntriesTag, actualTableLocation, amountOfEntriesCopyTag, colorTableType2);
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

    public long getTableEndPosition() {
        long fullColorTableComponentSize = sizeTag.getConvertedValue();
        if(fullColorTableComponentSize == 0){ // content is missing - we need to calculate it ourselves (was only the case on G268 dir type)
            fullColorTableComponentSize = calculateFullTableComponentSize();
        }
        return getStartPosition() + fullColorTableComponentSize;
    }

    private long calculateFullTableComponentSize(){
        final long calculatedHeaderSize = componentsOrdered.get(componentsOrdered.size() - 1).getEndPos() - componentsOrdered.get(0).getStartPos();
        final long calculatedTableSize = amountOfEntriesTag.getConvertedValue() * 4; // I assume 4 bytes here. the info is probably available somewhere
        final long fullColorTableComponentSize = calculatedHeaderSize + calculatedTableSize;
        final long fullSizeWithBuffer = (long) Math.ceil((double)fullColorTableComponentSize/16) * 16; // lenght increases in groups of 16, 0x00 filled
        return fullSizeWithBuffer;
    }
}
