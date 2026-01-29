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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 工具用类,用于在java端调用cmake工具编译并启动java2c后的程序
 */
public class CMakeBuilder {

	public static void create(String[] args) {
		try {
			String installPath = null;
			String runFileName = null;
			boolean useWinToLinux = false;
			boolean useOverwriteFile = false;
			for (String arg : args) {
				if (arg.startsWith("installPath=")) {
					installPath = arg.substring("installPath=".length());
				}
				if (arg.startsWith("fileName=")) {
					runFileName = arg.substring("fileName=".length());
				}
				if (arg.equalsIgnoreCase("wintolinux")) {
					useWinToLinux = true;
				}
				if (arg.equalsIgnoreCase("overwrite")) {
					useOverwriteFile = true;
				}
			}
			String cmakePath = useWinToLinux ? findOrInstallMSYS2CMake(installPath, useOverwriteFile, true)
					: findOrInstallCMake(installPath);
			if (cmakePath != null) {
				runCMake(cmakePath);
				buildProject(cmakePath);
				runExecutable(runFileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String findOrInstallMSYS2CMake(String installPath, boolean useOverwriteFile, boolean loop)
			throws Exception {
		String[] paths = { "C:\\msys64\\mingw64\\bin\\cmake.exe", "D:\\msys64\\mingw64\\bin\\cmake.exe",
				"C:\\Program Files\\msys64\\mingw64\\bin\\cmake.exe",
				"D:\\Program Files\\msys64\\mingw64\\bin\\cmake.exe",
				"C:\\Program Files (x86)\\msys64\\mingw64\\bin\\cmake.exe",
				"D:\\Program Files (x86)\\msys64\\mingw64\\bin\\cmake.exe" };
		Path msysPath = null;
		for (String path : paths) {
			msysPath = Paths.get(path);
			if (Files.exists(msysPath)) {
				CBuilder.println("Found MSYS2/MinGW64 CMake at: " + msysPath);
				return msysPath.toString();
			}
		}
		if (!loop) {
			return null;
		}
		msysPath = Paths.get(paths[0]);
		CBuilder.println("MSYS2/MinGW64 not found, preparing to download...");
		String[] urls = { "https://github.com/msys2/msys2-installer/releases/latest/download/msys2-x86_64-latest.exe",
				"https://github.com/msys2/msys2-installer/releases/download/2025-12-13/msys2-x86_64-20251213.exe",
				"https://mirror.msys2.org/distrib/x86_64/msys2-x86_64-latest.exe" };
		Path targetDir = (installPath != null) ? Paths.get(installPath)
				: Paths.get(System.getProperty("java.io.tmpdir"), "msys2");
		if (Files.exists(targetDir)) {
			CBuilder.println("Target directory already exists: " + targetDir);
			if (!useOverwriteFile || !askUserOverwrite()) {
				CBuilder.println("Keeping existing MSYS2 installation.");
				return msysPath.toString();
			} else {
				deleteDirectory(targetDir);
			}
		}
		Files.createDirectories(targetDir);

		Path installer = targetDir.resolve("msys2-installer.exe");
		downloadFile(urls, installer);

		CBuilder.println("Downloaded MSYS2 installer: " + installer.toAbsolutePath());

		if (installMsys2(installer.toAbsolutePath().toString())) {
			if (updateMsys2()) {
				return findOrInstallMSYS2CMake(installPath, useOverwriteFile, false);
			}
		} else {
			CBuilder.println("Please run the installer manually to complete installation.");
			throw new RuntimeException("MSYS2 installation required. Run installer and retry.");
		}
		return null;
	}

	private static String extractMsys64Path(String fullPath) {
		if (fullPath == null) {
			return null;
		}
		String keyword = "msys64";
		int index = fullPath.toLowerCase().indexOf(keyword);
		if (index == -1) {
			return fullPath;
		}
		int endIndex = index + keyword.length();
		return fullPath.substring(0, endIndex);
	}

	private static boolean updateMsys2() {
		String[] paths = { "C:\\msys64\\usr\\bin\\bash.exe", "D:\\msys64\\usr\\bin\\bash.exe",
				"C:\\Program Files\\msys64\\usr\\bin\\bash.exe", "D:\\Program Files\\msys64\\usr\\bin\\bash.exe",
				"C:\\Program Files (x86)\\msys64\\usr\\bin\\bash.exe",
				"D:\\Program Files (x86)\\msys64\\usr\\bin\\bash.exe" };
		Path msysPath = null;
		for (String path : paths) {
			msysPath = Paths.get(path);
			if (Files.exists(msysPath)) {
				String bashPath = extractMsys64Path(path);
				ProcessBuilder pb = new ProcessBuilder(bashPath, "-lc", "pacman -Syu --noconfirm");
				pb.inheritIO();
				try {
					Process process = pb.start();
					int exitCode = process.waitFor();
					if (exitCode == 0) {
						CBuilder.println("MSYS2 Updated ！");
						return true;
					} else {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}

	private static boolean installMsys2(String installerPath) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(installerPath, "/S");
		pb.inheritIO();
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			CBuilder.println("MSYS2 installed！");
			return true;
		} else {
			return false;
		}
	}

	private static String findOrInstallCMake(String installPath) throws Exception {
		try {
			Process check = new ProcessBuilder("cmake", "--version").start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(check.getInputStream()));
			String line = reader.readLine();
			if (line != null && line.contains("cmake")) {
				CBuilder.println("Found local CMake: " + line);
				return "cmake";
			}
		} catch (IOException e) {
			CBuilder.println("CMake not found locally, preparing to download...");
		}

		String os = System.getProperty("os.name").toLowerCase();
		String[] urls;
		String archiveName;
		String cmakeDirName;
		if (os.contains("win")) {
			urls = new String[] { "https://cmake.org/files/v3.29/cmake-3.29.0-windows-x86_64.zip",
					"https://github.com/Kitware/CMake/releases/download/v3.29.0/cmake-3.29.0-windows-x86_64.zip" };
			archiveName = "cmake.zip";
			cmakeDirName = "cmake-3.29.0-windows-x86_64";
		} else if (os.contains("mac")) {
			urls = new String[] { "https://cmake.org/files/v3.29/cmake-3.29.0-macos-universal.tar.gz",
					"https://github.com/Kitware/CMake/releases/download/v3.29.0/cmake-3.29.0-macos-universal.tar.gz" };
			archiveName = "cmake.tar.gz";
			cmakeDirName = "cmake-3.29.0-macos-universal";
		} else {
			urls = new String[] { "https://cmake.org/files/v3.29/cmake-3.29.0-linux-x86_64.tar.gz",
					"https://github.com/Kitware/CMake/releases/download/v3.29.0/cmake-3.29.0-linux-x86_64.tar.gz" };
			archiveName = "cmake.tar.gz";
			cmakeDirName = "cmake-3.29.0-linux-x86_64";
		}

		Path targetDir = (installPath != null) ? Paths.get(installPath)
				: Paths.get(System.getProperty("java.io.tmpdir"), "cmake");
		Files.createDirectories(targetDir);

		Path archive = targetDir.resolve(archiveName);
		downloadFile(urls, archive);

		Path cmakeBin;
		if (os.contains("win")) {
			cmakeBin = targetDir.resolve(cmakeDirName).resolve("bin").resolve("cmake.exe");
			if (!tryUnzipWithCommand("powershell",
					new String[] { "Expand-Archive", archive.toString(), targetDir.toString() })) {
				unzipWithJavaZip(archive, targetDir);
			}
		} else {
			cmakeBin = targetDir.resolve(cmakeDirName).resolve("bin").resolve("cmake");
			if (!tryUnzipWithCommand("tar", new String[] { "-xzf", archive.toString(), "-C", targetDir.toString() })) {
				CBuilder.println("Using TarGz decompression...");
				try {
					CTarGzExtractor.extractTarGz(archive.toAbsolutePath().toString(),
							targetDir.toAbsolutePath().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (!Files.exists(cmakeBin)) {
			throw new RuntimeException("CMake installation failed, executable not found: " + cmakeBin);
		}

		CBuilder.println("Installed CMake at: " + cmakeBin.toAbsolutePath());
		return cmakeBin.toAbsolutePath().toString();
	}

	private static boolean tryUnzipWithCommand(String cmd, String[] args) {
		try {
			String[] fullCmd = new String[args.length + 1];
			fullCmd[0] = cmd;
			System.arraycopy(args, 0, fullCmd, 1, args.length);
			Process p = new ProcessBuilder(fullCmd).inheritIO().start();
			int exit = p.waitFor();
			return exit == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private static void unzipWithJavaZip(Path zipFile, Path targetDir) throws IOException {
		CBuilder.println("Using Zip decompression...");
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				Path newFile = targetDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(newFile);
				} else {
					Files.createDirectories(newFile.getParent());
					try (OutputStream os = new FileOutputStream(newFile.toFile())) {
						byte[] buffer = new byte[4096];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							os.write(buffer, 0, len);
						}
					}
				}
				zis.closeEntry();
			}
		}
	}

	private static void downloadFile(String[] urls, Path target) throws Exception {
		Exception lastEx = null;
		for (String urlStr : urls) {
			try {
				CBuilder.println("Trying download: " + urlStr);
				URL url = URI.create(urlStr).toURL();
				InputStream in = url.openStream();
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
				in.close();
				CBuilder.println("Downloaded to: " + target.toAbsolutePath());
				return;
			} catch (Exception e) {
				lastEx = e;
				CBuilder.println("Download failed from: " + urlStr);
			}
		}
		throw new RuntimeException("All download attempts failed", lastEx);
	}

	private static boolean askUserOverwrite() {
		CBuilder.print("Directory already exists. Overwrite? (y/n): ");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine().trim().toLowerCase();
		scanner.close();
		return input.equals("y") || input.equals("yes");
	}

	private static void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path))
			return;
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void runCMake(String cmakePath) throws Exception {
		CBuilder.println("Running CMake configure...");
		ProcessBuilder pb = new ProcessBuilder(cmakePath, "-S", ".", "-B", "build");
		pb.inheritIO();
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			CBuilder.println("CMake configuration executed successfully.");
		} else {
			throw new RuntimeException("CMake configuration failed with code: " + exitCode);
		}
	}

	private static void buildProject(String cmakePath) throws Exception {
		CBuilder.println("Building project...");
		ProcessBuilder pb = new ProcessBuilder(cmakePath, "--build", "build");
		pb.inheritIO();
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			CBuilder.println("Build completed successfully.");
		} else {
			throw new RuntimeException("Build failed with code: " + exitCode);
		}
	}

	private static void runExecutable(String fileName) throws Exception {
		CBuilder.println("Running generated executable...");
		Path buildDir = Paths.get("build");
		Path exePath = findExecutable(buildDir, fileName);

		if (!Files.exists(exePath)) {
			throw new RuntimeException("Executable not found: " + exePath.toAbsolutePath());
		}

		ProcessBuilder pb = new ProcessBuilder(exePath.toAbsolutePath().toString());
		pb.inheritIO();
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			CBuilder.println("Executable ran successfully.");
		} else {
			throw new RuntimeException("Executable failed with code: " + exitCode);
		}
	}

	private static Path findExecutable(Path buildDir, String fileName) throws IOException {
		final Path[] found = { null };
		Files.walkFileTree(buildDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String name = file.getFileName().toString().toLowerCase();
				if (name.contains(fileName) && (name.endsWith(".exe") || !name.contains("."))) {
					found[0] = file;
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}
		});
		if (found[0] == null) {
			throw new RuntimeException("Executable not found in build directory");
		}
		return found[0];
	}
}
