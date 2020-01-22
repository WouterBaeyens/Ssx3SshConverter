package image.ssh2;

import image.ssh2.fileheader.ImageHeaderInfoTag;
import util.ByteUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Ssh2File {

    private static final String READ_ONLY_MODE = "r";
    private final RandomAccessFile sshFile;

    private final Ssh2FileHeader ssh2FileHeader;
    private final List<Ssh2Image> images;

    public Ssh2File(File sshFile) throws IOException {
        System.out.println("Deserialising ssh file");
        this.sshFile = openStream(sshFile);
        this.ssh2FileHeader = deserializeFileHeader(0);
        this.images = deserializeImages(ssh2FileHeader.getImageInfoList());
        printFormatted();
    }

    private RandomAccessFile openStream(File sshFile) throws FileNotFoundException {
        return new RandomAccessFile(sshFile, READ_ONLY_MODE);
    }

    private Ssh2FileHeader deserializeFileHeader(long filePosition) throws IOException {
        return new Ssh2FileHeader(sshFile, filePosition);
    }

    private List<Ssh2Image> deserializeImages(List<ImageHeaderInfoTag> imageInfoList) throws IOException {
        List<Ssh2Image> images = new ArrayList<>();
        Ssh2Image previousImage = null;
        for (ImageHeaderInfoTag imageInfo : imageInfoList) {
            final Ssh2Image currentImage = deserializeImage(imageInfo);
            images.add(currentImage);
            if (previousImage != null) {
                System.out.println("From " + ByteUtil.printLongWithHex(previousImage.getEndPosition()) + "\tto " + ByteUtil.printLongWithHex(imageInfo.getHeaderLocation()));
                System.out.println("PADDING: " + ByteUtil.printLongWithHex(imageInfo.getHeaderLocation() - previousImage.getEndPosition()));
                System.out.println("SIZE: " + ByteUtil.printLongWithHex(currentImage.getEndPosition() - previousImage.getEndPosition()));
            }
            previousImage = currentImage;
        }
        return images;
    }

    private Ssh2Image deserializeImage(final ImageHeaderInfoTag imageInfo) throws IOException {
        final Ssh2Image ssh2Image = new Ssh2Image(sshFile, imageInfo);
        return ssh2Image;
    }

    public void printFormatted() {
        ssh2FileHeader.printFormatted();
        images.forEach(Ssh2Image::printFormatted);
        long leftOverSize = ssh2FileHeader.getFileSize() - images.get(images.size() - 1).getEndPosition();
        System.out.println("Leftover size: " + ByteUtil.printLongWithHex(leftOverSize));
    }

    public List<Ssh2Image> getImages() {
        return images;
    }

    public void close() throws IOException {
        sshFile.close();
    }
}
