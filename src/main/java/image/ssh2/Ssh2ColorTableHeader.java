package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableEntriesCopyTag;
import image.ssh2.colortableheader.ColorTableEntriesTag;
import image.ssh2.colortableheader.ColorTableSizeTag;
import image.ssh2.colortableheader.ColorTableType2;
import image.ssh2.colortableheader.ColorTableType3;
import image.ssh2.colortableheader.ColorTableTypeTag;
import image.ssh2.colortableheader.ExtendedColorTableTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Ssh2ColorTableHeader {

    private static final int COLOURS_PER_PIXEL = 4;

    private final List<ImgSubComponent> subComponents = new ArrayList<>();

    // This is the start of this component; so also the start of the header
    private final long tableHeaderStartPosition;

    // This is the end of the base header fields
    private final long tableBaseHeaderEndPosition;

    // This is the start of the actual table within the colourTable component
    private final long actualTableStartPosition;

    // This is the end of this component; it is also the end of the actual table
    private final long tableComponentEndPosition;

    private final int amountOfColours;

    public Ssh2ColorTableHeader(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.tableHeaderStartPosition = filePosition;
        final ColorTableTypeTag typeTag = new ColorTableTypeTag(sshFile, filePosition);
        subComponents.add(typeTag);

        final ColorTableSizeTag sizeTag = new ColorTableSizeTag(sshFile, typeTag.getEndPos());
        tableComponentEndPosition = tableHeaderStartPosition + sizeTag.getConvertedValue();
        subComponents.add(sizeTag);

        final ColorTableEntriesTag amountOfEntriesTag = new ColorTableEntriesTag(sshFile, sizeTag.getEndPos());
        this.amountOfColours = amountOfEntriesTag.getConvertedValue();
        subComponents.add(amountOfEntriesTag);

        final ColorTableType2 actualTableLocation = new ColorTableType2(sshFile, amountOfEntriesTag.getEndPos());
        subComponents.add(actualTableLocation);

        final ColorTableEntriesCopyTag amountOfEntriesCopyTag = new ColorTableEntriesCopyTag(sshFile, actualTableLocation.getEndPos(), amountOfColours);
        subComponents.add(amountOfEntriesCopyTag);

        final ColorTableType3 colorTableType3 = new ColorTableType3(sshFile, amountOfEntriesCopyTag.getEndPos());
        this.tableBaseHeaderEndPosition = colorTableType3.getEndPos();
        subComponents.add(colorTableType3);

        this.actualTableStartPosition = tableComponentEndPosition - getTableSize();
        long extendedTableSize = actualTableStartPosition - tableBaseHeaderEndPosition;

        // to be replaced as it seems to be part of the table itself
        if (extendedTableSize > 0) {
            final ExtendedColorTableTag extendedColorTableTag = new ExtendedColorTableTag(sshFile, colorTableType3.getEndPos(), extendedTableSize);
            subComponents.add(extendedColorTableTag);
        }

        printFormatted();

    }

    public void printFormatted() {
        System.out.println("--COLOR TABLE HEADER--");
        long actualTableSize = tableComponentEndPosition - tableBaseHeaderEndPosition;
        long calculatedActualTableSize = amountOfColours * COLOURS_PER_PIXEL;
        System.out.println("table_header_start(" + ByteUtil.printLongWithHex(tableHeaderStartPosition) + ") | table_header_end/table_start(" + ByteUtil.printLongWithHex(tableBaseHeaderEndPosition) + ") | table_end(" + ByteUtil.printLongWithHex(tableComponentEndPosition) + ")");
        if (calculatedActualTableSize != actualTableSize) {
            System.out.println("WARNING: Extended header present!");
        }
        System.out.println(PrintUtil.toRainbow(subComponents.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = subComponents.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

    /**
     * This returns the table size; but it seems inaccurate. (amountOfColours might be the amount of different rgb values instead)
     *
     * @return
     */
    private long getTableSize() {
        return (long) amountOfColours * COLOURS_PER_PIXEL;
    }

    public long getTableStartPositionUncertain() {
        return tableComponentEndPosition - getTableSize();
    }

    public long getTableComponentEndPosition() {
        return tableComponentEndPosition;
    }

    public long getActualTableStartPosition() {
        return tableBaseHeaderEndPosition;
    }

    public long getActualTableSize() {
        return getTableComponentEndPosition() - getActualTableStartPosition();
    }
}
