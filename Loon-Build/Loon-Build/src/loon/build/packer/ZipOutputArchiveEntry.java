package loon.build.packer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutputArchiveEntry implements OutputArchiveEntry {
    public ZipOutputArchiveEntry(ZipOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void create(String name) throws IOException {
        this.entry = new ZipEntry(name);
        stream.putNextEntry(entry);
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public long getTime() {
        return entry.getTime();
    }

    @Override
    public long getSize() {
        return entry.getSize();
    }

    @Override
    public void setTime(long time) {
        entry.setTime(time);
    }

    @Override
    public OutputStream getContentStream() {
        return stream;
    }

    @Override
    public void closeEntry() throws IOException {
        stream.closeEntry();
    }

    private ZipEntry entry;
    private ZipOutputStream stream;
}
