package bam.data.image;

import bam.data.image.header.BamImageComponentHeader;
import bam.data.image.header.imageheader.BamImageComponentImageHeader;
import bam.data.image.header.imagetable.BamImageColorTable;
import com.mycompany.sshtobpmconverter.IPixel;
import converter.Image;
import image.ssh.InterleafedBitwiseDecoderStrategy;
import image.ssh2.Ssh2ImageHeader;
import image.ssh2.fileheader.FillerTag;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.ConverterConfig;
import util.FileUtil;

import java.awt.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static image.ssh2.imageheader.ImageEncodingTypeTag.EncodingType.*;
import static util.ConverterConfig.PRINT_INFO;

public class BamImageComponent implements Image {

    private static final Logger LOGGER = LoggerFactory.getLogger(BamImageComponent.class);

    final BamImageComponentHeader header;
    final Ssh2ImageHeader imageHeader;
    private final ByteBuffer imageByteBuffer;
    private final BamImageColorTable imageColorTable;

    public BamImageComponent(final ByteBuffer buffer){
        this.header = new BamImageComponentHeader(buffer);
        final int startOfComponent = buffer.position();
        final ByteBuffer componentBuffer = FileUtil.sliceAndSkip(buffer, header.getComponentSize());
        if(containsImage()) {
            this.imageHeader = new Ssh2ImageHeader(componentBuffer);
            // new FillerTag(componentBuffer);
            this.imageByteBuffer = FileUtil.sliceAndSkip(componentBuffer, imageHeader.getImageMemorySize());
            imageColorTable = imageHeader.requiresPalette() ? new BamImageColorTable(componentBuffer) : null;
            if(componentBuffer.hasRemaining()){
                LOGGER.warn("Component {} at {} still has {} unprocessed bytes. (at offset {})", header.getImgNumber(), startOfComponent, componentBuffer.remaining(), ByteUtil.printLongWithHex(componentBuffer.position()));
            }
        } else {
            this.imageHeader = null;
            this.imageByteBuffer = null;
            imageColorTable = null;
        }
        if(PRINT_INFO){
            printFormatted();
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
//        ByteBuffer tmpImageByteBuffer = new InterleafedBitwiseDecoderStrategy().decodeBuffer(imageByteBuffer.duplicate().rewind(), new Dimension(imageHeader.getImageRowSizeInBytes(), getImgHeight()));
        try {

            for (int rowNr = 0; rowNr < imgHeight; rowNr++) {
                List<IPixel> imageRow = new ArrayList<>();
                for (int i = 0; i < imgWidth; i++) {
                    IPixel pixel = getByteToPixelStrategy().readNextPixel(tmpImageByteBuffer, imageColorTable, i);
                    imageRow.add(pixel);
                }
                image.add(imageRow);
            }
        } catch (BufferUnderflowException e){
            long currentPosition = imageHeader.getImageHeaderEndPosition() + tmpImageByteBuffer.position();
            long bufferSize = tmpImageByteBuffer.position() + tmpImageByteBuffer.limit();
            throw new IllegalStateException("Encountered an error reading image data at " + ByteUtil.printLongWithHex(currentPosition) + " (image-data start=" + ByteUtil.printLongWithHex(imageHeader.getImageHeaderEndPosition()) + ", bufferSize=" + ByteUtil.printLongWithHex(bufferSize) +")", e);
        }
        if(image.size() > 16) {
            return imageHeader.getImageDecodingStrategy().decodeImage(image);
//            return image;
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

    @Override
    public int getImgHeight(){
        return imageHeader.getImageHeight();
    }

    public int getImageNr(){
        return header.getImgNumber();
    }

    @Override
    public void printFormatted() {
        header.printFormatted();
        Optional.ofNullable(imageHeader).ifPresent(Ssh2ImageHeader::printFormatted);
        if(ConverterConfig.PRINT_TABLE_INFO) Optional.ofNullable(imageColorTable).ifPresent(BamImageColorTable::printFormatted);
    }
}
