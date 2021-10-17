package bam.data.image;

import bam.data.image.header.BamImageComponentHeader;
import bam.data.image.header.imageheader.BamImageComponentImageHeader;
import bam.data.image.header.imagetable.BamImageColorTable;
import com.mycompany.sshtobpmconverter.IPixel;
import converter.Image;
import image.ssh.SshImageHeader;
import image.ssh2.imageheader.ImageTypeTag;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import util.FileUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static image.ssh2.imageheader.ImageEncodingTypeTag.EncodingType.INTERLACED;
import static image.ssh2.imageheader.ImageEncodingTypeTag.EncodingType.SCRAMBLED;

public class BamImageComponent implements Image {

    final BamImageComponentHeader header;
    final BamImageComponentImageHeader imageHeader;
    private final ByteBuffer imageByteBuffer;
    private final BamImageColorTable imageColorTable;

    public BamImageComponent(final ByteBuffer buffer){
        this.header = new BamImageComponentHeader(buffer);
        // todo: skip bytes also
        if(containsImage()) {
            this.imageHeader = new BamImageComponentImageHeader(buffer);
            this.imageByteBuffer = FileUtil.sliceAndSkip(buffer, imageHeader.getImageByteSize());
            imageColorTable = imageHeader.requiresPalette() ? new BamImageColorTable(buffer) : null;

        } else {
            this.imageHeader = null;
            this.imageByteBuffer = null;
            imageColorTable = null;
        }
    }

    public boolean containsImage(){
        return header.isImage();
    }

    public int getStartOfNextComponent(){
        return header.getStartOfNextComponent();
    }

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
                IPixel pixel = getByteToPixelStrategy().readNextPixel(tmpImageByteBuffer, imageColorTable, i);
                imageRow.add(pixel);
            }
            image.add(imageRow);
        }
        if(image.size() > 16) {
            return SCRAMBLED.getDecoderStrategy().decodeImage(image);
        } else {
            return image;
        }
    }

    private ByteToPixelStrategy getByteToPixelStrategy(){
        return imageHeader.getByteToPixelStrategy();
    }

    @Override
    public int getImgWidth() {
        return imageHeader.getImageWidth();
    }

    public int getImgTotalHeight() {
        return imageHeader.getImageHeight();
    }

    @Override
    public int getImgHeight(){
        return imageHeader.getImageHeightHighRez();
    }

    @Override
    public void printFormatted() {
        header.printFormatted();
        imageHeader.printFormatted();
    }
}
