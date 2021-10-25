package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import image.UnknownComponent;
import image.ssh2.imageheader.ImageDownScalingTag;
import image.ssh2.imageheader.ImageTypeTag;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class BamImageComponentImageHeader {

    private static Logger LOGGER = LoggerFactory.getLogger(BamImageComponentImageHeader.class);

    final ImageTypeTag imageTypeTag;
    final ImageHeaderSizeTag imageHeaderSizeTag;
    final BamImageSizeTag imageSizeTag;
    final BamImageWidthTag imageWidthTag;
    final BamImageHeightTag imageHeightTag;
    final UnknownComponent u_10_16_data;
    final ImageDownScalingTag downScalingTag;
    final UnknownComponent u_18_1C_data;
    final UnknownComponent u_1C_88_padding;

    private final List<ImgSubComponent> componentsOrdered;

    public BamImageComponentImageHeader(final ByteBuffer buffer){
        this.imageTypeTag = new ImageTypeTag(buffer);
        this.imageHeaderSizeTag = new ImageHeaderSizeTag(buffer);
        this.imageSizeTag = new BamImageSizeTag(buffer);
        this.imageWidthTag = new BamImageWidthTag(buffer);
        this.imageHeightTag = new BamImageHeightTag(buffer);
        this.u_10_16_data = new UnknownComponent(buffer, 6);
        this.downScalingTag = new ImageDownScalingTag(buffer);
        this.u_18_1C_data = new UnknownComponent(buffer, 4);
        this.u_1C_88_padding = new UnknownComponent(buffer, 108);

        componentsOrdered = List.of(imageTypeTag, imageHeaderSizeTag, imageSizeTag, imageWidthTag, imageHeightTag, u_10_16_data, downScalingTag, u_18_1C_data);
        assertCalculatedImageSizeEqualsImageSizeProperty();
    }

    private void assertCalculatedImageSizeEqualsImageSizeProperty(){
        if(getImageSizeFromHeaderProperties() != 0 && getImageSizeFromHeaderProperties() != getCalculatedImageSize()){
            LOGGER.warn("ImageSize by headerProperties ({}) does not match calculated imageSize({})", getImageSizeFromHeaderProperties(), getCalculatedImageSize());
        }
    }

    public long getStartPosition(){
        return componentsOrdered.get(0).getStartPos();
    }

    public long getEndPosition(){
        return u_1C_88_padding.getEndPos();
    }

    public int getImageWidthHighRez(){
        return imageWidthTag.getConvertedValue();
    }

    public int getImageHeightHighRez(){
        return imageHeightTag.getConvertedValue();
    }

    public int getImageByteSize(){
        if(getImageSizeFromHeaderProperties() != 0
            && getImageSizeFromHeaderProperties() > 256 // this is a specific rule to avoid an error reading bam_013/0x47da8 where the actual length is 128, with the imageDataSize showing 256.
        ){
            return getImageSizeFromHeaderProperties();
        } else {
            return getCalculatedImageSize();
        }
    }

    private int getImageSizeFromHeaderProperties(){
        return imageSizeTag.getConvertedValue();
    }

    private int getCalculatedImageSize(){
        final int highRezImageSize = (int) (getImageWidthHighRez() * getImageHeightHighRez() * getBytesPerPixel());
        switch (downScalingTag.getConvertedValue()){
            case 0:
                return highRezImageSize;
            case 1:
                return highRezImageSize + highRezImageSize / 4;
            case 2:
                return highRezImageSize + highRezImageSize / 4 + highRezImageSize / 16;
            case 3:
                return highRezImageSize + highRezImageSize / 4 + highRezImageSize / 16 + highRezImageSize / 64;
            default:
                LOGGER.error("Expected downscaling value [0-3], but was {}", downScalingTag.getConvertedValue());
                return highRezImageSize;
        }
    }

    public double getBytesPerPixel(){
        return imageTypeTag.getImageType().getBytesPerPixel();
    }

    public boolean requiresPalette(){
        return imageTypeTag.getImageType().getByteToPixelStrategy().requiresPalette();
    }

    public ByteToPixelStrategy getByteToPixelStrategy() {
        return imageTypeTag.getImageType().getByteToPixelStrategy();
    }

    public void printFormatted(){
        System.out.println("--BAM IMG HEADER--");
        System.out.println("header_start(" + ByteUtil.printLongWithHex(getStartPosition()) + ")");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));

    }

    @Override
    public String toString() {
        return "BamImageComponentImageHeader{" +
                "imageTypeTag=" + imageTypeTag +
                ", imageHeaderSizeTag=" + imageHeaderSizeTag +
                ", amountOfRowsTag=" + imageSizeTag +
                ", u_0C_1C_data=" + u_10_16_data +
                ", u_1C_88_padding=" + u_1C_88_padding +
                '}';
    }
}
