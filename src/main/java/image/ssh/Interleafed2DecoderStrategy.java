package image.ssh;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mycompany.sshtobpmconverter.IPixel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Interleafed2DecoderStrategy implements SshImageDecoderStrategy{

    public record Decoder(BiMap<Point, Point> map, int rows, int columns){}

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage) {
        List<List<IPixel>> decodedImage = new ArrayList<>();
        int nrOfRows = encodedImage.size();
        int nrOfColumns = encodedImage.get(0).size();
        Point[][] mask = getDecodingMask(nrOfRows, nrOfColumns);
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++){
            decodedImage.add(new ArrayList<>());
            for(int colNr = 0; colNr < nrOfColumns; colNr++){
                Point pxlLocation = mask[rowNr][colNr];
                decodedImage.get(rowNr).add(colNr, encodedImage.get(pxlLocation.x).get(pxlLocation.y));
            }
        }
        return decodedImage;
    }

    private Point[][] getDecodingMask(final int nrOfRows, final int nrOfColumns){
        Point[][] mask = createDefaultmask(nrOfRows, nrOfColumns);
        Point[][] mask2 = manipulateMask(mask, point -> decode(switchLeftBottomRightTop, point));
        Point[][] mask3 = manipulateMask(mask2, point -> splitEvenUnevenColumnes(nrOfColumns, point));
        Point[][] mask4 = manipulateMask(mask3, point -> decode(switchRows2031, point));
        Point[][] mask5 = manipulateMask(mask4, point -> decode(switch16by2, point));
        Point[][] mask6 = manipulateMask(mask5, point -> decode(switchColumns16, point));
        Point[][] mask7 = manipulateMask(mask6, point -> decode(switchBlocks8, point));
        return mask6;
    }

    private Point[][] createDefaultmask(final int nrOfRows, final int nrOfColumns){
        Point[][] raster = new Point[nrOfRows][nrOfColumns];
        for (int rowNr = 0; rowNr < nrOfRows; rowNr ++){
            for (int colNr = 0; colNr < nrOfColumns; colNr ++){
                raster[rowNr][colNr] = new Point(rowNr, colNr);
            }
        }
        return raster;
    }

    private Point[][] manipulateMask(Point[][] mask, Function<Point, Point> manipulationMethod){
        int rowTot = mask.length;
        int colTot = mask[0].length;
        Point[][] newMask = new Point[rowTot][colTot];//clone2dArray(mask);
        for (int rowNr = 0; rowNr < rowTot; rowNr ++){
            for (int colNr = 0; colNr < colTot; colNr ++){
                Point newCoordinate = manipulationMethod.apply(new Point(rowNr, colNr));
                newMask[newCoordinate.x][newCoordinate.y] = mask[rowNr][colNr];
            }
        }
        return newMask;
    }

    private static Point[][] clone2dArray(Point[][] matrix){
        Point [][] newMatrix = new Point[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
        {
            Point[] aMatrix = matrix[i];
            int aLength = aMatrix.length;
            newMatrix[i] = new Point[aLength];
            System.arraycopy(aMatrix, 0, newMatrix[i], 0, aLength);
        }
        return newMatrix;
    }

    private IPixel getPixelInEncodedImage(List<List<IPixel>> encodedImage, final int rowNr, final int colNr) {
        final Point lastPixel = new Point(encodedImage.size(), encodedImage.get(0).size());
        Point decoded = new Point(rowNr, colNr);
        decoded = decode(switchLeftBottomRightTop, decoded);
        //decoded = decode(switchLeftBottomRightTop, decoded);
        //decoded = splitEvenUnevenColumnes(lastPixel.y, decoded);
        decoded = stretchRow1(decoded);
        return encodedImage.get(decoded.x).get(decoded.y);
    }

    private static final BiMap<Point, Point> m1 = HashBiMap.create();
    Decoder switchLeftBottomRightTop = new Decoder(m1, 2,2);
    static{
        m1.put(new Point(0,0), new Point(0,0));
        m1.put(new Point(0,1), new Point(1,0));
        m1.put(new Point(1,0), new Point(0,1));
        m1.put(new Point(1,1), new Point(1,1));
    }

    private static final BiMap<Point, Point> m2 = HashBiMap.create();
    Decoder switchRows2031 = new Decoder(m1, 2,2);
    static{
        m2.put(new Point(0,0), new Point(2,0));
        m2.put(new Point(1,0), new Point(0,0));
        m2.put(new Point(2,0), new Point(3,0));
        m2.put(new Point(3,0), new Point(1,0));
    }

    private static final BiMap<Point, Point> m3 = HashBiMap.create();
    Decoder switch16by2 = new Decoder(m1, 16,2);
    static{
        m2.put(new Point(3,1), new Point(5,0));
        m2.put(new Point(4,1), new Point(6,0));
        m2.put(new Point(5,0), new Point(3,1));
        m2.put(new Point(6,0), new Point(4,1));
    }

    private static final BiMap<Point, Point> m4 = HashBiMap.create();
    Decoder switchColumns16 = new Decoder(m4, 1,16);
    static{
        m4.put(new Point(0,0), new Point(0,2));
        m4.put(new Point(0,1), new Point(0,6));
        m4.put(new Point(0,2), new Point(0,3));
        m4.put(new Point(0,3), new Point(0,7));
        m4.put(new Point(0,4), new Point(0,10));
        m4.put(new Point(0,5), new Point(0,14));
        m4.put(new Point(0,6), new Point(0,11));
        m4.put(new Point(0,7), new Point(0,15));
        m4.put(new Point(0,8), new Point(0,0));
        m4.put(new Point(0,9), new Point(0,4));
        m4.put(new Point(0,10), new Point(0,1));
        m4.put(new Point(0,11), new Point(0,5));
        m4.put(new Point(0,12), new Point(0,8));
        m4.put(new Point(0,13), new Point(0,12));
        m4.put(new Point(0,14), new Point(0,9));
        m4.put(new Point(0,15), new Point(0,13));
    }

    private static final BiMap<Point, Point> m5 = HashBiMap.create();
    Decoder switchBlocks8 = new Decoder(m5, 16,16);
    static{
        m5.put(new Point(0, 0), new Point(0,2));
        m5.put(new Point(0, 1), new Point(0,3));
        m5.put(new Point(0, 2), new Point(0,0));
        m5.put(new Point(0, 3), new Point(0,1));

//        m5.put(new Point(4, 14), new Point(11,14));
//        m5.put(new Point(4, 15), new Point(11,15));

        m5.put(new Point(1, 2), new Point(2,2));
        m5.put(new Point(1, 3), new Point(2,3));
        m5.put(new Point(1, 4), new Point(2,4));
        m5.put(new Point(1, 5), new Point(2,5));
        m5.put(new Point(1, 6), new Point(2,6));
        m5.put(new Point(1, 7), new Point(2,7));
       // m5.put(new Point(1, 8), new Point(2,8));
       // m5.put(new Point(1, 9), new Point(2,9));
        m5.put(new Point(1, 10), new Point(2,10));
        m5.put(new Point(1, 11), new Point(2,11));
        m5.put(new Point(1, 12), new Point(2,12));
        m5.put(new Point(1, 13), new Point(2,13));
        m5.put(new Point(1, 14), new Point(2,14));
        m5.put(new Point(1, 15), new Point(2,15));

        m5.put(new Point(2, 0), new Point(2,0));
        m5.put(new Point(2, 1), new Point(2,1));

        m5.put(new Point(2, 2), new Point(1,2));
        m5.put(new Point(2, 3), new Point(1,3));
        m5.put(new Point(2, 4), new Point(1,4));
        m5.put(new Point(2, 5), new Point(1,5));
        m5.put(new Point(2, 6), new Point(1,6));
        m5.put(new Point(2, 7), new Point(1,7));

        m5.put(new Point(2, 8), new Point(2,8));
        m5.put(new Point(2, 9), new Point(2,9));

        m5.put(new Point(2, 10), new Point(1,10));
        m5.put(new Point(2, 11), new Point(1,11));
        m5.put(new Point(2, 12), new Point(1,12));
        m5.put(new Point(2, 13), new Point(1,13));
        m5.put(new Point(2, 14), new Point(1,14));
        m5.put(new Point(2, 15), new Point(1,15));

        m5.put(new Point(3, 0), new Point(3,0));
        m5.put(new Point(3, 1), new Point(3,1));
        m5.put(new Point(3, 2), new Point(3,2));
        m5.put(new Point(3, 3), new Point(3,3));
        m5.put(new Point(3, 4), new Point(3,4));
        m5.put(new Point(3, 5), new Point(3,5));
        m5.put(new Point(3, 6), new Point(3,6));
        m5.put(new Point(3, 7), new Point(3,7));
        m5.put(new Point(3, 8), new Point(3,8));
        m5.put(new Point(3, 9), new Point(3,9));
        m5.put(new Point(3, 10), new Point(3,10));
        m5.put(new Point(3, 11), new Point(3,11));
        m5.put(new Point(3, 12), new Point(3,12));
        m5.put(new Point(3, 13), new Point(3,13));
        m5.put(new Point(3, 14), new Point(3,14));
        m5.put(new Point(3, 15), new Point(3,15));

        m5.put(new Point(4, 0), new Point(4,0));
        m5.put(new Point(4, 1), new Point(4,1));
        m5.put(new Point(4, 2), new Point(4,2));
        m5.put(new Point(4, 3), new Point(4,3));
        m5.put(new Point(4, 4), new Point(4,4));
        m5.put(new Point(4, 5), new Point(4,5));
        m5.put(new Point(4, 6), new Point(4,6));
        m5.put(new Point(4, 7), new Point(4,7));
        m5.put(new Point(4, 8), new Point(4,8));
        m5.put(new Point(4, 9), new Point(4,9));
        m5.put(new Point(4, 10), new Point(4,10));
        m5.put(new Point(4, 11), new Point(4,11));
        m5.put(new Point(4, 12), new Point(4,12));
        m5.put(new Point(4, 13), new Point(4,13));
        m5.put(new Point(4, 14), new Point(4,14));
        m5.put(new Point(4, 15), new Point(4,15));

        m5.put(new Point(5, 0), new Point(6,0));
        m5.put(new Point(5, 1), new Point(6,1));
        m5.put(new Point(5, 2), new Point(6,2));
        m5.put(new Point(5, 3), new Point(6,3));
        m5.put(new Point(5, 4), new Point(6,4));
        m5.put(new Point(5, 5), new Point(6,5));
        m5.put(new Point(5, 6), new Point(6,6));
        m5.put(new Point(5, 7), new Point(6,7));
        m5.put(new Point(5, 8), new Point(6,8));
        m5.put(new Point(5, 9), new Point(6,9));
        m5.put(new Point(5, 10), new Point(6,10));
        m5.put(new Point(5, 11), new Point(6,11));
        m5.put(new Point(5, 12), new Point(6,12));
        m5.put(new Point(5, 13), new Point(6,13));
        m5.put(new Point(5, 14), new Point(6,14));
        m5.put(new Point(5, 15), new Point(6,15));

        m5.put(new Point(6, 0), new Point(5,0));
        m5.put(new Point(6, 1), new Point(5,1));
        m5.put(new Point(6, 2), new Point(5,2));
        m5.put(new Point(6, 3), new Point(5,3));
        m5.put(new Point(6, 4), new Point(5,4));
        m5.put(new Point(6, 5), new Point(5,5));
        m5.put(new Point(6, 6), new Point(5,6));
        m5.put(new Point(6, 7), new Point(5,7));
        m5.put(new Point(6, 8), new Point(5,8));
        m5.put(new Point(6, 9), new Point(5,9));
        m5.put(new Point(6, 10), new Point(5,10));
        m5.put(new Point(6, 11), new Point(5,11));
        m5.put(new Point(6, 12), new Point(5,12));
        m5.put(new Point(6, 13), new Point(5,13));
        m5.put(new Point(6, 14), new Point(5,14));
        m5.put(new Point(6, 15), new Point(5,15));

        m5.put(new Point(8, 4), new Point(9,4));
        m5.put(new Point(8, 5), new Point(9,5));
        m5.put(new Point(8, 6), new Point(9,6));
        m5.put(new Point(8, 7), new Point(9,7));
        m5.put(new Point(8, 12), new Point(9,12));
        m5.put(new Point(8, 13), new Point(9,13));
        m5.put(new Point(8, 14), new Point(9,14));
        m5.put(new Point(8, 15), new Point(9,15));

        m5.put(new Point(9, 0), new Point(10,0));
        m5.put(new Point(9, 1), new Point(10,1));
        m5.put(new Point(9, 2), new Point(10,2));
        m5.put(new Point(9, 3), new Point(10,3));

        m5.put(new Point(9, 4), new Point(8,4));
        m5.put(new Point(9, 5), new Point(8,5));
        m5.put(new Point(9, 6), new Point(8,6));
        m5.put(new Point(9, 7), new Point(8,7));

        m5.put(new Point(9, 8), new Point(10,8));
        m5.put(new Point(9, 9), new Point(10,9));
        m5.put(new Point(9, 10), new Point(10,10));
        m5.put(new Point(9, 11), new Point(10,11));

        m5.put(new Point(9, 12), new Point(8,12));
        m5.put(new Point(9, 13), new Point(8,13));
        m5.put(new Point(9, 14), new Point(8,14));
        m5.put(new Point(9, 15), new Point(8,15));

        m5.put(new Point(10, 0), new Point(9,0));
        m5.put(new Point(10, 1), new Point(9,1));
        m5.put(new Point(10, 2), new Point(9,2));
        m5.put(new Point(10, 3), new Point(9,3));

        m5.put(new Point(10, 8), new Point(9,8));
        m5.put(new Point(10, 9), new Point(9,9));
        m5.put(new Point(10, 10), new Point(9,10));
        m5.put(new Point(10, 11), new Point(9,11));

//        m5.put(new Point(11, 14), new Point(4,14));
//        m5.put(new Point(11, 15), new Point(4,15));

//        m5.put(new Point(12, 8), new Point(13,8));
//        m5.put(new Point(12, 9), new Point(13,9));
//        m5.put(new Point(12, 10), new Point(13,10));
//        m5.put(new Point(12, 11), new Point(13,11));

        m5.put(new Point(13, 0), new Point(14,0));
        m5.put(new Point(13, 1), new Point(14,1));
        m5.put(new Point(13, 2), new Point(14,2));
        m5.put(new Point(13, 3), new Point(14,3));
        m5.put(new Point(13, 4), new Point(14,4));
        m5.put(new Point(13, 5), new Point(14,5));
        m5.put(new Point(13, 6), new Point(14,6));
        m5.put(new Point(13, 7), new Point(14,7));
        m5.put(new Point(13, 8), new Point(14,8));
        m5.put(new Point(13, 9), new Point(14,9));
        m5.put(new Point(13, 10), new Point(14,10));
        m5.put(new Point(13, 11), new Point(14,11));
        m5.put(new Point(13, 12), new Point(14,12));
        m5.put(new Point(13, 13), new Point(14,13));
        m5.put(new Point(13, 14), new Point(14,14));
        m5.put(new Point(13, 15), new Point(14,15));

        m5.put(new Point(14, 0), new Point(13,0));
        m5.put(new Point(14, 1), new Point(13,1));
        m5.put(new Point(14, 2), new Point(13,2));
        m5.put(new Point(14, 3), new Point(13,3));
        m5.put(new Point(14, 4), new Point(13,4));
        m5.put(new Point(14, 5), new Point(13,5));
        m5.put(new Point(14, 6), new Point(13,6));
        m5.put(new Point(14, 7), new Point(13,7));
        m5.put(new Point(14, 8), new Point(13,8));
        m5.put(new Point(14, 9), new Point(13,9));
        m5.put(new Point(14, 10), new Point(13,10));
        m5.put(new Point(14, 11), new Point(13,11));
        m5.put(new Point(14, 12), new Point(13,12));
        m5.put(new Point(14, 13), new Point(13,13));
        m5.put(new Point(14, 14), new Point(13,14));
        m5.put(new Point(14, 15), new Point(13,15));


    }

    private Point splitEvenUnevenColumnes(int rowSize,final Point point){
        boolean isEven = point.y % 2 == 0;
        if(isEven){
            return new Point(point.x, point.y / 2);
        } else {
            int halfWayPoint = rowSize / 2;
            return new Point(point.x, point.y / 2 + halfWayPoint);
        }
    }

    private Point stretchRow1(final Point point){
        return new Point(point.x, 0);
    }

    private Point decode(final Decoder decoder, final Point point){
        final int rowOffset = point.x % decoder.rows;
        final int rowBase = point.x - rowOffset;

        final int columnOffset = point.y % decoder.columns;
        final int columnBase = point.y - columnOffset;

        final Point originalOffsets = new Point(rowOffset, columnOffset);
        final Point decodedOffsets = decoder.map.getOrDefault(originalOffsets, originalOffsets);

        return new Point(rowBase + decodedOffsets.x, columnBase + decodedOffsets.y);
    }



    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> image) {
        return null;
    }



}