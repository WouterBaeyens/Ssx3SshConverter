package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.footer.FooterBodyNameTag;
import image.ssh2.footer.FooterBodyTypeTag;
import image.ssh2.footer.FooterBodyUnknown1Tag;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class Ssh2ImageFooterFooter {

    private final FooterBodyTypeTag footerBodyTypeTag;
    private final FooterBodyUnknown1Tag footerBodyUnknown1Tag;
    private final FooterBodyNameTag footerBodyNameTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ImageFooterFooter(final ByteBuffer sshFileBuffer) throws IOException {
        this.footerBodyTypeTag = new FooterBodyTypeTag(sshFileBuffer);
        this.footerBodyUnknown1Tag = new FooterBodyUnknown1Tag(sshFileBuffer);
        this.footerBodyNameTag = new FooterBodyNameTag(sshFileBuffer);

        this.componentsOrdered = List.of(footerBodyTypeTag, footerBodyUnknown1Tag, footerBodyNameTag);
    }

    public void printFormatted() {
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
