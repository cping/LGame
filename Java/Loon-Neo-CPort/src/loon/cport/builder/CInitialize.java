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

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

import loon.cport.assets.AssetFile;

public class CInitialize {

	private final static String DEF_BUILD_PATH = "build/dist";

	private final static String DEF_BUILD_SRC = "src";

	public static void create(Class<?> clazz, TeaVMOptimizationLevel level, String buildPath) throws IOException {
		create(MemoryMode.NORMAL, clazz, level, buildPath);
	}

	public static void create(MemoryMode memory, Class<?> clazz, TeaVMOptimizationLevel level) throws IOException {
		create(memory, clazz, level, DEF_BUILD_PATH, DEF_BUILD_SRC);
	}

	public static void create(MemoryMode memory, Class<?> clazz, TeaVMOptimizationLevel level, String buildPath)
			throws IOException {
		create(memory, clazz, level, buildPath, DEF_BUILD_SRC);
	}

	public static void create(MemoryMode memory, Class<?> clazz, TeaVMOptimizationLevel level, String buildPath,
			String sourceName) throws IOException {
		create(memory, clazz, false, false, true, level, buildPath, sourceName);
	}

	public static void create(MemoryMode memory, Class<?> clazz, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level) throws IOException {
		create(memory, clazz, obfuscated, debug, outputResources, level, DEF_BUILD_PATH, DEF_BUILD_SRC);
	}

	public static void create(MemoryMode memory, Class<?> clazz, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level, String source) throws IOException {
		create(memory, clazz, obfuscated, debug, outputResources, level, DEF_BUILD_PATH, source);
	}

	public static void create(MemoryMode memory, Class<?> clazz, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level, String buildPath, String source) throws IOException {
		create(memory, clazz, obfuscated, debug, outputResources, level, TargetType.CPort, null, null, null, buildPath,
				source);
	}

	public static void create(MemoryMode memory, Class<?> clazz, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level, TargetType target, String buildPath, String source)
			throws IOException {
		create(memory, clazz, obfuscated, debug, outputResources, level, target, null, null, null, buildPath, source);
	}

	public static void create(MemoryMode memory, Class<?> clazz, TeaVMOptimizationLevel level, String[] assetPath,
			String[] sourcePath, String buildPath) throws IOException {
		create(memory, (clazz == null ? "none" : clazz.getName()), false, false, true, level, TargetType.CPort,
				assetPath, sourcePath, null, buildPath, DEF_BUILD_SRC);
	}

	public static void create(MemoryMode memory, Class<?> clazz, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level, TargetType target, String[] assetPath,
			String[] sourcePath, String[] reflects, String buildPath, String source) throws IOException {
		create(memory, (clazz == null ? "none" : clazz.getName()), obfuscated, debug, outputResources, level, target,
				assetPath, sourcePath, reflects, buildPath, source);
	}

	public static void create(MemoryMode memory, String mainClassName, boolean obfuscated, boolean debug,
			boolean outputResources, TeaVMOptimizationLevel level, TargetType target, String[] assetPath,
			String[] sourcePath, String[] reflects, String buildPath, String source) throws IOException {
		CBuildConfiguration cbuildConfiguration = new CBuildConfiguration();
		cbuildConfiguration.cappOutputSource = source;
		cbuildConfiguration.assetsPath.add(new AssetFile("../assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("../src/main/java/loon/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("../src/loon/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("src/main/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("src/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("assets"));
		if (assetPath != null) {
			for (int i = 0; i < assetPath.length; i++) {
				final String pathName = assetPath[i];
				cbuildConfiguration.assetsPath.add(new AssetFile(pathName));
			}
		}
		cbuildConfiguration.shouldGenerateAssetFile = true;
		cbuildConfiguration.outputResources = outputResources;
		cbuildConfiguration.cappPath = new File(buildPath).getCanonicalPath();
		cbuildConfiguration.targetType = target;
		cbuildConfiguration.minHeapSize = memory.minSize * (1 << 20);
		cbuildConfiguration.maxHeapSize = memory.maxSize * (1 << 20);
		create(cbuildConfiguration, mainClassName, obfuscated, debug, outputResources, level, target, assetPath,
				sourcePath, reflects, buildPath, source);
	}

	public static void create(CBuildConfiguration configuration, String mainClassName, boolean obfuscated,
			boolean debug, boolean outputResources, TeaVMOptimizationLevel level, TargetType target, String[] assetPath,
			String[] sourcePath, String[] reflects, String buildPath, String source) throws IOException {
		if (reflects != null) {
			for (int i = 0; i < reflects.length; i++) {
				final String refPackName = reflects[i];
				TeaReflectionSupplier.addReflectionClass(refPackName);
			}
		}
		if (obfuscated) {
			debug = false;
		}
		CBuilder.config(configuration);
		TeaVMTool tool = new TeaVMTool();
		tool.setTargetType(TeaVMTargetType.C);
		tool.setObfuscated(obfuscated);
		final int defMinHeapSize = 4 * (1 << 20);
		if (configuration.minHeapSize < defMinHeapSize) {
			tool.setMinHeapSize(defMinHeapSize);
		} else {
			tool.setMinHeapSize(configuration.minHeapSize);
		}
		final int defMaxHeapSize = 64 * (1 << 20);
		if (configuration.maxHeapSize < defMaxHeapSize) {
			tool.setMaxHeapSize(defMaxHeapSize);
		} else {
			tool.setMaxHeapSize(configuration.maxHeapSize);
		}
		tool.setMinDirectBuffersSize(configuration.minDirectBuffersSize);
		tool.setMaxDirectBuffersSize(configuration.maxDirectBuffersSize);
		tool.setHeapDump(false);
		tool.setOptimizationLevel(level);
		tool.setMainClass(mainClassName);
		tool.setDebugInformationGenerated(debug);
		tool.setSourceMapsFileGenerated(debug);
		Path tempDir = null;
		try {
			if (sourcePath != null) {
				if (configuration.synRemoved) {
					tempDir = Files.createTempDirectory("removesyn_teavm_sources");
					for (String pathName : sourcePath) {
						File sourceFile = new File(pathName);
						if (sourceFile.exists()) {
							Path sourceAssets = sourceFile.toPath();
							if (sourceFile.isDirectory()) {
								AssetsCopy.copy(sourceAssets, tempDir.resolve(sourceFile.getName()));
							} else {
								Files.copy(sourceAssets, tempDir.resolve(sourceFile.getName()),
										StandardCopyOption.REPLACE_EXISTING);
							}
							SynRemoveUtils.removeSynchronizedFromFiles(tempDir.toFile());
						}
					}
					tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
					tool.addSourceFileProvider(new DirectorySourceFileProvider(tempDir.toFile()));
				} else {
					tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
					for (String pathName : sourcePath) {
						File sourceFile = new File(pathName);
						tool.addSourceFileProvider(new DirectorySourceFileProvider(sourceFile));
					}
				}
			}
			CBuilder.build(tool);
		} finally {
			if (tempDir != null) {
				try {
					AssetsCopy.delete(tempDir);
				} catch (IOException e) {
				}
			}
		}
	}
}
