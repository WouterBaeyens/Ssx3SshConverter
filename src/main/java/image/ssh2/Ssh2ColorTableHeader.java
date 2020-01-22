package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.colortableheader.ColorTableEntriesCopyTag;
import image.ssh2.colortableheader.ColorTableEntriesTag;
import image.ssh2.colortableheader.ColorTableSizeTag;
import image.ssh2.colortableheader.ColorTableType2;
import image.ssh2.colortableheader.ColorTableType3;
import image.ssh2.colortableheader.ColorTableTypeTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class Ssh2ColorTableHeader {

    //these components (in this order) make up the complete tableHeader
    private final ColorTableTypeTag typeTag;
    private final ColorTableSizeTag sizeTag;
    private final ColorTableEntriesTag amountOfEntriesTag;
    private final ColorTableType2 actualTableLocation;
    private final ColorTableEntriesCopyTag amountOfEntriesCopyTag;
    private final ColorTableType3 colorTableType3;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ColorTableHeader(final RandomAccessFile sshFile, final long startPosition) throws IOException {
        this.typeTag = new ColorTableTypeTag(sshFile, startPosition);
        this.sizeTag = new ColorTableSizeTag(sshFile, typeTag.getEndPos());
        this.amountOfEntriesTag = new ColorTableEntriesTag(sshFile, sizeTag.getEndPos());
        this.actualTableLocation = new ColorTableType2(sshFile, amountOfEntriesTag.getEndPos());
        this.amountOfEntriesCopyTag = new ColorTableEntriesCopyTag(sshFile, actualTableLocation.getEndPos(), amountOfEntriesTag.getConvertedValue());
        this.colorTableType3 = new ColorTableType3(sshFile, amountOfEntriesCopyTag.getEndPos());

        this.componentsOrdered = List.of(typeTag, sizeTag, amountOfEntriesTag, actualTableLocation, amountOfEntriesCopyTag, colorTableType3);
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
        return getStartPosition() + fullColorTableComponentSize;
    }
}
