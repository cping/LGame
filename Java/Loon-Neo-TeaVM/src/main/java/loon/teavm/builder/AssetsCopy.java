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
package loon.teavm.builder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import loon.teavm.assets.AssetFile;
import loon.teavm.assets.AssetType;

public class AssetsCopy {

	public static class Asset {
		AssetFile file;
		AssetType type;
		AssetFilterOption op;

		public Asset(AssetFile file, AssetType type, AssetFilterOption op) {
			this.file = file;
			this.type = type;
			this.op = op;
		}
	}

	public static ArrayList<Asset> copyResources(ClassLoader classLoader, List<String> classPathAssetsFiles,
			AssetFilter filter, AssetFile assetsOutputPath) {
		return copy(classLoader, classPathAssetsFiles, filter, assetsOutputPath);
	}

	public static ArrayList<Asset> copyScripts(ClassLoader classLoader, List<String> classPathAssetsFiles,
			AssetFile assetsOutputPath) {
		return copy(classLoader, classPathAssetsFiles, null, assetsOutputPath);
	}

	public static ArrayList<Asset> copyAssets(AssetFile AssetFile, AssetFilter filter, AssetFile assetsOutputPath) {
		return copy(AssetFile, filter, assetsOutputPath);
	}

	private static ArrayList<Asset> copy(AssetFile assetsPath, AssetFilter filter, AssetFile target) {
		String assetsOutputPath = target.path();
		ArrayList<Asset> assets = new ArrayList<Asset>();
		if (assetsPath.filter != null) {
			filter = assetsPath.filter;
		}
		AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();
		if (assetsPath != null && assetsPath.exists() && assetsPath.isDirectory()) {
			TeaBuilder.log("Copying assets from:");
			AssetFile source = assetsPath;
			String path = source.path();
			TeaBuilder.log(path);
			copyDirectory(source, assetsPath.assetsChildDir, target, defaultAssetFilter, assets);

			TeaBuilder.log("to:");
			TeaBuilder.log(assetsOutputPath);
		}

		TeaBuilder.log("to:");
		TeaBuilder.log(assetsOutputPath);

		return assets;
	}

