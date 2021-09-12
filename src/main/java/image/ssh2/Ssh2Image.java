package image.ssh2;

import com.mycompany.sshtobpmconverter.IPixel;
import converter.Image;
import image.ssh2.fileheader.ImageHeaderInfoTag;

import java.awt.image.Raster;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Ssh2Image implements Image {

    private final RandomAccessFile sshFile;
    private final String imageName;
    private final long filePosition;

    private final Ssh2ImageHeader ssh2ImageHeader;
    private final Ssh2ColorTable ssh2ColorTable;
    private final Ssh2ImageFooter ssh2ImageFooter;


    public Ssh2Image(final RandomAccessFile sshFile, final ImageHeaderInfoTag imageInfo) throws IOException {
        this.sshFile = sshFile;
        this.filePosition = imageInfo.getHeaderLocation();
        this.imageName = imageInfo.getName();
        this.ssh2ImageHeader = deserializeImageHeader();
        this.ssh2ColorTable = deserializeColorTable();
        this.ssh2ImageFooter = deserializeImageFooter();
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

    private Ssh2ImageHeader deserializeImageHeader() throws IOException {
        return new Ssh2ImageHeader(sshFile, filePosition);
    }

    private Ssh2ColorTable deserializeColorTable() throws IOException {
        return new Ssh2ColorTable(sshFile, getImageEndPosition());
    }

    private Ssh2ImageFooter deserializeImageFooter() throws IOException {
        return new Ssh2ImageFooter(sshFile, ssh2ColorTable.getEndPosition());
    }

    /**
     * Returns the complete image as a list of rows
     * I'm a bit paranoid about big files and the space it would consume,
     * but the positives (faster, easier to use) probably outweigh the negatives
     *
     * @return A list of list, representing a list of rows containing a list of pixels.
     * @throws IOException
     */
    @Override
    public List<List<IPixel>> getImage() throws IOException {
        List<List<IPixel>> image = new ArrayList<>();
        int imgHeight = getImgHeight();
        int imgWidth = getImgWidth();
        sshFile.seek(getImageStartPosition());
        for (int rowNr = 0; rowNr < imgHeight; rowNr++) {
            List<IPixel> imageRow = new ArrayList<>();
            for (int i = 0; i < imgWidth; i++) {
                byte pixelCode = sshFile.readByte();
                imageRow.add(ssh2ColorTable.getPixelFromByte(pixelCode));
            }
            image.add(imageRow);
        }
        return ssh2ImageHeader.getImageDecodingStrategy().decodeImage(image);
    }

    /**
     * Get the width (in pixels) of the image.
     *
     * @return
     */
    @Override
    public int getImgWidth() {
        return ssh2ImageHeader.getImageWidth();
    }

    /**
     * Get the height (in pixels) of the image
     *
     * @return
     */
    @Override
    public int getImgHeight() {
        return ssh2ImageHeader.getImageHeight();
    }

    @Override
    public void printFormatted() {
        String imageTitle = "* image: " + imageName + " *";
        System.out.println("\n" + "*".repeat(imageTitle.length() + 2));
        System.out.println(imageTitle);
        System.out.println("*".repeat(imageTitle.length() + 2) + "\n");

        ssh2ImageHeader.printFormatted();
        ssh2ColorTable.printFormatted();
        ssh2ImageFooter.printFormatted();
    }

    @Override
    public String getImageName() {
        return imageName;
    }
}
