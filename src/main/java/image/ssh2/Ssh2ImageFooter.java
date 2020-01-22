package image.ssh2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Ssh2ImageFooter {

    private final RandomAccessFile sshFile;
    private final long filePosition;

    private final Ssh2ImageFooterHeader ssh2ImageFooterHeader;
    private final Ssh2ImageFooterFooter ssh2ImageFooterFooter;

    public Ssh2ImageFooter(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.sshFile = sshFile;
        this.filePosition = filePosition;
        this.ssh2ImageFooterHeader = deserializeFooterHeader(filePosition);
        this.ssh2ImageFooterFooter = deserializeFooterFooter(ssh2ImageFooterHeader.getEndPosition());
    }

    private Ssh2ImageFooterHeader deserializeFooterHeader(long filePosition) throws IOException {
        return new Ssh2ImageFooterHeader(sshFile, filePosition);
    }

    private Ssh2ImageFooterFooter deserializeFooterFooter(long filePosition) throws IOException {
        return new Ssh2ImageFooterFooter(sshFile, filePosition);
    }

    public void printFormatted() {
        System.out.println("--FOOTER--");
        ssh2ImageFooterHeader.printFormatted();
        ssh2ImageFooterFooter.printFormatted();
    }
}
