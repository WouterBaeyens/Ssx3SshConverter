package image.ssh;

import com.mycompany.sshtobpmconverter.IPixel;
import util.ConverterConfig;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static util.ByteUtil.*;

public class InterleafedBitwiseDecoderStrategy2 implements SshImageDecoderStrategy{

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
                Point decodedpxlLocation = getDecodedPixelLocation(imageDimensions, imageBlockDimensions, pxlLocation);
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
                Point decodedpxlLocation = getDecodedPixelLocation(imageDimensionsInBytes, new Dimension(16,16), pxlLocation);
                decodedBuffer.put(buffer.get(decodedpxlLocation.x * imageDimensionsInBytes.width + decodedpxlLocation.y));
            }
        }
        return decodedBuffer.rewind();
    }

    private Point getDecodedPixelLocation(Dimension imageDimensions, Dimension imageBlockDimensions, Point pxlLocation) {
        final int pixelLocation = pxlLocation.x * imageDimensions.width + pxlLocation.y;

        int result = pixelLocation;
//        result = swapRowBit0Bit1(imageDimensions, result);
//        if(!rowBit0EqualsRowBit2(imageDimensions, result)){
//            result = toggleBit(result, 2);
//        }
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int nrOfBitsInRow = Integer.numberOfTrailingZeros(imageDimensions.width);
        //result = rotateLeft(result, 3, nrOfBitsInColumn, 2);

        //result = rotateColumnBitsLeft(imageDimensions, result, 3);

        // result = rotateRowBitsLeft(imageDimensions, result, 2);



        //result = rotateLeft(result, 0, nrOfBitsInColumn + 1);
        result = rotateLeft(result, 0, nrOfBitsInRow -1, 3);
        result = rotateRowBitsLeft(imageDimensions, result, 2); // rows (all)  01234567 -> 04152637
        //result = swapBits(result, 0, nrOfBitsInColumn); // swaps 0,1 and 1,0 (in 2 by 2 grid)
        return new Point(result / imageDimensions.width, result % imageDimensions.width);
    }

    /**
     * pixelLocation = 1111 0000 (row=1111, column=0000) -> 1110 0001
     */
    private int rotateColumnWithOneBitOfRowLeft(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        return rotateLeft(pixelLocation, 0, nrOfBitsInColumn + 1, 1);
    }

    private int swapRowBit0Bit1(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int rowBitIndex1 = nrOfBitsInColumn + 0;
        int rowBitIndex2 = nrOfBitsInColumn + 1;
        return swapBits(pixelLocation, rowBitIndex1, rowBitIndex2);
    }

    private boolean rowBit0EqualsRowBit2(final Dimension imageDimensions, int pixelLocation){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        int rowBitIndex0 = nrOfBitsInColumn + 0;
        int rowBitIndex2 = nrOfBitsInColumn + 2;
        return getBit(pixelLocation, rowBitIndex0) == getBit(pixelLocation, rowBitIndex2);
    }

    /**
     * pixelLocation = 1111 0001 (row=1111, column=0001) -> 1111 0010
     */
    private int rotateColumnBitsLeft(Dimension imageDimensions, int pixelLocation){
        return rotateColumnBitsLeft(imageDimensions, pixelLocation, 1);
    }

    private int rotateColumnBitsLeft(Dimension imageDimensions, int pixelLocation, int rotationAmount){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.width);
        return rotateLeft(pixelLocation, 0, nrOfBitsInColumn, rotationAmount);
    }

    private int rotateRowBitsLeft(Dimension imageDimensions, int pixelLocation, int rotationAmount){
        int nrOfBitsInColumn = Integer.numberOfTrailingZeros(imageDimensions.height);
        int nrOfBitsInRow = Integer.numberOfTrailingZeros(imageDimensions.width);
        return rotateLeft(pixelLocation, nrOfBitsInRow, nrOfBitsInRow + nrOfBitsInColumn, rotationAmount);
    }



    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> image) {
        return null;
    }

    private int rotateLeft(final int input, int start, int end, int rotationAmount){
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
