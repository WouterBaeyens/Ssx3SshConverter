package image.ssh2.compression;

import java.nio.ByteBuffer;

public interface RepackExecutionStrategy {

    void executeInstruction(final ByteBuffer in, final ByteBuffer out);

    default void copySelf(final ByteBuffer buffer, final int offset, final int length){
        if(offset > length) {
            final byte[] clipboard = new byte[length];
            buffer.get(buffer.position() - offset, clipboard);
            buffer.put(clipboard);
        } else {
            final int maxChunkSize = offset;
            for(int bytesCopied = 0; bytesCopied < length;){
                final int bytesToCopy = Math.min(length - bytesCopied, maxChunkSize);
                final byte[] clipboard = new byte[bytesToCopy];
                buffer.get(buffer.position() - offset, clipboard);
                buffer.put(clipboard);
                bytesCopied += bytesToCopy;
            }
        }
    }
}
