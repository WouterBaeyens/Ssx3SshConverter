package image.ssh2.compression;

import util.ByteUtil;

import java.nio.ByteBuffer;

/**
 * Command format: 111PPPPP
 *  -> copy ((PPPPP + 1) * 4) bytes from in to out.
 */
public class _1ByteStrategy implements RepackExecutionStrategy{

    @Override
    public void executeInstruction(ByteBuffer in, ByteBuffer out) {
        final byte _byte1 = in.get();
        final int p = Byte.toUnsignedInt((byte) (_byte1 & 0x1F));
        int proceedingLength = (p+1) * 4;
        if(proceedingLength > 112){
            // this means the code is actually a stop-code, should be the last instruction
            proceedingLength = Byte.toUnsignedInt((byte) (_byte1 & 0x03));
        }
        final byte[] proceedingBytes = new byte[proceedingLength];
        in.get(proceedingBytes);
        out.put(proceedingBytes);
    }
}
