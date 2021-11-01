package image.ssh;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mycompany.sshtobpmconverter.IPixel;
import com.mycompany.sshtobpmconverter.Pixel2;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Interleafed2DecoderStrategy implements SshImageDecoderStrategy{

    private static final boolean ENABLE_DEBUG_GRID_LINES = false;

    /**
     * The map defines for each innerBlock coordinate in outerBlock a new coordinate.
     * ex: map = {{0,1}, {0,0}} ; outerBlock=(w:8,h:1) ; innerBlock=(w:4,h:1)
     *      -----------------------------------           -----------------------------------
     *     |  -------------    -------------   |         |  -------------    -------------   |
     *     | | 1  2  3  4 |   |  5  6  7  8 |  |         | | 5  6  7  8 |   |  1  2  3  4 |  |
     *     |  -------------    -------------   |         |  -------------    -------------   |
     *     -------------------------------------   ->    -------------------------------------
     *     -------------------------------------         -------------------------------------
     *     |  -------------    -------------   |         |  -------------    -------------   |
     *     | | 9  10 11 12 |   | 13 14 15 16|  |         | | 13 14 15 16 |   | 9 10 11 12 |  |
     *     |  -------------    -------------   |         |  -------------    -------------   |
     */
    public record Decoder(BiMap<Point, Point> map, Dimension outerBlock, Dimension innerBlock){}

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage, ByteToPixelStrategy byteToPixelStrategy) {
        final Dimension imgDimensions = new Dimension(encodedImage.get(0).size(), encodedImage.size());
        Point[][] mask = createLowRezWideDecodingMask(imgDimensions);
        List<List<IPixel>> decodedImage = decodeImage(encodedImage, mask);
        if(ENABLE_DEBUG_GRID_LINES) addDebugGridlines(decodedImage);
        return decodedImage;
    }

    private Point[][] createDecodingMask(final Dimension imgDimension){
        Point[][] mask = createDefaultmask(imgDimension);
        Point[][] mask2 = manipulateMask(mask, point -> decode(switchLeftBottomRightTop, point));
        Point[][] mask3 = manipulateMask(mask2, point -> splitEvenUnevenColumnes(imgDimension.width, point));
        Point[][] maskNew = manipulateMask(mask3, point -> decode(switchBlocksAll, point));
        return maskNew;
    }

    private Point[][] createLowRezDecodingMask(final Dimension imgDimension){
        Point[][] mask = createDefaultmask(imgDimension);
        var mask2 = manipulateMask(mask, point -> decodeVerticalUnrepeating(imgDimension, List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0)), point));
        var mask3 = manipulateMask(mask2, point -> decode(testDecoderBlock, point));
        var mask4 = manipulateMask(mask3, point -> decodeHorizontalUnrepeating(new Dimension(32, 1), List.of(new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4), new Point(0,5), new Point(0,6), new Point(0,7)), point));
        var mask5 = manipulateMask(mask4, point -> decodeVerticalUnrepeating(new Dimension(1, 16), List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(4, 0), new Point(5, 0), new Point(6, 0), new Point(7, 0)), point));
        var mask6 = manipulateMask(mask5, point -> decode(testDecoderBlock2, point));
        var mask7 = manipulateMask(mask6, point -> decode(testDecoderBlock3, point));
        var maskNew = manipulateMask(mask7, point -> decode(testDecoderBlock4, point));
        return maskNew;
    }

    private Point[][] createLowRezWideDecodingMask(final Dimension imgDimension){
        Point[][] mask = createDefaultmask(imgDimension);
        var mask2 = manipulateMask(mask, point -> decodeVerticalUnrepeating(new Dimension(1,16), List.of(new Point(0, 0), new Point(1, 0)), point));
        var mask3 = manipulateMask(mask2, point -> decodeHorizontalUnrepeating(new Dimension(64,1), List.of(new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4), new Point(0,5), new Point(0,6), new Point(0,7)), point));
        var mask4 = manipulateMask(mask3, point -> decode(interleafVertical8Decoder, point));
        var mask5 = manipulateMask(mask4, point -> dedoubleMask(imgDimension,point));

        var mask6 = manipulateMask(mask5, point -> decode(new Decoder(asBimap(lowRez), new Dimension(8, 2), new Dimension(4,1)), point));
        var mask7 = manipulateMask(mask6, point -> decode(decoderLR1, point));
        var mask8 = manipulateMask(mask7, point -> decode(decoderLR2, point));
        var newMask = manipulateMask(mask8, point -> decode(decoderLR3, point));


