package image.ssh2.attachments;

import image.ImgSubComponent;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * This attachment contains the full name of the image, padded with 0x00.
 * Example imageName of allegra : p���allegra�����
 */
public class ImageNameAttachment implements Attachment {

    private final AttachmentTypeTag attachmentTypeTag;
    private final ImageNameSizeTag imageNameSizeTag;
    private final ImageNameContentTag imageNameContentTag;

    private final List<ImgSubComponent> componentsOrdered;

    public ImageNameAttachment(final ByteBuffer sshFileBuffer) {
        this.attachmentTypeTag = new AttachmentTypeTag(sshFileBuffer);
        this.imageNameSizeTag = new ImageNameSizeTag(sshFileBuffer);
        this.imageNameContentTag = new ImageNameContentTag(sshFileBuffer);

        this.componentsOrdered = List.of(attachmentTypeTag, imageNameSizeTag, imageNameContentTag);
    }

    public long getEndPosition() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }


    public String getFullName(){
        return imageNameContentTag.getConvertedValue();
    }

    public void printFormatted() {
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
