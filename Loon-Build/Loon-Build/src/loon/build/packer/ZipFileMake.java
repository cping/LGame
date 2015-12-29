package loon.build.packer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileMake {

	public ArrayList<EZipFile> classFileToEZipFile(String classRes)
			throws Exception {
		InputStream input = Packer.class.getClassLoader().getResourceAsStream(
				classRes);
		byte[] buffer = new byte[2048];
		ZipInputStream it = new ZipInputStream(input);
		ZipEntry zipentry = it.getNextEntry();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ArrayList<EZipFile> zips = new ArrayList<EZipFile>(10);
		while (zipentry != null) {
			String entryName = zipentry.getName();
			int n;
			while ((n = it.read(buffer, 0, 1024)) > -1) {
				out.write(buffer, 0, n);
			}
			out.close();
			it.closeEntry();
			zipentry = it.getNextEntry();
			final byte[] bytes = out.toByteArray();
			EZipFile resource = new EZipFile(entryName, bytes);
			out.reset();
			zips.add(resource);
		}
		it.close();
		it = null;
		return zips;
	}

	public ArrayList<ZipEntry> classResList(String resName) {
		ArrayList<ZipEntry> zips = new ArrayList<ZipEntry>(10);
		ZipInputStream input = new ZipInputStream(Packer.class.getClassLoader()
				.getResourceAsStream(resName));
		ZipInputArchiveIterator it = new ZipInputArchiveIterator(input);
		for (; it.hasNext();) {
			ZipEntry entry = it.nextZip();
			if (entry != null) {
				zips.add(entry);
			}
		}
		try {
			input.close();
			input = null;
		} catch (IOException e) {
		}
		return zips;
	}

	public void zipFolder(String srcFolder, String destZipFile)
			throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile,
			ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close();
		}
	}

	static private void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileName, zip);
			}
		}
	}
}
