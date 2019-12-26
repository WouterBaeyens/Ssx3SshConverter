package image.ssh2;

import image.ImgSubComponent;
import image.ssh.SshImageDecoderStrategy;
import image.ssh2.imageheader.ImageEncodingTypeTag;
import image.ssh2.imageheader.ImageHeightTag;
import image.ssh2.imageheader.ImageMaterialTag;
import image.ssh2.imageheader.ImageSizeTag;
import image.ssh2.imageheader.ImageTypeTag;
import image.ssh2.imageheader.ImageWidthTag;
import util.ByteUtil;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Ssh2ImageHeader {

    private final List<ImgSubComponent> subComponents = new ArrayList<>();

    private final long imageHeaderStartPosition;
    private final long imageHeaderEndPosition;
    private final long imageWithHeaderSize;
    private final int imageWidth;
    private final int imageHeight;
    private final ImageEncodingTypeTag.EncodingType encodingType;

    public Ssh2ImageHeader(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.imageHeaderStartPosition = filePosition;

        final ImageTypeTag imageTypeTag = new ImageTypeTag(sshFile, filePosition);
        subComponents.add(imageTypeTag);

        final ImageSizeTag imageSizeTag = new ImageSizeTag(sshFile, imageTypeTag.getEndPos());
        this.imageWithHeaderSize = imageSizeTag.getConvertedValue();
        subComponents.add(imageSizeTag);

        final ImageWidthTag imageWidthTag = new ImageWidthTag(sshFile, imageSizeTag.getEndPos());
        imageWidth = imageWidthTag.getConvertedValue();
        subComponents.add(imageWidthTag);

        final ImageHeightTag imageHeightTag = new ImageHeightTag(sshFile, imageWidthTag.getEndPos());
        imageHeight = imageHeightTag.getConvertedValue();
        subComponents.add(imageHeightTag);

        final ImageMaterialTag imageMaterialTag = new ImageMaterialTag(sshFile, imageHeightTag.getEndPos());
        subComponents.add(imageMaterialTag);

        final ImageEncodingTypeTag encodingTypeTag = new ImageEncodingTypeTag(sshFile, imageMaterialTag.getEndPos());
        encodingType = encodingTypeTag.getEncodingType();
        this.imageHeaderEndPosition = encodingTypeTag.getEndPos();
        subComponents.add(encodingTypeTag);

        printFormatted();
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public long getImageHeaderEndPosition() {
        return imageHeaderEndPosition;
    }

    public long getImageEndPosition() {
        return imageHeaderStartPosition + imageWithHeaderSize;
    }

    public SshImageDecoderStrategy getImageDecodingStrategy() {
        return encodingType.getDecoderStrategy();
    }

    public void printFormatted() {
        System.out.println("--SSH IMG HEADER--");

        long endOfImagePixels = imageHeaderEndPosition + imageHeight * imageWidth;
        System.out.println("header_start(" + ByteUtil.printLongWithHex(imageHeaderStartPosition) + ") | header_end/img_pixels_start(" + ByteUtil.printLongWithHex(imageHeaderEndPosition) + ") | img_pixels_end(" + ByteUtil.printLongWithHex(endOfImagePixels) + ")");
        long calculatedImageWithHeaderSize = imageHeaderEndPosition - imageHeaderStartPosition + imageWidth * imageWidth;
        if (calculatedImageWithHeaderSize != imageWithHeaderSize) {
            System.out.println("ERROR: image_size+header_size=" + calculatedImageWithHeaderSize + "; should be " + imageWithHeaderSize);
        }
        System.out.println(PrintUtil.toRainbow(subComponents.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = subComponents.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
