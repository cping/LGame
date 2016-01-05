package loon.build.project.gwt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loon.build.Project;

public class GwtCompilerOption extends GwtOption {

	private LogLevel logLevel;

	private File workDir;

	private File war;

	private File deploy;

	private File gen;

	private File extra;

	private File saveSourceOutput;

	private File missingDepsFile;

	private Boolean strict;

	private Boolean compileReport;

	private Boolean draftCompile;

	private Boolean checkAssertions;

	private CodeStyle style;

	private Integer optimize;

	private Boolean overlappingSourceWarnings;

	private Boolean saveSource;

	private Boolean failOnError;

	private String sourceLevel;

	private Integer localWorkers;

	private Integer localWorkersMem = 2048;

	private Boolean incremental;

	private NameDisplayMode methodNameDisplayMode;

	private Boolean enforceStrictResources;

	private Boolean checkCasts;

	private Boolean classMetadata;

	private Boolean closureCompiler;

	private JSMode jsInteropMode;

	private Boolean generateJsInteropExports;

	private List<String> extraArgs = new ArrayList<String>();

	public void init(Project project) {
		final File buildDir = new File(project.getBuildDir());
		this.war = new File(buildDir, "out");
		this.workDir = new File(buildDir, "work");
		this.gen = new File(buildDir, "extra/gen");
		this.deploy = new File(buildDir, "extra/deploy");
		this.extra = new File(buildDir, "extra");
		this.saveSourceOutput = new File(buildDir, "extra/source");
		this.missingDepsFile = new File(buildDir, "extra/missingDepsFile");
	}

	public Boolean getStrict() {
		return strict;
	}

	public void setStrict(Boolean strict) {
		this.strict = strict;
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = new File(workDir);
	}

	public Boolean getCompileReport() {
		return compileReport;
	}

	public void setCompileReport(Boolean compileReport) {
		this.compileReport = compileReport;
	}

	public Boolean getDraftCompile() {
		return draftCompile;
	}

	public void setDraftCompile(Boolean draftCompile) {
		this.draftCompile = draftCompile;
	}

	public Boolean getCheckAssertions() {
		return checkAssertions;
	}

	public void setCheckAssertions(Boolean checkAssertions) {
		this.checkAssertions = checkAssertions;
	}

	public File getGen() {
		return gen;
	}

	public void setGen(File gen) {
		this.gen = gen;
	}

	public void setGen(String gen) {
		this.gen = new File(gen);
	}

	public File getMissingDepsFile() {
		return missingDepsFile;
	}

	public void setMissingDepsFile(String missingDepsFile) {
		this.missingDepsFile = new File(missingDepsFile);
	}

	public Integer getOptimize() {
		return optimize;
	}

	public void setOptimize(Integer optimize) {
		this.optimize = optimize;
	}

	public Boolean getOverlappingSourceWarnings() {
		return overlappingSourceWarnings;
	}

	public void setOverlappingSourceWarnings(Boolean overlappingSourceWarnings) {
		this.overlappingSourceWarnings = overlappingSourceWarnings;
	}

	public Boolean getSaveSource() {
		return saveSource;
	}

	public void setSaveSource(Boolean saveSource) {
		this.saveSource = saveSource;
	}

	public CodeStyle getStyle() {
		return style;
	}

	public void setStyle(CodeStyle style) {
		this.style = style;
	}

	public void setStyle(String style) {
		this.style = CodeStyle.valueOf(style);
	}

	public Boolean getFailOnError() {
		return failOnError;
	}

	public void setFailOnError(Boolean failOnError) {
		this.failOnError = failOnError;
	}

	public String getSourceLevel() {
		return sourceLevel;
	}

	public void setSourceLevel(String sourceLevel) {
		this.sourceLevel = sourceLevel;
	}

	public Integer getLocalWorkers() {
		return localWorkers;
	}

	public void setLocalWorkers(Integer localWorkers) {
		this.localWorkers = localWorkers;
	}

	public Integer getLocalWorkersMem() {
		return localWorkersMem;
	}

	public void setLocalWorkersMem(Integer localWorkersMem) {
		this.localWorkersMem = localWorkersMem;
	}

	public Boolean getIncremental() {
		return incremental;
	}

	public void setIncremental(Boolean incremental) {
		this.incremental = incremental;
	}

	public File getWar() {
		return war;
	}

	public void setWar(String war) {
		this.war = new File(war);
	}

	public File getDeploy() {
		return deploy;
	}

	public void setDeploy(String deploy) {
		this.deploy = new File(deploy);
	}

	public File getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = new File(extra);
	}

	public File getSaveSourceOutput() {
		return saveSourceOutput;
	}

	public void setSaveSourceOutput(String saveSourceOutput) {
		this.saveSourceOutput = new File(saveSourceOutput);
	}

	public NameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	public void setMethodNameDisplayMode(NameDisplayMode methodNameDisplayMode) {
		this.methodNameDisplayMode = methodNameDisplayMode;
	}

	public void setMethodNameDisplayMode(String methodNameDisplayMode) {
		this.methodNameDisplayMode = NameDisplayMode
				.valueOf(methodNameDisplayMode);
	}

	public Boolean getEnforceStrictResources() {
		return enforceStrictResources;
	}

	public void setEnforceStrictResources(Boolean enforceStrictResources) {
		this.enforceStrictResources = enforceStrictResources;
	}

	public Boolean getCheckCasts() {
		return checkCasts;
	}

	public void setCheckCasts(Boolean checkCasts) {
		this.checkCasts = checkCasts;
	}

	public Boolean getClassMetadata() {
		return classMetadata;
	}

	public void setClassMetadata(Boolean classMetadata) {
		this.classMetadata = classMetadata;
	}

	public Boolean getClosureCompiler() {
		return closureCompiler;
	}

	public void setClosureCompiler(Boolean closureCompiler) {
		this.closureCompiler = closureCompiler;
	}

	public JSMode getJsInteropMode() {
		return jsInteropMode;
	}

	public void setJsInteropMode(String jsInteropMode) {
		this.jsInteropMode = JSMode.valueOf(jsInteropMode);
	}

	public void setJsInteropMode(JSMode jsInteropMode) {
		this.jsInteropMode = jsInteropMode;
	}

	public Boolean getGenerateJsInteropExports() {
		return generateJsInteropExports;
	}

	public void setGenerateJsInteropExports(Boolean generateJsInteropExports) {
		this.generateJsInteropExports = generateJsInteropExports;
	}

	public List<String> getExtraArgs() {
		return extraArgs;
	}

	public void setExtraArgs(String... extraArgs) {
		this.extraArgs.addAll(Arrays.asList(extraArgs));
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = LogLevel.valueOf(logLevel);
	}

}