	private static ArrayList<Asset> copy(ClassLoader classloader, List<String> classPathAssetsFiles, AssetFilter filter,
			AssetFile target) {
		String assetsOutputPath = target.path();
		ArrayList<Asset> assets = new ArrayList<Asset>();
		AssetFilter defaultAssetFilter = filter != null ? filter : new DefaultAssetFilter();

		if (classloader != null && classPathAssetsFiles != null) {
			addDirectoryClassPathFiles(classPathAssetsFiles);
			TeaBuilder.log("");
			TeaBuilder.log("Copying assets from:");
			for (String classpathFile : classPathAssetsFiles) {
				String path = classpathFile;
				if (path.startsWith("/") == false) {
					path = "/" + path;
				} else {
					classpathFile = classpathFile.replaceFirst("/", "");
				}
				AssetFilterOption op = new AssetFilterOption();
				if (defaultAssetFilter.accept(path, false, op)) {
					try {
						TeaBuilder.log(classpathFile);
						InputStream is = classloader.getResourceAsStream(classpathFile);
						if (is != null) {
							AssetFile dest = target.child(classpathFile);
							dest.write(is, false);
							String destPath = dest.path();
							if (!destPath.endsWith(".js") && !destPath.endsWith(".wasm")) {
								AssetFile dest2 = AssetFile.createHandle(dest.file());
								assets.add(new Asset(dest2, AssetType.Binary, op));
							}
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		TeaBuilder.log("to:");
		TeaBuilder.log(assetsOutputPath);

		return assets;
	}

	public static void generateAssetsFile(ArrayList<Asset> assets, AssetFile location, AssetFile assetFile) {
		StringBuffer buffer = new StringBuffer();
		String assetsOutputPath = location.path();
		for (int i = 0; i < assets.size(); i++) {
			Asset asset = assets.get(i);
			setupPreloadAssetFileFormat(asset, buffer, assetsOutputPath);
		}
		assetFile.writeString(buffer.toString(), true);
	}

	private static void setupPreloadAssetFileFormat(Asset asset, StringBuffer buffer, String assetsOutputPath) {
		AssetFile AssetFile = asset.file;

		String path = AssetFile.path();
		path = path.replace(assetsOutputPath, "");
		String fileTypeStr = "f";

		buffer.append(fileTypeStr);
		buffer.append(":");
		buffer.append(asset.type.code);
		buffer.append(":");
		buffer.append(path);
		buffer.append(":");
		buffer.append(asset.file.isDirectory() ? 0 : asset.file.length());
		buffer.append(":");
		buffer.append(asset.op.shouldOverwriteLocalData ? 1 : 0);
		buffer.append("\n");
	}

	private static void addDirectoryClassPathFiles(List<String> classPathFiles) {
		ArrayList<String> folderFilePaths = new ArrayList<>();
		for (int k = 0; k < classPathFiles.size(); k++) {
			String classpathFile = classPathFiles.get(k);
			classpathFile = classpathFile.replace("\\", "/");
			if (classpathFile.startsWith("/") == false)
				classpathFile = "/" + classpathFile;
			URL resource = AssetsCopy.class.getResource(classpathFile);
			if (resource != null) {
				URI uri = null;
				try {
					uri = resource.toURI();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
				if (uri == null)
					continue;
				Path myPath = null;
				String scheme = uri.getScheme();
				FileSystem fileSystem = null;
				if (scheme.equals("jar")) {
					try {
						fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					myPath = fileSystem.getPath(classpathFile);
				} else {
					myPath = Paths.get(uri);
				}
				Stream<Path> walk = null;
				try {
					walk = Files.walk(myPath, 1);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				boolean first = true;
				for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
					Path next = it.next();
					String path = next.toString();
					boolean directory = Files.isDirectory(next);
					path = path.replace("\\", "/");
					int i = path.lastIndexOf(classpathFile);
					path = path.substring(i + 1);
					if (path.startsWith("/") == false)
						path = "/" + path;
					if (path.contains(".class") || path.contains(".java"))
						continue;
					if (directory) {
						if (first) {
							first = false;
							classPathFiles.remove(k);
							k--;
							continue;
						} else {
							classPathFiles.add(path);
							continue;
						}
					}
					folderFilePaths.add(path);
				}
				walk.close();
				if (fileSystem != null) {
					try {
						fileSystem.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				classPathFiles.remove(k);
				k--;
			}
		}
		classPathFiles.addAll(folderFilePaths);
		HashSet<String> set = new HashSet<String>(folderFilePaths);
		classPathFiles.clear();
		classPathFiles.addAll(set);
	}

	private static void copyDirectory(AssetFile sourceDir, String assetsChildDir, AssetFile destDir, AssetFilter filter,
			ArrayList<Asset> assets) {
		if (!assetsChildDir.isEmpty()) {
			destDir = destDir.child(assetsChildDir);
		}
		copyDirectory(sourceDir, destDir, filter, assets);
	}

	private static void copyFile(AssetFile source, AssetFile dest, AssetFilter filter, ArrayList<Asset> assets) {
		AssetFilterOption op = new AssetFilterOption();
		if (!filter.accept(dest.path(), false, op))
			return;
		try {
			assets.add(new Asset(dest, AssetType.Binary, op));
			InputStream read = source.read();
			dest.write(read, false);
			read.close();
		} catch (Exception ex) {
			throw new RuntimeException("Error copying source file: " + source + "\n" //
					+ "To destination: " + dest, ex);
		}
	}

	private static void copyDirectory(AssetFile sourceDir, AssetFile destDir, AssetFilter filter,
			ArrayList<Asset> assets) {
		String destPath = destDir.path();
		destDir.mkdirs();
		AssetFile[] files = sourceDir.list();
		for (int i = 0, n = files.length; i < n; i++) {
			AssetFile srcFile = files[i];
			AssetFile destFile1 = destDir.child(srcFile.name());
			AssetFile destFile = AssetFile.createHandle(destFile1.file());
			if (srcFile.isDirectory()) {
				AssetFilterOption op = new AssetFilterOption();
				if (!filter.accept(destPath, true, op))
					continue;
				assets.add(new Asset(destFile, AssetType.Directory, op));
				copyDirectory(srcFile, destFile, filter, assets);
			} else {
				copyFile(srcFile, destFile, filter, assets);
			}
		}
	}

}