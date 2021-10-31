package image.ssh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class InterleafedBitwiseDecoderStrategyTest {

    InterleafedBitwiseDecoderStrategy bitDecoderStrategy;

    @BeforeEach
    void setup() {
        bitDecoderStrategy = new InterleafedBitwiseDecoderStrategy();
    }

    @ParameterizedTest(name = "[{0},{1}] -> [{2},{3}]")
    @CsvSource({"0,0, 0,0", "0,1, 0,8", "0,7, 0,56", "0,8, 0,2", "0,24, 0,6", "0,32, 32, 0", "0,120, 96,6",
                "1,0, 4,0", "1,7, 4,56", "1,8, 4,2", "1,32, 36,0", "1,120, 100,6",
                "2,0, 0,33", "2,1, 0,41", "2,2, 0,49", "2,3, 0,57", "2,4, 0,1", "2,5, 0,9", "2,12, 0,3", "2,20, 0,5", "2,32, 32,33",
                "3,0, 4,33",
                "4,0, 8,32",
                "5,0, 12,32",
                "6,0, 8,1",
                "7,0, 12,1",
                "8,0, 16, 0",
                "16,0, 0,64",
                "32,0, 1,0",
                "64,0, 2,0",
                "96,0, 3,0"})
    void shouldTranslateLowRezPoint(int origX, int origY, int newX, int newY){
        var orig = new Point(origX, origY);
        var expected = new Point(newX, newY);

        var result = bitDecoderStrategy.decodeLowRezPixelLocation(orig);

        var prefix = String.format("[%d,%d] -> [%d,%d] || ", origX, origY, newX,newY);
        var info = asBin(orig) + " -> " + asBin(expected);
        var info2 = "in" + asBin(orig) + ";exOut" + asBin(expected) + ";acOut"+ asBin(result);
        System.out.println(prefix + info);
        assertEquals(expected, result, info2);
    }

    @ParameterizedTest(name = "[{0},{1}] -> [{2},{3}]")
    @CsvSource({"0,0, 0,0", "0,1, 0,4", "0,7, 0,28", "0,8, 0,2", "0,24, 0,34", "0,32, 0,64", "0,64, 1,0", "0,120, 1,98",
            "1,0, 2,0", "1,7, 2,28", "1,8, 2,2", "1,32, 2,64", "1,120, 3,98",
            "2,0, 0,17", "2,1, 0,21", "2,2, 0,25", "2,3, 0,29", "2,4, 0,1", "2,5, 0,5", "2,6, 0,9", "2,12, 0,3", "2,20, 0,33", "2,32, 0,81",
            "3,0, 2,17",
            "4,0, 4,16",
            "5,0, 6,16",
            "96,0, 96,0"})
    void shouldTranslatePoint(int origX, int origY, int newX, int newY){
        var orig = new Point(origX, origY);
        var expected = new Point(newX, newY);
        var imageDimensions = new Dimension(128, 128);
        var blockDimensions = new Dimension(16,16);
        var result = bitDecoderStrategy.decodePixelLocation(imageDimensions, blockDimensions, orig);

        var prefix = String.format("[%d,%d] -> [%d,%d] || ", origX, origY, newX,newY);
        var info = asBin(orig) + " -> " + asBin(expected);
        var info2 = "in" + asBin(orig) + ";exOut" + asBin(expected) + ";acOut"+ asBin(result);
        System.out.println(prefix + info);
        assertEquals(expected, result, info2);
    }

    private String asBin(Point point){
        return "[" + String.format("%7s",Integer.toBinaryString(point.x)).replace(' ', '0') + " " + String.format("%7s",Integer.toBinaryString(point.y)).replace(' ', '0') + "]";
    }

}