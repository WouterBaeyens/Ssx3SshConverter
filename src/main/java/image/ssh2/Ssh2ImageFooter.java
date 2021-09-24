package image.ssh2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Ssh2ImageFooter {

    private final Ssh2ImageFooterHeader ssh2ImageFooterHeader;
    private final Ssh2ImageFooterFooter ssh2ImageFooterFooter;

    public Ssh2ImageFooter(final ByteBuffer sshFileBuffer) throws IOException {
        this.ssh2ImageFooterHeader = new Ssh2ImageFooterHeader(sshFileBuffer);
        this.ssh2ImageFooterFooter = new Ssh2ImageFooterFooter(sshFileBuffer);
    }

    public void printFormatted() {
        System.out.println("--FOOTER--");
        ssh2ImageFooterHeader.printFormatted();
        ssh2ImageFooterFooter.printFormatted();
    }
}
