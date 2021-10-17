package bam.data.image.header;

import image.ImgSubComponent;
import image.UnknownComponent;
import util.ByteUtil;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class BamImageComponentHeader {

    private final BamImageTypeTag bamImageTypeTag;
    private final BamTotalComponentSizeTag totalComponentSizeTag;
    private final UnknownComponent u_04_05; // Seems to be always "FF"
    private final BamImageNumberTag imageNumberTag;

    List<ImgSubComponent> componentsOrdered;

    public BamImageComponentHeader(final ByteBuffer buffer){
        this.bamImageTypeTag = new BamImageTypeTag(buffer);
        this.totalComponentSizeTag = new BamTotalComponentSizeTag(buffer);
        this.u_04_05 = new UnknownComponent(buffer, 1);
        this.imageNumberTag = new BamImageNumberTag(buffer);

        componentsOrdered = Arrays.asList(bamImageTypeTag, totalComponentSizeTag, u_04_05, imageNumberTag);
    }

    public boolean isImage(){
        return bamImageTypeTag.getImageType() == BamImageTypeTag.BamImageType.DEFAULT;
    }

    public int getStartOfNextComponent(){
        return Math.toIntExact(componentsOrdered.get(componentsOrdered.size() - 1).getEndPos() +
                totalComponentSizeTag.getConvertedValue());
    }

    public void printFormatted() {
        System.out.println("--BAM IMG COMPONENT HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

}
