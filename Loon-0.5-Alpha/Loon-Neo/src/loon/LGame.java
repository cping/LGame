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
package loon;

import loon.event.InputMake;
import loon.utils.reply.Act;

/*
 * 这类为最主要的游戏功能类集合对象，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame {

	public Act<Error> errors = Act.create();

	public LGame(final LSetting config) {
		if (config == null) {
			this.setting = new LSetting();
		}
		this.setting = config;
		String appName = config.appName;
		if (appName != null) {
			LSystem.APP_NAME = appName;
		} else if (LSystem.APP_NAME != null) {
			appName = LSystem.APP_NAME;
		} else {
			appName = "loon";
			LSystem.APP_NAME = appName;
		}
		setting.appName = appName;
		String fontName = config.fontName;
		if (fontName != null) {
			LSystem.FONT_NAME = fontName;
		} else if (LSystem.FONT_NAME != null) {
			fontName = LSystem.FONT_NAME;
		} else {
			fontName = "Dialog";
			LSystem.FONT_NAME = fontName;
		}
		setting.fontName = fontName;
	}

	public void initProcess() {
		LSystem.init(this);
	}

	public static enum Type {
		JAVASE, ANDROID, IOS, WP, HTML5, STUB
	}

	public LSetting setting;

	private Display display;

	public static enum Status {
		PAUSE, RESUME, EXIT
	};

	public Act<Status> status = Act.create();

	public Act<LGame> frame = Act.create();

	public static class Error {
		public final String message;
		public final Throwable cause;

		public Error(String message, Throwable cause) {
			this.message = message;
			this.cause = cause;
		}
	}

	private static Class<?> getType(Object o) {
		if (o instanceof Integer) {
			return Integer.TYPE;
		} else if (o instanceof Float) {
			return Float.TYPE;
		} else if (o instanceof Double) {
			return Double.TYPE;
		} else if (o instanceof Long) {
			return Long.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Boolean) {
			return Boolean.TYPE;
		} else {
			return o.getClass();
		}
	}

	public Display register(Class<? extends Screen> clazz, Object... args) {
		LSystem.viewSize.setSize(setting.width, setting.height);
		this.display = new Display(this, setting.fps);
		if (clazz != null) {
			if (args != null) {
				try {
					final int funs = args.length;
					if (funs == 0) {
						display.setScreen(clazz.newInstance());
					} else {
						Class<?>[] functions = new Class<?>[funs];
						for (int i = 0; i < funs; i++) {
							functions[i] = getType(args[i]);
						}
						java.lang.reflect.Constructor<?> constructor = Class
								.forName(clazz.getName()).getConstructor(
										functions);
						Object o = constructor.newInstance(args);
						if (o != null && (o instanceof Screen)) {
							display.setScreen((Screen) o);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return display;
	}

	public void reportError(String message, Throwable cause) {
		errors.emit(new Error(message, cause));
		log().warn(message, cause);
	}

	public <E> void dispatchEvent(Act<E> signal, E event) {
		try {
			signal.emit(event);
		} catch (Throwable cause) {
			reportError("Event dispatch failure", cause);
		}
	}

	protected void emitFrame() {
		try {
			frame.emit(this);
		} catch (Exception e) {
			log().warn("Frame tick exception", e);
		}
	}

	public void invokeLater(Runnable runnable) {
		asyn().invokeLater(runnable);
	}

	public boolean isAsyncSupported() {
		return asyn().isAsyncSupported();
	}

	public void invokeAsync(Runnable action) {
		asyn().invokeAsync(action);
	}

	public abstract LGame.Type type();

	public abstract double time();

	public abstract int tick();

	public abstract void openURL(String url);

	public abstract Assets assets();

	public abstract Asyn asyn();

	public abstract Graphics graphics();

	public abstract InputMake input();

	public abstract Json json();

	public abstract Log log();

	public abstract Save save();

	public abstract Support support();

	public final Display display() {
		return display;
	}

}
