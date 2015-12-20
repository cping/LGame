package loon;

import java.io.File;

import loon.core.FileHandle;

public final class JavaSEFiles implements Files {
	static public final String externalPath = System.getProperty("user.home")
			+ File.separator;
	static public final String localPath = new File("").getAbsolutePath()
			+ File.separator;

	@Override
	public FileHandle getFileHandle(String fileName, FileType type) {
		return new JavaSEFileHandle(fileName, type);
	}

	@Override
	public FileHandle classpath(String path) {
		return new JavaSEFileHandle(path, FileType.Classpath);
	}

	@Override
	public FileHandle internal(String path) {
		return new JavaSEFileHandle(path, FileType.Internal);
	}

	@Override
	public FileHandle external(String path) {
		return new JavaSEFileHandle(path, FileType.External);
	}

	@Override
	public FileHandle absolute(String path) {
		return new JavaSEFileHandle(path, FileType.Absolute);
	}

	@Override
	public FileHandle local(String path) {
		return new JavaSEFileHandle(path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath() {
		return externalPath;
	}

	@Override
	public boolean isExternalStorageAvailable() {
		return true;
	}

	@Override
	public String getLocalStoragePath() {
		return localPath;
	}

	@Override
	public boolean isLocalStorageAvailable() {
		return true;
	}
}
