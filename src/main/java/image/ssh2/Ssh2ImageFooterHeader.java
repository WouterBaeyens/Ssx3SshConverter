package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.footer.FooterContentSizeTag;
import image.ssh2.footer.FooterHeaderTypeTag;
import image.ssh2.footer.FooterHeaderUnknown1Tag;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Example footer of allegra (single-picture) : p���allegra�����
 */
public class Ssh2ImageFooterHeader {

    private final FooterHeaderTypeTag footerHeaderTypeTag;
    private final FooterContentSizeTag footerContentSizeTag;
    private final FooterHeaderUnknown1Tag footerHeaderUnknown1Tag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ImageFooterHeader(final ByteBuffer sshFileBuffer) throws IOException {
        this.footerHeaderTypeTag = new FooterHeaderTypeTag(sshFileBuffer);
        this.footerContentSizeTag = new FooterContentSizeTag(sshFileBuffer);
        this.footerHeaderUnknown1Tag = new FooterHeaderUnknown1Tag(sshFileBuffer);
        componentsOrdered = List.of(footerHeaderTypeTag, footerContentSizeTag, footerHeaderUnknown1Tag);
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
