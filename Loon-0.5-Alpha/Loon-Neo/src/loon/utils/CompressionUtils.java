package loon.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class CompressionUtils {
	private CompressionUtils() {
	}

	public static byte[] compressGZIP(byte[] data) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

		byte[] buffer = new byte[1024];

		int count;
		while ((count = inputStream.read(buffer, 0, 1024)) != -1) {
			gzipOutputStream.write(buffer, 0, count);
		}

		gzipOutputStream.close();
		return outputStream.toByteArray();
	}

	public static byte[] decompressGZIP(byte[] data) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GZIPInputStream inputStream = new GZIPInputStream(
				new ByteArrayInputStream(data));

		byte[] buffer = new byte[1024];

		while (inputStream.read(buffer) != -1) {
			outputStream.write(buffer);
		}
		outputStream.close();

		return outputStream.toByteArray();
	}

	public static byte[] compressZLIB(byte[] data) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();

		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}

		outputStream.close();
		return outputStream.toByteArray();
	}

	public static byte[] decompressZLIB(byte[] data) throws IOException,
			DataFormatException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		Inflater inflater = new Inflater();
		inflater.setInput(data);

		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}

		outputStream.close();
		return outputStream.toByteArray();
	}
}
