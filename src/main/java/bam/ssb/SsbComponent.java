package bam.ssb;

import image.ssh2.compression.CompressedFile;
import util.FileUtil;

import java.nio.ByteBuffer;

public class SsbComponent {
    final SsbComponentHeader ssbComponentHeader;
    final ByteBuffer decompressedSubfile;

    public SsbComponent(final ByteBuffer buffer){
        this.ssbComponentHeader = new SsbComponentHeader(buffer);
        this.decompressedSubfile = copyRawDataToBufferAndSkip(buffer);

    }

    public ByteBuffer getDecompressedSubfile(){
        return decompressedSubfile;
    }

    public boolean isLastComponentOfFile(){
        return ssbComponentHeader.isLastComponentOfFile();
    }

    /**
     * This does not include the header as these headers are not relevant for the full file.
     */
    public int getSize(){
        return decompressedSubfile.position() + decompressedSubfile.remaining();
    }

    private ByteBuffer copyRawDataToBufferAndSkip(final ByteBuffer buffer){
        int compressedSize = ssbComponentHeader.getSubFileSize() - ssbComponentHeader.getSize();
        ByteBuffer compressedImageBuffer = FileUtil.slice(buffer, buffer.position(), compressedSize);
        ByteBuffer decompressedImageBuffer = new CompressedFile(compressedImageBuffer).decompress();
        buffer.position(buffer.position() + compressedSize);
        return decompressedImageBuffer;
    }

    @Override
    public String toString() {
        return "SsbSubFile{" +
                "ssbHeader=" + ssbComponentHeader +
                ", decompressedSubfile=" + decompressedSubfile +
                '}';
    }
}
