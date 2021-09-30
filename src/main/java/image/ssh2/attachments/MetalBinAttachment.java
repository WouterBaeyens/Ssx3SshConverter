package image.ssh2.attachments;

import image.ImgSubComponent;
import image.ssh2.attachments.*;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class MetalBinAttachment implements Attachment {

    private final AttachmentTypeTag attachmentTypeTag;
    private final MetalBinSizeTag metalBinSizeTag;
    private final MetalBinContentTag metalBinContentTag;

    private final List<ImgSubComponent> componentsOrdered;

    public MetalBinAttachment(final ByteBuffer sshFileBuffer) {
        this.attachmentTypeTag = new AttachmentTypeTag(sshFileBuffer);
        this.metalBinSizeTag = new MetalBinSizeTag(sshFileBuffer);
        this.metalBinContentTag = new MetalBinContentTag(sshFileBuffer);
        componentsOrdered = List.of(attachmentTypeTag, metalBinSizeTag, metalBinContentTag);
    }

    public long getEndPosition() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }


    public void printFormatted() {
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
