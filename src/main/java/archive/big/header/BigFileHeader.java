package archive.big.header;

public interface BigFileHeader {
    public int getNumberOfEntries();

    public BigFileTypeTag.BigArchiveType getArchiveType();
}
