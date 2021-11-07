package image;

import image.ssh2.fileheader.NumberOfEntriesTag;

import java.nio.ByteBuffer;

public class AmountTag extends AbstractAmountTag{
    public AmountTag(ByteBuffer buffer, int size, String name, ByteOrder byteOrder) {
        super(buffer, size, name, byteOrder);
    }

    public static class Reader extends image.AbstractAmountTag.Reader<Reader>{
        public AmountTag read(ByteBuffer buffer){
            return new AmountTag(buffer, getSize(), getName(), getOrder());
        }

    }
}
