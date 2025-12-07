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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.teavm.diagnostics.DefaultProblemTextConsumer;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.JarSourceFileProvider;
import org.teavm.vm.TeaVMPhase;
import org.teavm.vm.TeaVMProgressFeedback;
import org.teavm.vm.TeaVMProgressListener;

import loon.teavm.assets.AssetFile;

public class TeaBuilder {

	public static void print(String msg) {
		String text = "| " + msg;
		println(text);
	}

	public static void error(String msg) {
		System.err.print(msg);
	}

	public static void println(String msg) {
		error(msg + "\n");
	}

	public static void begin(String text) {
		String msg = "";
		msg += "*********************************\n";
		msg += "|\n| " + text + "\n|";
		msg += "\n" + "*********************************";

		println(msg);
	}

	public static void end() {
		String msg = "\n*********************************";
		println(msg);
	}

	enum ACCEPT_STATE {
		ACCEPT, NOT_ACCEPT, NO_MATCH
	}

	private static String webappName = "webapp";
	private static TeaBuildConfiguration configuration;
	private static File setTargetDirectory;
	private static TeaClassLoader classLoader;
	private static ArrayList<URL> acceptedURL;

	public static void config(TeaBuildConfiguration configuration) {
		TeaBuilder.configuration = configuration;
		acceptedURL = new ArrayList<URL>();
		String webappDirectory = configuration.webappPath;

		configClasspath(configuration, acceptedURL);

		TeaBuilder.print("");
		TeaBuilder.print("targetDirectory: " + webappDirectory);
		TeaBuilder.print("");

		URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
		classLoader = new TeaClassLoader(classPaths, TeaBuilder.class.getClassLoader());

		setTargetDirectory = new File(webappDirectory + File.separator + webappName);

		if (configuration.webApp == null) {
			configuration.webApp = new DefaultWebApp();
		}
		configAssets();
	}

	public static boolean build(TeaVMTool tool) {
		return build(tool, false);
	}