//        var mask4 = manipulateMask(mask3, point -> decodeHorizontalUnrepeating(new Dimension(32, 1), List.of(new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3), new Point(0,4), new Point(0,5), new Point(0,6), new Point(0,7)), point));
//        var mask5 = manipulateMask(mask4, point -> decodeVerticalUnrepeating(new Dimension(1, 16), List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(4, 0), new Point(5, 0), new Point(6, 0), new Point(7, 0)), point));
//        var mask6 = manipulateMask(mask5, point -> decode(testDecoderBlock2, point));
//        var mask7 = manipulateMask(mask6, point -> decode(testDecoderBlock3, point));
//        var maskNew = manipulateMask(mask7, point -> decode(testDecoderBlock4, point));
        return newMask;
    }

    Point[][] createDefaultmask(final Dimension imgDimension){
        Point[][] raster = new Point[imgDimension.height][imgDimension.width];
        IntStream.range(0, imgDimension.height)
                .forEach(rowNr -> IntStream.range(0, imgDimension.width)
                        .forEach(colNr -> raster[rowNr][colNr] = new Point(rowNr, colNr)));
        return raster;
    }

    List<Point> createDefaultRowList(int height){
        List<Point> points = new ArrayList<>();
        IntStream.range(0, height)
                .forEach(rowNr -> points.add(new Point(rowNr, 0)));
        return points;
    }

    List<Point> createDefaultColumnList(int width){
        List<Point> points = new ArrayList<>();
        IntStream.range(0, width)
                .forEach(colNr -> points.add(new Point(0, colNr)));
        return points;
    }

    Point[][] manipulateMask(Point[][] mask, Function<Point, Point> manipulationMethod){
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

    private Point dedoubleMask(Dimension imageDimension, Point point){
        boolean isUpperHalf = point.x < imageDimension.height / 2;
        if (isUpperHalf){
            int newY = point.y / 2;
            int newX = point.x * 2 + point.y % 2;
            return new Point(newX, newY);
        } else {
            int newY = point.y / 2 + imageDimension.width / 2;
            int newX = (point.x - imageDimension.height / 2) * 2 + point.y % 2;
            return new Point(newX, newY);
        }
    }

    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage, Point[][] decodingMask) {
        final Dimension imgDimensions = new Dimension(encodedImage.get(0).size(), encodedImage.size());
        List<List<IPixel>> decodedImage = createNewImage(imgDimensions);
        for(int rowNr = 0; rowNr < imgDimensions.height; rowNr ++){
            for(int colNr = 0; colNr < imgDimensions.width; colNr++){
                Point pxlLocation = decodingMask[rowNr][colNr];
                decodedImage.get(rowNr).set(colNr, encodedImage.get(pxlLocation.x).get(pxlLocation.y));
            }
        }
        return decodedImage;
    }

        private List<List<IPixel>> createNewImage(Dimension dimensions){
        final List<List<IPixel>> newImage = new ArrayList<>(dimensions.height);
        IntStream.range(0, dimensions.height)
                .forEach(ignored -> newImage.add(Arrays.asList(new IPixel[dimensions.width])));
        return newImage;
    }

    private void addDebugGridlines(List<List<IPixel>> image){
        for (int rowNr = 0; rowNr < image.size(); rowNr += 16){
            for(int colNr = 0; colNr < image.get(0).size(); colNr ++){
                if(colNr % 16 == 0){
                    image.get(rowNr).set(colNr, Pixel2.GREY_PIXEL);
                } else if(colNr%4==0) {
                    image.get(rowNr).set(colNr, Pixel2.DARK_GREY_PIXEL);
                } else {
                    image.get(rowNr).set(colNr, Pixel2.getDefaultPixel());
                }
            }
        }
    }

    Point[][] maskLR1 = {
            {new Point(0,0)},
            {new Point(2,0)},
            {new Point(1,0)},
            {new Point(3,0)},
            {new Point(4,0)},
            {new Point(6,0)},
            {new Point(5,0)},
            {new Point(7,0)},
    };
    Decoder decoderLR1 = new Decoder(asBimap(maskLR1), new Dimension(1,8), new Dimension(1,1));

    Point[][] maskLR2 = {
            {new Point(0,0), new Point(0,2), new Point(0,1), new Point(0,3)},
            {new Point(1,1), new Point(1,3), new Point(1,0), new Point(1,2)},
            {new Point(2,1), new Point(2,3), new Point(2,0), new Point(2,2)},
            {new Point(3,0), new Point(3,2), new Point(3,1), new Point(3,3)},
    };
    Decoder decoderLR2 = new Decoder(asBimap(maskLR2), new Dimension(8,8), new Dimension(2,2));

    Point[][] maskLR3 = {
            {new Point(0,0), new Point(2,0),new Point(1,0),new Point(3,0)}
            };
    Decoder decoderLR3 = new Decoder(asBimap(maskLR2), new Dimension(4,1), new Dimension(1,1));


    Point[][] lowRez = {
            {new Point(0,0), new Point(1,0)},
            {new Point(0,1), new Point(1,1)},
    };

    Point[][] interleafVertical8 = {
            {new Point(0,0), new Point(2,0)},
            {new Point(0,1), new Point(2,1)},
            {new Point(1,0), new Point(3,0)},
            {new Point(1,1), new Point(3,1)},
    };
    Decoder interleafVertical8Decoder = new Decoder(asBimap(interleafVertical8), new Dimension(128,32), new Dimension(64,8));


    Point[][] testDecoderBlockMask = {
            {new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
            {new Point(0,1), new Point(1,1), new Point(2,1), new Point(3,1)},
            {new Point(0,2), new Point(1,2), new Point(2,2), new Point(3,2)},
            {new Point(0,3), new Point(1,3), new Point(2,3), new Point(3,3)},
            {new Point(4,0), new Point(5,0), new Point(6,0), new Point(7,0)},
            {new Point(4,1), new Point(5,1), new Point(6,1), new Point(7,1)},
            {new Point(4,2), new Point(5,2), new Point(6,2), new Point(7,2)},
            {new Point(4,3), new Point(5,3), new Point(6,3), new Point(7,3)},
    };
    Decoder testDecoderBlock = new Decoder(asBimap(testDecoderBlockMask), new Dimension(128, 64), new Dimension(32, 8));

    Point[][] testDecoderBlockMask2 = {
            {new Point(0,0), new Point(0,1)},
            {new Point(1,1), new Point(1,0)},
            {new Point(2,0), new Point(2,1)},
            {new Point(3,1), new Point(3,0)},
            {new Point(4,1), new Point(4,0)},
            {new Point(5,0), new Point(5,1)},
            {new Point(6,1), new Point(6,0)},
            {new Point(7,0), new Point(7,1)},
    };
    Decoder testDecoderBlock2 = new Decoder(asBimap(testDecoderBlockMask2), new Dimension(8,8), new Dimension(4,1));

    Point[][] testDecoderBlockMask3 = {
            {new Point(1,0), new Point(0,1)},
            {new Point(3,0), new Point(2,1)},
            {new Point(0,0), new Point(1,1)},
            {new Point(2,0), new Point(3,1)},
    };
    Decoder testDecoderBlock3 = new Decoder(asBimap(testDecoderBlockMask3), new Dimension(8,4), new Dimension(4,1));

    Point[][] testDecoderBlockMask4 = {
            {new Point(1,0)},
            {new Point(0,0)},
            {new Point(2,0)},
            {new Point(3,0)},
    };
    Decoder testDecoderBlock4 = new Decoder(asBimap(testDecoderBlockMask4), new Dimension(1,8), new Dimension(1,2));


    Point[][] testDecoderMask = {
            {new Point(0,2), new Point(0,0), new Point(0,1), new Point(0,3)}
    };
    Decoder testDecoder = new Decoder(asBimap(testDecoderMask), new Dimension(4,2),new Dimension(1,1));

    private static final BiMap<Point, Point> m1 = HashBiMap.create();
    Decoder switchLeftBottomRightTop = new Decoder(m1, new Dimension(2,2),new Dimension(1,1));
    static{
        m1.put(new Point(0,0), new Point(0,0));
        m1.put(new Point(0,1), new Point(1,0));
        m1.put(new Point(1,0), new Point(0,1));
        m1.put(new Point(1,1), new Point(1,1));
    }

    private static final BiMap<Point, Point> m7 = HashBiMap.create();
    Decoder switchBlocksLow = new Decoder(m7.inverse(), new Dimension(16,16),new Dimension(1,1));
    static {
        m7.put(new Point(0, 0), new Point(0, 0));
        m7.put(new Point(0, 1), new Point(0, 1));
        m7.put(new Point(0, 2), new Point(0, 2));
        m7.put(new Point(0, 3), new Point(0, 3));
        m7.put(new Point(0, 4), new Point(0, 4));
        m7.put(new Point(0, 5), new Point(0, 5));
        m7.put(new Point(0, 6), new Point(0, 6));
        m7.put(new Point(0, 7), new Point(0, 7));
        m7.put(new Point(0, 8), new Point(0, 8));
        m7.put(new Point(0, 9), new Point(0, 9));
        m7.put(new Point(0, 10), new Point(0, 10));
        m7.put(new Point(0, 11), new Point(0, 11));
        m7.put(new Point(0, 12), new Point(0, 12));
        m7.put(new Point(0, 13), new Point(0, 13));
        m7.put(new Point(0, 14), new Point(0, 14));
        m7.put(new Point(0, 15), new Point(0, 15));

        m7.put(new Point(1, 0), new Point(4, 0));
        m7.put(new Point(1, 1), new Point(4, 1));
        m7.put(new Point(1, 2), new Point(4, 2));
        m7.put(new Point(1, 3), new Point(4, 3));
        m7.put(new Point(1, 4), new Point(4, 4));
        m7.put(new Point(1, 5), new Point(4, 5));
        m7.put(new Point(1, 6), new Point(4, 6));
        m7.put(new Point(1, 7), new Point(4, 7));
        m7.put(new Point(1, 8), new Point(4, 8));
        m7.put(new Point(1, 9), new Point(4, 9));
        m7.put(new Point(1, 10), new Point(4, 10));
        m7.put(new Point(1, 11), new Point(4, 11));
        m7.put(new Point(1, 12), new Point(4, 12));
        m7.put(new Point(1, 13), new Point(4, 13));
        m7.put(new Point(1, 14), new Point(4, 14));
        m7.put(new Point(1, 15), new Point(4, 15));

        m7.put(new Point(2, 0), new Point(8, 0));
        m7.put(new Point(2, 1), new Point(8, 1));
        m7.put(new Point(2, 2), new Point(8, 2));
        m7.put(new Point(2, 3), new Point(8, 3));
        m7.put(new Point(2, 4), new Point(8, 4));
        m7.put(new Point(2, 5), new Point(8, 5));
        m7.put(new Point(2, 6), new Point(8, 6));
        m7.put(new Point(2, 7), new Point(8, 7));
        m7.put(new Point(2, 8), new Point(8, 8));
        m7.put(new Point(2, 9), new Point(8, 9));
        m7.put(new Point(2, 10), new Point(8, 10));
        m7.put(new Point(2, 11), new Point(8, 11));
        m7.put(new Point(2, 12), new Point(8, 12));
        m7.put(new Point(2, 13), new Point(8, 13));
        m7.put(new Point(2, 14), new Point(8, 14));
        m7.put(new Point(2, 15), new Point(8, 15));

        m7.put(new Point(3, 0), new Point(12, 0));
        m7.put(new Point(3, 1), new Point(12, 1));
        m7.put(new Point(3, 2), new Point(12, 2));
        m7.put(new Point(3, 3), new Point(12, 3));
        m7.put(new Point(3, 4), new Point(12, 4));
        m7.put(new Point(3, 5), new Point(12, 5));
        m7.put(new Point(3, 6), new Point(12, 6));
        m7.put(new Point(3, 7), new Point(12, 7));
        m7.put(new Point(3, 8), new Point(12, 8));
        m7.put(new Point(3, 9), new Point(12, 9));
        m7.put(new Point(3, 10), new Point(12, 10));
        m7.put(new Point(3, 11), new Point(12, 11));
        m7.put(new Point(3, 12), new Point(12, 12));
        m7.put(new Point(3, 13), new Point(12, 13));
        m7.put(new Point(3, 14), new Point(12, 14));
        m7.put(new Point(3, 15), new Point(12, 15));

        m7.put(new Point(4, 0), new Point(1, 0));
        m7.put(new Point(4, 1), new Point(1, 1));
        m7.put(new Point(4, 2), new Point(1, 2));
        m7.put(new Point(4, 3), new Point(1, 3));
        m7.put(new Point(4, 4), new Point(1, 4));
        m7.put(new Point(4, 5), new Point(1, 5));
        m7.put(new Point(4, 6), new Point(1, 6));
        m7.put(new Point(4, 7), new Point(1, 7));
        m7.put(new Point(4, 8), new Point(1, 8));
        m7.put(new Point(4, 9), new Point(1, 9));
        m7.put(new Point(4, 10), new Point(1, 10));
        m7.put(new Point(4, 11), new Point(1, 11));
        m7.put(new Point(4, 12), new Point(1, 12));
        m7.put(new Point(4, 13), new Point(1, 13));
        m7.put(new Point(4, 14), new Point(1, 14));
        m7.put(new Point(4, 15), new Point(1, 15));

        m7.put(new Point(5, 0), new Point(5, 0));
        m7.put(new Point(5, 1), new Point(5, 1));
        m7.put(new Point(5, 2), new Point(5, 2));
        m7.put(new Point(5, 3), new Point(5, 3));
        m7.put(new Point(5, 4), new Point(5, 4));
        m7.put(new Point(5, 5), new Point(5, 5));
        m7.put(new Point(5, 6), new Point(5, 6));
        m7.put(new Point(5, 7), new Point(5, 7));
        m7.put(new Point(5, 8), new Point(5, 8));
        m7.put(new Point(5, 9), new Point(5, 9));
        m7.put(new Point(5, 10), new Point(5, 10));
        m7.put(new Point(5, 11), new Point(5, 11));
        m7.put(new Point(5, 12), new Point(5, 12));
        m7.put(new Point(5, 13), new Point(5, 13));
        m7.put(new Point(5, 14), new Point(5, 14));
        m7.put(new Point(5, 15), new Point(5, 15));

        m7.put(new Point(6, 0), new Point(9, 0));
        m7.put(new Point(6, 1), new Point(9, 1));
        m7.put(new Point(6, 2), new Point(9, 2));
        m7.put(new Point(6, 3), new Point(9, 3));
        m7.put(new Point(6, 4), new Point(9, 4));
        m7.put(new Point(6, 5), new Point(9, 5));
        m7.put(new Point(6, 6), new Point(9, 6));
        m7.put(new Point(6, 7), new Point(9, 7));
        m7.put(new Point(6, 8), new Point(9, 8));
        m7.put(new Point(6, 9), new Point(9, 9));
        m7.put(new Point(6, 10), new Point(9, 10));
        m7.put(new Point(6, 11), new Point(9, 11));
        m7.put(new Point(6, 12), new Point(9, 12));
        m7.put(new Point(6, 13), new Point(9, 13));
        m7.put(new Point(6, 14), new Point(9, 14));
        m7.put(new Point(6, 15), new Point(9, 15));

        m7.put(new Point(7, 0), new Point(13, 0));
        m7.put(new Point(7, 1), new Point(13, 1));
        m7.put(new Point(7, 2), new Point(13, 2));
        m7.put(new Point(7, 3), new Point(13, 3));
        m7.put(new Point(7, 4), new Point(13, 4));
        m7.put(new Point(7, 5), new Point(13, 5));
        m7.put(new Point(7, 6), new Point(13, 6));
        m7.put(new Point(7, 7), new Point(13, 7));
        m7.put(new Point(7, 8), new Point(13, 8));
        m7.put(new Point(7, 9), new Point(13, 9));
        m7.put(new Point(7, 10), new Point(13, 10));
        m7.put(new Point(7, 11), new Point(13, 11));
        m7.put(new Point(7, 12), new Point(13, 12));
        m7.put(new Point(7, 13), new Point(13, 13));
        m7.put(new Point(7, 14), new Point(13, 14));
        m7.put(new Point(7, 15), new Point(13, 15));

        m7.put(new Point(8, 0), new Point(2, 0));
        m7.put(new Point(8, 1), new Point(2, 1));
        m7.put(new Point(8, 2), new Point(2, 2));
        m7.put(new Point(8, 3), new Point(2, 3));
        m7.put(new Point(8, 4), new Point(2, 4));
        m7.put(new Point(8, 5), new Point(2, 5));
        m7.put(new Point(8, 6), new Point(2, 6));
        m7.put(new Point(8, 7), new Point(2, 7));
        m7.put(new Point(8, 8), new Point(2, 8));
        m7.put(new Point(8, 9), new Point(2, 9));
        m7.put(new Point(8, 10), new Point(2, 10));
        m7.put(new Point(8, 11), new Point(2, 11));
        m7.put(new Point(8, 12), new Point(2, 12));
        m7.put(new Point(8, 13), new Point(2, 13));
        m7.put(new Point(8, 14), new Point(2, 14));
        m7.put(new Point(8, 15), new Point(2, 15));

        m7.put(new Point(9, 0), new Point(6, 0));
        m7.put(new Point(9, 1), new Point(6, 1));
        m7.put(new Point(9, 2), new Point(6, 2));
        m7.put(new Point(9, 3), new Point(6, 3));
        m7.put(new Point(9, 4), new Point(6, 4));
        m7.put(new Point(9, 5), new Point(6, 5));
        m7.put(new Point(9, 6), new Point(6, 6));
        m7.put(new Point(9, 7), new Point(6, 7));
        m7.put(new Point(9, 8), new Point(6, 8));
        m7.put(new Point(9, 9), new Point(6, 9));
        m7.put(new Point(9, 10), new Point(6, 10));
        m7.put(new Point(9, 11), new Point(6, 11));
        m7.put(new Point(9, 12), new Point(6, 12));
        m7.put(new Point(9, 13), new Point(6, 13));
        m7.put(new Point(9, 14), new Point(6, 14));
        m7.put(new Point(9, 15), new Point(6, 15));

        m7.put(new Point(10, 0), new Point(10, 0));
        m7.put(new Point(10, 1), new Point(10, 1));
        m7.put(new Point(10, 2), new Point(10, 2));
        m7.put(new Point(10, 3), new Point(10, 3));
        m7.put(new Point(10, 4), new Point(10, 4));
        m7.put(new Point(10, 5), new Point(10, 5));
        m7.put(new Point(10, 6), new Point(10, 6));
        m7.put(new Point(10, 7), new Point(10, 7));
        m7.put(new Point(10, 8), new Point(10, 8));
        m7.put(new Point(10, 9), new Point(10, 9));
        m7.put(new Point(10, 10), new Point(10, 10));
        m7.put(new Point(10, 11), new Point(10, 11));
        m7.put(new Point(10, 12), new Point(10, 12));
        m7.put(new Point(10, 13), new Point(10, 13));
        m7.put(new Point(10, 14), new Point(10, 14));
        m7.put(new Point(10, 15), new Point(10, 15));

        m7.put(new Point(11, 0), new Point(14, 0));
        m7.put(new Point(11, 1), new Point(14, 1));
        m7.put(new Point(11, 2), new Point(14, 2));
        m7.put(new Point(11, 3), new Point(14, 3));
        m7.put(new Point(11, 4), new Point(14, 4));
        m7.put(new Point(11, 5), new Point(14, 5));
        m7.put(new Point(11, 6), new Point(14, 6));
        m7.put(new Point(11, 7), new Point(14, 7));
        m7.put(new Point(11, 8), new Point(14, 8));
        m7.put(new Point(11, 9), new Point(14, 9));
        m7.put(new Point(11, 10), new Point(14, 10));
        m7.put(new Point(11, 11), new Point(14, 11));
        m7.put(new Point(11, 12), new Point(14, 12));
        m7.put(new Point(11, 13), new Point(14, 13));
        m7.put(new Point(11, 14), new Point(14, 14));
        m7.put(new Point(11, 15), new Point(14, 15));

        m7.put(new Point(12, 0), new Point(3, 0));
        m7.put(new Point(12, 1), new Point(3, 1));
        m7.put(new Point(12, 2), new Point(3, 2));
        m7.put(new Point(12, 3), new Point(3, 3));
        m7.put(new Point(12, 4), new Point(3, 4));
        m7.put(new Point(12, 5), new Point(3, 5));
        m7.put(new Point(12, 6), new Point(3, 6));
        m7.put(new Point(12, 7), new Point(3, 7));
        m7.put(new Point(12, 8), new Point(3, 8));
        m7.put(new Point(12, 9), new Point(3, 9));
        m7.put(new Point(12, 10), new Point(3, 10));
        m7.put(new Point(12, 11), new Point(3, 11));
        m7.put(new Point(12, 12), new Point(3, 12));
        m7.put(new Point(12, 13), new Point(3, 13));
        m7.put(new Point(12, 14), new Point(3, 14));
        m7.put(new Point(12, 15), new Point(3, 15));

        m7.put(new Point(13, 0), new Point(7, 0));
        m7.put(new Point(13, 1), new Point(7, 1));
        m7.put(new Point(13, 2), new Point(7, 2));
        m7.put(new Point(13, 3), new Point(7, 3));
        m7.put(new Point(13, 4), new Point(7, 4));
        m7.put(new Point(13, 5), new Point(7, 5));
        m7.put(new Point(13, 6), new Point(7, 6));
        m7.put(new Point(13, 7), new Point(7, 7));
        m7.put(new Point(13, 8), new Point(7, 8));
        m7.put(new Point(13, 9), new Point(7, 9));
        m7.put(new Point(13, 10), new Point(7, 10));
        m7.put(new Point(13, 11), new Point(7, 11));
        m7.put(new Point(13, 12), new Point(7, 12));
        m7.put(new Point(13, 13), new Point(7, 13));
        m7.put(new Point(13, 14), new Point(7, 14));
        m7.put(new Point(13, 15), new Point(7, 15));

        m7.put(new Point(14, 0), new Point(11, 0));
        m7.put(new Point(14, 1), new Point(11, 1));
        m7.put(new Point(14, 2), new Point(11, 2));
        m7.put(new Point(14, 3), new Point(11, 3));
        m7.put(new Point(14, 4), new Point(11, 4));
        m7.put(new Point(14, 5), new Point(11, 5));
        m7.put(new Point(14, 6), new Point(11, 6));
        m7.put(new Point(14, 7), new Point(11, 7));
        m7.put(new Point(14, 8), new Point(11, 8));
        m7.put(new Point(14, 9), new Point(11, 9));
        m7.put(new Point(14, 10), new Point(11, 10));
        m7.put(new Point(14, 11), new Point(11, 11));
        m7.put(new Point(14, 12), new Point(11, 12));
        m7.put(new Point(14, 13), new Point(11, 13));
        m7.put(new Point(14, 14), new Point(11, 14));
        m7.put(new Point(14, 15), new Point(11, 15));

        m7.put(new Point(15, 0), new Point(15, 0));
        m7.put(new Point(15, 1), new Point(15, 1));
        m7.put(new Point(15, 2), new Point(15, 2));
        m7.put(new Point(15, 3), new Point(15, 3));
        m7.put(new Point(15, 4), new Point(15, 4));
        m7.put(new Point(15, 5), new Point(15, 5));
        m7.put(new Point(15, 6), new Point(15, 6));
        m7.put(new Point(15, 7), new Point(15, 7));
        m7.put(new Point(15, 8), new Point(15, 8));
        m7.put(new Point(15, 9), new Point(15, 9));
        m7.put(new Point(15, 10), new Point(15, 10));
        m7.put(new Point(15, 11), new Point(15, 11));
        m7.put(new Point(15, 12), new Point(15, 12));
        m7.put(new Point(15, 13), new Point(15, 13));
        m7.put(new Point(15, 14), new Point(15, 14));
        m7.put(new Point(15, 15), new Point(15, 15));
    }

    private static final BiMap<Point, Point> m6 = HashBiMap.create();
    Decoder switchBlocksAll = new Decoder(m6.inverse(), new Dimension(16,16),new Dimension(1,1));
    private static BiMap<Point, Point> createMap(){
        BiMap<Point, Point> encodingMap = HashBiMap.create();

        return encodingMap;
    }
    static {
        m6.put(new Point(0, 0), new Point(0, 0));
        m6.put(new Point(0, 1), new Point(0, 2));
        m6.put(new Point(0, 2), new Point(0, 4));
        m6.put(new Point(0, 3), new Point(0, 6));
        m6.put(new Point(0, 4), new Point(0, 8));
        m6.put(new Point(0, 5), new Point(0, 10));
        m6.put(new Point(0, 6), new Point(0, 12));
        m6.put(new Point(0, 7), new Point(0, 14));
        m6.put(new Point(0, 8), new Point(0, 1));
        m6.put(new Point(0, 9), new Point(0, 3));
        m6.put(new Point(0, 10), new Point(0, 5));
        m6.put(new Point(0, 11), new Point(0, 7));
        m6.put(new Point(0, 12), new Point(0, 9));
        m6.put(new Point(0, 13), new Point(0, 11));
        m6.put(new Point(0, 14), new Point(0, 13));
        m6.put(new Point(0, 15), new Point(0, 15));

        m6.put(new Point(1, 0), new Point(2, 0));
        m6.put(new Point(1, 1), new Point(2, 2));
        m6.put(new Point(1, 2), new Point(2, 4));
        m6.put(new Point(1, 3), new Point(2, 6));
        m6.put(new Point(1, 4), new Point(2, 8));
        m6.put(new Point(1, 5), new Point(2, 10));
        m6.put(new Point(1, 6), new Point(2, 12));
        m6.put(new Point(1, 7), new Point(2, 14));
        m6.put(new Point(1, 8), new Point(2, 1));
        m6.put(new Point(1, 9), new Point(2, 3));
        m6.put(new Point(1, 10), new Point(2, 5));
        m6.put(new Point(1, 11), new Point(2, 7));
        m6.put(new Point(1, 12), new Point(2, 9));
        m6.put(new Point(1, 13), new Point(2, 11));
        m6.put(new Point(1, 14), new Point(2, 13));
        m6.put(new Point(1, 15), new Point(2, 15));

        m6.put(new Point(2, 0), new Point(1, 8));
        m6.put(new Point(2, 1), new Point(1, 10));
        m6.put(new Point(2, 2), new Point(1, 12));
        m6.put(new Point(2, 3), new Point(1, 14));
        m6.put(new Point(2, 4), new Point(1, 0));
        m6.put(new Point(2, 5), new Point(1, 2));
        m6.put(new Point(2, 6), new Point(1, 4));
        m6.put(new Point(2, 7), new Point(1, 6));
        m6.put(new Point(2, 8), new Point(1, 9));
        m6.put(new Point(2, 9), new Point(1, 11));
        m6.put(new Point(2, 10), new Point(1, 13));
        m6.put(new Point(2, 11), new Point(1, 15));
        m6.put(new Point(2, 12), new Point(1, 1));
        m6.put(new Point(2, 13), new Point(1, 3));
        m6.put(new Point(2, 14), new Point(1, 5));
        m6.put(new Point(2, 15), new Point(1, 7));

        m6.put(new Point(3, 0), new Point(3, 8));
        m6.put(new Point(3, 1), new Point(3, 10));
        m6.put(new Point(3, 2), new Point(3, 12));
        m6.put(new Point(3, 3), new Point(3, 14));
        m6.put(new Point(3, 4), new Point(3, 0));
        m6.put(new Point(3, 5), new Point(3, 2));
        m6.put(new Point(3, 6), new Point(3, 4));
        m6.put(new Point(3, 7), new Point(3, 6));
        m6.put(new Point(3, 8), new Point(3, 9));
        m6.put(new Point(3, 9), new Point(3, 11));
        m6.put(new Point(3, 10), new Point(3, 13));
        m6.put(new Point(3, 11), new Point(3, 15));
        m6.put(new Point(3, 12), new Point(3, 1));
        m6.put(new Point(3, 13), new Point(3, 3));
        m6.put(new Point(3, 14), new Point(3, 5));
        m6.put(new Point(3, 15), new Point(3, 7));

        m6.put(new Point(4, 0), new Point(4, 8));
        m6.put(new Point(4, 1), new Point(4, 10));
        m6.put(new Point(4, 2), new Point(4, 12));
        m6.put(new Point(4, 3), new Point(4, 14));
        m6.put(new Point(4, 4), new Point(4, 0));
        m6.put(new Point(4, 5), new Point(4, 2));
        m6.put(new Point(4, 6), new Point(4, 4));
        m6.put(new Point(4, 7), new Point(4, 6));
        m6.put(new Point(4, 8), new Point(4, 9));
        m6.put(new Point(4, 9), new Point(4, 11));
        m6.put(new Point(4, 10), new Point(4, 13));
        m6.put(new Point(4, 11), new Point(4, 15));
        m6.put(new Point(4, 12), new Point(4, 1));
        m6.put(new Point(4, 13), new Point(4, 3));
        m6.put(new Point(4, 14), new Point(4, 5));
        m6.put(new Point(4, 15), new Point(4, 7));

        m6.put(new Point(5, 0), new Point(6, 8));
        m6.put(new Point(5, 1), new Point(6, 10));
        m6.put(new Point(5, 2), new Point(6, 12));
        m6.put(new Point(5, 3), new Point(6, 14));
        m6.put(new Point(5, 4), new Point(6, 0));
        m6.put(new Point(5, 5), new Point(6, 2));
        m6.put(new Point(5, 6), new Point(6, 4));
        m6.put(new Point(5, 7), new Point(6, 6));
        m6.put(new Point(5, 8), new Point(6, 9));
        m6.put(new Point(5, 9), new Point(6, 11));
        m6.put(new Point(5, 10), new Point(6, 13));
        m6.put(new Point(5, 11), new Point(6, 15));
        m6.put(new Point(5, 12), new Point(6, 1));
        m6.put(new Point(5, 13), new Point(6, 3));
        m6.put(new Point(5, 14), new Point(6, 5));
        m6.put(new Point(5, 15), new Point(6, 7));

        m6.put(new Point(6, 0), new Point(5, 0));
        m6.put(new Point(6, 1), new Point(5, 2));
        m6.put(new Point(6, 2), new Point(5, 4));
        m6.put(new Point(6, 3), new Point(5, 6));
        m6.put(new Point(6, 4), new Point(5, 8));
        m6.put(new Point(6, 5), new Point(5, 10));
        m6.put(new Point(6, 6), new Point(5, 12));
        m6.put(new Point(6, 7), new Point(5, 14));
        m6.put(new Point(6, 8), new Point(5, 1));
        m6.put(new Point(6, 9), new Point(5, 3));
        m6.put(new Point(6, 10), new Point(5, 5));
        m6.put(new Point(6, 11), new Point(5, 7));
        m6.put(new Point(6, 12), new Point(5, 9));
        m6.put(new Point(6, 13), new Point(5, 11));
        m6.put(new Point(6, 14), new Point(5, 13));
        m6.put(new Point(6, 15), new Point(5, 15));

        m6.put(new Point(7, 0), new Point(7, 0));
        m6.put(new Point(7, 1), new Point(7, 2));
        m6.put(new Point(7, 2), new Point(7, 4));
        m6.put(new Point(7, 3), new Point(7, 6));
        m6.put(new Point(7, 4), new Point(7, 8));
        m6.put(new Point(7, 5), new Point(7, 10));
        m6.put(new Point(7, 6), new Point(7, 12));
        m6.put(new Point(7, 7), new Point(7, 14));
        m6.put(new Point(7, 8), new Point(7, 1));
        m6.put(new Point(7, 9), new Point(7, 3));
        m6.put(new Point(7, 10), new Point(7, 5));
        m6.put(new Point(7, 11), new Point(7, 7));
        m6.put(new Point(7, 12), new Point(7, 9));
        m6.put(new Point(7, 13), new Point(7, 11));
        m6.put(new Point(7, 14), new Point(7, 13));
        m6.put(new Point(7, 15), new Point(7, 15));

        m6.put(new Point(8, 0), new Point(8, 0));
        m6.put(new Point(8, 1), new Point(8, 2));
        m6.put(new Point(8, 2), new Point(8, 4));
        m6.put(new Point(8, 3), new Point(8, 6));
        m6.put(new Point(8, 4), new Point(8, 8));
        m6.put(new Point(8, 5), new Point(8, 10));
        m6.put(new Point(8, 6), new Point(8, 12));
        m6.put(new Point(8, 7), new Point(8, 14));
        m6.put(new Point(8, 8), new Point(8, 1));
        m6.put(new Point(8, 9), new Point(8, 3));
        m6.put(new Point(8, 10), new Point(8, 5));
        m6.put(new Point(8, 11), new Point(8, 7));
        m6.put(new Point(8, 12), new Point(8, 9));
        m6.put(new Point(8, 13), new Point(8, 11));
        m6.put(new Point(8, 14), new Point(8, 13));
        m6.put(new Point(8, 15), new Point(8, 15));

        m6.put(new Point(9, 0), new Point(10, 0));
        m6.put(new Point(9, 1), new Point(10, 2));
        m6.put(new Point(9, 2), new Point(10, 4));
        m6.put(new Point(9, 3), new Point(10, 6));
        m6.put(new Point(9, 4), new Point(10, 8));
        m6.put(new Point(9, 5), new Point(10, 10));
        m6.put(new Point(9, 6), new Point(10, 12));
        m6.put(new Point(9, 7), new Point(10, 14));
        m6.put(new Point(9, 8), new Point(10, 1));
        m6.put(new Point(9, 9), new Point(10, 3));
        m6.put(new Point(9, 10), new Point(10, 5));
        m6.put(new Point(9, 11), new Point(10, 7));
        m6.put(new Point(9, 12), new Point(10, 9));
        m6.put(new Point(9, 13), new Point(10, 11));
        m6.put(new Point(9, 14), new Point(10, 13));
        m6.put(new Point(9, 15), new Point(10, 15));

        m6.put(new Point(10, 0), new Point(9, 8));
        m6.put(new Point(10, 1), new Point(9, 10));
        m6.put(new Point(10, 2), new Point(9, 12));
        m6.put(new Point(10, 3), new Point(9, 14));
        m6.put(new Point(10, 4), new Point(9, 0));
        m6.put(new Point(10, 5), new Point(9, 2));
        m6.put(new Point(10, 6), new Point(9, 4));
        m6.put(new Point(10, 7), new Point(9, 6));
        m6.put(new Point(10, 8), new Point(9, 9));
        m6.put(new Point(10, 9), new Point(9, 11));
        m6.put(new Point(10, 10), new Point(9, 13));
        m6.put(new Point(10, 11), new Point(9, 15));
        m6.put(new Point(10, 12), new Point(9, 1));
        m6.put(new Point(10, 13), new Point(9, 3));
        m6.put(new Point(10, 14), new Point(9, 5));
        m6.put(new Point(10, 15), new Point(9, 7));

        m6.put(new Point(11, 0), new Point(11, 8));
        m6.put(new Point(11, 1), new Point(11, 10));
        m6.put(new Point(11, 2), new Point(11, 12));
        m6.put(new Point(11, 3), new Point(11, 14));
        m6.put(new Point(11, 4), new Point(11, 0));
        m6.put(new Point(11, 5), new Point(11, 2));
        m6.put(new Point(11, 6), new Point(11, 4));
        m6.put(new Point(11, 7), new Point(11, 6));
        m6.put(new Point(11, 8), new Point(11, 9));
        m6.put(new Point(11, 9), new Point(11, 11));
        m6.put(new Point(11, 10), new Point(11, 13));
        m6.put(new Point(11, 11), new Point(11, 15));
        m6.put(new Point(11, 12), new Point(11, 1));
        m6.put(new Point(11, 13), new Point(11, 3));
        m6.put(new Point(11, 14), new Point(11, 5));
        m6.put(new Point(11, 15), new Point(11, 7));

        m6.put(new Point(12, 0), new Point(12, 8));
        m6.put(new Point(12, 1), new Point(12, 10));
        m6.put(new Point(12, 2), new Point(12, 12));
        m6.put(new Point(12, 3), new Point(12, 14));
        m6.put(new Point(12, 4), new Point(12, 0));
        m6.put(new Point(12, 5), new Point(12, 2));
        m6.put(new Point(12, 6), new Point(12, 4));
        m6.put(new Point(12, 7), new Point(12, 6));
        m6.put(new Point(12, 8), new Point(12, 9));
        m6.put(new Point(12, 9), new Point(12, 11));
        m6.put(new Point(12, 10), new Point(12, 13));
        m6.put(new Point(12, 11), new Point(12, 15));
        m6.put(new Point(12, 12), new Point(12, 1));
        m6.put(new Point(12, 13), new Point(12, 3));
        m6.put(new Point(12, 14), new Point(12, 5));
        m6.put(new Point(12, 15), new Point(12, 7));

        m6.put(new Point(13, 0), new Point(14, 8));
        m6.put(new Point(13, 1), new Point(14, 10));
        m6.put(new Point(13, 2), new Point(14, 12));
        m6.put(new Point(13, 3), new Point(14, 14));
        m6.put(new Point(13, 4), new Point(14, 0));
        m6.put(new Point(13, 5), new Point(14, 2));
        m6.put(new Point(13, 6), new Point(14, 4));
        m6.put(new Point(13, 7), new Point(14, 6));
        m6.put(new Point(13, 8), new Point(14, 9));
        m6.put(new Point(13, 9), new Point(14, 11));
        m6.put(new Point(13, 10), new Point(14, 13));
        m6.put(new Point(13, 11), new Point(14, 15));
        m6.put(new Point(13, 12), new Point(14, 1));
        m6.put(new Point(13, 13), new Point(14, 3));
        m6.put(new Point(13, 14), new Point(14, 5));
        m6.put(new Point(13, 15), new Point(14, 7));

        m6.put(new Point(14, 0), new Point(13, 0));
        m6.put(new Point(14, 1), new Point(13, 2));
        m6.put(new Point(14, 2), new Point(13, 4));
        m6.put(new Point(14, 3), new Point(13, 6));
        m6.put(new Point(14, 4), new Point(13, 8));
        m6.put(new Point(14, 5), new Point(13, 10));
        m6.put(new Point(14, 6), new Point(13, 12));
        m6.put(new Point(14, 7), new Point(13, 14));
        m6.put(new Point(14, 8), new Point(13, 1));
        m6.put(new Point(14, 9), new Point(13, 3));
        m6.put(new Point(14, 10), new Point(13, 5));
        m6.put(new Point(14, 11), new Point(13, 7));
        m6.put(new Point(14, 12), new Point(13, 9));
        m6.put(new Point(14, 13), new Point(13, 11));
        m6.put(new Point(14, 14), new Point(13, 13));
        m6.put(new Point(14, 15), new Point(13, 15));

        m6.put(new Point(15, 0), new Point(15, 0));
        m6.put(new Point(15, 1), new Point(15, 2));
        m6.put(new Point(15, 2), new Point(15, 4));
        m6.put(new Point(15, 3), new Point(15, 6));
        m6.put(new Point(15, 4), new Point(15, 8));
        m6.put(new Point(15, 5), new Point(15, 10));
        m6.put(new Point(15, 6), new Point(15, 12));
        m6.put(new Point(15, 7), new Point(15, 14));
        m6.put(new Point(15, 8), new Point(15, 1));
        m6.put(new Point(15, 9), new Point(15, 3));
        m6.put(new Point(15, 10), new Point(15, 5));
        m6.put(new Point(15, 11), new Point(15, 7));
        m6.put(new Point(15, 12), new Point(15, 9));
        m6.put(new Point(15, 13), new Point(15, 11));
        m6.put(new Point(15, 14), new Point(15, 13));
        m6.put(new Point(15, 15), new Point(15, 15));
    }

    private BiMap<Point, Point> asBimap(Point[][] arrayMap) {
        BiMap<Point, Point> result = HashBiMap.create();
        IntStream.range(0, arrayMap.length).forEach(rowNr -> {
            IntStream.range(0, arrayMap[0].length).forEach(colNr ->
                    result.put(arrayMap[rowNr][colNr], new Point(rowNr, colNr)));
        });
        return result;
    }

    private void generateCode(Point[][] mask, int rowLength, int colLength){
        for(int rowNr = 0; rowNr < rowLength; rowNr++){
            for (int colNr = 0; colNr < colLength; colNr ++){
                Point end = mask[rowNr][colNr];
                System.out.println("m6.put(new Point(" + end.x + ", " + end.y + "), new Point(" + rowNr + ", " + colNr + "));");
            }
            System.out.println();

        }
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

    Point decode(final Decoder decoder, final Point point){
        // offset relative to the full picture
        final Point outerBlockBase = new Point(point.x - point.x % decoder.outerBlock.height, point.y - point.y % decoder.outerBlock.width);

        // offset relative to outerBlockBase (x and y are counted in innerBlock lengths)
        final Point innerBlockOffset = new Point((point.x - outerBlockBase.x) / decoder.innerBlock.height, (point.y - outerBlockBase.y) / decoder.innerBlock.width);

        // point offset relative to InnerBlock
        final Point pointOffset = new Point(point.x % decoder.innerBlock.height, point.y % decoder.innerBlock.width);

        final Point decodedInnerBlockOffset = decoder.map.getOrDefault(innerBlockOffset, innerBlockOffset);

        Point result = new Point(outerBlockBase.x + decoder.innerBlock.height * decodedInnerBlockOffset.x + pointOffset.x, outerBlockBase.y + decoder.innerBlock.width * decodedInnerBlockOffset.y + pointOffset.y);
        return result;
    }

    Point decodeVerticalUnrepeating(final Dimension blockDimensions, final List<Point> rowOrder, final Point point){
        int rowOffset = point.x % rowOrder.size();
        int baseOffset = point.x % blockDimensions.height;
        int blockBase = point.x - baseOffset;
        int compartmentSize = blockDimensions.height / rowOrder.size();

        int decodedRowOffset = rowOrder.indexOf(new Point(rowOffset, 0));
        int compartmentBase = compartmentSize * decodedRowOffset;
        int compartmentOffset = baseOffset / rowOrder.size();
        Point result = new Point(blockBase + compartmentBase + compartmentOffset, point.y);
        return result;
    }

    Point decodeHorizontalUnrepeating(final Dimension blockDimensions, final List<Point> columnOrder, final Point point){
        int columnOffset = point.y % columnOrder.size();
        int baseOffset = point.y % blockDimensions.width;
        int blockBase = point.y - baseOffset;
        int compartmentSize = blockDimensions.width / columnOrder.size();

        int decodedColumnOffset = columnOrder.indexOf(new Point(0, columnOffset));
        int compartmentBase = compartmentSize * decodedColumnOffset;
        int compartmentOffset = baseOffset / columnOrder.size();
        Point result = new Point(point.x, blockBase + compartmentBase + compartmentOffset);
        return result;
    }



    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> image) {
        return null;
    }



}
