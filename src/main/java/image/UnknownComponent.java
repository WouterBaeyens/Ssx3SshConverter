package image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ByteUtil;
import util.PrintUtil;

import java.nio.ByteBuffer;

public class UnknownComponent extends ImgSubComponent{

    private static Logger LOGGER = LoggerFactory.getLogger(UnknownComponent.class);


    public UnknownComponent(final ByteBuffer buffer, final int size, final String expectedValueAsHex){
        this(buffer, size);
        assertExpectedValue(expectedValueAsHex);
    }

    public UnknownComponent(final ByteBuffer buffer, final int size){
        super(buffer, size);
    }

    private void assertExpectedValue(final String expectedValueAsHex){
        if(!expectedValueAsHex.equalsIgnoreCase(PrintUtil.toHexString(getRawBytes()).trim())) {
            LOGGER.error("Expected content \"{}\", but was \"{}\" at component with pos: {}", expectedValueAsHex, PrintUtil.toHexString(getRawBytes()), ByteUtil.printLongWithHex(getStartPos()));
        }
    }

    @Override
    public String getInfo() {
        return "???: " + getHexData();
    }
}
