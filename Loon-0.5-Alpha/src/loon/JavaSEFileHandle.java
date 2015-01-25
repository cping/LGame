package loon;

import java.io.File;

import loon.Files.FileType;
import loon.core.FileHandle;

public final class JavaSEFileHandle extends FileHandle {
	
	public JavaSEFileHandle(String fileName, FileType type) {
		super(fileName, type);
	}

	public JavaSEFileHandle(File file, FileType type) {
		super(file, type);
	}

	public FileHandle child(String name) {
		if (file.getPath().length() == 0)
			return new JavaSEFileHandle(new File(name), type);
		return new JavaSEFileHandle(new File(file, name), type);
	}

	public FileHandle sibling(String name) {
		if (file.getPath().length() == 0){
			throw new RuntimeException("Cannot get the sibling of the root.");
		}
		return new JavaSEFileHandle(new File(file.getParent(), name), type);
	}

	public FileHandle parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileType.Absolute){
				parent = new File("/");
			}
			else{
				parent = new File("");
			}
		}
		return new JavaSEFileHandle(parent, type);
	}

	public File file() {
		if (type == FileType.External){
			return new File(JavaSEFiles.externalPath, file.getPath());
		}
		if (type == FileType.Local){
			return new File(JavaSEFiles.localPath, file.getPath());
		}
		return file;
	}
}
