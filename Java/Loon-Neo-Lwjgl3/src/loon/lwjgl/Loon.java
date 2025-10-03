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
package loon.lwjgl;

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake;
import loon.events.SysInput;

public class Loon implements Platform {

	private static String _prevTmpDir;

	private static String _prevUser;

	public static boolean fixJVMTempDir() {
		final String osName = System.getProperty("os.name").toLowerCase();
		if (!osName.contains("mac")) {
			if (osName.contains("windows")) {
				String fixProgramData = System.getenv("ProgramData");
				if (fixProgramData == null) {
					fixProgramData = "C:\\Temp\\";
				}
				_prevTmpDir = System.getProperty("java.io.tmpdir", fixProgramData);
				_prevUser = System.getProperty("user.name", "loon_temp_user");
				System.setProperty("java.io.tmpdir", fixProgramData + "/loon-temp");
				System.setProperty("user.name",
						("User_" + _prevUser.hashCode() + "_Loon" + LSystem.getVersion()).replace('.', '_'));
			}
			return false;
		}
		if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
			return false;
		}
		try {
			final String jvm_args = "jvmIsRestarted";
			final long objc_msgSend = org.lwjgl.system.macosx.ObjCRuntime.getLibrary()
					.getFunctionAddress("objc_msgSend");
			final long NSThread = org.lwjgl.system.macosx.ObjCRuntime.objc_getClass("NSThread");
			final long currentThread = org.lwjgl.system.JNI.invokePPP(NSThread,
					org.lwjgl.system.macosx.ObjCRuntime.sel_getUid("currentThread"), objc_msgSend);
			final boolean isMainThread = org.lwjgl.system.JNI.invokePPZ(currentThread,
					org.lwjgl.system.macosx.ObjCRuntime.sel_getUid("isMainThread"), objc_msgSend);
			if (isMainThread) {
				return false;
			}
			final long pid = org.lwjgl.system.macosx.LibC.getpid();
			if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
				return false;
			}
			if ("true".equals(System.getProperty(jvm_args))) {
				return false;
			}
			final java.util.ArrayList<String> jvmArgs = new java.util.ArrayList<String>();
			final String separator = System.getProperty("file.separator", "/");
			final String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
			if (!(new java.io.File(javaExecPath)).exists()) {
				return false;
			}
			jvmArgs.add(javaExecPath);
			jvmArgs.add("-XstartOnFirstThread");
			jvmArgs.add("-D" + jvm_args + "=true");
			jvmArgs.addAll(java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments());
			jvmArgs.add("-cp");
			jvmArgs.add(System.getProperty("java.class.path"));
			String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
			if (mainClass == null) {
				final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
				if (trace.length > 0) {
					mainClass = trace[trace.length - 1].getClassName();
				} else {
					return false;
				}
			}
			jvmArgs.add(mainClass);
			try {
				final ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
				processBuilder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private Lwjgl3Game game;

	public Loon(LSetting config) {
		this.game = new Lwjgl3Game(this, config);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		register(setting, lazy, true);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy, boolean fixTempDir) {
		if (fixTempDir) {
			fixJVMTempDir();
		}
		final Loon plat = new Loon(setting);
		if (fixTempDir) {
			if (_prevTmpDir != null && _prevTmpDir.length() > 0) {
				System.setProperty("java.io.tmpdir", _prevTmpDir);
			}
			if (_prevUser != null && _prevUser.length() > 0) {
				System.setProperty("user.name", _prevUser);
			}
		}
		plat.game.register(lazy.onScreen());
		plat.game.reset();
	}

	@Override
	public int getContainerWidth() {
		return ((Lwjgl3Graphics) game.graphics()).screenSize().getWidth();
	}

	@Override
	public int getContainerHeight() {
		return ((Lwjgl3Graphics) game.graphics()).screenSize().getHeight();
	}

	@Override
	public void close() {
		System.exit(-1);
	}

	@Override
	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	@Override
	public void sysText(final SysInput.TextEvent event, final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (Lwjgl3Game.isMacOS()) {
			return;
		}
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {
				final String output = (String) javax.swing.JOptionPane.showInputDialog(null, label, "",
						javax.swing.JOptionPane.QUESTION_MESSAGE, null, null, initVal);
				if (output != null) {
					event.input(output);
				} else {
					event.cancel();
				}
			}
		});
	}

	@Override
	public void sysDialog(final SysInput.ClickEvent event, final String title, final String text, final String ok,
			final String cancel) {
		if (Lwjgl3Game.isMacOS()) {
			return;
		}
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {
				int optType = javax.swing.JOptionPane.OK_CANCEL_OPTION;
				int msgType = cancel == null ? javax.swing.JOptionPane.INFORMATION_MESSAGE
						: javax.swing.JOptionPane.QUESTION_MESSAGE;
				Object[] options = (cancel == null) ? new Object[] { ok } : new Object[] { ok, cancel };
				Object defOption = (cancel == null) ? ok : cancel;
				int result = javax.swing.JOptionPane.showOptionDialog(null, text, title, optType, msgType, null,
						options, defOption);
				if (result == 0) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});
	}

	@Override
	public LGame getGame() {
		return game;
	}

}
