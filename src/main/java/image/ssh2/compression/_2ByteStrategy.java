package image.ssh2.compression;

import java.nio.ByteBuffer;

/**
 * Command format: 0DDRRRPP DDDDDDDD
 *  -> copy PP bytes from in to out.
 *  -> copy (DD DDDD.DDDD + 3) bytes from out (offset DD+1) to out
 */
public class _2ByteStrategy implements RepackExecutionStrategy{
    @Override
    public void executeInstruction(ByteBuffer in, ByteBuffer out) {
        final byte _byte1 = in.get();
        final byte _byte2 = in.get();
        final int p = Byte.toUnsignedInt((byte) (_byte1 & 0x03));
        final int r = Byte.toUnsignedInt((byte) (_byte1 & 0x1C)) >> 2;
        final int d = (Byte.toUnsignedInt((byte) (_byte1 & 0x60)) << 3) + Byte.toUnsignedInt(_byte2);
        final int proceedingLength = p;
        final int copyLength = r + 3;
        final int offsetCopy = d + 1;

        final byte[] proceedingBytes = new byte[proceedingLength];
        in.get(proceedingBytes);
        out.put(proceedingBytes);

        copySelf(out, offsetCopy, copyLength);
    }
}
