package image.ssh2;

import image.bmp2.bmpheader.FileTypeTag;
import image.ssh2.compression.CompressedFile;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import util.ByteUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static util.FileUtil.attachBuffer;

public class Ssh2File {

    private static final String READ_ONLY_MODE = "r";
    private final ByteBuffer sshFileBuffer;

    private final Ssh2FileHeader ssh2FileHeader;
    private final List<Ssh2Image> images;


    public Ssh2File(ByteBuffer sshFileBuffer) throws IOException {
        System.out.println("Deserialising ssh file");
        this.sshFileBuffer = decompressFile(sshFileBuffer);
        // todo - consider static read methods instead to improve readability
        this.ssh2FileHeader = deserializeFileHeader(0);
        this.images = deserializeImages(ssh2FileHeader.getImageInfoList(), ssh2FileHeader.getFileType());
        printFormatted();
        assertFileFullyConsumed(sshFileBuffer);
    }

    private ByteBuffer decompressFile(final ByteBuffer byteBuffer) {
        CompressedFile compressedFile = new CompressedFile(byteBuffer);
        return compressedFile.decompress();
    }


    private Ssh2FileHeader deserializeFileHeader(long fileHeaderPosition) {
        sshFileBuffer.position(Math.toIntExact(fileHeaderPosition));
        return new Ssh2FileHeader(sshFileBuffer);
    }

    private List<Ssh2Image> deserializeImages(List<ImageHeaderInfoTag> imageInfoList, image.ssh2.fileheader.FileTypeTag.VersionType versionType) throws IOException {
        List<Ssh2Image> images = new ArrayList<>();
        Ssh2Image previousImage = null;
        for (ImageHeaderInfoTag imageInfo : imageInfoList) {
            final Ssh2Image currentImage = deserializeImage(imageInfo, versionType);
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

    private Ssh2Image deserializeImage(final ImageHeaderInfoTag imageInfo, image.ssh2.fileheader.FileTypeTag.VersionType versionType) throws IOException {
        return new Ssh2Image(sshFileBuffer, imageInfo, versionType);
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

    private void assertFileFullyConsumed(final ByteBuffer buffer){
        if(buffer.hasRemaining()){
            //todo figure out why crwd.ssh has 1 spare byte *facepalm*
            //throw new IllegalStateException("Likely something went wrong reading the data: The file should be fully read, but the buffer has " + buffer.remaining() + " bytes not consumed.");
        }
    }
}
