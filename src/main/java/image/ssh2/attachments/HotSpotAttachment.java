package image.ssh2.attachments;

import image.ImgSubComponent;
import image.UnknownComponent;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class HotSpotAttachment implements Attachment{

    private final AttachmentTypeTag attachmentTypeTag;
    private final UnknownComponent u01_u30;
    private final List<ImgSubComponent> componentsOrdered;

    public HotSpotAttachment(final ByteBuffer sshFileBuffer){
        this.attachmentTypeTag = new AttachmentTypeTag(sshFileBuffer);
        this.u01_u30 = new UnknownComponent(sshFileBuffer, 48 - 1);
        componentsOrdered = List.of(attachmentTypeTag, u01_u30);
    }

    @Override
    public void printFormatted() {
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

    @Override
    public long getEndPosition() {
        return u01_u30.getEndPos();
    }
}
