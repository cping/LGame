/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.cport.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class CTarGzExtractor {

	static class TarInputStream extends InputStream {

		private final InputStream in;
		private TarEntry currentEntry;
		private long entrySize;
		private long bytesRead;

		public TarInputStream(InputStream in) {
			this.in = in;
		}

		public TarEntry getNextEntry() throws IOException {
			byte[] header = new byte[512];
			int read = in.read(header);
			if (read < 512) {
				return null;
			}
			boolean isEOF = true;
			for (byte b : header) {
				if (b != 0) {
					isEOF = false;
					break;
				}
			}
			if (isEOF) {
				return null;
			}
			String name = extractString(header, 0, 100).trim();
			String sizeStr = extractString(header, 124, 12).trim();
			long size = sizeStr.isEmpty() ? 0 : Long.parseLong(sizeStr, 8);

			char typeFlag = (char) header[156];
			boolean isDir = (typeFlag == '5') || name.endsWith("/");
			currentEntry = new TarEntry(name, size, isDir);
			entrySize = size;
			bytesRead = 0;
			return currentEntry;
		}

		@Override
		public int read(byte[] buf) throws IOException {
			if (currentEntry == null || bytesRead >= entrySize) {
				return -1;
			}
			int maxRead = (int) Math.min(buf.length, entrySize - bytesRead);
			int len = in.read(buf, 0, maxRead);
			if (len == -1) {
				return -1;
			}
			bytesRead += len;
			if (bytesRead == entrySize) {
				skipPadding();
			}
			return len;
		}

		@Override
		public int read() throws IOException {
			byte[] b = new byte[1];
			int len = read(b);
			return (len == -1) ? -1 : (b[0] & 0xFF);
		}

		private void skipPadding() throws IOException {
			long skip = (512 - (entrySize % 512)) % 512;
			while (skip > 0) {
				long skipped = in.skip(skip);
				if (skipped <= 0) {
					break;
				}
				skip -= skipped;
			}
		}

		private String extractString(byte[] buf, int offset, int length) {
			int end = offset + length;
			int i = offset;
			while (i < end && buf[i] != 0) {
				i++;
			}
			return new String(buf, offset, i - offset);
		}

		@Override
		public void close() throws IOException {
			in.close();
		}
	}

	static class TarEntry {
		private final String name;
		private final long size;
		private final boolean directory;

		public TarEntry(String name, long size, boolean directory) {
			this.name = name;
			this.size = size;
			this.directory = directory;
		}

		public String getName() {
			return name;
		}

		public boolean isDirectory() {
			return directory;
		}

		public long getSize() {
			return size;
		}
	}

	public static void extractTarGz(String tarGzPath, String destDir) throws IOException {
		try (FileInputStream fis = new FileInputStream(tarGzPath);
				GZIPInputStream gis = new GZIPInputStream(fis);
				TarInputStream tis = new TarInputStream(gis)) {

			TarEntry entry;
			while ((entry = tis.getNextEntry()) != null) {
				File outFile = new File(destDir, entry.getName());
				if (entry.isDirectory()) {
					outFile.mkdirs();
				} else {
					outFile.getParentFile().mkdirs();
					try (FileOutputStream fos = new FileOutputStream(outFile)) {
						byte[] buffer = new byte[8192];
						int len;
						while ((len = tis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
					}
				}
			}
		}
	}
}
