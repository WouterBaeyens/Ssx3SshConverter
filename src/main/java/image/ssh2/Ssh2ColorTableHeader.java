package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableEntriesCopyTag;
import image.ssh2.colortableheader.ColorTableEntriesTag;
import image.ssh2.colortableheader.ColorTableSizeTag;
import image.ssh2.colortableheader.ColorTableTypeTag;
import image.ssh2.colortableheader.TableType2;
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

    // This is the start of the actual table within the colourTable component
    private final long tableHeaderEndPosition;

    // This is the end of this component; it is also the end of the actual table
    private final long tableEndPosition;

    private final int amountOfColours;

    public Ssh2ColorTableHeader(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.tableHeaderStartPosition = filePosition;
        final ColorTableTypeTag typeTag = new ColorTableTypeTag(sshFile, filePosition);
        subComponents.add(typeTag);

        final ColorTableSizeTag sizeTag = new ColorTableSizeTag(sshFile, typeTag.getEndPos());
        tableEndPosition = tableHeaderStartPosition + sizeTag.getConvertedValue();
        subComponents.add(sizeTag);

        final ColorTableEntriesTag amountOfEntriesTag = new ColorTableEntriesTag(sshFile, sizeTag.getEndPos());
        this.amountOfColours = amountOfEntriesTag.getConvertedValue();
        subComponents.add(amountOfEntriesTag);

        final TableType2 actualTableLocation = new TableType2(sshFile, amountOfEntriesTag.getEndPos());
        subComponents.add(actualTableLocation);

        final ColorTableEntriesCopyTag amountOfEntriesCopyTag = new ColorTableEntriesCopyTag(sshFile, actualTableLocation.getEndPos(), amountOfColours);
        subComponents.add(amountOfEntriesCopyTag);

        this.tableHeaderEndPosition = amountOfEntriesCopyTag.getEndPos();
        printFormatted();
    }

    public void printFormatted() {
        System.out.println("--COLOR TABLE HEADER--");
        long actualTableSize = tableEndPosition - tableHeaderEndPosition;
        long calculatedActualTableSize = amountOfColours * COLOURS_PER_PIXEL;
        System.out.println("table_header_start(" + ByteUtil.printLongWithHex(tableHeaderStartPosition) + ") | table_header_end/table_start(" + ByteUtil.printLongWithHex(tableHeaderEndPosition) + ") | table_end(" + ByteUtil.printLongWithHex(tableEndPosition) + ")");
        if (calculatedActualTableSize != actualTableSize) {
            System.out.println("ERROR: table_size=(" + ByteUtil.printLongWithHex(calculatedActualTableSize) + "); should be(" + ByteUtil.printLongWithHex(actualTableSize) + ")");
        }
        System.out.println(PrintUtil.toRainbow(subComponents.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = subComponents.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
