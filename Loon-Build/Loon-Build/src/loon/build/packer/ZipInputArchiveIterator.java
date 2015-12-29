package loon.build.packer;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipInputArchiveIterator extends InputArchiveIterator {
	

    ZipInputStream input;
	
    ZipInputArchiveIterator(ZipInputStream i) {
        this.input = i;
    }
    ZipInputArchiveIterator(InputStream i) {
        this.input = new ZipInputStream(i);
    }
    @Override
    public boolean hasNext() {
        try {
            return input.available() == 1;
        } catch (IOException e) {
            System.err.println("dctc ZipInputArchiveIterator: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ZipInputArchiveEntry next() {
        try {
            ZipEntry entry = input.getNextEntry();
            if (entry == null) {
                return null;
            }
            return new ZipInputArchiveEntry(entry, input);
        } catch (IOException e) {
            System.err.println("dctc ZipInputArchiveIterator: " + e.getMessage());
            return null;
        }
    }
    
    public ZipEntry nextZip() {
        try {
        	return input.getNextEntry();
        } catch (IOException e) {
            System.err.println("dctc ZipInputArchiveIterator: " + e.getMessage());
            return null;
        }
    }
    
}
