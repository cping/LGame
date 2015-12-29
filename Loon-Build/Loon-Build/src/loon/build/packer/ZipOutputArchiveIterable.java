package loon.build.packer;

import java.io.OutputStream;

public class ZipOutputArchiveIterable extends OutputArchiveIterable {
    public ZipOutputArchiveIterable(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    @Override
    public String getArchiveType() {
        return "zip";
    }

    @Override
    public ZipOutputArchiveIterator iterator() {
        ZipOutputArchiveIterator res = new ZipOutputArchiveIterator(outputStream);
        outputStream = null;
        return res;
    }

    private OutputStream outputStream;
}
