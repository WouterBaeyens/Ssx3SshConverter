package filecollection;

import java.io.File;

public interface FileWrapper {

    File getFile();

    FileExtension getExtension();

    default String getFileNameWithoutExtension() {
        final String completeFileName = getFile().getName();
        final int fileNameWithoutExtensionLength = completeFileName.length() - getExtension().value.length();
        return completeFileName.substring(0, fileNameWithoutExtensionLength);
    }
}
