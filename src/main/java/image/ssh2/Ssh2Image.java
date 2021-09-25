package image.ssh2;

import com.mycompany.sshtobpmconverter.IPixel;
import converter.Image;
import image.ssh2.colortableheader.strategies.ByteToPixelStrategy;
import image.ssh2.fileheader.ImageHeaderInfoTag;
import util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Ssh2Image implements Image {

    private final ImageHeaderInfoTag imageInfo;

    private final Ssh2ImageHeader ssh2ImageHeader;
    private final ByteBuffer imageByteBuffer;
    private final Ssh2ColorTable ssh2ColorTable;
    private final Ssh2ImageFooter ssh2ImageFooter;


    public Ssh2Image(final ByteBuffer sshFileBuffer, final ImageHeaderInfoTag imageInfo) throws IOException {
        this.imageInfo = imageInfo;
        assertBufferAtStartOfImage(sshFileBuffer, imageInfo);
        this.ssh2ImageHeader = new Ssh2ImageHeader(sshFileBuffer);
        // todo replace by raster if possible
        this.imageByteBuffer = copyRawImageDataToBufferAndSkip(sshFileBuffer, ssh2ImageHeader);
        this.ssh2ColorTable = new Ssh2ColorTable(sshFileBuffer);
        this.ssh2ImageFooter = new Ssh2ImageFooter(sshFileBuffer);
    }

    public long getImageEndPosition() {
        return ssh2ImageHeader.getImageEndPosition();
    }

    public long getEndPosition() {
        return ssh2ColorTable.getEndPosition();
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
        ByteBuffer imageBuffer = slice(sshFileBuffer, ssh2ImageHeader.getImageMemorySize());
        sshFileBuffer.position(sshFileBuffer.position() + ssh2ImageHeader.getImageMemorySize());
        return imageBuffer;
    }

    private ByteBuffer slice(final ByteBuffer buffer, final int size){
        final ByteBuffer tmp = buffer.duplicate();
        tmp.limit(tmp.position() + size);
        return tmp.slice();
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
        return ssh2ImageHeader.getImageDecodingStrategy().decodeImage(image);
    }

    private ByteToPixelStrategy getByteToPixelStrategy(){
        return ssh2ImageHeader.getByteToPixelStrategy();
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
        String imageTitle = "* image: " + getImageName() + " *";
        System.out.println("\n" + "*".repeat(imageTitle.length() + 2));
        System.out.println(imageTitle);
        System.out.println("*".repeat(imageTitle.length() + 2) + "\n");

        ssh2ImageHeader.printFormatted();
        ssh2ColorTable.printFormatted();
        ssh2ImageFooter.printFormatted();
    }

    @Override
    public String getImageName() {
        return imageInfo.getName();
    }


    private void assertBufferAtStartOfImage(final ByteBuffer buffer, final ImageHeaderInfoTag imageInfo){
        int expectedStartOfImage = buffer.position();
        int actualStartOfImage = Math.toIntExact(imageInfo.getHeaderLocation());
        if(expectedStartOfImage != actualStartOfImage){
            throw new IllegalStateException("Likely something went wrong reading the data: location of " + imageInfo.getInfo() + "does not align with the current position of the buffer: " + ByteUtil.printLongWithHex(actualStartOfImage));
        }
    }
}
