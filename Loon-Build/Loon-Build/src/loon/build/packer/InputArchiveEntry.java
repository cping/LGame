package loon.build.packer;

import java.io.IOException;
import java.io.InputStream;

public interface InputArchiveEntry extends ArchiveEntry {
    public String getName();
    public InputStream getContentStream();
    public boolean isDirectory();
    public long getTime();
    public long getSize();
    public long getCompressSize();
    public boolean hasHash();
    public String getHashMethod();
    public String getHash();
    public void closeEntry() throws IOException;
}
