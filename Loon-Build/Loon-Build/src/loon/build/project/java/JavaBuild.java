package loon.build.project.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loon.build.packer.Packer;
import loon.build.packer.ZipFileMake;
import loon.build.sys.JDK;
import loon.build.sys.JVM;
import loon.build.sys.LSystem;
import loon.build.sys.Log;
import loon.build.tools.ArrayMap;
import loon.build.tools.FileUtils;
import loon.build.tools.ParseData;
import loon.build.tools.StringUtils;

public class JavaBuild {

	public static String sourceFileName = "src";

	private Set<String> built, builds;
	private Projects prjList;
	private int turnNo;
	private ProjectName project;
	private ArrayMap params;
	private String outputDir;

	public JavaBuild(ArrayMap params) {
		this.params = params;
	}

	public JavaBuild build(Projects prjList, String outputDir) throws Exception {
		this.outputDir = outputDir;
		this.prjList = prjList;
		built = new HashSet<String>();
		builds = new HashSet<String>();
		for (ProjectItem p : prjList.maps.values()) {
			builds.add(p.name);
		}
		log("total " + builds);
		checkDeps();
		while (builds.size() > 0) {
			Set<String> turn = new HashSet<String>();
			for (String n : builds) {
				ProjectItem prj = prjList.maps.get(n);
				if (prj.depends == null) {
					turn.add(n);
				} else {
					if (isDepBuilt(prj.depends)) {
						turn.add(n);
					}
				}
			}
			if (turn.size() == 0) {
				throw new RuntimeException("to build " + builds + " but they depend on each other");
			}
			buildTurn(turn);
			built.addAll(turn);
			builds.removeAll(turn);
		}
		return this;
	}

