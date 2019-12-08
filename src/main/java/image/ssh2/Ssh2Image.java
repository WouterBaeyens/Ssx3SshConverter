package image.ssh2;

import image.ssh2.fileheader.ImageHeaderInfoTag;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Ssh2Image {

    private final RandomAccessFile sshFile;
    private final String imageName;
    private final long filePosition;

    private final Ssh2ImageHeader ssh2ImageHeader;
    private final Ssh2ColorTable ssh2ColorTable;


    public Ssh2Image(final RandomAccessFile sshFile, final ImageHeaderInfoTag imageInfo) throws IOException {
        this.sshFile = sshFile;
        this.filePosition = imageInfo.getHeaderLocation();
        this.imageName = imageInfo.getName();
        System.out.println("image: " + imageName);
        this.ssh2ImageHeader = deserializeImageHeader();
        this.ssh2ColorTable = deserializeColorTable();
    }

    public long getImageEndPosition() {
        return ssh2ImageHeader.getImageEndPosition();
    }

    private Ssh2ImageHeader deserializeImageHeader() throws IOException {
        return new Ssh2ImageHeader(sshFile, filePosition);
    }

    private Ssh2ColorTable deserializeColorTable() throws IOException {
        return new Ssh2ColorTable(sshFile, getImageEndPosition());
    }
}
