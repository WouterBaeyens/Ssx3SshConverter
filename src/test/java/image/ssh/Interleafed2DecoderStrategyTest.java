package image.ssh;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mycompany.sshtobpmconverter.IPixel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Interleafed2DecoderStrategyTest {

    Interleafed2DecoderStrategy interleafed2DecoderStrategy;

    @BeforeEach
    void setup() {
        interleafed2DecoderStrategy = new Interleafed2DecoderStrategy();
    }

    @Test
    void shouldCreateDefaultMask() {
        // Given
        Point[][] expectedDecodingMask = {
                {new Point(0, 0), new Point(0, 1), new Point(0, 2),},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2),}
        };
        // When
        Point[][] result = interleafed2DecoderStrategy.createDefaultmask(new Dimension(3, 2));

        // Then
        IntStream.range(0, expectedDecodingMask.length)
                .forEach(rowNr -> Assertions.assertArrayEquals(expectedDecodingMask[rowNr], result[rowNr], "At row index [" + rowNr + "]"));
    }

    @Test
    void shouldDecodeImage() {
        // Given
        Point[][] decodingMask = {
                {new Point(0, 2), new Point(0, 0), new Point(0, 1), new Point(0, 3)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3),}
        };

        IPixel[][] encodedImg = {
                {new TPixel("0"), new TPixel("1"), new TPixel("2"), new TPixel("3")},
                {new TPixel("4"), new TPixel("5"), new TPixel("6"), new TPixel("7")}
        };

        IPixel[][] expectedDecodedImg = {
                {new TPixel("2"), new TPixel("0"), new TPixel("1"), new TPixel("3")},
                {new TPixel("4"), new TPixel("5"), new TPixel("6"), new TPixel("7")}
        };

        // When
        var result = asArray(interleafed2DecoderStrategy.decodeImage(asList(encodedImg), decodingMask));

        // Then
        IntStream.range(0, expectedDecodedImg.length)
                .forEach(rowNr -> Assertions.assertArrayEquals(expectedDecodedImg[rowNr], result[rowNr], "At row index [" + rowNr + "]"));
    }

    @Test
    void shouldManipulateMask(){
        Point[][] defaultmask = interleafed2DecoderStrategy.createDefaultmask(new Dimension(8, 4));
        Point[][] expectedMask = {
                {new Point(0, 2), new Point(0, 0), new Point(0, 1), new Point(1, 3), new Point(0, 6), new Point(0, 4), new Point(0, 5), new Point(1, 7)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 3), new Point(1, 4), new Point(1, 5), new Point(1, 6), new Point(0, 7),},
                {new Point(2, 2), new Point(2, 0), new Point(2, 1), new Point(3, 3), new Point(2, 6), new Point(2, 4), new Point(2, 5), new Point(3, 7)},
                {new Point(3, 0), new Point(3, 1), new Point(3, 2), new Point(2, 3), new Point(3, 4), new Point(3, 5), new Point(3, 6), new Point(2, 7),},
        };
        Point[][] decoderMask = {
                {new Point(0, 2), new Point(0, 0), new Point(0, 1), new Point(1, 3)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 3),}
        };
        var bimap = asBimap(decoderMask);
        var outerBlock = new Dimension(4,2);
        var innerBlock = new Dimension(1,1);
        var decoder = new Interleafed2DecoderStrategy.Decoder(bimap, outerBlock, innerBlock);

        var actualMask = interleafed2DecoderStrategy.manipulateMask(defaultmask, point -> interleafed2DecoderStrategy.decode(decoder, point));

        IntStream.range(0, expectedMask.length)
                .forEach(rowNr -> Assertions.assertArrayEquals(expectedMask[rowNr], actualMask[rowNr], "At row index [" + rowNr + "]"));
    }

    @Test
    void shouldManipulateMaskRows(){
        final Dimension imgDimension = new Dimension(3, 6);
        Point[][] defaultmask = interleafed2DecoderStrategy.createDefaultmask(imgDimension);
        Point[][] expectedMask = {
                {new Point(1, 0), new Point(1, 1), new Point(1, 2)},
                {new Point(4, 0), new Point(4, 1), new Point(4, 2)},
                {new Point(2, 0), new Point(2, 1), new Point(2, 2)},
                {new Point(5, 0), new Point(5, 1), new Point(5, 2)},
                {new Point(0, 0), new Point(0, 1), new Point(0, 2)},
                {new Point(3, 0), new Point(3, 1), new Point(3, 2)},
        };
        Point[] rowOrder = {new Point(1, 0), new Point(2, 0), new Point(0, 0)};

        var actualMask = interleafed2DecoderStrategy.manipulateMask(defaultmask, point -> interleafed2DecoderStrategy.decodeVerticalUnrepeating(imgDimension, List.of(rowOrder), point));

        IntStream.range(0, expectedMask.length)
                .forEach(rowNr -> Assertions.assertArrayEquals(expectedMask[rowNr], actualMask[rowNr], "At row index [" + rowNr + "]"));
    }


    @Test
    void shouldManipulateMaskColumns(){
        final Dimension imgDimension = new Dimension(12, 2);
        Point[][] defaultmask = interleafed2DecoderStrategy.createDefaultmask(imgDimension);
        Point[][] expectedMask = {
                {new Point(0, 0), new Point(0, 3), new Point(0, 1), new Point(0, 4), new Point(0, 2), new Point(0, 5),new Point(0, 6), new Point(0, 9), new Point(0, 7),new Point(0, 10), new Point(0, 8), new Point(0, 11)},
                {new Point(1, 0), new Point(1, 3), new Point(1, 1), new Point(1, 4), new Point(1, 2), new Point(1, 5),new Point(1, 6), new Point(1, 9), new Point(1, 7),new Point(1, 10), new Point(1, 8), new Point(1, 11)},
        };
        Point[] rowOrder = {new Point(0, 0), new Point(0, 1), new Point(0, 2)};

        var actualMask = interleafed2DecoderStrategy.manipulateMask(defaultmask, point -> interleafed2DecoderStrategy.decodeHorizontalUnrepeating(new Dimension(6, 1), List.of(rowOrder), point));

        IntStream.range(0, expectedMask.length)
                .forEach(rowNr -> Assertions.assertArrayEquals(expectedMask[rowNr], actualMask[rowNr], "At row index [" + rowNr + "]"));
    }

    private BiMap<Point, Point> asBimap(Point[][] arrayMap) {
        BiMap<Point, Point> result = HashBiMap.create();
        IntStream.range(0, arrayMap.length).forEach(rowNr -> {
            IntStream.range(0, arrayMap[0].length).forEach(colNr ->
                    result.put(arrayMap[rowNr][colNr], new Point(rowNr, colNr)));
        });
        return result;
    }

    private <A> List<List<A>> asList(A[][] array) {
        return Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList());
    }

    private IPixel[][] asArray(List<List<IPixel>> array) {
        return array.stream().map(u -> u.toArray(new IPixel[0])).toArray(IPixel[][]::new);
    }

    class TPixel implements IPixel {

        final String pixelName;

        TPixel(final String name) {
            this.pixelName = name;
        }

        @Override
        public byte[] getRGBValue() {
            return null;
        }

        @Override
        public byte[] getRGBValueLE() {
            return null;
        }

        @Override
        public String toString() {
            return pixelName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TPixel TPixel = (TPixel) o;
            return pixelName.equals(TPixel.pixelName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pixelName);
        }
    }
}