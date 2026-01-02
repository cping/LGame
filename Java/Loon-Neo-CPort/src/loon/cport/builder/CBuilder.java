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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

import loon.LSystem;
import loon.cport.assets.AssetFile;
import loon.cport.builder.CCodeFix.FileFix;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class CBuilder {

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

	private static String cappName = "capp";
	private static CBuildConfiguration configuration;
	private static File setTargetDirectory;
	private static TeaClassLoader classLoader;
	private static ArrayList<URL> acceptedURL;

	public static void config(CBuildConfiguration configuration) {
		CBuilder.configuration = configuration;
		acceptedURL = new ArrayList<URL>();
		String cappDirectory = configuration.cappPath;

		configClasspath(configuration, acceptedURL);

		CBuilder.print("");
		CBuilder.print("targetDirectory: " + cappDirectory);
		CBuilder.print("");

		URL[] classPaths = acceptedURL.toArray(new URL[acceptedURL.size()]);
		ClassLoader loader = null;
		try {
			loader = CBuilder.class.getClassLoader();
		} catch (Exception e) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		classLoader = new TeaClassLoader(classPaths, loader);

		setTargetDirectory = new File(cappDirectory + File.separator + cappName);

		if (configuration.baseApp == null) {
			configuration.baseApp = new DefaultCPortApp();
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
				CBuilder.begin("Compiler problems");

				DefaultProblemTextConsumer p = new DefaultProblemTextConsumer();

				for (int i = 0; i < problems.size(); i++) {
					Problem problem = problems.get(i);

					if (i > 0) {
						CBuilder.print("");
						CBuilder.print("----");
						CBuilder.print("");
					}
					CBuilder.print(problem.getSeverity().toString() + "[" + i + "]");
					final StringBuilder sbr = new StringBuilder();
					TeaVMProblemRenderer.renderCallStack(tool.getDependencyInfo().getCallGraph(), problem.getLocation(),
							sbr);
					String locationString = sbr.toString();
					locationString.lines().forEach(CBuilder::print);
					p.clear();
					problem.render(p);
					String text = p.getText();
					CBuilder.print("Text: " + text);
				}
				CBuilder.end();
			} else {
				isSuccess = true;
				
				CBuilder.begin("FIX SOURCE CODE");
				
				final CCodeFix fixCFile = new CCodeFix();

				String cappDirectory = configuration.cappPath;

				AssetFile distFolder = new AssetFile(cappDirectory);
				AssetFile cappFolder = distFolder.child(cappName);

				final TArray<FileFix> fixs = fixCFile.getFixList();
				for (FileFix fix : fixs) {
					AssetFile fixFile = cappFolder.child(fix.fileName);
					if (fixFile.exists()) {
						CBuilder.println("fix code in source : " + fix.fileName);
						final StringBuilder content = new StringBuilder();
						try {
							try (BufferedReader reader = new BufferedReader(
									new FileReader(fixFile.file, StandardCharsets.UTF_8))) {
								String line;
								while ((line = reader.readLine()) != null) {
									ObjectMap<String, String> fixContext = fix.fixContexts;
									Entries<String, String> list = fixContext.entries();
									for (; list.hasNext();) {
										Entry<String, String> replaceText = list.next();
										final String key = replaceText.key;
										if (line.indexOf(key) != -1) {
											content.append(StringUtils.replace(line, key, replaceText.value));
										} else {
											content.append(line);
										}
										content.append(LSystem.NL);
									}
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (content.length() > 0) {
							fixFile.writeString(content.toString(), false);
						}
					}
					CBuilder.println("*********************************");
				}

				CBuilder.begin("Build complete in " + seconds + " seconds. Total Classes: " + classes.size());
			}

			if (logClassNames) {
				Stream<String> sorted = classes.stream().sorted();
				Iterator<String> iterator = sorted.iterator();
				while (iterator.hasNext()) {
					String clazz = iterator.next();
					CBuilder.print(clazz);
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

	private static void preserveClasses(TeaVMTool tool, CBuildConfiguration configuration, TeaClassLoader classLoader) {
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

	private static void configClasspath(CBuildConfiguration configuration, ArrayList<URL> acceptedURL) {
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

		CBuilder.begin("ACCEPTED CLASSPATH");
		for (int i = 0; i < acceptedURL.size(); i++) {
			CBuilder.print(i + " true: " + acceptedURL.get(i).getPath());
		}
	}

	private static void configTool(TeaVMTool tool) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File setCacheDirectory = new File(tmpdir + File.separator + "TeaVMCache");

		if (configuration.targetType == TargetType.CPort) {
			tool.setTargetFileName(configuration.targetFileName + ".c");
			tool.setTargetType(TeaVMTargetType.C);
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
					CBuilder.begin("DEPENDENCY_ANALYSIS");
				} else if (teaVMPhase == TeaVMPhase.COMPILING) {
					CBuilder.println("");
					CBuilder.begin("COMPILING");
				}
				phase = teaVMPhase;
				return TeaVMProgressFeedback.CONTINUE;
			}

			@Override
			public TeaVMProgressFeedback progressReached(int i) {
				if (phase == TeaVMPhase.DEPENDENCY_ANALYSIS) {
					CBuilder.println("|");
				}
				return TeaVMProgressFeedback.CONTINUE;
			}
		});
		preserveClasses(tool, configuration, classLoader);
	}

	public static void configAssets() {
		CBuilder.begin("COPYING ASSETS");

		String cappDirectory = configuration.cappPath;

		AssetFile distFolder = new AssetFile(cappDirectory);
		AssetFile cappFolder = distFolder.child(cappName);
		AssetFile assetsFolder = cappFolder.child("assets");
		AssetFile scriptsFolder = cappFolder.child("scripts");
		AssetFile assetFile = assetsFolder.child("assets.txt");

		AssetFilter filter = configuration.assetFilter;

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

		List<String> resources = CProperties.getResources(acceptedURL);

		List<String> scripts = new ArrayList<String>();

		for (int i = 0; i < resources.size(); i++) {
			String asset = resources.get(i);
			if (asset.endsWith(".c") || asset.endsWith(".h")) {
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

		CBuilder.print("");
	}

}
