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
package loon.cport.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.teavm.backend.javascript.rendering.RenderingManager;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.spi.RendererListener;

import loon.LSystem;
import loon.cport.builder.CBuilder;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class AssetsCopy implements RendererListener {

	private FileDescriptor rootFileDescriptor = new FileDescriptor();
	private RenderingManager context;

	@Override
	public void begin(RenderingManager context, BuildTarget buildTarget) {
		this.context = context;
	}

	@Override
	public void complete() {
		try {
			String dirName = PathUtils.normalize(context.getProperties().getProperty("loon.genAssetsDirectory", ""));
			if (!dirName.isEmpty()) {
				File dir = new File(dirName);
				dir.mkdirs();
				copyClasspathAssets(dir);
				createFSDescriptor(dir);
			} else {
				createFSDescriptor(null);
			}
		} catch (Exception e) {
			CBuilder.println(e.getMessage());
		}
	}

	private void createFSDescriptor(File dir) throws IOException {
		String assetsPath = PathUtils.normalize(context.getProperties().getProperty("loon.assetsPath", ""));
		if (assetsPath.isEmpty()) {
			return;
		}
		String assetsOutPath = PathUtils.normalize(context.getProperties().getProperty("loon.warAssetsDirectory", ""));
		if (dir != null) {
			processFile(assetsOutPath, dir, rootFileDescriptor);
		}
		if (!assetsOutPath.isEmpty()) {
			dir = new File(assetsOutPath);
			processFile(assetsPath, dir, rootFileDescriptor);
		}
		File assetFile = new File(assetsPath);
		if (assetFile.exists()) {
			assetFile.delete();
		}
		CBuilder.println("output assets file : " + assetsPath);
		try (FileOutputStream output = new FileOutputStream(new File(assetsPath))) {
			writeAssets(output, rootFileDescriptor);
		}
		final String assetPath = assetFile.getPath();
		final Path source = Paths.get(assetPath);
		final String webAssetPath = PathUtils.normalizeCombinePaths(assetsOutPath,
				PathUtils.getFullFileName(assetPath));
		final Path target = Paths.get(webAssetPath);
		CBuilder.println("copy assets file " + assetPath + " to " + webAssetPath);
		try {
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
		}
	}

	private void writeAssets(OutputStream output, FileDescriptor root) throws IOException {
		final StringBuilder sbr = new StringBuilder();
		for (FileDescriptor desc : root.getChildFiles()) {
			writeAssetChild(sbr, desc);
		}
		output.write(sbr.toString().getBytes(LSystem.ENCODING));
		output.flush();
	}

	private void writeAssetChild(StringBuilder sbr, FileDescriptor desc) {
		final boolean isDir = desc.isDirectory();
		final String path = desc.getPath();
		sbr.append('i');
		sbr.append(':');
		sbr.append(isDir ? "d" : "b");
		sbr.append(':');
		sbr.append(path);
		sbr.append(':');
		sbr.append(isDir ? 0 : desc.getLength());
		sbr.append(':');
		sbr.append(1);
		sbr.append('\n');
		for (FileDescriptor file : desc.getChildFiles()) {
			writeAssetChild(sbr, file);
		}
	}

	private FileDescriptor processFile(String assetsPath, File file, FileDescriptor desc) {
		desc.setName(file.getName());
		String rootPath = PathUtils.getBaseFileName(assetsPath);
		if (StringUtils.isEmpty(rootPath)) {
			rootPath = "assets";
		}
		String path = file.getAbsolutePath();
		int idx = path.indexOf(rootPath);
		if (idx != -1) {
			path = path.substring(idx + rootPath.length(), path.length());
		}
		if (path.startsWith("\\") || path.startsWith("/")) {
			path = path.substring(1, path.length());
		}
		desc.setPath(path.replace("\\", "/"));
		desc.setDirectory(file.isDirectory());
		desc.setLength(file.length());
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				FileDescriptor childDesc = new FileDescriptor();
				processFile(assetsPath, child, childDesc);
				desc.getChildFiles().add(childDesc);
			}
		}
		return desc;
	}

	private void copyClasspathAssets(File dir) throws IOException {
		Enumeration<URL> resources = context.getClassLoader().getResources("META-INF/loon-teavm/classpath-assets");
		Set<String> resourcesToCopy = new HashSet<String>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			InputStream input = resource.openStream();
			if (input == null) {
				continue;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, LSystem.ENCODING))) {
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					resourcesToCopy.add(line);
				}
			}
		}

		for (String resourceToCopy : resourcesToCopy) {
			File resource = new File(dir, resourceToCopy);
			if (resource.exists()) {
				URL url = context.getClassLoader().getResource(resourceToCopy);
				if (url != null && url.getProtocol().equals("file")) {
					try {
						File sourceFile = new File(url.toURI());
						if (sourceFile.exists() && sourceFile.length() == resource.length()
								&& sourceFile.lastModified() == resource.lastModified()) {
							continue;
						}
					} catch (URISyntaxException e) {
					}
				}
			}
			InputStream input = context.getClassLoader().getResourceAsStream(resourceToCopy);
			if (input == null) {
				continue;
			}
			resource.getParentFile().mkdirs();
			IOUtils.copy(input, new FileOutputStream(resource));
		}
	}

}
