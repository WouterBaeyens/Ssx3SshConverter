package bam.data.image.header.imageheader;

import image.ImgSubComponent;
import image.UnknownComponent;
import image.ssh2.imageheader.ImageTypeTag;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import util.PrintUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class BamImageComponentImageHeader {

    final ImageTypeTag imageTypeTag;
    final ImageHeaderSizeTag imageHeaderSizeTag;
    final BamImageRowsTag amountOfRowsTag;
    final UnknownComponent u_0C_1C_data;
    final UnknownComponent u_1C_88_padding;

    private final List<ImgSubComponent> componentsOrdered;

    public BamImageComponentImageHeader(final ByteBuffer buffer){
        this.imageTypeTag = new ImageTypeTag(buffer);
        this.imageHeaderSizeTag = new ImageHeaderSizeTag(buffer);
        this.amountOfRowsTag = new BamImageRowsTag(buffer);
        this.u_0C_1C_data = new UnknownComponent(buffer, 16);
        this.u_1C_88_padding = new UnknownComponent(buffer, 108);

        componentsOrdered = List.of(imageTypeTag, imageHeaderSizeTag, amountOfRowsTag, u_0C_1C_data);
    }

    public int getImageHeight(){
        return amountOfRowsTag.getConvertedValue();
    }

    public int getImageWidth(){
        return (int ) (getImageRowLengthInBytes() / imageTypeTag.getImageType().getBytesPerPixel());
    }

    public int getImageHeightHighRez(){
        return Integer.highestOneBit(getImageHeight());
    }

    public int getImageRowLengthInBytes(){
        return 256;
    }

    public int getImageByteSize(){
        return getImageHeight() * getImageRowLengthInBytes();
    }

    public boolean requiresPalette(){
        return imageTypeTag.getImageType().getByteToPixelStrategy().requiresPalette();
    }

    public ByteToPixelStrategy getByteToPixelStrategy() {
        return imageTypeTag.getImageType().getByteToPixelStrategy();
    }

    public void printFormatted(){
        System.out.println("--BAM IMG HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));

    }

    @Override
    public String toString() {
        return "BamImageComponentImageHeader{" +
                "imageTypeTag=" + imageTypeTag +
                ", imageHeaderSizeTag=" + imageHeaderSizeTag +
                ", amountOfRowsTag=" + amountOfRowsTag +
                ", u_0C_1C_data=" + u_0C_1C_data +
                ", u_1C_88_padding=" + u_1C_88_padding +
                '}';
    }
}
