package bam.data.image.header.imagetable;

import com.mycompany.sshtobpmconverter.Pixel2;
import image.ColorTable;
import image.UnknownComponent;
import image.ssh2.Ssh2ColorTableHeader;
import image.ssh2.Ssh2ColorTableTable;

import java.nio.ByteBuffer;

public class BamImageColorTable implements ColorTable {

    private final Ssh2ColorTableHeader ssh2ColorTableHeader;
    private final PaddingSizeTag paddingSize;
    private final UnknownComponent padding;
    private final Ssh2ColorTableTable actualTable;

    public BamImageColorTable(final ByteBuffer buffer) {
        this.ssh2ColorTableHeader = new Ssh2ColorTableHeader(buffer);
        this.paddingSize = new PaddingSizeTag(buffer);
        this.padding = new UnknownComponent(buffer, paddingSize.getConvertedValue() - paddingSize.getSize() - ssh2ColorTableHeader.getTableHeaderSize());
        this.actualTable = new Ssh2ColorTableTable(buffer, ssh2ColorTableHeader.getTableSize(), ssh2ColorTableHeader.getLookupStrategy());
    }

    public long getEndPosition() {
        return ssh2ColorTableHeader.getTableEndPosition();
    }

    public Pixel2 getPixelFromByte(byte byte_) {
        return actualTable.getPixelFromByte(byte_);
    }

    public void printFormatted() {
        ssh2ColorTableHeader.printFormatted();
        actualTable.printFormatted();
    }
}
