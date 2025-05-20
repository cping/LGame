package loon.lwjgl;

import org.lwjgl.glfw.GLFW;

public class Lwjgl3Sync {

	final static double NANO_RESOLUTION = 1000000000.0D, GLFW_RESOLUTION = 1.0D;

	public final static int JAVA_NANO = 1, LWJGL_GLFW = 2;

	private int mode;
	private double timeThen;
	private boolean enabled = true;

	public Lwjgl3Sync(int mode) {
		setNewMode(mode);
	}

	private double getResolution() {
		switch (mode) {
		case JAVA_NANO:
			return NANO_RESOLUTION;
		case LWJGL_GLFW:
			return GLFW_RESOLUTION;
		}
		return 0;
	}

	private double getTime() {
		switch (mode) {
		case JAVA_NANO:
			return System.nanoTime();
		case LWJGL_GLFW:
			return GLFW.glfwGetTime();
		}
		return 0;
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setNewMode(int timerMode) {
		mode = timerMode;
		timeThen = getTime();
	}

	public String getModeString() {
		switch (mode) {
		case JAVA_NANO:
			return "NANO";
		case LWJGL_GLFW:
			return "LWJGL";
		}
		return null;
	}

	public int getMode() {
		return mode;
	}

	public int sync(int fps) {
		double resolution = getResolution();
		double timeNow = getTime();
		int updates = 0;

		// 支持线程sleep
		if (enabled) {
			double gapTo = resolution / fps + timeThen;

			while (gapTo < timeNow) {
				gapTo = resolution / fps + gapTo;
				updates++;
			}
			while (gapTo > timeNow) {
				try {
					Thread.sleep(1);
				} catch (Exception ex) {
				}
				timeNow = getTime();
			}
			updates++;

			timeThen = gapTo;
		} else { // 不支持线程sleep
			while (timeThen < timeNow) {
				timeThen = resolution / fps + timeThen;
				updates++;
			}
		}

		return updates;
	}
}
