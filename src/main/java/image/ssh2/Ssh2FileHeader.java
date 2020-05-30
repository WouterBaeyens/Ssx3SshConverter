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
import java.io.RandomAccessFile;
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
//    private final FillerTag fillerTag;
    private final FillerTag fillerTag;

    private final List<ImgSubComponent> componentsOrdered;

    public Ssh2FileHeader(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        this.fileTypeTag = new FileTypeTag(sshFile, filePosition);
        this.fileSizeTag = new FileSizeTag(sshFile, fileTypeTag.getEndPos());
        this.archiveTag = new ArchiveTag(sshFile, fileSizeTag.getEndPos());
        this.versionTag = new VersionTag(sshFile, archiveTag.getEndPos());
        this.imageHeaderInfoTags = readImageInfoList(sshFile, versionTag.getEndPos());

        final long fillerStart = imageHeaderInfoTags.get(imageHeaderInfoTags.size() - 1).getEndPos();
        final long headerSpaceLeft = getStartOfImageFiles() - fillerStart;
        this.fillerTag = new FillerTag(sshFile, fillerStart, headerSpaceLeft);
        this.componentsOrdered = List.of(
                Stream.of(List.of(this.fileTypeTag, fileSizeTag, archiveTag, versionTag), imageHeaderInfoTags, List.of(fillerTag))
                        .flatMap(List::stream)
                        .toArray(ImgSubComponent[]::new)
        );
    }

    private List<ImageHeaderInfoTag> readImageInfoList(final RandomAccessFile sshFile, long positionInFile) throws IOException {
        List<ImageHeaderInfoTag> imageHeaders = new ArrayList<>();
        for (int imageNr = 0; imageNr < getNumberOfImages(); imageNr++) {
            final ImageHeaderInfoTag imageHeaderInfoTag = new ImageHeaderInfoTag(sshFile, positionInFile);
            imageHeaders.add(imageHeaderInfoTag);
            positionInFile = imageHeaderInfoTag.getEndPos();
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
