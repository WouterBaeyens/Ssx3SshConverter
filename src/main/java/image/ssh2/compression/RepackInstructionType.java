package image.ssh2.compression;

import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

public enum RepackInstructionType {
    _1_BYTE_COMMAND(new _1ByteStrategy()), // 1-byte command: 111PPPPP - E(0-3) = copy 4/8/12/16
    _2_BYTE_COMMAND(new _2ByteStrategy()), // 2-byte command: 0DDRRRPP DDDDDDDD
    _3_BYTE_COMMAND(new _3ByteStrategy()), // 3-byte command: 10RRRRRR PPDDDDDD DDDDDDDD
    _4_BYTE_COMMAND(new _4ByteStrategy()); // 4-byte command: 110DRRPP DDDDDDDD DDDDDDDD RRRRRRRR

    private RepackExecutionStrategy repackExecutionStrategy;

    RepackInstructionType(final RepackExecutionStrategy repackExecutionStrategy){
        this.repackExecutionStrategy = repackExecutionStrategy;
    }

    public void executeInstruction(final ByteBuffer in, final ByteBuffer out){
        repackExecutionStrategy.executeInstruction(in, out);
    }

    public static RepackInstructionType checkNextInstructionType(final ByteBuffer compressedFileBuffer) {
        byte b = compressedFileBuffer.get(compressedFileBuffer.position());
        if(ByteUtil.getBitFromByte(b, 8) == 0) {
            return _2_BYTE_COMMAND;
        } else if(ByteUtil.getBitFromByte(b, 7) == 0) {
            return _3_BYTE_COMMAND;
        }else if(ByteUtil.getBitFromByte(b, 6) == 0) {
            return _4_BYTE_COMMAND;
        } else {
            return _1_BYTE_COMMAND;
        }
    }
}
