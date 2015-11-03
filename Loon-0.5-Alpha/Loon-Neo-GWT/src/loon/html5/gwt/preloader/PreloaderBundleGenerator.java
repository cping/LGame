/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt.preloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import loon.LSystem;
import loon.html5.gwt.preloader.AssetFilter.AssetType;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class PreloaderBundleGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {
		System.out.println(new File(".").getAbsolutePath());
		String assetPath = getAssetPath(context);
		System.out.println("my assets path : " + assetPath);
		String assetOutputPath = getAssetOutputPath(context);
		if (assetOutputPath == null) {
			assetOutputPath = "war/";
		}
		AssetFilter assetFilter = getAssetFilter(context);

		ResourcesWrapper source = new ResourcesWrapper(assetPath);
		if (!source.exists()) {
			source = new ResourcesWrapper("../" + assetPath);
			if (!source.exists()) {
				source = new ResourcesWrapper(assetPath.substring(
						assetPath.indexOf('/') + 1, assetPath.length()));
				if (!source.exists()) {
					throw new RuntimeException(
							"assets path '"
									+ assetPath
									+ "' does not exist. Check your loon.assetpath property in your GWT project's module gwt.xml file");
				}
			}
		}
		if (!source.isDirectory()) {
			throw new RuntimeException(
					"assets path '"
							+ assetPath
							+ "' is not a directory. Check your loon.assetpath property in your GWT project's module gwt.xml file");
		}
		System.out.println("Copying resources from " + assetPath + " to "
				+ assetOutputPath);
		System.out.println(source.file.getAbsolutePath());
		ResourcesWrapper target = new ResourcesWrapper("assets/");
		System.out.println(target.file.getAbsolutePath());
		if (!target.file.getAbsolutePath().replace("\\", "/")
				.endsWith(assetOutputPath + "assets")) {
			target = new ResourcesWrapper(assetOutputPath + "assets/");
		}
		if (target.exists()) {
			if (!target.deleteDirectory())
				throw new RuntimeException("Couldn't clean target path '"
						+ target + "'");
		}
		ArrayList<Asset> assets = new ArrayList<Asset>();
		copyDirectory(source, target, assetFilter, assets);

		List<String> classpathFiles = getClasspathFiles(context);
		for (String classpathFile : classpathFiles) {
			System.out.println(classpathFile);
			if (assetFilter.accept(classpathFile, false)) {
				try {
					InputStream is = context.getClass().getClassLoader()
							.getResourceAsStream(classpathFile);
					ResourcesWrapper dest = target.child(classpathFile);
					dest.write(is, false);
					assets.add(new Asset(dest, assetFilter.getType(dest.path())));
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		HashMap<String, ArrayList<Asset>> bundles = new HashMap<String, ArrayList<Asset>>();
		for (Asset asset : assets) {
			String bundleName = assetFilter.getBundleName(asset.file.path());
			if (bundleName == null) {
				bundleName = "assets";
			}
			ArrayList<Asset> bundleAssets = bundles.get(bundleName);
			if (bundleAssets == null) {
				bundleAssets = new ArrayList<Asset>();
				bundles.put(bundleName, bundleAssets);
			}
			bundleAssets.add(asset);
		}

		for (Entry<String, ArrayList<Asset>> bundle : bundles.entrySet()) {
			StringBuffer buffer = new StringBuffer();
			for (Asset asset : bundle.getValue()) {
				String path = asset.file.path().replace('\\', '/')
						.replace(assetOutputPath, "")
						.replaceFirst("assets/", "");
				if (path.startsWith("/"))
					path = path.substring(1);
				buffer.append(asset.type.code);
				buffer.append(":");
				buffer.append(path);
				buffer.append(":");
				buffer.append(asset.file.isDirectory() ? 0 : asset.file
						.length());
				buffer.append(":");
				String mimetype = URLConnection
						.guessContentTypeFromName(asset.file.name());
				String ext = LSystem.getExtension(asset.file.name())
						.toLowerCase();
				if (ext.equals("an") || ext.equals("tmx")) {
					buffer.append("text/plain");
				} else {
					buffer.append(mimetype == null ? "application/unknown"
							: mimetype);
				}
				buffer.append("\n");
			}
			target.child(bundle.getKey() + ".txt").writeString(
					buffer.toString(), false);
		}
		return createDummyClass(logger, context);
	}

	private class Asset {
		ResourcesWrapper file;
		AssetType type;

		public Asset(ResourcesWrapper file, AssetType type) {
			this.file = file;
			this.type = type;
		}
	}

	private void copyFile(ResourcesWrapper source, ResourcesWrapper dest,
			AssetFilter filter, ArrayList<Asset> assets) {
		if (!filter.accept(dest.path(), false))
			return;
		try {
			assets.add(new Asset(dest, filter.getType(dest.path())));
			dest.write(source.read(), false);
		} catch (Exception ex) {
			throw new RuntimeException("Error copying source file: " + source
					+ "\n" //
					+ "To destination: " + dest, ex);
		}
	}

	private void copyDirectory(ResourcesWrapper sourceDir,
			ResourcesWrapper destDir, AssetFilter filter,
			ArrayList<Asset> assets) {
		if (!filter.accept(destDir.path(), true))
			return;
		assets.add(new Asset(destDir, AssetType.Directory));
		destDir.mkdirs();
		ResourcesWrapper[] files = sourceDir.list();
		for (int i = 0, n = files.length; i < n; i++) {
			ResourcesWrapper srcFile = files[i];
			ResourcesWrapper destFile = destDir.child(srcFile.name());
			if (srcFile.isDirectory())
				copyDirectory(srcFile, destFile, filter, assets);
			else
				copyFile(srcFile, destFile, filter, assets);
		}
	}

	private AssetFilter getAssetFilter(GeneratorContext context) {
		ConfigurationProperty assetFilterClassProperty = null;
		try {
			assetFilterClassProperty = context.getPropertyOracle()
					.getConfigurationProperty("loon.assetfilterclass");
		} catch (BadPropertyValueException e) {
			return new DefaultAssetFilter();
		}
		if (assetFilterClassProperty.getValues().size() == 0) {
			return new DefaultAssetFilter();
		}
		String assetFilterClass = assetFilterClassProperty.getValues().get(0);
		if (assetFilterClass == null)
			return new DefaultAssetFilter();
		try {
			return (AssetFilter) Class.forName(assetFilterClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Couldn't instantiate custom AssetFilter '"
							+ assetFilterClass
							+ "', make sure the class is public and has a public default constructor",
					e);
		}
	}

	private String getAssetPath(GeneratorContext context) {
		ConfigurationProperty assetPathProperty = null;
		try {
			assetPathProperty = context.getPropertyOracle()
					.getConfigurationProperty("loon.assetpath");
		} catch (BadPropertyValueException e) {
			throw new RuntimeException(
					"No loon.assetpath defined. Add <set-configuration-property name=\"loon.assetpath\" value=\"relative/path/to/assets/\"/> to your GWT projects gwt.xml file");
		}
		if (assetPathProperty.getValues().size() == 0) {
			throw new RuntimeException(
					"No loon.assetpath defined. Add <set-configuration-property name=\"loon.assetpath\" value=\"relative/path/to/assets/\"/> to your GWT projects gwt.xml file");
		}
		String paths = assetPathProperty.getValues().get(0);
		if (paths == null) {
			throw new RuntimeException(
					"No loon.assetpath defined. Add <set-configuration-property name=\"loon.assetpath\" value=\"relative/path/to/assets/\"/> to your GWT projects gwt.xml file");
		} else {
			String[] tokens = paths.split(",");
			for (String token : tokens) {
				System.out.println(token);
				if (new ResourcesWrapper(token).exists()
						|| new ResourcesWrapper("../" + token).exists()) {
					return token;
				}
			}
			throw new RuntimeException(
					"No valid loon.assetpath defined. Fix <set-configuration-property name=\"loon.assetpath\" value=\"relative/path/to/assets/\"/> in your GWT projects gwt.xml file");
		}
	}

	private String getAssetOutputPath(GeneratorContext context) {
		ConfigurationProperty assetPathProperty = null;
		try {
			assetPathProperty = context.getPropertyOracle()
					.getConfigurationProperty("loon.assetoutputpath");
		} catch (BadPropertyValueException e) {
			return null;
		}
		if (assetPathProperty.getValues().size() == 0) {
			return null;
		}
		String paths = assetPathProperty.getValues().get(0);
		if (paths == null) {
			return null;
		} else {
			String[] tokens = paths.split(",");
			String path = null;
			for (String token : tokens) {
				if (new ResourcesWrapper(token).exists()
						|| new ResourcesWrapper(token).mkdirs()) {
					path = token;
				}
			}
			if (path != null && !path.endsWith("/")) {
				path += "/";
			}
			return path;
		}
	}

	private List<String> getClasspathFiles(GeneratorContext context) {
		List<String> classpathFiles = new ArrayList<String>();
		try {
			ConfigurationProperty prop = context.getPropertyOracle()
					.getConfigurationProperty("loon.files.classpath");
			for (String value : prop.getValues()) {
				classpathFiles.add(value);
			}
		} catch (BadPropertyValueException e) {
		}
		return classpathFiles;
	}

	private String createDummyClass(TreeLogger logger, GeneratorContext context) {
		String packageName = "loon.html5.gwt.preloader";
		String className = "PreloaderBundleImpl";
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(
				packageName, className);
		composer.addImplementedInterface(packageName + ".PreloaderBundle");
		PrintWriter printWriter = context.tryCreate(logger, packageName,
				className);
		if (printWriter == null) {
			return packageName + "." + className;
		}
		SourceWriter sourceWriter = composer.createSourceWriter(context,
				printWriter);
		sourceWriter.commit(logger);
		return packageName + "." + className;
	}
}
