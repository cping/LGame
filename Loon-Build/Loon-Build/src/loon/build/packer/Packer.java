package loon.build.packer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import loon.build.tools.FileUtils;
import loon.build.tools.Resources;

public class Packer {

	public static String getJarMainPackage(String jarPath) {
		String mainName = null;
		JarFile jarfile = null;
		try {
			jarfile = new JarFile(jarPath);
			if (jarfile.getManifest() != null) {
				Attributes attributes = jarfile.getManifest().getMainAttributes();
				if (attributes != null) {
					mainName = attributes.getValue("Main-Class");
				}
				if (mainName == null || mainName.toLowerCase().lastIndexOf("JarRsrcLoader") != -1
						|| mainName.toLowerCase().lastIndexOf("JarInternal") != -1) {
					mainName = attributes.getValue("Rsrc-Main-Class");
					if (mainName == null) {
						mainName = attributes.getValue("Main-Class");
					}
				}
				if (mainName != null && mainName.trim().length() > 0) {
					return mainName == null ? null
							: mainName.replace("/", ".").replace("\\", ".").replace(".class", "");
				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Enumeration<JarEntry> en = jarfile.entries();
			byte[] buffer = new byte[2048];
			while (en.hasMoreElements()) {
				JarEntry clazz = en.nextElement();
				InputStream in = jarfile.getInputStream(clazz);
				out.reset();
				int n;
				while ((n = in.read(buffer, 0, 1024)) > -1) {
					out.write(buffer, 0, n);
				}
				String context = new String(out.toByteArray());
				if (context.indexOf("main") != -1) {
					mainName = clazz.getName();
					break;
				}
			}
		} catch (Exception ex) {

		} finally {
			try {
				if (jarfile != null) {
					jarfile.close();
					jarfile = null;
				}
			} catch (IOException e) {
			}
		}
		return mainName == null ? null : mainName.replace("/", ".").replace("\\", ".").replace(".class", "");
	}

	public static void outputLJar(String assets, String out, String other, String mainClassName, boolean enableNative) {
		ArrayList<String> list = new ArrayList<String>(10);
		if (other.indexOf(",") != -1) {
			String[] res = other.split(",");
			for (String path : res) {
				list.add(path.trim());
			}
		} else {
			File file = new File(other);
			if (!file.exists()) {
				throw new RuntimeException(other + " does not exist !");
			}
			if (file.isDirectory()) {
				try {
					list.addAll(FileUtils.getAllDir(other));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				list.add(other);
			}
		}

		Packer.outputLJar(assets, out, list, mainClassName, enableNative);
	}

	public static byte[] getResourceZipFile(String name, String fileName) throws Exception {
		InputStream input = Resources.getResourceAsStream(name);

		byte[] buffer = new byte[2048];
		ZipInputStream it = new ZipInputStream(input);
		ZipEntry zipentry = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((zipentry = it.getNextEntry()) != null) {
			String entryName = zipentry.getName();
			if (entryName.equals(fileName)) {
				int n;
				while ((n = it.read(buffer, 0, 1024)) > -1) {
					out.write(buffer, 0, n);
				}
				out.close();
				break;
			}
		}
		it.close();
		it = null;
		return out.toByteArray();
	}

	public static void outputLJar(String assets, String out, ArrayList<String> paths, String mainClassName,
			boolean enableNative) {
		ZipFileMake make = new ZipFileMake();
		try {
			ArrayList<EZipFile> res = make.classFileToEZipFile(assets);
			File outDest = new File(out);
			if (outDest.isFile() && outDest.exists()) {
				outDest.delete();
				FileUtils.makedirs(outDest);
			}
			StringBuilder libs = new StringBuilder(32);
			FileOutputStream fileWriter = new FileOutputStream(outDest);
			ZipOutputStream output = new ZipOutputStream(fileWriter);
			for (Iterator<EZipFile> it = res.iterator(); it.hasNext();) {
				EZipFile file = it.next();
				file.serialize(output);
				if (file.name.indexOf(".class") == -1 && file.name.indexOf("\\") == -1
						&& file.name.indexOf("/") == -1) {
					libs.append(file.name);
					libs.append(" ");
				}
			}
			if (paths != null) {
				for (Iterator<String> it = paths.iterator(); it.hasNext();) {
					String path = it.next();
					File file = new File(path);
					String name = file.getName();
					EZipFile ezip = new EZipFile(name, new FileInputStream(file));
					ezip.serialize(output);
					if (ezip.name.indexOf(".class") == -1 && ezip.name.indexOf("\\") == -1
							&& ezip.name.indexOf("/") == -1) {
						libs.append(ezip.name);
						libs.append(" ");
					}
				}
			}
			final StringBuilder sbr = new StringBuilder(32);
			final String newLine = System.getProperty("line.separator", "\n");
			sbr.append("Manifest-Version: 1.0");
			sbr.append(newLine);
			sbr.append("Rsrc-Class-Path: ./ ");
			sbr.append(libs.toString().trim());
			sbr.append(newLine);
			sbr.append("Class-Path: .");
			sbr.append(newLine);
			sbr.append("Rsrc-Main-Class: ");
			sbr.append(mainClassName);
			sbr.append(newLine);
			sbr.append("Main-Class: loon.JarInternal");
			if (enableNative) {
				sbr.append(newLine);
				sbr.append("Enable-Native-Access: ALL-UNNAMED");
			}
			sbr.append(newLine);
			EZipFile MANIFEST = new EZipFile("META-INF/MANIFEST.MF", sbr.toString().getBytes("UTF-8"));
			MANIFEST.serialize(output);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
