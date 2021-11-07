package image.ssh2.attachments;

import image.AmountTag;
import image.ByteOrder;
import image.ImgSubComponent;
import image.UnknownComponent;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class HotSpotAttachment implements Attachment{

    private final AttachmentTypeTag attachmentTypeTag;
    private final AmountTag attachmentSize;
    private final UnknownComponent remaining;
    private final List<ImgSubComponent> componentsOrdered;

    public HotSpotAttachment(final ByteBuffer sshFileBuffer){
        this.attachmentTypeTag = new AttachmentTypeTag(sshFileBuffer);
        this.attachmentSize = new AmountTag.Reader()
                .withSize(3)
                .withByteOrder(ByteOrder.LITTLE_ENDIAN)
                .read(sshFileBuffer);
        int remainingSize = (int) (attachmentSize.getConvertedValue() - (attachmentTypeTag.getSize() + attachmentSize.getSize()));
        this.remaining = new UnknownComponent(sshFileBuffer, remainingSize);
        componentsOrdered = List.of(attachmentTypeTag, remaining);
    }

    @Override
    public void printFormatted() {
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

    @Override
    public long getEndPosition() {
        return remaining.getEndPos();
    }
}
