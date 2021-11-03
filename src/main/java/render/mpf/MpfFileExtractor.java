package render.mpf;

import image.UnknownComponent;
import image.ssh2.compression.CompressedFile;
import render.mpf.globalheader.MpfFileLocationTag;
import render.mpf.globalheader.MpfGlobalHeader;
import util.FileUtil;

import java.nio.ByteBuffer;

/**
 * Model MPF:
 * 12 bytes - global header
 * 	0-3  : "0D 00 00 00"
 * 	4-5  : amount of subheaders that follow
 * 	6-7  : "0C 00" = global header size
 * 	8-11 : LE, location of DATA-START - often start of compression
 *
 * 96 bytes each - subheader
 * 	0-15 : subheader name (multiple resolutions? H/M/L and shadow)
 * 	16-17: might be some type of address, seems to go up
 * 	18-21: ??? (for the first subheader it contains the full data-size)
 * 	22-25: LE - address of "rotation-info" (offset DATA-START/seeing DATA-START as adress 0x00)
 * 	26-29: LE -  --- address of ??? ; end of rotation-info (offset DATA-START)
 * 	...
 * 	36-37: LE - seems a bit before the mech, maybe header of mech or later offset? (offset DATA-START)
 * 	..
 * 	74-75: #rotation-info-objects
 * 	76-77: #body-location-refs
 *
 * 32 bytes each - body-location-refs (?) Looks like some commands changing pointers on "head", "boot", "suit", "ext.."
 *
 * 80 bytes each - rotation-info (?)
 * 	0-15 : rotation name
 *
 *
 * zoe_Wings
 * 00 00 Global header: (data start=01 90; #renders=4)
 * -- sub-headers --
 * 00 0C "Wings_H" (00 00), total img size=3650, rotation-info=0180, 02C0, 044C, 0470
 * 00 6C "Wings_M"
 * 00 CC "Wings_L"
 * 01 2C "Wings_Shdw"
 * -- 0-buffer to start of data --
 * -- pointer-ops? -- Size:180
 * 01 90
 * 	some non-empty operations:
 * 		exte�   �   �xegenv0�ÿþ>
 * 		extd�   �   �xdgenv0�ÿþ>
 * 		alph�   �   �h_genv0ÎÌL>
 * 		extc�   �   �xcgenv0ÂÀ@=
 * -- rotation-info -- Size:140
 * 03 10 "sec_wings_l1"
 * 03 60 "sec_wings_l1"
 * 03 B0 "sec_wings_l1"
 * 04 00 "sec_wings_l1"
 *
 * -- ???? -- header before mech? (xE size before mesh)
 * 04 50
 *
 * 05 DB -> next part if logic holds up
 *
 * After each blob: 01 00 10 .. $18
 *
 *
 * 1860 - 1B18 (56/3A --- s=2A0)
 *
 * Buffer: 0000 0001 ...
 *
 * 2B41000
 */
public class MpfFileExtractor {

    //eliwe_TopB.mpf - 8c8, 1000

    private final MpfGlobalHeader mpfGlobalHeader;

    private final UnknownComponent after;
    private final ByteBuffer decompressedMesh;

    public MpfFileExtractor(final ByteBuffer buffer){
        this.mpfGlobalHeader = new MpfGlobalHeader(buffer);
        final int afterSize = (int) (getCompressedMeshLocation() - mpfGlobalHeader.getEndPos());
        this.after = new UnknownComponent(buffer, afterSize);
        decompressedMesh = copyRawImageDataToBufferAndSkip(buffer);
    }

    private int getCompressedMeshLocation(){
        return mpfGlobalHeader.getCompressedMeshLocation();
    }

    public ByteBuffer getMergedBuffer(){
        int totalSize = getCompressedMeshLocation() + decompressedMesh.remaining();
        return ByteBuffer.allocate(totalSize)
                .put(mpfGlobalHeader.getBytes())
                .put(after.getBytes())
                .put(decompressedMesh);
    }

    private ByteBuffer copyRawImageDataToBufferAndSkip(final ByteBuffer buffer){
        ByteBuffer compressedImageBuffer = FileUtil.slice(buffer, buffer.position(), buffer.remaining());
        final int compressedSize = compressedImageBuffer.remaining();
        ByteBuffer decompressedImageBuffer = new CompressedFile(compressedImageBuffer).decompress();
        buffer.position(buffer.position() + compressedSize);
        return decompressedImageBuffer;
    }
}
