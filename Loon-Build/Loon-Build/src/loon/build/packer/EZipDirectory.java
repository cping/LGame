package loon.build.packer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EZipDirectory {
	private final static String SEPARATOR = "/";

	EZipDirectory parent;
	String name;

	ArrayList<EZipFile> files = new ArrayList<EZipFile>();
	ArrayList<EZipDirectory> directories = new ArrayList<EZipDirectory>();

	public EZipDirectory(String name) {
		this.name = name;
	}

	public EZipDirectory(EZipDirectory parent, String name) {
		this(name);
		this.parent = parent;
	}

	public EZipDirectory addZipDirectory(String name) {
		EZipDirectory zipDirectory = new EZipDirectory(this, name);
		directories.add(zipDirectory);
		return zipDirectory;
	}

	public EZipFile addZipFile(String name, byte[] bytes) {
		EZipFile zipFile = new EZipFile(this, name, bytes);
		files.add(zipFile);
		return zipFile;
	}

	public EZipFile addZipFile(String name, InputStream inputStream)
			throws IOException {
		EZipFile zipFile = new EZipFile(this, name, inputStream);
		files.add(zipFile);
		return zipFile;
	}

	public EZipFile addZipFile(EZipFile zipFile) throws IOException {
		files.add(zipFile);
		return zipFile;
	}

	public String getPath() {
		String path = "";
		if (parent != null) {
			path = parent.getPath();
		}
		path = path + name + SEPARATOR;
		return path;
	}

	public ZipOutputStream serialize(ZipOutputStream zos) throws IOException {
		zos.putNextEntry(new ZipEntry(getPath()));
		for (EZipDirectory zipDirectory : directories) {
			zipDirectory.serialize(zos);
		}
		for (EZipFile zipFile : files) {
			zipFile.serialize(zos);
		}
		zos.closeEntry();
		return zos;
	}
}
