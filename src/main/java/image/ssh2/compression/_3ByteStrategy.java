package image.ssh2.compression;

import java.nio.ByteBuffer;

/**
 * Command format: 10RRRRRR PPDDDDDD DDDDDDDD
 *  -> copy PP bytes from in to out.
 *  -> copy (DDDDDD DDDDDDDD + 4) bytes from out (offset RRRRRR+1) to out
 */
public class _3ByteStrategy implements RepackExecutionStrategy{
    @Override
    public void executeInstruction(ByteBuffer in, ByteBuffer out) {
        final byte _byte1 = in.get();
        final byte _byte2 = in.get();
        final byte _byte3 = in.get();
        final int p = Byte.toUnsignedInt((byte) (_byte2 & 0xC0)) >> 6;
        final int r = Byte.toUnsignedInt((byte) (_byte1 & 0x3F));
        final int d = (Byte.toUnsignedInt((byte) (_byte2 & 0x3F)) << 8) + Byte.toUnsignedInt(_byte3);
        final int proceedingLength = p;
        final int copyLength = r + 4;
        final int offsetCopy = d + 1;

        final byte[] proceedingBytes = new byte[proceedingLength];
        in.get(proceedingBytes);
        out.put(proceedingBytes);

        copySelf(out, offsetCopy, copyLength);
    }
}
