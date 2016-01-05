package loon.build.project.gwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GwtOption {

	private List<String> javaArgs = new ArrayList<String>(20);

	private String maxHeapSize;
	private String minHeapSize;
	private String maxPermSize;
	private boolean debugJava = false;
	private int debugPort = 8000;
	private boolean debugSuspend = false;

	public List<String> getJavaArgs() {
		return javaArgs;
	}

	public void setJavaArgs(String... javaArgs) {
		this.javaArgs.addAll(Arrays.asList(javaArgs));
	}

	public String getMaxHeapSize() {
		return maxHeapSize;
	}

	public void setMaxHeapSize(String maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
	}

	public String getMinHeapSize() {
		return minHeapSize;
	}

	public void setMinHeapSize(String minHeapSize) {
		this.minHeapSize = minHeapSize;
	}

	public String getMaxPermSize() {
		return maxPermSize;
	}

	public void setMaxPermSize(String maxPermSize) {
		this.maxPermSize = maxPermSize;
	}

	public boolean isDebugJava() {
		return debugJava;
	}

	public void setDebugJava(boolean debugJava) {
		this.debugJava = debugJava;
	}

	public int getDebugPort() {
		return debugPort;
	}

	public void setDebugPort(int debugPort) {
		this.debugPort = debugPort;
	}

	public boolean isDebugSuspend() {
		return debugSuspend;
	}

	public void setDebugSuspend(boolean debugSuspend) {
		this.debugSuspend = debugSuspend;
	}
}
