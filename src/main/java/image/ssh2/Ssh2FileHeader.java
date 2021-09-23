package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.fileheader.ArchiveTag;
import image.ssh2.fileheader.FileSizeTag;
import image.ssh2.fileheader.FileTypeTag;
import image.ssh2.fileheader.FillerTag;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import image.ssh2.fileheader.VersionTag;
import util.PrintUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Ssh2FileHeader {

//    private final List<ImgSubComponent> subComponents = new ArrayList<>();
//    private final List<ImageHeaderInfoTag> imageInfoList = new ArrayList<>();
//    private final long fileSize;
//    private final long amountOfImages;

    //these components (in this order) make up the complete fileHeader

    private final FileTypeTag fileTypeTag;
    private final FileSizeTag fileSizeTag;
    private final ArchiveTag archiveTag;
    private final VersionTag versionTag;
    private final List<ImageHeaderInfoTag> imageHeaderInfoTags;
    //    private final EaTag eaTag;
    private final FillerTag fillerTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2FileHeader(final MappedByteBuffer sshFileBuffer) throws IOException {
        this.fileTypeTag = new FileTypeTag(sshFileBuffer);
        this.fileSizeTag = new FileSizeTag(sshFileBuffer);
        this.archiveTag = new ArchiveTag(sshFileBuffer);
        this.versionTag = new VersionTag(sshFileBuffer);
        this.imageHeaderInfoTags = readImageInfoList(sshFileBuffer);

        final long fillerStart = imageHeaderInfoTags.get(imageHeaderInfoTags.size() - 1).getEndPos();
        final long headerSpaceLeft = getStartOfImageFiles() - fillerStart;
        this.fillerTag = new FillerTag(sshFileBuffer, fillerStart, headerSpaceLeft);
        this.componentsOrdered = List.of(
                Stream.of(List.of(this.fileTypeTag, fileSizeTag, archiveTag, versionTag), imageHeaderInfoTags, List.of(fillerTag))
                        .flatMap(List::stream)
                        .toArray(ImgSubComponent[]::new)
        );
    }

    private List<ImageHeaderInfoTag> readImageInfoList(final MappedByteBuffer buffer) throws IOException {
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
        return archiveTag.getConvertedValue();
    }

    public long getStartOfImageFiles() {
        return imageHeaderInfoTags.get(0).getHeaderLocation();
    }

    public long getFileSize() {
        return fileSizeTag.getConvertedValue();
    }

    public void printFormatted() {
        System.out.println("--SSH HEADER--");
        System.out.println(PrintUtil.toRainbow(componentsOrdered.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = componentsOrdered.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }


}
