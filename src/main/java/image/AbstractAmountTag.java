package image;

import util.ByteUtil;

import java.nio.ByteBuffer;
import java.util.Optional;

public class AbstractAmountTag extends ImgSubComponent{


    private final String name;

    public AbstractAmountTag(final ByteBuffer buffer, final int size, final String name, final ByteOrder byteOrder){
        super(buffer, size, byteOrder);
        this.name = name;
    }

    @Override
    public String getInfo() {
        return name + ": " + ByteUtil.printLongWithHex(getConvertedValue());
    }

    public long getConvertedValue(){
        return ByteUtil.convertToLongBE(getBytesBE());
    }

    public static class Reader <T extends Reader<T>> {
        private static final int DEFAULT_COMPONENT_SIZE = 3;
        private static final String DEFAULT_COMPONENT_NAME = "?Amount?";
        private static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;
        private Integer componentSize;
        private String componentName;
        private ByteOrder byteOrder;

        public T withSize(final int componentSize){
            this.componentSize = componentSize;
            return (T) this;
        }

        public T withName(final String componentName){
            this.componentName = componentName;
            return (T) this;
        }

        public T withByteOrder(final ByteOrder byteOrder){
            this.byteOrder = byteOrder;
            return (T) this;
        }

        protected int getSize(){
            return Optional.ofNullable(componentSize).orElse(DEFAULT_COMPONENT_SIZE);
        }

        protected String getName(){
            return Optional.ofNullable(componentName).orElse(DEFAULT_COMPONENT_NAME);
        }

        protected ByteOrder getOrder(){
            return Optional.ofNullable(byteOrder).orElse(DEFAULT_BYTE_ORDER);
        }

        public AbstractAmountTag read(final ByteBuffer buffer){
            return new AbstractAmountTag(buffer, getSize(), getName(), getOrder());
        }
    }
}
