package image.ssh2;

import image.ImgSubComponent;
import image.ssh.SshImageDecoderStrategy;
import image.ssh2.colortableheader.strategies.ByteToPixelStrategy;
import image.ssh2.imageheader.ImageEncodingTypeTag;
import image.ssh2.imageheader.ImageHeightTag;
import image.ssh2.imageheader.ImageMaterialTag;
import image.ssh2.imageheader.ImageSizeTag;
import image.ssh2.imageheader.ImageTypeTag;
import image.ssh2.imageheader.ImageWidthTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class Ssh2ImageHeader {


    private final long imageHeaderStartPosition;

    private final ImageTypeTag imageTypeTag;
    private final ImageSizeTag imageSizeTag;
    private final ImageWidthTag imageWidthTag;
    private final ImageHeightTag imageHeightTag;
    private final ImageMaterialTag imageMaterialTag;
    private final ImageEncodingTypeTag encodingTypeTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2ImageHeader(final ByteBuffer sshFileBuffer) throws IOException {
        this.imageHeaderStartPosition = sshFileBuffer.position();

        this.imageTypeTag = new ImageTypeTag(sshFileBuffer);

        this.imageSizeTag = new ImageSizeTag(sshFileBuffer);

        this.imageWidthTag = new ImageWidthTag(sshFileBuffer);

        this.imageHeightTag = new ImageHeightTag(sshFileBuffer);

        this.imageMaterialTag = new ImageMaterialTag(sshFileBuffer);

        this.encodingTypeTag = new ImageEncodingTypeTag(sshFileBuffer);

        // todo check calculated image_with_header_size equals value of sizeTag (or find another meaning)

        componentsOrdered = List.of(imageTypeTag, imageSizeTag, imageWidthTag, imageHeightTag, imageMaterialTag, encodingTypeTag);
    }

    public int getImageHeight() {
        return imageHeightTag.getConvertedValue();
    }

    public int getImageWidth() {
        return imageWidthTag.getConvertedValue();
    }

    public int getImageSize(){
        return getImageWidth() * getImageHeight();
    }

    public int getImageMemorySize(){
        // - If the ssh is of an early version sometimes the properties are not filled (0-filled) and need to be calculated
        // - If the image is compressed; calculated size will not be accurate (as compression lowers size)
        if(getImageWithHeaderSize() != 0) {
            return getImageMemorySizeFromHeaderProperties();
        } else {
            return getCalculatedImageMemorySize();
        }
    }

    private int getImageMemorySizeFromHeaderProperties(){
        return (int) (getImageWithHeaderSize() - getImageHeaderSize());
    }

    private int getCalculatedImageMemorySize(){
        return (int) (getImageSize() * getBytesPerPixel());
    }

    public double getBytesPerPixel(){
        return imageTypeTag.getImageType().getBytesPerPixel();
    }

    public long getImageWithHeaderSize(){
        return imageSizeTag.getConvertedValue();
    }

    public long getImageHeaderStartPosition() {
        return componentsOrdered.get(0).getStartPos();
    }

    public long getImageHeaderEndPosition() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }

    public long getImageHeaderSize() {
        return getImageHeaderEndPosition() - getImageHeaderStartPosition();
    }

    public long getImageEndPosition() {
        return imageHeaderStartPosition + getImageWithHeaderSize();
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

    public void printFormatted() {
        System.out.println("--SSH IMG HEADER--");

        long endOfImagePixels = getImageHeaderEndPosition() + getImageSize();
        System.out.println("header_start(" + ByteUtil.printLongWithHex(imageHeaderStartPosition) + ") | header_end/img_pixels_start(" + ByteUtil.printLongWithHex(getImageHeaderEndPosition()) + ") | img_pixels_end(" + ByteUtil.printLongWithHex(endOfImagePixels) + ")");
        long calculatedImageWithHeaderSize = getImageHeaderEndPosition() - imageHeaderStartPosition + getImageMemorySize();
        if (calculatedImageWithHeaderSize != getImageWithHeaderSize()) {
            System.out.println("ERROR: image_size+header_size=" + calculatedImageWithHeaderSize + "; should be " + getImageWithHeaderSize());
        }
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
