package bam.data.image.header.imagetable;

import com.mycompany.sshtobpmconverter.Pixel2;
import image.ColorTable;
import image.UnknownComponent;
import image.ssh2.Ssh2ColorTableHeader;
import image.ssh2.Ssh2ColorTableTable;
import image.ssh2.fileheader.FillerTag;

import java.nio.ByteBuffer;

public class BamImageColorTable implements ColorTable {

    private final Ssh2ColorTableHeader ssh2ColorTableHeader;
    //private final TotalHeaderWithPaddingSizeTag totalHeaderWithPaddingSize;
    private final Ssh2ColorTableTable actualTable;

    public BamImageColorTable(final ByteBuffer buffer) {
        this.ssh2ColorTableHeader = new Ssh2ColorTableHeader(buffer);
        //this.totalHeaderWithPaddingSize = new TotalHeaderWithPaddingSizeTag(buffer);
        //new FillerTag.Reader().withDesiredStartAddress(0x0100).withPrefix(new byte[]{(byte) 0x80}).read(buffer);

        // Note regarding table size: BAM_001 IMG 009 has 49 actual colors, but the table is actually 64, (with 0-filled in the middle)
        this.actualTable = new Ssh2ColorTableTable(buffer, ssh2ColorTableHeader.getTableSize(), ssh2ColorTableHeader.getLookupStrategy());
    }

//    private int getPaddingSize(){
//        int padding = totalHeaderWithPaddingSize.getConvertedValue() - totalHeaderWithPaddingSize.getSize() - ssh2ColorTableHeader.getTableHeaderSize();
//        if(padding < 0){
//            String calculationInfo = String.format("totalSize(=%s) - totalSizeTag.length(=%s) - tableHeader.size(=%s)", totalHeaderWithPaddingSize.getConvertedValue(), totalHeaderWithPaddingSize.getSize(), ssh2ColorTableHeader.getTableHeaderSize());
//            throw new IllegalStateException("Color table padding size may not be negative, but was " + padding + ". (Calculation details: " + calculationInfo + ")");
//        }
//        return padding;
//    }

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