	private void buildTurn(Set<String> turn) throws Exception {
		turnNo++;
		log("Turn " + turnNo + " start " + turn.size() + " projects " + turn);
		if (!prjList.multithread) {
			for (String n : turn) {
				ProjectItem prj = prjList.maps.get(n);
				buildProject(prj);
			}
		} else {
			final HashMap<Long, Boolean> success = new HashMap<Long, Boolean>();
			Thread[] ts = new Thread[turn.size()];
			int i = 0;
			for (String n : turn) {
				final ProjectItem prj = prjList.maps.get(n);
				Thread t = new Thread() {
					public void run() {
						try {
							buildProject(prj);
							success.put(getId(), true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
				ts[i++] = t;
			}
			for (Thread t : ts) {
				t.join();
				Boolean succ = success.get(t.getId());
				if (succ == null)
					succ = false;
				if (!succ) {
					log("build fail:" + t.getId());
					throw new RuntimeException("build failed");
				}
			}
		}
		log("Task " + turnNo + " finish");
	}

	private File srcDirFile;

	@SuppressWarnings("unchecked")
	public void buildProject(ProjectItem prj) throws Exception {
		String prjName = prj.name;
		log(prjName + ":build start");
		File path = addPath(prjList.sourceDir, prj.dir);
		project = new ProjectName(prjList);
		project.setName(prjName);
		JavaCompile javac = new JavaCompile();
		javac.setProject(project);
		javac.setExecutable(prjList.javaHome + (JDK.isWindows() ? "/bin/javac.exe" : "/bin/javac"));
		javac.setTarget(getParam("target", "1.8"));
		javac.setSource(getParam("source", "1.8"));
		javac.setEncoding(getParam("encoding", LSystem.ENCODING));
		javac.setDebug(new Boolean(getParam("debug", "false")));
		srcDirFile = new File(path.getCanonicalPath(), "/" + sourceFileName);
		if (!srcDirFile.exists()) {
			throw new RuntimeException("src dir not found:" + srcDirFile.getCanonicalPath());
		}
		javac.setSrcdir(getPath(path.getCanonicalPath() + "/" + sourceFileName));
		File buildDir = new File(getPath(path.getCanonicalPath() + "/build"));
		buildDir.mkdirs();
		String mainCalss = prj.mainClass;
		if (mainCalss != null) {
			// 如果使用loon提供的内部类引用器
			if ("loon.JarInternal".equals(mainCalss)) {
				File jarInternalFile = new File(srcDirFile, "/loon/JarInternal.java");
				if (!jarInternalFile.exists()) {
					byte[] buffer = Packer.getResourceZipFile("assets/loon.zip", "loon/JarInternal.java");
					if (buffer != null) {
						FileUtils.write(jarInternalFile, buffer);
					}
				}
			}
		}

		String buildOutputPath = getPath(buildDir.getCanonicalPath());
		javac.setDestdir(buildOutputPath);
		JavaPath cp = new JavaPath(project);
		if (prj.classpaths != null) {
			for (Object o : prj.classpaths) {
				String classPath = getPath(o.toString());
				File classFile = new File(classPath);
				if (!classFile.exists()) {
					classFile = addPath(prjList.sourceDir, classPath);
				}
				cp.add(classFile.getAbsolutePath());
			}
		}

		if (prj.cp != null) {
			for (Object o : prj.cp) {
				File f1 = addPath(prjList.sourceDir, o.toString());
				if (f1.isDirectory()) {
					File[] fs = f1.listFiles();
					for (File f : fs) {
						if (f.getName().endsWith(".jar")) {
							cp.add(getPath(f.getCanonicalPath()));
						}
					}
				} else {
					cp.add(addPath(prjList.sourceDir, o.toString()).getCanonicalPath());
				}
			}
		}
		if (prj.depends != null) {
			for (Object o : prj.depends) {
				ProjectItem p1 = prjList.maps.get(o.toString());
				String po = addPath(prjList.sourceDir, p1.dir).getCanonicalPath() + "/completed/" + p1.name + ".jar";
				cp.add(po);
			}
		}

		javac.setClasspath(cp);
		int cnt = javac.execute();
		if (cnt < 0) {
			throw new RuntimeException("javac failed with code:" + cnt);
		}
		if (cnt == 0) {
			log(prjName + "::the project no more to compile");
		} else {
			log(prjName + "::the compile files count(" + cnt + ")");
		}

		if (prj.jars != null) {
			for (Object o : prj.jars) {
				File f1 = addPath(prjList.sourceDir, o.toString());
				if (f1.exists()) {
					FileUtils.copyDir(getPath(f1.getAbsolutePath()), getPath(buildDir.getAbsolutePath()));
				}
			}
		}

		JarFileCopy copy = new JarFileCopy();
		copy.setProject(project);
		copy.setTodir(buildDir);
		JavaFileSet fileSet = new JavaFileSet();
		fileSet.addFile(new File(path.getCanonicalPath() + "/" + sourceFileName));
		fileSet.setExcludesEndsWith(".java");
		fileSet.ignoreEclipsePrjFile = true;
		copy.addFileset(fileSet);
		int cnt2 = copy.execute();

		log(String.format("%s:copy %d resources", prjName, cnt2));

		JarConfig jar = new JarConfig();
		jar.setProject(project);
		File jarFile = new File(getPath(path.getCanonicalPath() + "/completed/" + prjName + ".jar"));
		jarFile.getParentFile().mkdirs();
		jar.setDestFile(jarFile);
		jar.setBasedir(buildDir);
		if (prj.manifests != null) {
			for (Object o : prj.manifests) {
				String result = o.toString().trim();
				int idx = result.indexOf(':');
				if (idx != -1 && StringUtils.charCount(result, ':') == 1) {
					String key = result.substring(0, idx);
					String value = result.substring(idx + 1, result.length());
					jar.addManifest(key, value);
				}
			}
		}
		if (mainCalss != null) {
			// 主函数
			jar.addManifest("Main-Class", mainCalss);
		}
		jar.execute();
		copyTo(prj, outputDir);

		if (prj.run != null) {
			JavaCall run = new JavaCall();
			cp.add(jarFile.getCanonicalPath());
			run.setClasspath(cp);
			run.setProject(project);
			for (Object o : prj.run) {

				List<Object> row = (List<Object>) o;
				run.setClassname((String) row.get(0));
				for (Object o1 : (List<Object>) row.get(2)) {
					run.addArg(o1.toString());
				}
				run.execute();
			}
		}

		if (prj.outSrc != null && StringUtils.toBoolean(prj.outSrc)) {
			// 打包源码
			File file = new File(getPath(path.getCanonicalPath()) + "/" + sourceFileName);
			if (file.exists()) {
				ZipFileMake make = new ZipFileMake();
				File output = addPath(prjList.sourceDir, outputDir);
				String sourceFilePath = output.getCanonicalPath();
				make.zipFolder(file.getCanonicalPath(), sourceFilePath + "/" + prjName + "-source.jar");
			}
		}
	}

	private String getParam(String key, String value) {
		Object o = params.get(key);
		if (params == null || o == null)
			return value;
		return o.toString();
	}

	private boolean isDepBuilt(List<Object> depends) {
		for (Object pre : depends) {
			if (!built.contains(pre.toString()))
				return false;
		}
		return true;
	}

	private void checkDeps() {
		for (ProjectItem p : prjList.maps.values()) {
			if (p.depends != null) {
				for (Object n : p.depends) {
					if (!builds.contains(n.toString())) {
						throw new RuntimeException("[" + p.name + "] need [" + n + "] which is not exists");
					}
				}
			}
		}
	}

	public void clean(Projects prjList) throws IOException {
		for (ProjectItem prj : prjList.maps.values()) {
			String path = addPath(prjList.sourceDir, prj.dir).getCanonicalPath();
			deleteDirectory(new File(path + "/completed"), 0);
			deleteDirectory(new File(path + "/build"), 0);
		}
	}

	public void copyTo(ProjectItem prj, String dest) throws IOException {
		File outputDir = addPath(prjList.sourceDir, dest);
		outputDir.mkdirs();
		String path = addPath(prjList.sourceDir, prj.dir).getCanonicalPath();
		JarFileCopy copy = new JarFileCopy();
		copy.setProject(project);
		copy.setFile(new File(path + "/completed/" + prj.name + ".jar"));
		copy.setTodir(outputDir);
		int cnt = copy.execute();
		if (cnt > 0) {
			log(prj.name + ":jar copied to " + dest);
		}
		if (prj.cp != null) {
			for (Object o : prj.cp) {
				File f = addPath(prjList.sourceDir, o.toString());
				if (f.isDirectory()) {
					for (File f1 : f.listFiles()) {
						if (f1.getName().endsWith(".jar")) {
							copy.setFile(f1);
							copy.execute();
						}
					}
				} else {
					copy.setFile(f);
					copy.execute();
				}
			}
		}
	}

	static public boolean deleteDirectory(File path, int lv) throws IOException {
		if (lv == 0)
			log("delete " + path.getCanonicalPath());
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i], lv + 1);
				} else {
					files[i].delete();
				}
			}
		}
		if (lv == 0)
			log("delete " + path.getCanonicalPath() + " " + path.delete());
		return path.delete();
	}

	public static String join(String delima, List<Object> list) {
		if (list == null || list.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			sb.append(delima).append(list.get(i));
		}
		return sb.toString();
	}

	public static File addPath(String sourceDir, String dir) {
		File path;
		if (dir.startsWith("/") || dir.indexOf(":") > 0) {
			path = new File(getPath(dir));
		} else {
			path = new File(getPath(sourceDir), getPath(dir));
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	static ArrayMap makeDefaultConfig(String[] args) throws Exception {
		File dir = args.length > 0 ? new File(getPath(args[0])).getAbsoluteFile().getParentFile() : new File(".");
		log("Current Dir:" + dir.getCanonicalPath());
		File srcDir = new File(dir, getPath(sourceFileName));
		if (!(srcDir.exists() && srcDir.isDirectory())) {
			log("'src' dir not found, exiting...");
			return null;
		}
		String prjName = getPath(dir.getCanonicalFile().getName());
		log("user default project name:" + prjName);
		ArrayMap maps = new ArrayMap();
		maps.put("sourceDir", ".");
		maps.put("outputDir", ".");
		maps.put("debug", "true");
		maps.put("list", (List<Object>) ParseData.parseAll(String.format("[ [ %s , . ],  ]", getEncodePath(prjName))));
		return maps;
	}

	public static String getEncodePath(String path) {
		if (path == null) {
			return null;
		}
		try {
			return URLEncoder.encode(path, LSystem.ENCODING);
		} catch (UnsupportedEncodingException e) {
			return path;
		}
	}

	public static String getPath(String path) {
		if (path == null) {
			return null;
		}
		try {
			return URLDecoder.decode(path, LSystem.ENCODING);
		} catch (UnsupportedEncodingException e) {
			return path;
		}
	}

	@SuppressWarnings("unchecked")
	public static void load(String[] args) throws Exception {
		ArrayMap params = makeDefaultConfig(args);
		if (args.length == 0) {
			if (params == null)
				return;
		} else {
			String path = args[0];
			if (".".equals(path)) {
				File thisPath = new File("");
				args[0] = getPath(thisPath.getAbsolutePath()) + "\\build.txt";
				args[0] = StringUtils.replace(args[0], "\\", "/");
			}
			String fileContext = readString(new FileInputStream(args[0]), LSystem.ENCODING);
			ArrayMap map = (ArrayMap) ParseData.parseAll(fileContext);
			if (map != null && params != null) {
				params.putAll(map);
			}
		}
		if (params == null) {
			return;
		}

		String pb1 = getPath((String) params.get("sourceDir"));
		String outputDir = getPath((String) params.get("outputDir"));
		String javaHome = getPath(JVM.getJavaHome((String) params.get("javaHome")));
		if (outputDir == null) {
			outputDir = ".";
		}
		if (!StringUtils.isEmpty(javaHome)) {
			log("found latest JDK:" + javaHome);
		} else {
			log("didnot found JDK");
		}
		Object prjList = params.get("list");
		Projects prjs1 = new Projects();
		prjs1.verbose = true;
		prjs1.addPrjs((List<Object>) prjList);
		prjs1.sourceDir = args.length == 0 ? "." : addPath(new File(args[0]).getParent(), pb1).getCanonicalPath();
		prjs1.javaHome = javaHome;

		long t1 = System.currentTimeMillis();
		if (args.length > 1 && args[1].equals("clean")) {
			new JavaBuild(params).clean(prjs1);
		}
		log("outputDir:" + outputDir);
		new JavaBuild(params).build(prjs1, outputDir);
		long t2 = System.currentTimeMillis();
		log(String.format(
				"Compile end. time cost %,d ms, javac(compiled):%,d, copy:%,d(%,d bytes), jar:%,d, java(exec):%,d.",
				t2 - t1, prjs1.totalJavac, prjs1.totalCopy, prjs1.totalCopys, prjs1.totalJar, prjs1.totalJava));
	}

	public static void log(String s) {
		Log.log(s);
	}

	public static String readString(InputStream ins, String enc) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(ins, enc));
		char[] buf = new char[1000];
		int len;
		StringBuffer sb = new StringBuffer();
		while ((len = in.read(buf)) > 0) {
			sb.append(buf, 0, len);
		}
		in.close();
		return sb.toString();
	}

}
