package image.ssh2;

import com.mycompany.sshtobpmconverter.IPixel;
import converter.Image;
import image.ssh2.fileheader.FileTypeTag;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import image.ssh2.compression.CompressedFile;
import image.ssh2.fileheader.FillerTag;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import image.ssh2.imageheader.ImageTypeTag;
import util.ByteUtil;
import util.FileUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Ssh2Image implements Image {

    // Default settings for reading fillerTag
    private final FillerTag.Reader fillerTagReader = new FillerTag.Reader()
            .withPrefix(FillerTag.BUY_ERTS_AS_BYTE)
            .withDesiredStartAddress(FillerTag.DESIRED_IMG_HEADER_START_ADDRESS);

    private final ImageHeaderInfoTag imageInfo;

    private final Ssh2ImageHeader ssh2ImageHeader;
    private final ByteBuffer imageByteBuffer;
    @Nullable private final Ssh2ColorTable ssh2ColorTable;
    private final Ssh2ImageAttachments ssh2ImageAttachments;
    private final FillerTag fillerTag;

    public Ssh2Image(final ByteBuffer sshFileBuffer, final ImageHeaderInfoTag imageInfo, FileTypeTag.VersionType versionType) throws IOException {
        this.imageInfo = imageInfo;
        assertBufferAtStartOfImage(sshFileBuffer, imageInfo);
        this.ssh2ImageHeader = new Ssh2ImageHeader(sshFileBuffer);
        // todo replace by raster if possible
        this.imageByteBuffer = copyRawImageDataToBufferAndSkip(sshFileBuffer, ssh2ImageHeader);
        final boolean needsTable = ssh2ImageHeader.requiresPalette();
        this.ssh2ColorTable = needsTable ? new Ssh2ColorTable(sshFileBuffer) : null;
        this.ssh2ImageAttachments = new Ssh2ImageAttachments(sshFileBuffer);
        configureFillerTagReader(sshFileBuffer, getImageType(), versionType);
        fillerTag = fillerTagReader.read(sshFileBuffer);
    }

    private void configureFillerTagReader(ByteBuffer sshFileBuffer, ImageTypeTag.ImageType imageType, FileTypeTag.VersionType versionType){
        if(!sshFileBuffer.hasRemaining() || getImageType() == ImageTypeTag.ImageType.HIGH_REZ){
            fillerTagReader.withFillerSize(0); // there is no filler
        }
        if(versionType == FileTypeTag.VersionType.TRICKY_PRE_ALPHA){
            fillerTagReader.withPrefix(FillerTag.EA_SPORTS_AS_BYTE);
        }
    }


    private ImageTypeTag.ImageType getImageType(){
        return ssh2ImageHeader.getImageType();
    }

    public long getEndPosition() {
        return ssh2ImageAttachments.getEndPosition();
    }

    private long getImageStartPosition() {
        return ssh2ImageHeader.getImageHeaderEndPosition();
    }

    private byte[] readRawImageData(final ByteBuffer sshFileBuffer, final Ssh2ImageHeader ssh2ImageHeader){
        final byte[] imageData = new byte[ssh2ImageHeader.getImageSize()];
        sshFileBuffer.get(imageData);
        return imageData;
    }

    private ByteBuffer copyRawImageDataToBufferAndSkip(final ByteBuffer sshFileBuffer, final Ssh2ImageHeader ssh2ImageHeader){
        ByteBuffer compressedImageBuffer = FileUtil.slice(sshFileBuffer, sshFileBuffer.position(), ssh2ImageHeader.getImageMemorySize());
        ByteBuffer decompressedImageBuffer = new CompressedFile(compressedImageBuffer).decompress();
        sshFileBuffer.position(sshFileBuffer.position() + ssh2ImageHeader.getImageMemorySize());
        return decompressedImageBuffer;
    }

    /**
     * Returns the complete image as a list of rows
     * I'm a bit paranoid about big files and the space it would consume,
     * but the positives (faster, easier to use) probably outweigh the negatives
     *
     * @return A list of list, representing a list of rows containing a list of pixels.
     */
    @Override
    public List<List<IPixel>> getImage() {
        ByteBuffer tmpImageByteBuffer = imageByteBuffer.duplicate();
        tmpImageByteBuffer.rewind();
        List<List<IPixel>> image = new ArrayList<>();
        int imgHeight = getImgHeight();
        int imgWidth = getImgWidth();
        for (int rowNr = 0; rowNr < imgHeight; rowNr++) {
            List<IPixel> imageRow = new ArrayList<>();
            for (int i = 0; i < imgWidth; i++) {
                IPixel pixel = getByteToPixelStrategy().readNextPixel(tmpImageByteBuffer, ssh2ColorTable, i);
                imageRow.add(pixel);
            }
            image.add(imageRow);
        }
        return ssh2ImageHeader.getImageDecodingStrategy().decodeImage(image, getByteToPixelStrategy());
    }

    private ByteToPixelStrategy getByteToPixelStrategy(){
        return ssh2ImageHeader.getByteToPixelStrategy();
    }

    private Optional<Ssh2ColorTable> getSsh2ColorTable(){
        return Optional.ofNullable(ssh2ColorTable);
    }

    /**
     * Get the width (in pixels) of the image.
     */
    @Override
    public int getImgWidth() {
        return ssh2ImageHeader.getImageWidth();
    }

    /**
     * Get the height (in pixels) of the image
     */
    @Override
    public int getImgHeight() {
        return ssh2ImageHeader.getImageHeight();
    }

    @Override
    public void printFormatted() {
        String imageTitle = "* image: " + getImageName() + "   *";
        System.out.println("\n" + "*".repeat(imageTitle.length()));
        System.out.println(imageTitle);
        System.out.println("*".repeat(imageTitle.length()) + "\n");

        ssh2ImageHeader.printFormatted();
        getSsh2ColorTable().ifPresent(Ssh2ColorTable::printFormatted);
        ssh2ImageAttachments.printFormatted();
    }

    @Override
    public String getImageName() {
        return ssh2ImageAttachments.getFullName().filter(name -> !name.isBlank())
                .orElse(imageInfo.getName());
    }


    private void assertBufferAtStartOfImage(final ByteBuffer buffer, final ImageHeaderInfoTag imageInfo){
        int expectedStartOfImage = buffer.position();
        int actualStartOfImage = Math.toIntExact(imageInfo.getHeaderLocation());
        if(expectedStartOfImage != actualStartOfImage){
            throw new IllegalStateException("Likely something went wrong reading the data: location of " + imageInfo.getInfo() + "does not align with the current position of the buffer: " + ByteUtil.printLongWithHex(expectedStartOfImage));
        }
    }
}
