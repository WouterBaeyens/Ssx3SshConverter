package image.ssh2;

import image.ssh2.attachments.*;
import image.ssh2.fileheader.FillerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Ssh2ImageAttachments {

    private static Logger LOGGER = LoggerFactory.getLogger(FillerTag.class);

    // all these attachments are optional
    private final int startPosition;
    private MetalBinAttachment metalBinAttachment;
    private ImageNameAttachment imageNameAttachment;
    private HotSpotAttachment hotSpotAttachment;

    private List<Attachment> attachments = new ArrayList<>();

    public Ssh2ImageAttachments(final ByteBuffer sshFileBuffer) {
        this.startPosition = sshFileBuffer.position();
        readAttachments(sshFileBuffer);
    }

    public long getEndPosition() {
        if (attachments.isEmpty()) {
            return startPosition;
        } else {
            return attachments.get(attachments.size() - 1).getEndPosition();
        }
    }

    private void readAttachments(final ByteBuffer sshFileBuffer) {
        while (sshFileBuffer.hasRemaining()) {
            AttachmentTypeTag attachmentTypeTag = new AttachmentTypeTag(sshFileBuffer.duplicate());
            Optional<AttachmentTypeTag.AttachmentType> attachmentType = attachmentTypeTag.getType();

            if (attachmentType.isEmpty()) {
                LOGGER.warn("Unknown componentTag at " + sshFileBuffer.position() + ": " + attachmentTypeTag.getInfo());
                break;
            }
            readAttachment(sshFileBuffer, attachmentType.get());
            // todo - find decent logic when to stop instead of catching and breaking.
            //          Possible solutions:
            //           - Calculate the end-position
            //           - Use the start position of the next image header
            //           - Use the content size tag of the attachments somehow
            //           - If tag 70 is always present and always the last one, this can be used
        }
    }

    public Optional<String> getFullName() {
        return Optional.ofNullable(imageNameAttachment).map(ImageNameAttachment::getFullName);
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
            case HOT_SPOT:
                this.hotSpotAttachment = new HotSpotAttachment(sshFileBuffer);
                attachments.add(hotSpotAttachment);
                break;
            default:
                throw new IllegalStateException("Recognized attachement; but no logic is implemented to read " + attachmentType);
        }
    }

    private Optional<AttachmentTypeTag.AttachmentType> peekNextAttachmentType(final ByteBuffer sshFileBuffer) {
        return new AttachmentTypeTag(sshFileBuffer.duplicate()).getType();
    }

    public void printFormatted() {
        System.out.println("--ATTACHMENTS--");
        attachments.forEach(Attachment::printFormatted);
    }
}
