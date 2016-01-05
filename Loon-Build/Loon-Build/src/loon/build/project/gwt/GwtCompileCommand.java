package loon.build.project.gwt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import loon.build.Project;
import loon.build.project.java.JavaCommand;
import loon.build.sys.LSystem;

public class GwtCompileCommand extends JavaCommand {

	private String _classPath;

	public GwtCompileCommand(String javaHome, String classPath) {
		super(javaHome);
		_classPath = classPath;
		setMainClass("com.google.gwt.dev.Compiler");
	}

	public void configure(Project project, GwtCompilerOption compilerOptions,
			ArrayList<File> sources, File war, Collection<String> modules) {

		configureJavaArgs(compilerOptions);
		addJavaArgs("-Dgwt.persistentunitcachedir=" + project.getBuildDir()
				+ "/" + LSystem.PROJECT + "/work/cache");

		for (File sourceDir : sources) {
			addClassPath(sourceDir.getAbsolutePath());
		}

		if (_classPath != null) {
			addClassPath(_classPath);
		}

		addArg("-war", war);
		addArg("-extra", compilerOptions.getExtra());
		addArg("-workDir", compilerOptions.getWorkDir());
		addArg("-gen", compilerOptions.getGen());
		addArg("-deploy", compilerOptions.getDeploy());

		addArg("-logLevel", compilerOptions.getLogLevel());
		addArg("-localWorkers", compilerOptions.getLocalWorkers());
		addArgIf(compilerOptions.getStrict(), "-strict");
		addArgIf(compilerOptions.getFailOnError(), "-failOnError",
				"-nofailOnError");
		addArg("-sourceLevel", compilerOptions.getSourceLevel());
		addArgIf(compilerOptions.getDraftCompile(), "-draftCompile",
				"-nodraftCompile");
		addArg("-optimize", compilerOptions.getOptimize());
		addArg("-style", compilerOptions.getStyle());
		addArgIf(compilerOptions.getCompileReport(), "-compileReport",
				"-nocompileReport");
		addArgIf(compilerOptions.getIncremental(), "-incremental");
		addArgIf(compilerOptions.getCheckAssertions(), "-checkAssertions",
				"-nocheckAssertions");
		addArgIf(compilerOptions.getCheckCasts(), "-XcheckCasts",
				"-XnocheckCasts");
		addArgIf(compilerOptions.getEnforceStrictResources(),
				"-XenforceStrictResources", "-XnoenforceStrictResources");
		addArgIf(compilerOptions.getClassMetadata(), "-XclassMetadata",
				"-XnoclassMetadata");

		addArgIf(compilerOptions.getOverlappingSourceWarnings(),
				"-overlappingSourceWarnings", "-nooverlappingSourceWarnings");
		addArgIf(compilerOptions.getSaveSource(), "-saveSource",
				"-nosaveSource");
		addArg("-XmethodNameDisplayMode",
				compilerOptions.getMethodNameDisplayMode());

		addArgIf(compilerOptions.getClosureCompiler(), "-XclosureCompiler",
				"-XnoclosureCompiler");

		addArg("-XjsInteropMode", compilerOptions.getJsInteropMode());
		addArgIf(compilerOptions.getGenerateJsInteropExports(),
				"-generateJsInteropExports");

		if (compilerOptions.getExtraArgs() != null) {
			for (String arg : compilerOptions.getExtraArgs()) {
				if (arg != null && arg.length() > 0) {
					addArg(arg);
				}
			}
		}

		for (String module : modules) {
			addArg(module);
		}
	}

}
