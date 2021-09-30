package filecollection;

import java.io.File;

public interface SourceFileWrapper {

    File getFile();

    FileExtension getExtension();

    default String getFileNameWithoutExtension() {
        final String completeFileName = getFile().getName();
        final int fileNameWithoutExtensionLength = completeFileName.length() - getExtension().value.length();
        return completeFileName.substring(0, fileNameWithoutExtensionLength);
    }
}
