package image.ssh2;

import com.mycompany.sshtobpmconverter.Pixel2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Ssh2ColorTable {

    private final Ssh2ColorTableHeader ssh2ColorTableHeader;
    private final Ssh2ColorTableTable actualTable;

    public Ssh2ColorTable(final ByteBuffer sshFileBuffer) throws IOException {
        this.ssh2ColorTableHeader = new Ssh2ColorTableHeader(sshFileBuffer);
        this.actualTable = new Ssh2ColorTableTable(sshFileBuffer, ssh2ColorTableHeader.getTableSize());
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