	public static boolean build(TeaVMTool tool, boolean logClassNames) {
		boolean isSuccess = false;
		try {
			configTool(tool);

			long timeStart = new Date().getTime();
			tool.generate();
			long timeEnd = new Date().getTime();
			float seconds = (timeEnd - timeStart) / 1000f;
			ProblemProvider problemProvider = tool.getProblemProvider();
			Collection<String> classes = tool.getClasses();
			List<Problem> problems = problemProvider.getProblems();
			if (problems.size() > 0) {
				TeaBuilder.begin("Compiler problems");

				DefaultProblemTextConsumer p = new DefaultProblemTextConsumer();

				for (int i = 0; i < problems.size(); i++) {
					Problem problem = problems.get(i);
			
					if (i > 0) {
						TeaBuilder.print("");
						TeaBuilder.print("----");
						TeaBuilder.print("");
					}
					TeaBuilder.print(problem.getSeverity().toString() + "[" + i + "]");
					final StringBuilder sbr = new StringBuilder();
					TeaVMProblemRenderer.renderCallStack(tool.getDependencyInfo().getCallGraph(), problem.getLocation(),
							sbr);
					String locationString = sbr.toString();
					locationString.lines().forEach(TeaBuilder::print);
					p.clear();
					problem.render(p);
					String text = p.getText();
					TeaBuilder.print("Text: " + text);
				}
				TeaBuilder.end();
			} else {
				isSuccess = true;
				TeaBuilder.begin("Build complete in " + seconds + " seconds. Total Classes: " + classes.size());
			}

			if (logClassNames) {
				Stream<String> sorted = classes.stream().sorted();
				Iterator<String> iterator = sorted.iterator();
				while (iterator.hasNext()) {
					String clazz = iterator.next();
					TeaBuilder.print(clazz);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		if (!isSuccess) {
			throw new RuntimeException("Build Failed");
		}
		return isSuccess;
	}

	private static void preserveClasses(TeaVMTool tool, TeaBuildConfiguration configuration,
			TeaClassLoader classLoader) {
		List<String> classesToPreserve = tool.getClassesToPreserve();
		ArrayList<String> configClassesToPreserve = configuration.classesToPreserve;
		List<String> reflectionClasses = TeaReflectionSupplier.getReflectionClasses();
		configClassesToPreserve.addAll(reflectionClasses);
		ArrayList<String> preserveClasses = classLoader.getAllClasses(configClassesToPreserve);
		classesToPreserve.addAll(preserveClasses);
	}

	private static void sortAcceptedClassPath(ArrayList<URL> acceptedURL) {
		makeClassPathFirst(acceptedURL, "loon");
	}

	private static void makeClassPathFirst(ArrayList<URL> acceptedURL, String module) {
		for (int i = 0; i < acceptedURL.size(); i++) {
			URL url = acceptedURL.get(i);
			String string = url.toString();
			if (string.contains(module)) {
				acceptedURL.remove(i);
				acceptedURL.add(0, url);
				break;
			}
		}
	}

	private static void automaticReflection(ArrayList<URL> acceptedURL) {
		for (URL classPath : acceptedURL) {
			try {
				ZipInputStream zip = new ZipInputStream(classPath.openStream());
				for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
					if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
						String className = entry.getName().replace('/', '.');
						String name = className.substring(0, className.length() - ".class".length());
						boolean add = false;
						if (configuration.reflectionListener != null) {
							add = configuration.reflectionListener.shouldEnableReflection(name);
						}
						if (add) {
							TeaReflectionSupplier.addReflectionClass(name);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void configClasspath(TeaBuildConfiguration configuration, ArrayList<URL> acceptedURL) {
		String pathSeparator = System.getProperty("path.separator");
		String[] classPathEntries = System.getProperty("java.class.path").split(pathSeparator);

		for (String path : classPathEntries) {
			File file = new File(path);
			if (file.isDirectory() && !path.endsWith(File.separator)) {
				path += File.separator;
			}
			try {
				acceptedURL.add(new File(path).toURI().toURL());
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		acceptedURL.addAll(configuration.additionalClasspath);

		sortAcceptedClassPath(acceptedURL);

		automaticReflection(acceptedURL);

		TeaBuilder.begin("ACCEPTED CLASSPATH");
		for (int i = 0; i < acceptedURL.size(); i++) {
			TeaBuilder.print(i + " true: " + acceptedURL.get(i).getPath());
		}
	}

	private static void configTool(TeaVMTool tool) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");

		if (configuration.targetType == TargetType.WebAssembly) {
			tool.setTargetFileName(configuration.targetFileName + ".wasm");
			tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);
		} else {
			tool.setTargetFileName(configuration.targetFileName + ".js");
			tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
		}

		for (int i = 0; i < acceptedURL.size(); i++) {
			URL url = acceptedURL.get(i);
			try {
				URI uri = url.toURI();

				String path = uri.getPath();
				File file = new File(uri);
				if (file.isFile() && path.endsWith("-sources.jar")) {
					tool.addSourceFileProvider(new JarSourceFileProvider(file));
				}
			} catch (URISyntaxException e) {
			}
		}

		tool.setClassLoader(classLoader);
		tool.setTargetDirectory(setTargetDirectory);
		tool.setCacheDirectory(setCacheDirectory);
		tool.setProgressListener(new TeaVMProgressListener() {
			TeaVMPhase phase = null;

			@Override
			public TeaVMProgressFeedback phaseStarted(TeaVMPhase teaVMPhase, int i) {
				if (teaVMPhase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
					TeaBuilder.begin("DEPENDENCY_ANALYSIS");
				} else if (teaVMPhase == TeaVMPhase.COMPILING) {
					TeaBuilder.println("");
					TeaBuilder.begin("COMPILING");
				}
				phase = teaVMPhase;
				return TeaVMProgressFeedback.CONTINUE;
			}

			@Override
			public TeaVMProgressFeedback progressReached(int i) {
				if (phase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
					TeaBuilder.println("|");
				}
				return TeaVMProgressFeedback.CONTINUE;
			}
		});
		preserveClasses(tool, configuration, classLoader);
	}

	public static void copyRuntime(File setTargetDirectory) {
		try {
			StringBuilder name = new StringBuilder("wasm-gc-runtime.min");
			setTargetDirectory.mkdirs();
			String resourceName = "org/teavm/backend/wasm/" + name + ".js";
			ClassLoader classLoader = TeaBuilder.class.getClassLoader();
			try (InputStream input = classLoader.getResourceAsStream(resourceName)) {
				Files.copy(input, setTargetDirectory.toPath().resolve(name + ".js"),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void configAssets() {
		TeaBuilder.begin("COPYING ASSETS");
		String webappDirectory = configuration.webappPath;

		AssetFile distFolder = new AssetFile(webappDirectory);
		AssetFile webappFolder = distFolder.child(webappName);
		AssetFile assetsFolder = webappFolder.child("assets");
		AssetFile scriptsFolder = webappFolder.child("scripts");
		AssetFile assetFile = assetsFolder.child("assets.txt");

		AssetFilter filter = configuration.assetFilter;

		boolean shouldUseDefaultHtmlIndex = configuration.useDefaultHtmlIndex;
		if (shouldUseDefaultHtmlIndex) {
			useDefaultHTMLIndexFile(configuration.webApp, webappFolder, assetsFolder);
		}

		boolean generateAssetPaths = configuration.shouldGenerateAssetFile;

		ArrayList<AssetsCopy.Asset> alLAssets = new ArrayList<AssetsCopy.Asset>();
		ArrayList<AssetFile> assetsPaths = configuration.assetsPath;
		for (int i = 0; i < assetsPaths.size(); i++) {
			AssetFile assetFileHandle = assetsPaths.get(i);
			ArrayList<AssetsCopy.Asset> assets = AssetsCopy.copyAssets(assetFileHandle, filter, assetsFolder);
			alLAssets.addAll(assets);
		}
		if (assetFile.exists()) {
			assetFile.delete();
		}
		if (generateAssetPaths) {
			AssetsCopy.generateAssetsFile(alLAssets, assetsFolder, assetFile);
		}

		List<String> resources = TeaProperties.getResources(acceptedURL);

		List<String> scripts = new ArrayList<String>();

		for (int i = 0; i < resources.size(); i++) {
			String asset = resources.get(i);
			if (asset.endsWith(".js") || asset.endsWith(".wasm")) {
				resources.remove(i);
				scripts.add(asset);
				i--;
			}
		}

		ArrayList<String> classPathAssetsFiles = configuration.assetsClasspath;
		ArrayList<AssetsCopy.Asset> classpathAssets = AssetsCopy.copyResources(classLoader, classPathAssetsFiles,
				filter, assetsFolder);

		ArrayList<AssetsCopy.Asset> resourceAssets = AssetsCopy.copyResources(classLoader, resources, filter,
				assetsFolder);

		AssetsCopy.copyScripts(classLoader, scripts, scriptsFolder);

		AssetsCopy.generateAssetsFile(classpathAssets, assetsFolder, assetFile);
		AssetsCopy.generateAssetsFile(resourceAssets, assetsFolder, assetFile);

		TeaBuilder.print("");
	}

	private static void useDefaultHTMLIndexFile(WebBaseApp webApp, AssetFile webappDistFolder, AssetFile assetsFolder) {
		configuration.webApp.setup(classLoader, configuration);
		AssetFile indexHandler = webappDistFolder.child("index.html");
		AssetFile webXML = webappDistFolder.child("WEB-INF").child("web.xml");
		indexHandler.writeString(webApp.mainHtml, false);
		webXML.writeString(webApp.webXML, false);
		AssetsCopy.copyResources(classLoader, webApp.rootAssets, null, assetsFolder);
		if (configuration.targetType == TargetType.WebAssembly) {
			copyRuntime(setTargetDirectory);
		}
	}
}
