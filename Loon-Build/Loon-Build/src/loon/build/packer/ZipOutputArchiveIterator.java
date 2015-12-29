package loon.build.packer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public class ZipOutputArchiveIterator extends OutputArchiveIterator {
    public ZipOutputArchiveIterator(ZipOutputStream output) {
        this.output = output;
    }
    public ZipOutputArchiveIterator(OutputStream outputStream) {
        this.output = new ZipOutputStream(outputStream);
    }

    @Override
    public ZipOutputArchiveEntry next() {
        return new ZipOutputArchiveEntry(output);
    }
    @Override
    public void close() throws IOException {
        output.close();
    }

    private ZipOutputStream output;
}
