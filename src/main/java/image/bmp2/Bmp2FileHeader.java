package image.bmp2;

import image.ImgSubComponent;
import image.bmp2.bmpheader.FileSizeTag;
import image.bmp2.bmpheader.FileTypeTag;
import image.bmp2.bmpheader.ImagePointerTag;
import image.bmp2.bmpheader.Reserved1Tag;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class Bmp2FileHeader {

    private final FileTypeTag fileTypeTag;
    private final FileSizeTag fileSizeTag;
    private final Reserved1Tag reserved1Tag;
    private final ImagePointerTag imagePointerTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Bmp2FileHeader(final RandomAccessFile bmpFile, final long filePosition) throws IOException {
        this.fileTypeTag = new FileTypeTag(bmpFile, filePosition);
        this.fileSizeTag = new FileSizeTag(bmpFile, fileTypeTag.getEndPos());
        this.reserved1Tag = new Reserved1Tag(bmpFile, fileSizeTag.getEndPos());
        this.imagePointerTag = new ImagePointerTag(bmpFile, reserved1Tag.getEndPos());

        componentsOrdered = List.of(fileTypeTag, fileSizeTag, reserved1Tag, imagePointerTag);
    }

    public long getStartOfImage() {
        return imagePointerTag.getConvertedValue();
    }

    public long getEndPos() {
        return componentsOrdered.get(componentsOrdered.size() - 1).getEndPos();
    }

    public void printFormatted() {
        System.out.println("--BMP HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }
}
