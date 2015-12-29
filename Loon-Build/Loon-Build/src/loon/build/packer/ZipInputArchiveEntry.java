package loon.build.packer;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipInputArchiveEntry implements InputArchiveEntry {
    public ZipInputArchiveEntry(ZipEntry entry, ZipInputStream stream) {
        this.entry = entry;
        this.stream = stream;
    }
    public String getName() {
        return entry.getName();
    }
    public InputStream getContentStream() {
        return stream;
    }
    public boolean isDirectory() {
        return entry.isDirectory();
    }
    public long getTime() {
        return entry.getTime();
    }
    public long getSize() {
        return entry.getSize();
    }
    public long getCompressSize() {
        return entry.getCompressedSize();
    }
    public boolean hasHash() {
        return true;
    }
    public String getHashMethod() {
        return "CRC32";
    }
    public String getHash() {
        return Long.toString(entry.getCrc());
    }
    public void closeEntry() throws IOException {
        stream.closeEntry();
    }

    private ZipEntry entry;
    private ZipInputStream stream;
}
