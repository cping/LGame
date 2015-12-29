package loon.build.packer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

public class ZipFileSystem {
	ArrayList<EZipDirectory> directories = new ArrayList<EZipDirectory>();
	ArrayList<EZipFile> files = new ArrayList<EZipFile>();

	public ZipFileSystem() {

	}

	public EZipDirectory addZipDirectory(String name) {
		EZipDirectory zipDirectory = new EZipDirectory(name);
		directories.add(zipDirectory);
		return zipDirectory;
	}

	public EZipFile addZipFile(String name, byte[] bytes) {
		EZipFile zipFile = new EZipFile(name, bytes);
		files.add(zipFile);
		return zipFile;
	}

	public ZipOutputStream serialize(ZipOutputStream zos) throws IOException {
		for (EZipDirectory zipDirectory : directories) {
			zipDirectory.serialize(zos);
		}
		for (EZipFile zipFile : files) {
			zipFile.serialize(zos);
		}
		return zos;
	}
}
