package image.ssh;

import com.mycompany.sshtobpmconverter.IPixel;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;
import image.ssh2.imageheader.strategies._4BitByteToPixelStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static util.ByteUtil.*;

public class InterleafedBitwiseDecoderStrategy implements SshImageDecoderStrategy{

    private static final Logger LOGGER = LoggerFactory.getLogger(InterleafedBitwiseDecoderStrategy.class);

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage, ByteToPixelStrategy byteToPixelStrategy) {
        List<List<IPixel>> decodedImage = new ArrayList<>();
        int nrOfRows = encodedImage.size();
        int nrOfColumns = encodedImage.get(0).size();
        boolean isLowRezImage = byteToPixelStrategy instanceof _4BitByteToPixelStrategy;
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++) {
            decodedImage.add(new ArrayList<>());
        }
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++) {
            for (int colNr = 0; colNr < nrOfColumns; colNr++) {
                Point pxlLocation = new Point(rowNr, colNr);
                Dimension imageDimensions = new Dimension(nrOfColumns, nrOfRows);
                Point decodedpxlLocation;
                if(isLowRezImage) {
                    decodedpxlLocation = decodeLowRezPixelLocation(pxlLocation, imageDimensions);
                } else {
                    decodedpxlLocation = decodePixelLocation(imageDimensions, new Dimension(16,16), pxlLocation);
                }
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

    Point decodeLowRezPixelLocation(Point pxlLocation, Dimension imageDimensions){
        final Point locationAfterHighRezDecoding = decodePixelLocation(imageDimensions, new Dimension(32,32), pxlLocation);
        final int pixelLocation = locationAfterHighRezDecoding.x * imageDimensions.width + locationAfterHighRezDecoding.y;
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int nrOfBitsInRow = Integer.numberOfTrailingZeros(imageDimensions.height);

        int result = pixelLocation;
        result = rotateLeft(result, 1, 6, 1);
        result = rotateRight(result, nrOfBitsInColumn + 1, nrOfBitsInColumn + nrOfBitsInRow, 3);
        result = rotateRight(result, nrOfBitsInColumn - 1, nrOfBitsInColumn + nrOfBitsInRow, 2);

        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * Decoding a 128:128
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * 76edcba gf54321  //r56-c56
     * 76edcba gf32154  //RL2 c0_c5
     * 76dcbag fe32154  //RL1 c5_r5
     * 76dcagf e32154b  //RL1 c0_r3
     */
    Point decodeLowRezPixelLocationExperiment(Point pxlLocation, Dimension imageDimensions){
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
     * 76dcbag fe32154  //RL1 c5_r5
     * 76dcaf  e32154b  //RL1 c0_r3
     */
    Point decodeLowRezPixelLocationWideExperiment(Point pxlLocation){
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

        result = swapBits(result, 7, 8,1); info += " -Sw Ro 0,1-> " + asBin(result); // 0,32 -> 32,0
        result = rotateRight(result, 7, 14); info += " -RR Ro 0,7-> " + asBin(result); // 0,32 -> 32,0

        LOGGER.info(info);
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * gfedcab 7654321  //SW r0-r1
     * gfedcab 7653214  //RL1 c0_c3
     * gfedca7 653214b  //RL1 c0_r0
     */
    Point decodePixelLocation(Dimension imageDimensions, Dimension imageBlockDimensions, Point pxlLocation) {
        final int pixelLocation = pxlLocation.x * imageDimensions.width + pxlLocation.y;

        int result = pixelLocation;
        if(!rowBit1EqualsRowBit2(imageDimensions, result)){
            result = toggleBit(result, 2);  // info += " -neq r1,r2 ? ^c2-> " + asBin(result); // 4,0 -> 4,16 (^b2 <<2)
        }
        result = swapRowBit0Bit1(imageDimensions, result); // info += " -Sw Ro 0,1-> " + asBin(result); // 1,0 -> 2,0

        result = rotateColumnBlockBitsLeft(imageBlockDimensions, result);  // info += " -RL1 c0_c3-> " + asBin(result); // 0,32 -> 32,0

        result = rotateColumnWithOneBitOfRowLeft(imageDimensions, result);  // info += " -RL1 c0_r1-> " + asBin(result); // 0,32 -> 32,0
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * Steps:
     * gfedcba 7654321 (toggle 3 if b/c is different)
     * gfedcab 7654321  //SW r0-r1
     * gfedcab 7653214  //RL1 c0_c3
     * gfedca7 653214b  //RL1 c0_r0
     */
    Point decodePixelLocationExperiment(Dimension imageDimensions, Dimension imageBlockDimensions, Point pxlLocation) {
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

    private String asBin(int pxlLocation, Dimension imageDimensions){
        int rowBits = Integer.numberOfTrailingZeros(imageDimensions.height);
        int colBits = Integer.numberOfTrailingZeros(imageDimensions.width);
        return "[" + String.format("%"+rowBits+"s",Integer.toBinaryString(pxlLocation / imageDimensions.width)).replace(' ', '0') + " " + String.format("%"+colBits+"s",Integer.toBinaryString(pxlLocation % imageDimensions.width)).replace(' ', '0') + "]";
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
        return rotateLeft(input, start, end,1);
    }

    private int rotateRight(final int input, int start, int end, int rotationAmount){
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
