package bam.data.image.header;

import image.ImgSubComponent;
import image.UnknownComponent;
import util.ByteUtil;
import util.ConverterConfig;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static util.ConverterConfig.FULL_BAM_TITLE;

public class BamImageComponentHeader {

    private final BamImageTypeTag bamImageTypeTag;
    private final BamTotalComponentSizeTag totalComponentSizeTag;
    private final UnknownComponent u_04_05;
    private final BamImageNumberTag imageNumberTag;

    List<ImgSubComponent> componentsOrdered;

    public BamImageComponentHeader(final ByteBuffer buffer){
        this.bamImageTypeTag = new BamImageTypeTag(buffer);
        this.totalComponentSizeTag = new BamTotalComponentSizeTag(buffer);
        this.u_04_05 = new UnknownComponent(buffer, 1, "FF");
        this.imageNumberTag = new BamImageNumberTag(buffer);

        componentsOrdered = Arrays.asList(bamImageTypeTag, totalComponentSizeTag, u_04_05, imageNumberTag);
    }

    public String getStartPos(){
        return ByteUtil.printLongWithHex(componentsOrdered.get(0).getStartPos());
    }

    public int getStartOfActualComponent(){
        return Math.toIntExact(componentsOrdered.get(componentsOrdered.size() - 1).getEndPos());
    }

    public boolean isImage(){
        return bamImageTypeTag.getImageType() == BamImageTypeTag.BamImageType.DEFAULT;
    }

    public int getImgNumber(){
        return Math.toIntExact(imageNumberTag.getConvertedValue());
    }

    public int getStartOfNextComponent(){
        return getStartOfActualComponent() + totalComponentSizeTag.getConvertedValue();
    }

    public int getComponentSize(){
        return Math.toIntExact(totalComponentSizeTag.getConvertedValue());
    }

    public void printFormatted() {
        String imageTitle = "* BAM IMG " + String.format( "%03d", getImgNumber()) + "   ("+ ByteUtil.printLongWithHex(getStartOfActualComponent()) + " - "+ ByteUtil.printLongWithHex(getStartOfNextComponent())+")  *";
        if(FULL_BAM_TITLE) System.out.println("*".repeat(imageTitle.length()));
        System.out.println(imageTitle);
        if(FULL_BAM_TITLE) System.out.println("*".repeat(imageTitle.length()));
        if(ConverterConfig.PRINT_BAM_COMPONENT_INFO) {
            System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
            final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
            System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
        }
    }

}
