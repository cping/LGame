package loon.build.packer;

import java.io.IOException;

public abstract class OutputArchiveIterator implements ArchiveIterator {
    public boolean hasNext() {
        return true;
    }
    public abstract OutputArchiveEntry next();
    public void remove() {
    }
    public void close() throws IOException {
    }
}
