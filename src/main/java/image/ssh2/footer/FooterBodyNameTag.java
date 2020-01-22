package image.ssh2.footer;

import image.ImgSubComponent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FooterBodyNameTag extends ImgSubComponent {

    private static final int DEFAULT_SIZE = 4;

    public FooterBodyNameTag(final RandomAccessFile file, final long startPosition) throws IOException {
        super(file, startPosition, DEFAULT_SIZE);
    }

    public String getInfo() {
        return "Img:(name=" + getConvertedValue() + ")";
    }

    public String getConvertedValue() {
        return new String(Arrays.copyOf(getBytes(), DEFAULT_SIZE));
    }

}
