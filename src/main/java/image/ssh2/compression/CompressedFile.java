package image.ssh2.compression;

import image.bmp2.bmpheader.FileSizeTag;
import image.bmp2.dibheader.tags.CompressionTypeTag;
import image.ssh2.fileheader.TotalFileSizeTag;
import image.ssh2.imageheader.ImageTypeTag;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

public class CompressedFile {

    final ByteBuffer compressedFileBuffer;
    final CompressionType compressionType;
    final DecompressedFileSizeTag decompressedFileSizeTag;

    public CompressedFile(final ByteBuffer compressedFileBuffer){
        this.compressedFileBuffer = compressedFileBuffer;
        this.compressionType = CompressionType.readImageType(compressedFileBuffer);
        if(compressionType == CompressionType.REFPACK){
            this.decompressedFileSizeTag = new DecompressedFileSizeTag(compressedFileBuffer);
        } else {
            this.decompressedFileSizeTag = null;
        }
    }

    public ByteBuffer decompress(){
        if(compressionType == CompressionType.REFPACK){
            ByteBuffer decompressedFb = ByteBuffer.wrap(new byte[Math.toIntExact(decompressedFileSizeTag.getConvertedValue())]);
            decompress(compressedFileBuffer, decompressedFb);
            return decompressedFb.flip();
        } else {
            return compressedFileBuffer;
        }
    }

    private void decompress(ByteBuffer in, ByteBuffer out){
        while (out.hasRemaining()){
            RepackInstructionType.checkNextInstructionType(in).executeInstruction(in, out);
        }
    }

    public enum CompressionType{

        /**
         * RefPack is an LZ77/LZSS compression format made by Frank Barchard of EA Canada for the Gimex library used by many older games by EA.
         * See: https://simstek.fandom.com/wiki/RefPack
         */
        REFPACK("10FB"),
        NONE("XX");

        final String identifierTag;

        CompressionType(final String identifierTag){
            this.identifierTag = identifierTag;
        }

        public static CompressionType readImageType(final ByteBuffer compressedFileBuffer) {
            Optional<CompressionType> type = Arrays.stream(values())
                    .filter(compressionType -> fileIsOfCompressionType(compressedFileBuffer, compressionType))
                    .findAny();

            type.ifPresent(compressionType -> compressedFileBuffer.position(compressedFileBuffer.position() + compressionType.identifierTag.length()/2));

            return type.orElse(NONE);
        }

        private static boolean fileIsOfCompressionType(ByteBuffer fileByteBuffer, CompressedFile.CompressionType compressionType){
            byte[] startOfFile = new byte[compressionType.identifierTag.length()/2];
            fileByteBuffer.get(0, startOfFile);
            return compressionType.identifierTag.equals(ByteUtil.bytesToHex(startOfFile));
        }
    }
}
