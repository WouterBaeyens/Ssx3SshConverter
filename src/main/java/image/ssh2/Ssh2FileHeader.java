package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.fileheader.NumberOfEntriesTag;
import image.ssh2.fileheader.TotalFileSizeTag;
import image.ssh2.fileheader.PlatformTag;
import image.ssh2.fileheader.FillerTag;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import image.ssh2.fileheader.FileTypeTag;
import util.PrintUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Ssh2FileHeader {

    //these components (in this order) make up the complete fileHeader

    private final PlatformTag platformTag;
    private final TotalFileSizeTag totalFileSizeTag;
    private final NumberOfEntriesTag numberOfEntriesTag;
    private final FileTypeTag fileTypeTag;
    private final List<ImageHeaderInfoTag> imageHeaderInfoTags;
    //    private final EaTag eaTag;
    private final FillerTag fillerTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2FileHeader(final MappedByteBuffer sshFileBuffer) throws IOException {
        this.platformTag = new PlatformTag(sshFileBuffer);
        this.totalFileSizeTag = new TotalFileSizeTag(sshFileBuffer);
        this.numberOfEntriesTag = new NumberOfEntriesTag(sshFileBuffer);
        this.fileTypeTag = new FileTypeTag(sshFileBuffer);
        this.imageHeaderInfoTags = readImageInfoList(sshFileBuffer);

        final long fillerStart = imageHeaderInfoTags.get(imageHeaderInfoTags.size() - 1).getEndPos();
        final long headerSpaceLeft = getStartOfImageFiles() - fillerStart;
        this.fillerTag = new FillerTag(sshFileBuffer, fillerStart, headerSpaceLeft);
        this.componentsOrdered = List.of(
                Stream.of(List.of(this.platformTag, totalFileSizeTag, numberOfEntriesTag, fileTypeTag), imageHeaderInfoTags, List.of(fillerTag))
                        .flatMap(List::stream)
                        .toArray(ImgSubComponent[]::new)
        );
    }

    private List<ImageHeaderInfoTag> readImageInfoList(final MappedByteBuffer buffer) {
        List<ImageHeaderInfoTag> imageHeaders = new ArrayList<>();
        for (int imageNr = 0; imageNr < getNumberOfImages(); imageNr++) {
            final ImageHeaderInfoTag imageHeaderInfoTag = new ImageHeaderInfoTag(buffer);
            imageHeaders.add(imageHeaderInfoTag);
        }
        return imageHeaders;
    }

    public List<ImageHeaderInfoTag> getImageInfoList() {
        return imageHeaderInfoTags;
    }

    public long getNumberOfImages() {
        return numberOfEntriesTag.getConvertedValue();
    }

    public long getStartOfImageFiles() {
        return imageHeaderInfoTags.get(0).getHeaderLocation();
    }

    public long getFileSize() {
        return totalFileSizeTag.getConvertedValue();
    }

    public void printFormatted() {
        System.out.println("--SSH HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }


}
