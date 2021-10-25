package image.bmp2.dibheader;

import image.ImgSubComponent;
import image.bmp2.dibheader.tags.AmountOfColoursTag;
import image.bmp2.dibheader.tags.AmountOfImportantColoursTag;
import image.bmp2.dibheader.tags.AmountOfPlanesTag;
import image.bmp2.dibheader.tags.BitsPerPixelTag;
import image.bmp2.dibheader.tags.CompressionTypeTag;
import image.bmp2.dibheader.tags.DibHeaderSizeTag;
import image.bmp2.dibheader.tags.HorizontalResolutionTag;
import image.bmp2.dibheader.tags.ImageHeightTag;
import image.bmp2.dibheader.tags.ImageWidthTag;
import image.bmp2.dibheader.tags.VerticalResolutionTag;
import image.ssh2.imageheader.ImageComponentSizeTag;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class DibInfoHeader implements DibHeader {

    private final DibHeaderSizeTag dibHeaderSizeTag;
    private final ImageWidthTag imageWidthTag;
    private final ImageHeightTag imageHeightTag;
    private final AmountOfPlanesTag amountOfPlanesTag;
    private final BitsPerPixelTag bitsPerPixelTag;
    private final CompressionTypeTag compressionTypeTag;
    private final ImageComponentSizeTag imageComponentSizeTag;
    private final HorizontalResolutionTag horizontalResolutionTag;
    private final VerticalResolutionTag verticalResolutionTag;
    private final AmountOfColoursTag amountOfColoursTag;
    private final AmountOfImportantColoursTag amountOfImportantColoursTag;
    private final List<ImgSubComponent> componentsOrdered;

    public DibInfoHeader(final RandomAccessFile bmpFile, final long filePosition) throws IOException {
        this.dibHeaderSizeTag = new DibHeaderSizeTag(bmpFile, filePosition);
        this.imageWidthTag = new ImageWidthTag(bmpFile, dibHeaderSizeTag.getEndPos());
        this.imageHeightTag = new ImageHeightTag(bmpFile, imageWidthTag.getEndPos());
        this.amountOfPlanesTag = new AmountOfPlanesTag(bmpFile, imageHeightTag.getEndPos());
        this.bitsPerPixelTag = new BitsPerPixelTag(bmpFile, amountOfPlanesTag.getEndPos());
        this.compressionTypeTag = new CompressionTypeTag(bmpFile, bitsPerPixelTag.getEndPos());
        this.imageComponentSizeTag = new ImageComponentSizeTag(bmpFile, compressionTypeTag.getEndPos());
        this.horizontalResolutionTag = new HorizontalResolutionTag(bmpFile, imageComponentSizeTag.getEndPos());
        this.verticalResolutionTag = new VerticalResolutionTag(bmpFile, horizontalResolutionTag.getEndPos());
        this.amountOfColoursTag = new AmountOfColoursTag(bmpFile, verticalResolutionTag.getEndPos());
        this.amountOfImportantColoursTag = new AmountOfImportantColoursTag(bmpFile, amountOfColoursTag.getEndPos());

        componentsOrdered = List.of(dibHeaderSizeTag,
                imageWidthTag,
                imageHeightTag,
                amountOfPlanesTag,
                bitsPerPixelTag,
                compressionTypeTag,
                imageComponentSizeTag,
                horizontalResolutionTag,
                verticalResolutionTag,
                amountOfColoursTag,
                amountOfImportantColoursTag);
    }

    public void printFormatted() {
        System.out.println("--BMP DIB HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }

}
