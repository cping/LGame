package loon.build.packer;

import java.io.InputStream;
import java.io.OutputStream;

public class ArchiveFactory {
	
    public static InputArchiveIterable build(InputStream stream, String streamName){
        if (streamName.endsWith("." + "zip")) {
            return new ZipInputArchiveIterable(stream);
        }
        throw new RuntimeException("Unknown archive file extension for '" + streamName +"'. Expected 'zip'");
    }

    public static OutputArchiveIterable build(OutputStream stream, String streamName) {
        if (streamName.endsWith("." + "zip")) {
            return new ZipOutputArchiveIterable(stream);
        }
        throw new RuntimeException("Unknown archive file extension for '" + streamName + "'. Expected 'zip'");
    }

}
