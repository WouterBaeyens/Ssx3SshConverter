package render.mpf.globalheader.subheader;

import image.UnknownComponent;
import render.mpf.globalheader.MpfNameTag;
import util.ByteUtil;

import java.nio.ByteBuffer;

public class MpfSubHeader {

    private final MpfNameTag mpfNameTag;

    // seems to have something to do with location
    private final UnknownComponent u_10;
    private final UnknownComponent u_12;

    public MpfSubHeader(ByteBuffer buffer){
        this.mpfNameTag = new MpfNameTag(buffer);
        this.u_10 = new UnknownComponent(buffer, 2);
        this.u_12 = new UnknownComponent(buffer, 4);
    }
}
