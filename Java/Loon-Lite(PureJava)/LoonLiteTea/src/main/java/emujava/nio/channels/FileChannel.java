package emujava.nio.channels;

import java.nio.ByteBuffer;

public abstract class FileChannel {

    public static class MapMode {
        public static final MapMode READ_ONLY = new MapMode();
        public static final MapMode READ_WRITE = new MapMode();
        public static final MapMode PRIVATE = new MapMode();
    }

    public abstract ByteBuffer map (java.nio.channels.FileChannel.MapMode mode, long position, long size);
}
