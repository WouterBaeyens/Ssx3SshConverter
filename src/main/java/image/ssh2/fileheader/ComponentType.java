package image.ssh2.fileheader;

import java.util.function.Function;

public interface ComponentType {

        String getReadableValue();

        Function<byte[], String> toReadable();
}
