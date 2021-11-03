package image.ssh2;

import image.ssh2.attachments.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Ssh2ImageAttachments {

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

    public long getEndPosition(){
        if(attachments.isEmpty()){
            return startPosition;
        } else {
            return attachments.get(attachments.size() - 1).getEndPosition();
        }
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

    public Optional<String> getFullName(){
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

    private AttachmentTypeTag.AttachmentType peekNextAttachmentType(final ByteBuffer sshFileBuffer) {
        final byte attachmentTypeTag = sshFileBuffer.get(sshFileBuffer.position());
        return AttachmentTypeTag.AttachmentType.getAttachmentType(attachmentTypeTag);
    }

    public void printFormatted() {
        System.out.println("--ATTACHMENTS--");
        attachments.forEach(Attachment::printFormatted);
    }
}
