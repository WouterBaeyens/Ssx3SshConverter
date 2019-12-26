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
}