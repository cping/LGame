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

import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

import loon.cport.assets.AssetFile;

public class CInitialize {

	public static void create(Class<?> clazz, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level)
			throws IOException {
		create(clazz, obfuscated, debug, level, "build/dist", "capp");
	}

	public static void create(Class<?> clazz, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level,
			String appName) throws IOException {
		create(clazz, obfuscated, debug, level, "build/dist", appName);
	}

	public static void create(Class<?> clazz, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level,
			String buildPath, String appName) throws IOException {
		create(clazz, obfuscated, debug, level, TargetType.CPort, null, null, null, buildPath, appName);
	}

	public static void create(Class<?> clazz, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level,
			TargetType target, String buildPath, String appName) throws IOException {
		create(clazz, obfuscated, debug, level, target, null, null, null, buildPath, appName);
	}

	public static void create(Class<?> clazz, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level,
			TargetType target, String[] assetPath, String[] sourcePath, String[] reflects, String buildPath,
			String appName) throws IOException {
		create((clazz == null ? "none" : clazz.getName()), obfuscated, debug, level, target, assetPath, sourcePath,
				reflects, buildPath, appName);
	}

	public static void create(String mainClassName, boolean obfuscated, boolean debug, TeaVMOptimizationLevel level,
			TargetType target, String[] assetPath, String[] sourcePath, String[] reflects, String buildPath,
			String appName) throws IOException {
		if (reflects != null) {
			for (int i = 0; i < reflects.length; i++) {
				final String refPackName = reflects[i];
				TeaReflectionSupplier.addReflectionClass(refPackName);
			}
		}
		if (obfuscated) {
			debug = false;
		}
		CBuildConfiguration cbuildConfiguration = new CBuildConfiguration();
		cbuildConfiguration.cappName = appName;
		cbuildConfiguration.assetsPath.add(new AssetFile("../assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("../src/main/java/loon/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("../src/loon/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("src/main/webapp/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("src/assets"));
		cbuildConfiguration.assetsPath.add(new AssetFile("assets"));
		if (assetPath != null) {
			for (int i = 0; i < assetPath.length; i++) {
				final String pathName = assetPath[i];
				cbuildConfiguration.assetsPath.add(new AssetFile(pathName));
			}
		}
		cbuildConfiguration.shouldGenerateAssetFile = true;
		cbuildConfiguration.cappPath = new File(buildPath).getCanonicalPath();
		cbuildConfiguration.targetType = target;
		CBuilder.config(cbuildConfiguration);

		TeaVMTool tool = new TeaVMTool();
		tool.setObfuscated(obfuscated);
		tool.setOptimizationLevel(level);
		tool.setMainClass(mainClassName);
		tool.setDebugInformationGenerated(debug);
		tool.setSourceMapsFileGenerated(debug);

		if (sourcePath != null) {
			tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
			for (int i = 0; i < sourcePath.length; i++) {
				final String pathName = sourcePath[i];
				File sourceFile = new File(pathName);
				tool.addSourceFileProvider(new DirectorySourceFileProvider(sourceFile));
			}
		}
		CBuilder.build(tool);
	}

}
