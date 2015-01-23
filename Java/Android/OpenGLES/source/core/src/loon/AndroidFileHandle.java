package loon;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import loon.Files.FileType;
import loon.core.FileHandle;

public class AndroidFileHandle extends FileHandle {

	final AssetManager assets;

	AndroidFileHandle (AssetManager assets, String fileName, FileType type) {
		super(fileName.replace('\\', '/'), type);
		this.assets = assets;
	}

	AndroidFileHandle (AssetManager assets, File file, FileType type) {
		super(file, type);
		this.assets = assets;
	}

	public FileHandle child (String name) {
		name = name.replace('\\', '/');
		if (file.getPath().length() == 0){
			return new AndroidFileHandle(assets, new File(name), type);
		}
		return new AndroidFileHandle(assets, new File(file, name), type);
	}

	public FileHandle sibling (String name) {
		name = name.replace('\\', '/');
		if (file.getPath().length() == 0) {
			throw new RuntimeException("Cannot get the sibling of the root.");
		}
		return new AndroidFileHandle(assets, new File(file.getParent(), name), type);
	}

	public FileHandle parent () {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == FileType.Absolute){
				parent = new File("/");
			}
			else{
				parent = new File("");
			}
		}
		return new AndroidFileHandle(assets, parent, type);
	}

	public InputStream read () {
		if (type == FileType.Internal) {
			try {
				return assets.open(file.getPath());
			} catch (IOException ex) {
				throw new RuntimeException("Error reading file: " + file + " (" + type + ")", ex);
			}
		}
		return super.read();
	}

	public FileHandle[] list () {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets.list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				for (int i = 0, n = handles.length; i < n; i++)
					handles[i] = new AndroidFileHandle(assets, new File(file, relativePaths[i]), type);
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list();
	}

	public FileHandle[] list (FileFilter filter) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets.list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					FileHandle child = new AndroidFileHandle(assets, new File(file, path), type);
					if (!filter.accept(child.file())) continue;
					handles[count] = child;
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(filter);
	}

	public FileHandle[] list (FilenameFilter filter) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets.list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					if (!filter.accept(file, path)) continue;
					handles[count] = new AndroidFileHandle(assets, new File(file, path), type);
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(filter);
	}

	public FileHandle[] list (String suffix) {
		if (type == FileType.Internal) {
			try {
				String[] relativePaths = assets.list(file.getPath());
				FileHandle[] handles = new FileHandle[relativePaths.length];
				int count = 0;
				for (int i = 0, n = handles.length; i < n; i++) {
					String path = relativePaths[i];
					if (!path.endsWith(suffix)) continue;
					handles[count] = new AndroidFileHandle(assets, new File(file, path), type);
					count++;
				}
				if (count < relativePaths.length) {
					FileHandle[] newHandles = new FileHandle[count];
					System.arraycopy(handles, 0, newHandles, 0, count);
					handles = newHandles;
				}
				return handles;
			} catch (Exception ex) {
				throw new RuntimeException("Error listing children: " + file + " (" + type + ")", ex);
			}
		}
		return super.list(suffix);
	}

	public boolean isDirectory () {
		if (type == FileType.Internal) {
			try {
				return assets.list(file.getPath()).length > 0;
			} catch (IOException ex) {
				return false;
			}
		}
		return super.isDirectory();
	}

	public boolean exists () {
		if (type == FileType.Internal) {
			String fileName = file.getPath();
			try {
				assets.open(fileName).close(); 
				return true;
			} catch (Exception ex) {
				// This is SUPER slow! but we need it for directories.
				try {
					return assets.list(fileName).length > 0;
				} catch (Exception ignored) {
				}
				return false;
			}
		}
		return super.exists();
	}

	public long length () {
		if (type == FileType.Internal) {
			AssetFileDescriptor fileDescriptor = null;
			try {
				fileDescriptor = assets.openFd(file.getPath());
				return fileDescriptor.getLength();
			} catch (IOException ignored) {
			} finally {
				if (fileDescriptor != null) {
					try {
						fileDescriptor.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return super.length();
	}

	public long lastModified () {
		return super.lastModified();
	}

	public File file () {
		if (type == FileType.Local) {
			return new File(LSystem.files.getLocalStoragePath(), file.getPath());
		}
		return super.file();
	}

}
