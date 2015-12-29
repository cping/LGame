package loon.build.packer;

import java.io.InputStream;

public class ZipInputArchiveIterable extends InputArchiveIterable {
    ZipInputArchiveIterable(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    public ZipInputArchiveIterator iterator() {
        ZipInputArchiveIterator res = new ZipInputArchiveIterator(inputStream);
        inputStream = null;
        return res;
    }
    
    public String getArchiveType() {
        return "zip";
    }

    private InputStream inputStream;

}
