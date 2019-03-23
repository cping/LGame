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

/**
 * 此类为最主要的游戏功能类集合对象，所有Loon初始化由此开始，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame {

	/**
	 * 支持的运行库(Java版不支持的会由C++版和C#版实现)
	 */
	public static enum Type {
		JAVASE, ANDROID, IOS, WP, HTML5, UNITY, SWITCH, STUB
	}

	/**
	 * 基本游戏状态
	 */
	public static enum Status {
		PAUSE, RESUME, EXIT
	};

	public Act<Error> errors = Act.create();

	public Act<Status> status = Act.create();

	public Act<LGame> frame = Act.create();

	public LSetting setting;

	private Display display;

	public LGame(LSetting config, Platform plat) {
		LSystem._platform = plat;
		if (config == null) {
			config = new LSetting();
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

	public static class Error {
		public final String message;
		public final Throwable cause;

		public Error(String message, Throwable cause) {
			this.message = message;
			this.cause = cause;
		}
	}

	public Display register(Screen screen) {
		this.display = new Display(this, setting.fps);
		this.display.setScreen(screen);
		return display;
	}

	public LGame initProcess() {
		LSystem.initProcess(this);
		return this;
	}

	/**
	 * 由于GWT不支持真实的反射，而完全模拟反射需要耗费大量资源，精确反射又难以控制用户具体使用的类，所以统一放弃外部反射方法，
	 * 不让用户有机会使用自定义的类操作。
	 */
	/*
	 * private static Class<?> getType(Object o) { if (o instanceof Integer) {
	 * return Integer.TYPE; } else if (o instanceof Float) { return Float.TYPE;
	 * } else if (o instanceof Double) { return Double.TYPE; } else if (o
	 * instanceof Long) { return Long.TYPE; } else if (o instanceof Short) {
	 * return Short.TYPE; } else if (o instanceof Short) { return Short.TYPE; }
	 * else if (o instanceof Boolean) { return Boolean.TYPE; } else { return
	 * o.getClass(); } }
	 * 
	 * public Display register(Class<? extends Screen> clazz, Object... args) {
	 * LSystem.viewSize.setSize(setting.width, setting.height); this.display =
	 * new Display(this, setting.fps); if (args == null) { args = new Object[0];
	 * } if (clazz != null) { if (args != null) { try { final int funs =
	 * args.length; if (funs == 0) {
	 * display.setScreen(ClassReflection.newInstance(clazz)); } else {
	 * Class<?>[] functions = new Class<?>[funs]; for (int i = 0; i < funs; i++)
	 * { functions[i] = getType(args[i]); } Constructor constructor =
	 * ClassReflection .getConstructor(clazz, functions); Object o =
	 * constructor.newInstance(args);
	 * 
	 * if (o != null && (o instanceof Screen)) { display.setScreen((Screen) o);
	 * } } } catch (Exception e) { e.printStackTrace(); } } } return display; }
	 */

	public boolean isMobile() {
		Type type = this.type();
		return (type == LGame.Type.ANDROID || type == LGame.Type.IOS || type == LGame.Type.WP);
	}

	public boolean isHTML5() {
		Type type = this.type();
		return type == LGame.Type.HTML5;
	}

	public LGame reportError(String message, Throwable cause) {
		errors.emit(new Error(message, cause));
		log().warn(message, cause);
		return this;
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
		} catch (Throwable cause) {
			log().warn("Frame tick exception", cause);
		}
	}

	public LGame invokeLater(Runnable runnable) {
		asyn().invokeLater(runnable);
		return this;
	}

	public boolean isAsyncSupported() {
		return asyn().isAsyncSupported();
	}

	public LGame invokeAsync(Runnable action) {
		asyn().invokeAsync(action);
		return this;
	}

	public abstract LGame.Type type();

	public abstract double time();

	public abstract int tick();

	public abstract void openURL(String url);

	public abstract Assets assets();

	public abstract Asyn asyn();

	public abstract Graphics graphics();

	public abstract InputMake input();

	public abstract Log log();

	public abstract Save save();

	public abstract Accelerometer accel();

	public abstract Support support();

	public Json json() {
		return LSystem.json();
	}

	public final Display display() {
		return display;
	}

}
