package render.mpf.globalheader;

import image.ImgSubComponent;
import image.UnknownComponent;
import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.List;

public class MpfGlobalHeader {

    private final UnknownComponent u_0;
    private final MpfAmountOfSubFiles mpfAmountOfSubFiles;
    private final UnknownComponent u_6;
    private final MpfFileLocationTag mpfFileLocationTag;
    private final MpfNameTag mpfNameTag;

    private final List<ImgSubComponent> components;

    public MpfGlobalHeader(ByteBuffer buffer){
        this.u_0 = new UnknownComponent(buffer, 4, "0D 00 00 ");
        this.mpfAmountOfSubFiles = new MpfAmountOfSubFiles(buffer);
        this.u_6 = new UnknownComponent(buffer, 2, "0C 00 ");
        this.mpfFileLocationTag = new MpfFileLocationTag(buffer);
        this.mpfNameTag = new MpfNameTag(buffer);

        components = List.of(u_0, mpfAmountOfSubFiles, u_6, mpfFileLocationTag, mpfNameTag);
    }

    public int getCompressedMeshLocation(){
        return Math.toIntExact(mpfFileLocationTag.getConvertedValue());
    }

    public long getEndPos(){
        return mpfNameTag.getEndPos();
    }

    public byte[] getBytes(){
        return ByteUtil.combineByteArrays(components.stream().map(ImgSubComponent::getRawBytes).toArray(byte[][]::new));
    }
}
