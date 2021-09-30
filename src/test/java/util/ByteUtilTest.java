package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ByteUtilTest {

    @ParameterizedTest
    @CsvSource({"0x08, 0x10", "0x10, 0x08", "0x11, 0x09", "0x18, 0x18"})
    void shouldSwapBit(byte input, byte expectedOutput) {
        // Given - When
        final byte actualResult = ByteUtil.switchBit4And5(input);

        // Then
        Assertions.assertEquals(expectedOutput, actualResult);
    }

    @ParameterizedTest
    @CsvSource({"0x28, 0x02", "0x12, 0x01", "0x2a, 0x02", "0x78, 0x07"})
    void shouldGetLeftNibble(byte input, byte expectedOutput) {
        // Given - When
        final byte actualResult = ByteUtil.getLeftNibble(input);

        // Then
        Assertions.assertEquals(expectedOutput, actualResult);
    }

    @ParameterizedTest
    @CsvSource({"0x28, 0x08", "0x12, 0x02", "0x2a, 0x0a", "0x78, 0x08"})
    void shouldGetRightNibble(byte input, byte expectedOutput) {
        // Given - When
        final byte actualResult = ByteUtil.getRightNibble(input);

        // Then
        Assertions.assertEquals(expectedOutput, actualResult);
    }
}