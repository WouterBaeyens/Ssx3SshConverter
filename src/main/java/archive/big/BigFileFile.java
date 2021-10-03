package archive.big;

import com.google.common.io.Files;
import filecollection.FileExtension;
import image.bmp2.bmpheader.FileTypeTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class BigFileFile {

    private File file;
    private RandomAccessFile raf;

    public BigFileFile(File bigFile) throws FileNotFoundException {
        if (!isBigFile(bigFile)) {
            throw new IllegalArgumentException("only bmp files allowed!");
        }
        String filePermissions = "r";
        raf = new RandomAccessFile(bigFile, filePermissions);

    }

    private boolean isBigFile(File bigFile) {
        String extension = Files.getFileExtension(file.getPath());
        return extension.equals(FileExtension.BIG_EXTENSION.getExtension());
    }
}
