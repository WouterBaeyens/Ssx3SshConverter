package image.ssh;

import com.mycompany.sshtobpmconverter.IPixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConverterConfig;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static util.ByteUtil.*;

public class InterleafedBitwiseDecoderStrategy implements SshImageDecoderStrategy{

    private static final Logger LOGGER = LoggerFactory.getLogger(InterleafedBitwiseDecoderStrategy.class);

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage) {
        List<List<IPixel>> decodedImage = new ArrayList<>();
        int nrOfRows = encodedImage.size();
        int nrOfColumns = encodedImage.get(0).size();
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++) {
            decodedImage.add(new ArrayList<>());
        }
        Dimension imageBlockDimensions = new Dimension(ConverterConfig.BLOCK_DIMENSIONS,ConverterConfig.BLOCK_DIMENSIONS);
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++) {
//            rowNr = 2;
            for (int colNr = 0; colNr < nrOfColumns; colNr++) {
                Point pxlLocation = new Point(rowNr, colNr);
                Dimension imageDimensions = new Dimension(nrOfColumns, nrOfRows);
                Point decodedpxlLocation = decodeLowRezPixelLocation(pxlLocation);
                decodedImage.get(rowNr).add(colNr, encodedImage.get(decodedpxlLocation.x).get(decodedpxlLocation.y));
            }
        }
        return decodedImage;
    }

    public ByteBuffer decodeBuffer(ByteBuffer buffer, Dimension imageDimensionsInBytes){
        int imageSizeInBytes = imageDimensionsInBytes.height * imageDimensionsInBytes.width;
        ByteBuffer decodedBuffer = ByteBuffer.wrap(new byte[imageSizeInBytes]);
        for(int rowNr = 0; rowNr < imageDimensionsInBytes.height; rowNr ++) {
            for (int colNr = 0; colNr < imageDimensionsInBytes.width; colNr++) {
                Point pxlLocation = new Point(rowNr, colNr);
                Point decodedpxlLocation = decodePixelLocation(imageDimensionsInBytes, new Dimension(16,16), pxlLocation);
                decodedBuffer.put(buffer.get(decodedpxlLocation.x * imageDimensionsInBytes.width + decodedpxlLocation.y));
            }
        }
        return decodedBuffer.rewind();
    }

    /**
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * 76edcba gf54321  //r56-c56
     * 76edcba gf32154  //RL2 c0_c5
     * 76dcbag	fe32154  //RL1 c5_r5
     * 76dcagf e32154b  //RL1 c0_r3
     */
    Point decodeLowRezPixelLocation2(Point pxlLocation){
        Dimension imageDimensions = new Dimension(128, 128);
        final int pixelLocation = pxlLocation.x * imageDimensions.width + pxlLocation.y;
        int result = pixelLocation;
        String info = asBin(result);
        if(!bitsEqual(result, 8,9)){
            result = toggleBit(result, 2); info += " -neq r1,r2 ? ^c5-> " + asBin(result); // 4,0 -> 8,32 (^b2 <<2)
        }
        result = swapBits(result, 5, 12,2); info += " -Sw Ro Co 5,6-> " + asBin(result); // 0,32 -> 32,0
        result = rotateLeft(result, 0, 5, 2); info += " -RL2 c0_c5-> " + asBin(result); // 0,1 -> 0,8
        result = rotateLeft(result, 5, 12, 1); info += " -RL1 c5_6-r5-> " + asBin(result); // 0,1 -> 0,8 // could be anything
        result = rotateLeft(result, 0, 10, 1); info += " -RL1 c5_6-r5-> " + asBin(result); // 0,1 -> 0,8 // could be anything

        LOGGER.info(info);
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * 76edcba gf54321  //r56-c56
     * 76edcba gf32154  //RL2 c0_c5
     * 76dcbag	fe32154  //RL1 c5_r5
     * 76dcagf e32154b  //RL1 c0_r3
     */
    Point decodeLowRezPixelLocation(Point pxlLocation){
        Dimension imageDimensions = new Dimension(128, 128);
        final int pixelLocation = pxlLocation.x * imageDimensions.width + pxlLocation.y;
        int result = pixelLocation;
        String info = asBin(result);
        if(!bitsEqual(result, 8,9)){
            result = toggleBit(result, 2); info += " -neq r1,r2 ? ^c5-> " + asBin(result); // 4,0 -> 8,32 (^b2 <<2)
        }
        result = swapBits(result, 5, 11,2); info += " -Sw Ro Co 5,6-> " + asBin(result); // 0,32 -> 32,0
        result = rotateLeft(result, 0, 5, 2); info += " -RL2 c0_c5-> " + asBin(result); // 0,1 -> 0,8
        result = rotateLeft(result, 5, 11, 1); info += " -RL1 c5_6-r5-> " + asBin(result); // 0,1 -> 0,8 // could be anything
        result = rotateLeft(result, 0, 9, 1); info += " -RL1 c5_6-r5-> " + asBin(result); // 0,1 -> 0,8 // could be anything

        LOGGER.info(info);
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * gfedcab 7654321  //r0-c1
     * gfedcab 7653214  //RL1 c0_c3
     * gfedca7 653214b  //RL1 c5_r5
     */
    Point decodePixelLocation(Dimension imageDimensions, Dimension imageBlockDimensions, Point pxlLocation) {
        final int pixelLocation = pxlLocation.x * imageDimensions.width + pxlLocation.y;

        int result = pixelLocation;
        String info = asBin(result);
        if(!rowBit1EqualsRowBit2(imageDimensions, result)){
            result = toggleBit(result, 2);  info += " -neq r1,r2 ? ^c2-> " + asBin(result); // 4,0 -> 4,16 (^b2 <<2)
        }
        result = swapRowBit0Bit1(imageDimensions, result); info += " -Sw Ro 0,1-> " + asBin(result); // 1,0 -> 2,0

        result = rotateColumnBlockBitsLeft(imageBlockDimensions, result);  info += " -RL1 c0_c3-> " + asBin(result); // 0,32 -> 32,0

        result = rotateColumnWithOneBitOfRowLeft(imageDimensions, result);  info += " -RL1 c0_r1-> " + asBin(result); // 0,32 -> 32,0
        LOGGER.info(info);
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    private String asBin(int pxlLocation){
        return "[" + String.format("%7s",Integer.toBinaryString(pxlLocation / 128)).replace(' ', '0') + " " + String.format("%7s",Integer.toBinaryString(pxlLocation % 128)).replace(' ', '0') + "]";
    }

    /**
     * pixelLocation = 1111 0000 (row=1111, column=0000) -> 1110 0001
     */
    private int rotateColumnWithOneBitOfRowLeft(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        return rotateLeft(pixelLocation, 0, nrOfBitsInColumn + 1);
    }

    private int swapRowBit0Bit1(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int rowBitIndex1 = nrOfBitsInColumn + 0;
        int rowBitIndex2 = nrOfBitsInColumn + 1;
        return swapBits(pixelLocation, rowBitIndex1, rowBitIndex2);
    }

    private boolean rowBit1EqualsRowBit2(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int rowBitIndex0 = nrOfBitsInColumn + 1;
        int rowBitIndex2 = nrOfBitsInColumn + 2;
        return getBit(pixelLocation, rowBitIndex0) == getBit(pixelLocation, rowBitIndex2);
    }

    private boolean bitsEqual(int pixelLocation, int indx1, int indx2){
        return getBit(pixelLocation, indx1) == getBit(pixelLocation, indx2);
    }

    /**
     * pixelLocation = 1111 0001 (row=1111, column=0001) -> 1111 0010
     */
    private int rotateColumnBlockBitsLeft(Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        return rotateLeft(pixelLocation, 0, nrOfBitsInColumn);
    }

    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> image) {
        return null;
    }

    private int rotateLeft(final int input, int start, int end){
        return rotateLeft(input, start, end,1);
    }

    private int rotateLeft(final int input, int start, int end, final int rotationAmount){

        final int leftSideAfterRotation = getBits(input , start, end - rotationAmount);
        final int rightSideAfterRotation = getBits(input , end - rotationAmount, end);

        final int rotatedBits = rightSideAfterRotation | (leftSideAfterRotation << rotationAmount);
        final int rotatedBitsInPosition = rotatedBits << start;

        final int rotationMask = getMask(start, end);

        return (input & ~rotationMask) | rotatedBitsInPosition;
    }

    private int rotateRight(final int input, int start, int end){
        final int rotationAmount = 1;

        final int leftSideAfterRotation = getBits(input , start, start + rotationAmount);
        final int rightSideAfterRotation = getBits(input , start + rotationAmount, end);

        final int rotatedBits = rightSideAfterRotation | (leftSideAfterRotation << (end - start - rotationAmount));
        final int rotatedBitsInPosition = rotatedBits << start;

        final int rotationMask = getMask(start, end);

        return (input & ~rotationMask) | rotatedBitsInPosition;
    }

    private int toggleBit(final int input, final int bitIndex){
        int toggleMask = 1 << bitIndex;
        return input ^ toggleMask;
    }

    /**
     * ex: input 2,5 -> ..000 11100
     */
    private int getMask(int bitIndexStart, int bitIndexEnd){
        return ((1 << (bitIndexEnd - bitIndexStart)) - 1) << bitIndexStart;
    }

}
