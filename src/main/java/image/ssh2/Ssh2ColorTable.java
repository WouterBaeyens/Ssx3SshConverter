package image.ssh2;

import com.mycompany.sshtobpmconverter.Pixel2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Ssh2ColorTable {

    private final RandomAccessFile sshFile;
    private final long filePosition;

    private final Ssh2ColorTableHeader ssh2ColorTableHeader;
    private final Ssh2ColorTableTable actualTable;

    public Ssh2ColorTable(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.sshFile = sshFile;
        this.filePosition = filePosition;
        this.ssh2ColorTableHeader = deserializeColorTableHeader();
        this.actualTable = deserializeColourTableTable(ssh2ColorTableHeader.getActualTableStartPosition(), ssh2ColorTableHeader.getActualTableSize());
    }

    private Ssh2ColorTableHeader deserializeColorTableHeader() throws IOException {
        return new Ssh2ColorTableHeader(sshFile, filePosition);
    }

    private Ssh2ColorTableTable deserializeColourTableTable(long filePosition, long tableSize) throws IOException {
        return new Ssh2ColorTableTable(sshFile, filePosition, tableSize);
    }

    public Pixel2 getPixelFromByte(byte byte_) {
        return actualTable.getPixelFromByte(byte_);
    }
}
