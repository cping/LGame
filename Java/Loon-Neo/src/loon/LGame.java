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
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.VertexAttribute;
import loon.opengl.Mesh.VertexDataType;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.reply.Act;

/**
 * 此类为最主要的游戏功能类集合对象，所有Loon初始化由此开始，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame {

	private IntMap<LTextureBatch> _batch_pools;

	private ObjectMap<String, Mesh> _mesh_pools;

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
	}

	public static class Error {
		public String message;
		public Throwable cause;

		public Error(String message, Throwable cause) {
			this.message = message;
			this.cause = cause;
		}
	}

	public Act<Error> errors = Act.create();

	public Act<Status> status = Act.create();

	public Act<LGame> frame = Act.create();

	public LSetting setting;

	private Display display;

	public LGame(LSetting config, Platform plat) {
		LSystem._platform = plat;
		_batch_pools = new IntMap<LTextureBatch>(10);
		_mesh_pools = new ObjectMap<String, Mesh>(10);
		if (config == null) {
			config = new LSetting();
		}
		this.setting = config;
		String appName = config.appName;
		if (appName != null) {
			LSystem._app_name = appName;
		} else if (LSystem._app_name != null) {
			appName = LSystem._app_name;
		} else {
			appName = "loon";
			LSystem._app_name = appName;
		}
		setting.appName = appName;
		String fontName = config.fontName;
		if (fontName != null) {
			LSystem._font_name = fontName;
		} else if (LSystem._font_name != null) {
			fontName = LSystem._font_name;
		} else {
			fontName = "Dialog";
			LSystem._font_name = fontName;
		}
		setting.fontName = fontName;
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
	 * private Class<?> getType(Object o) { if (o instanceof Integer) { return
	 * Integer.TYPE; } else if (o instanceof Float) { return Float.TYPE; } else
	 * if (o instanceof Double) { return Double.TYPE; } else if (o instanceof
	 * Long) { return Long.TYPE; } else if (o instanceof Short) { return
	 * Short.TYPE; } else if (o instanceof Short) { return Short.TYPE; } else if
	 * (o instanceof Boolean) { return Boolean.TYPE; } else { return
	 * o.getClass(); } }
	 * 
	 * public Display register(Class<? extends Screen> clazz, Object... args) {
	 * LSystem.viewSize.setSize(setting.width, setting.height); this.display =
	 * new Display(this, setting.fps); if (args == null) { args = new Object[0];
	 * } if (clazz != null) { if (args != null) { try { int funs = args.length;
	 * if (funs == 0) { display.setScreen(ClassReflection.newInstance(clazz)); }
	 * else { Class<?>[] functions = new Class<?>[funs]; for (int i = 0; i <
	 * funs; i++) { functions[i] = getType(args[i]); } Constructor constructor =
	 * ClassReflection .getConstructor(clazz, functions); Object o =
	 * constructor.newInstance(args);
	 * 
	 * if (o != null && (o instanceof Screen)) { display.setScreen((Screen) o);
	 * } } } catch (Exception e) { e.printStackTrace(); } } } return display; }
	 */

	public boolean isMobile() {
		Type type = this.type();
		return (type == LGame.Type.ANDROID || type == LGame.Type.IOS || type == LGame.Type.WP
				|| type == LGame.Type.SWITCH);
	}

	public boolean isHTML5() {
		Type type = this.type();
		return type == LGame.Type.HTML5;
	}

	public LGame reportError(String message, Throwable cause) {
		return reportError(message, cause, true);
	}

	public LGame reportError(String message, Throwable cause, boolean logError) {
		errors.emit(new Error(message, cause));
		if (logError) {
			log().error(message, cause);
		}
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
			log().error("Frame tick exception :", cause);
			LSystem.stopRepaint();
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

	public int batchCacheSize() {
		return _batch_pools.size;
	}

	public void clearBatchCaches() {
		if (_batch_pools == null || _batch_pools.size == 0) {
			return;
		}
		IntMap<LTextureBatch> batchCaches;
		synchronized (_batch_pools) {
			batchCaches = new IntMap<LTextureBatch>(_batch_pools);
		}
		for (LTextureBatch bt : batchCaches.values()) {
			if (bt != null) {
				synchronized (bt) {
					bt.close();
					bt = null;
				}
			}
		}
		_batch_pools.clear();
		batchCaches = null;
	}

	public LTextureBatch getBatchCache(LTexture texture) {
		if (texture == null) {
			return null;
		}
		return _batch_pools.get(texture.getID());
	}

	public LTextureBatch bindBatchCache(LTextureBatch batch) {
		if (_batch_pools.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			clearBatchCaches();
		}
		int key = batch.getTextureID();
		LTextureBatch pBatch = _batch_pools.get(key);
		if (pBatch == null) {
			pBatch = batch;
			synchronized (_batch_pools) {
				_batch_pools.put(key, pBatch);
			}
		}
		return pBatch;
	}

	public LTextureBatch disposeBatchCache(LTextureBatch batch) {
		return disposeBatchCache(batch, true);
	}

	public LTextureBatch disposeBatchCache(LTextureBatch batch, boolean closed) {
		synchronized (_batch_pools) {
			LTextureBatch pBatch = _batch_pools.remove(batch.getTextureID());
			if (closed && pBatch != null) {
				synchronized (pBatch) {
					pBatch.close();
					pBatch = null;
				}
			}
			return pBatch;
		}
	}

	public void resetMeshPool(String n, int size) {
		String name = n + size;
		synchronized (_mesh_pools) {
			Mesh mesh = _mesh_pools.get(name);
			if (mesh != null) {
				mesh.close();
				mesh = null;
			}
			_mesh_pools.remove(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				LSystem.resetIndices(size, mesh);
				_mesh_pools.put(name, mesh);
			}
		}
	}

	public Mesh getMeshPool(String n, int size) {
		String name = n + size;
		synchronized (_mesh_pools) {
			Mesh mesh = _mesh_pools.get(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				LSystem.resetIndices(size, mesh);
				_mesh_pools.put(name, mesh);
			}
			return mesh;
		}
	}

	public int getMeshPoolSize() {
		return _mesh_pools.size;
	}

	public void disposeMeshPool(String name, int size) {
		String key = name + size;
		synchronized (_mesh_pools) {
			Mesh mesh = _mesh_pools.remove(key);
			if (mesh != null) {
				mesh.close();
			}
		}
	}

	public void disposeMeshPool() {
		synchronized (_mesh_pools) {
			for (Mesh mesh : _mesh_pools.values()) {
				if (mesh != null) {
					mesh.close();
				}
			}
		}
		_mesh_pools.clear();
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

	public Display display() {
		return display;
	}

}
