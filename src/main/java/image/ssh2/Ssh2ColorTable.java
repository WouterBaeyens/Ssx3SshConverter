package image.ssh2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Ssh2ColorTable {

    private final RandomAccessFile sshFile;
    private final long filePosition;

    private final Ssh2ColorTableHeader ssh2ColorTableHeader;

    public Ssh2ColorTable(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.sshFile = sshFile;
        this.filePosition = filePosition;
        this.ssh2ColorTableHeader = deserializeColorTableHeader();
    }

    private Ssh2ColorTableHeader deserializeColorTableHeader() throws IOException {
        return new Ssh2ColorTableHeader(sshFile, filePosition);
    }
}
