package image.ssh2;

import image.ssh2.attachments.Attachment;
import image.ssh2.attachments.AttachmentTypeTag;
import image.ssh2.attachments.ImageNameAttachment;
import image.ssh2.attachments.MetalBinAttachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Ssh2ImageAttachments {

    // all these attachments are optional
    private MetalBinAttachment metalBinAttachment;
    private ImageNameAttachment imageNameAttachment;

    private List<Attachment> attachments = new ArrayList<>();

    public Ssh2ImageAttachments(final ByteBuffer sshFileBuffer) {
        readAttachments(sshFileBuffer);
    }

    private void readAttachments(final ByteBuffer sshFileBuffer) {
        while (sshFileBuffer.hasRemaining()) {
            try {
                AttachmentTypeTag.AttachmentType attachmentType = peekNextAttachmentType(sshFileBuffer);
                readAttachment(sshFileBuffer, attachmentType);
            } catch (NoSuchElementException e) {
                // todo - find decent logic when to stop instead of catching and breaking.
                //          Possible solutions:
                //           - Calculate the end-position
                //           - Use the start position of the next image header
                //           - Use the content size tag of the attachments somehow
                //           - If tag 70 is always present and always the last one, this can be used
                System.out.println("ERROR at " + sshFileBuffer.position() + ": " + e.getMessage());
                break;
            }
        }
    }

    private void readAttachment(final ByteBuffer sshFileBuffer, final AttachmentTypeTag.AttachmentType attachmentType) {
        switch (attachmentType) {
            case IMAGE_NAME:
                this.imageNameAttachment = new ImageNameAttachment(sshFileBuffer);
                attachments.add(imageNameAttachment);
                break;
            case METAL_BIN:
                this.metalBinAttachment = new MetalBinAttachment(sshFileBuffer);
                attachments.add(metalBinAttachment);
                break;
        }
    }

    private AttachmentTypeTag.AttachmentType peekNextAttachmentType(final ByteBuffer sshFileBuffer) {
        final byte attachmentTypeTag = sshFileBuffer.get(sshFileBuffer.position());
        return AttachmentTypeTag.AttachmentType.getAttachmentType(attachmentTypeTag);
    }

    public void printFormatted() {
        System.out.println("--ATTACHMENTS--");
        attachments.forEach(Attachment::printFormatted);
    }
}
