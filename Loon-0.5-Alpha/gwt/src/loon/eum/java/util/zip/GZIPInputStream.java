package java.util.zip;

import java.io.InputStream;

public class GZIPInputStream extends InflaterInputStream {
	public GZIPInputStream (InputStream in, int size) {
		super(in);
	}
}
