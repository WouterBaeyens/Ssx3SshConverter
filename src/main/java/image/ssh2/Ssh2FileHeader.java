package image.ssh2;

import image.ImgSubComponent;
import image.ssh2.fileheader.ArchiveTag;
import image.ssh2.fileheader.CompressionTag;
import image.ssh2.fileheader.EaTag;
import image.ssh2.fileheader.FileSizeTag;
import image.ssh2.fileheader.FileTypeTag;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import image.ssh2.fileheader.VersionTag;
import util.PrintUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Ssh2FileHeader {

    private final List<ImgSubComponent> subComponents = new ArrayList<>();
    private final List<ImageHeaderInfoTag> imageInfoList = new ArrayList<>();
    private final long fileSize;
    private final long amountOfImages;

    public Ssh2FileHeader(final RandomAccessFile sshFile, final long filePosition) throws IOException {
        final FileTypeTag fileTypeTag = new FileTypeTag(sshFile, filePosition);
        subComponents.add(fileTypeTag);

        final FileSizeTag fileSizeTag = new FileSizeTag(sshFile, fileTypeTag.getEndPos());
        fileSize = fileSizeTag.getSize();
        subComponents.add(fileSizeTag);

        final ArchiveTag archiveTag = new ArchiveTag(sshFile, fileSizeTag.getEndPos());
        amountOfImages = archiveTag.getConvertedValue();
        subComponents.add(archiveTag);

        final VersionTag versionTag = new VersionTag(sshFile, archiveTag.getEndPos());
        subComponents.add(versionTag);

        long positionInFile = versionTag.getEndPos();
        for (int imageNr = 0; imageNr < amountOfImages; imageNr++) {
            final ImageHeaderInfoTag imageHeaderInfoTag = new ImageHeaderInfoTag(sshFile, positionInFile);
            subComponents.add(imageHeaderInfoTag);
            imageInfoList.add(imageHeaderInfoTag);
            positionInFile = imageHeaderInfoTag.getEndPos();
        }

        final EaTag eaTag = new EaTag(sshFile, positionInFile);
        subComponents.add(eaTag);

        // I am assuming the rest of the header will be the compressionTag
        long positionOfFirstImgHeader = imageInfoList.get(0).getHeaderLocation();
        long headerSpaceLeft = positionOfFirstImgHeader - eaTag.getEndPos();
        final CompressionTag compressionTag = new CompressionTag(sshFile, positionInFile, headerSpaceLeft);
        subComponents.add(compressionTag);

        printFormatted();
    }

    public List<ImageHeaderInfoTag> getImageInfoList() {
        return imageInfoList;
    }

    public void printFormatted() {
        System.out.println("--SSH HEADER--");
        System.out.println(PrintUtil.toRainbow(subComponents.stream().map(ImgSubComponent::getInfo).map(componentInfo -> componentInfo + " | ").toArray(String[]::new)));
        final String[] hexStrings = subComponents.stream().map(ImgSubComponent::getHexData).toArray(String[]::new);
        System.out.println(PrintUtil.insertForColouredString(PrintUtil.toRainbow(hexStrings), "\n", 16 * 3));
    }


}
