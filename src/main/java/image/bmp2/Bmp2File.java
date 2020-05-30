package image.bmp2;

import image.bmp2.dibheader.DibHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Bmp2File {

    private static final String READ_ONLY_MODE = "r";
    private final RandomAccessFile bmpFile;

    private final Bmp2DibFileHeaderFactory bmp2DibFileHeaderFactory = new Bmp2DibFileHeaderFactory();

    private final Bmp2FileHeader bmp2FileHeader;
    private final DibHeader dibInfoHeader;

    public Bmp2File(File bmpFile) throws IOException {
        System.out.println("Deserialising bmp file");
        this.bmpFile = openStream(bmpFile);
        this.bmp2FileHeader = deserializeFileHeader(0);
        this.dibInfoHeader = deserializeDibHeader(bmp2FileHeader.getEndPos());
        printFormatted();
    }

    private RandomAccessFile openStream(File bmpFile) throws FileNotFoundException {
        return new RandomAccessFile(bmpFile, READ_ONLY_MODE);
    }

    private Bmp2FileHeader deserializeFileHeader(long filePosition) throws IOException {
        return new Bmp2FileHeader(bmpFile, filePosition);
    }

    private DibHeader deserializeDibHeader(long filePosition) throws IOException {
        return bmp2DibFileHeaderFactory.create(bmpFile, filePosition);
    }

    public void printFormatted() {
        bmp2FileHeader.printFormatted();
        dibInfoHeader.printFormatted();
    }

}
