package image.ssh2;

import image.ssh2.footer.FooterHeaderTypeTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Optional;

public class Ssh2ImageFooter {

    private final Optional<Ssh2ImageFooterHeader> ssh2ImageFooterHeader;
    private final Ssh2ImageFooterFooter ssh2ImageFooterFooter;

    public Ssh2ImageFooter(final ByteBuffer sshFileBuffer) throws IOException {
        this.ssh2ImageFooterHeader = hasImageFooterHeader(sshFileBuffer) ? Optional.of(new Ssh2ImageFooterHeader(sshFileBuffer)) : Optional.empty();
        this.ssh2ImageFooterFooter = new Ssh2ImageFooterFooter(sshFileBuffer);
    }

    /**
     * checks if the next character in the buffer is an "i", which indicates the following 16 bytes contain an ImageFooterHeader
     * The fact that this header is only sometimes present (all ssh-files with multiple images and some coursepic's - Perpendiculous and Kick Doubt)
     * suggests this is not really a footerHeader, in fact I have no idea what it is or why it is only sometimes present.
     * Note: I have not yet seen this footerHeader when the image name is longer than 4 char's; this could be a coincidence
     *
     * @return
     */
    private boolean hasImageFooterHeader(final ByteBuffer sshFileBuffer){
        final byte footerTag = sshFileBuffer.get(sshFileBuffer.position());
        return FooterHeaderTypeTag.isRecognizedFooterHeaderTag(footerTag);
    }

    public void printFormatted() {
        System.out.println("--FOOTER--");
        ssh2ImageFooterHeader.ifPresent(Ssh2ImageFooterHeader::printFormatted);
        ssh2ImageFooterFooter.printFormatted();
    }
}
