package image.ssh2;

import bam.data.image.header.imageheader.BamImageComponentImageHeader;
import image.ImgSubComponent;
import image.ssh.SshImageDecoderStrategy;
import image.ssh2.fileheader.FillerTag;
import image.ssh2.imageheader.*;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.PrintUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public class Ssh2ImageHeader {

    private static Logger LOGGER = LoggerFactory.getLogger(Ssh2ImageHeader.class);

    private final ImageTypeTag imageTypeTag;
    private final ImageComponentSizeTag imageComponentSizeTag;
    private final ImageWidthTag imageWidthTag;
    private final ImageHeightTag imageHeightTag;
    private final ImageMaterialTag imageMaterialTag;
    private final ImageEncodingTypeTag encodingTypeTag;
    private final ImageDownScalingTag imageDownScalingTag;
    private final FillerTag fillerTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ImageHeader(final ByteBuffer sshFileBuffer) {
        this.imageTypeTag = new ImageTypeTag(sshFileBuffer);
        this.imageComponentSizeTag = new ImageComponentSizeTag(sshFileBuffer);
        this.imageWidthTag = new ImageWidthTag(sshFileBuffer);
        this.imageHeightTag = new ImageHeightTag(sshFileBuffer);
        this.imageMaterialTag = new ImageMaterialTag(sshFileBuffer);
        this.encodingTypeTag = new ImageEncodingTypeTag(sshFileBuffer);
        this.imageDownScalingTag = new ImageDownScalingTag(sshFileBuffer);
        this.fillerTag = new FillerTag.Reader()
                .withDesiredStartAddress(0x80)
                .withPrefix(new byte[]{(byte) 0x80})
                .read(sshFileBuffer);

        componentsOrdered = List.of(imageTypeTag, imageComponentSizeTag, imageWidthTag, imageHeightTag, imageMaterialTag, encodingTypeTag, imageDownScalingTag, fillerTag);
        assertCalculatedImageComponentSizeEqualsImageComponentSizeProperty();
    }

    public int getImageHeight() {
        return imageHeightTag.getConvertedValue();
    }

    public int getImageWidth() {
        return imageWidthTag.getConvertedValue();
    }

    public int getImageRowSizeInBytes(){
        return (int) (getImageWidth() * getBytesPerPixel());
    }

    public int getImageSize(){
        return getImageWidth() * getImageHeight();
    }

    public int getImageComponentSize(){
        return getImageComponentSizeFromHeaderProperties().orElse(getCalculatedImageComponentSize());
    }

    private Optional<Integer> getImageComponentSizeFromHeaderProperties(){
        return Optional.of(imageComponentSizeTag.getConvertedValue())
                .filter(componentSize -> componentSize != 0);
    }

    private int getCalculatedImageComponentSize(){
        return getImageHeaderSize() + getImageMemorySizeIncludingDownscales();
    }

    /**
     * Returns the memory-size of the total image data (high + lower rez images)
     */
    public int getImageMemorySize(){
        return getImageComponentSize() - getImageHeaderSize();
    }

    private void assertCalculatedImageComponentSizeEqualsImageComponentSizeProperty(){
        getImageComponentSizeFromHeaderProperties()
                .filter(definedSize -> definedSize != getCalculatedImageComponentSize())
                .ifPresent(definedSize -> LOGGER.warn("ImageSize by headerProperties ({}) does not match calculated imageSize({})", definedSize, getCalculatedImageComponentSize()));
    }

    /**
     * Calculates image memory size - includes downscaled images if they are present
     */
    private int getImageMemorySizeIncludingDownscales(){
        int numberOfDownscales = imageDownScalingTag.getConvertedValue();
        int imageSizeAtCurrentScale = getCalculatedHighRezImageMemorySize();
        int totalImageMemorySize = imageSizeAtCurrentScale;
        for(int i = 0; i < numberOfDownscales; i++) {
            imageSizeAtCurrentScale /= 4; // both length and width are halved
            totalImageMemorySize +=imageSizeAtCurrentScale;
        }
        return totalImageMemorySize;
    }

    private int getCalculatedHighRezImageMemorySize(){
        return (int) (getImageSize() * getBytesPerPixel());
    }

    public double getBytesPerPixel(){
        return imageTypeTag.getImageType().getBytesPerPixel();
    }

    public long getImageHeaderStartPosition() {
        return componentsOrdered.get(0).getStartPos();
    }

    public long getImageHeaderEndPosition() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }

    private int getImageHeaderSize() {
        return Math.toIntExact(getImageHeaderEndPosition() - getImageHeaderStartPosition());
    }

    public long getImageComponentEndPosition() {
        return getImageHeaderStartPosition() + getImageComponentSize();
    }

    public SshImageDecoderStrategy getImageDecodingStrategy() {
        return encodingTypeTag.getEncodingType().getDecoderStrategy();
    }

    public ImageTypeTag.ImageType getImageType(){
        return imageTypeTag.getImageType();
    }

    public ByteToPixelStrategy getByteToPixelStrategy(){
        return getImageType().getByteToPixelStrategy();
    }

    public boolean requiresPalette(){
        return imageTypeTag.getImageType().getByteToPixelStrategy().requiresPalette();
    }


    public void printFormatted() {
//        System.out.println("--SSH IMG HEADER--");

        System.out.println("header_start(" + ByteUtil.printLongWithHex(getImageHeaderStartPosition()) + ") | header_end/img_pixels_start(" + ByteUtil.printLongWithHex(getImageHeaderEndPosition()) + ") | img_pixels_end(" + ByteUtil.printLongWithHex(getImageComponentEndPosition()) + ")");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

    @Override
    public String toString() {
        return "Ssh2ImageHeader{" +
                "imageTypeTag=" + imageTypeTag +
                ", imageComponentSizeTag=" + imageComponentSizeTag +
                ", imageWidthTag=" + imageWidthTag +
                ", imageHeightTag=" + imageHeightTag +
                ", imageMaterialTag=" + imageMaterialTag +
                ", encodingTypeTag=" + encodingTypeTag +
                ", imageDownScalingTag=" + imageDownScalingTag +
                '}';
    }
}
