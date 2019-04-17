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

import loon.LTexture.Format;
import loon.canvas.Image;
import loon.canvas.NinePatchAbstract.Repeat;
import loon.event.InputMake;
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.VertexAttribute;
import loon.opengl.Mesh.VertexDataType;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Act;

/**
 * 此类为最主要的游戏功能类集合对象，所有Loon初始化由此开始，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame {

	private final TArray<LTexture> _texture_all_list;

	private final IntMap<LTextureBatch> _texture_batch_pools;

	private final ObjectMap<String, Mesh> _texture_mesh_pools;

	private final ObjectMap<String, LTexture> _texture_lazys;

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
		_texture_batch_pools = new IntMap<LTextureBatch>(12);
		_texture_mesh_pools = new ObjectMap<String, Mesh>(12);
		_texture_all_list = new TArray<LTexture>(128);
		_texture_lazys = new ObjectMap<String, LTexture>(128);
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

	public LGame invokeAsync(final Runnable action) {
		if (action == null) {
			return this;
		}
		if (isAsyncSupported()) {
			asyn().invokeAsync(action);
		} else {
			invokeLater(action);
		}
		return this;
	}

	public int batchCacheSize() {
		return _texture_batch_pools.size;
	}

	public void clearBatchCaches() {
		IntMap<LTextureBatch> batchCaches;
		synchronized (_texture_batch_pools) {
			batchCaches = new IntMap<LTextureBatch>(_texture_batch_pools);
		}
		for (LTextureBatch bt : batchCaches.values()) {
			if (bt != null) {
				synchronized (bt) {
					bt.close();
					bt = null;
				}
			}
		}
		_texture_batch_pools.clear();
		batchCaches = null;
	}

	public LTextureBatch getBatchCache(LTexture texture) {
		if (texture == null) {
			return null;
		}
		return _texture_batch_pools.get(texture.getID());
	}

	public LTextureBatch bindBatchCache(LTextureBatch batch) {
		if (_texture_batch_pools.size > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			clearBatchCaches();
		}
		int key = batch.getTextureID();
		LTextureBatch pBatch = _texture_batch_pools.get(key);
		if (pBatch == null) {
			pBatch = batch;
			synchronized (_texture_batch_pools) {
				_texture_batch_pools.put(key, pBatch);
			}
		}
		return pBatch;
	}

	public LTextureBatch disposeBatchCache(LTextureBatch batch) {
		return disposeBatchCache(batch, true);
	}

	public LTextureBatch disposeBatchCache(LTextureBatch batch, boolean closed) {
		synchronized (_texture_batch_pools) {
			LTextureBatch pBatch = _texture_batch_pools.remove(batch.getTextureID());
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
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.get(name);
			if (mesh != null) {
				mesh.close();
				mesh = null;
			}
			_texture_mesh_pools.remove(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				LSystem.resetIndices(size, mesh);
				_texture_mesh_pools.put(name, mesh);
			}
		}
	}

	public Mesh getMeshPool(String n, int size) {
		String name = n + size;
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.get(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				LSystem.resetIndices(size, mesh);
				_texture_mesh_pools.put(name, mesh);
			}
			return mesh;
		}
	}

	public int getMeshPoolSize() {
		return _texture_mesh_pools.size;
	}

	public void disposeMeshPool(String name, int size) {
		String key = name + size;
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.remove(key);
			if (mesh != null) {
				mesh.close();
			}
		}
	}

	public void disposeMeshPool() {
		synchronized (_texture_mesh_pools) {
			for (Mesh mesh : _texture_mesh_pools.values()) {
				if (mesh != null) {
					mesh.close();
				}
			}
		}
		_texture_mesh_pools.clear();
	}

	public boolean containsTexture(int id) {
		synchronized (_texture_all_list) {
			for (LTexture tex : _texture_all_list) {
				if (tex.getID() == id) {
					return true;
				}
			}
			return false;
		}
	}

	protected boolean delTexture(int id) {
		synchronized (_texture_all_list) {
			for (LTexture tex : _texture_all_list) {
				if (tex.getID() == id) {
					return _texture_all_list.remove(tex);
				}
			}
		}
		return false;
	}

	protected void putTexture(LTexture tex2d) {
		if (tex2d != null && !tex2d.isClosed() && !tex2d.isChild() && !_texture_all_list.contains(tex2d)) {
			synchronized (_texture_all_list) {
				_texture_all_list.add(tex2d);
			}
		}
	}

	public void reloadTexture() {
		TArray<LTexture> texs = null;
		synchronized (_texture_all_list) {
			texs = new TArray<LTexture>(_texture_all_list);
			_texture_all_list.clear();
		}
		for (LTexture tex : texs) {
			if (tex != null && !tex.isLoaded() && !tex.isClosed()) {
				tex.reload();
			}
		}
		_texture_all_list.addAll(texs);
	}

	public int getTextureMemSize() {
		int memTotal = 0;
		for (LTexture tex : _texture_all_list) {
			if (tex != null && !tex.isChild() && !tex.isClosed()) {
				memTotal += tex.getMemSize();
			}
		}
		return memTotal;
	}

	public void closeAllTexture() {
		if (_texture_all_list.size > 0) {
			TArray<LTexture> tex2d = new TArray<LTexture>(_texture_all_list);
			for (LTexture tex : tex2d) {
				if (tex != null && !tex.isChild() && !tex.isClosed()) {
					tex.close();
				}
			}
		}
		_texture_all_list.clear();
	}

	public int countTexture() {
		return _texture_all_list.size;
	}

	public boolean containsTextureValue(LTexture texture) {
		return _texture_all_list.contains(texture);
	}

	public int getRefTextureCount(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return 0;
		}
		String key = fileName.trim();
		LTexture texture = _texture_lazys.get(key);
		if (texture != null) {
			return texture.refCount;
		}
		for (int i = 0, size = _texture_all_list.size; i < size; i++) {
			LTexture tex2d = _texture_all_list.get(i);
			String source = tex2d.getSource();
			if (tex2d != null && source.indexOf("<canvas>") == -1) {
				if (key.equalsIgnoreCase(source) || key.equalsIgnoreCase(tex2d.tmpLazy)) {
					return tex2d.refCount;
				}
			}
		}
		return 0;
	}

	protected int removeTextureRef(String name, final boolean remove) {
		if (StringUtils.isEmpty(name)) {
			return 0;
		}
		final LTexture texture = _texture_lazys.get(name);
		if (texture != null) {
			return texture.refCount--;
		} else {
			for (int i = 0; i < _texture_all_list.size; i++) {
				LTexture tex = _texture_all_list.get(i);
				if (tex != null && tex.tmpLazy.equals(name)) {
					return tex.refCount--;
				}
			}
		}
		return -1;
	}

	protected int removeTextureRef(LTexture texture, final boolean remove) {
		if (texture == null) {
			return -1;
		}
		return removeTextureRef(texture.tmpLazy, remove);
	}

	public LTexture createTexture(int width, int height, Format config) {
		return graphics().createTexture(width, height, config);
	}

	public LTexture newTexture(String path) {
		return newTexture(path, Format.LINEAR);
	}

	public LTexture newTexture(String path, Format config) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		LSystem.debug("Texture : New " + path + " Loaded");
		return BaseIO.loadImage(path).onHaveToClose(true).createTexture(config);
	}

	public LTexture loadNinePatchTexture(String fileName, int x, int y, int w, int h) {
		return loadNinePatchTexture(fileName, null, x, y, w, h, Format.LINEAR);
	}

	public LTexture loadNinePatchTexture(String fileName, Repeat repeat, int x, int y, int w, int h, Format config) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		synchronized (_texture_lazys) {
			String key = fileName.trim().toLowerCase() + (repeat == null ? "" : repeat);
			ObjectMap<String, LTexture> texs = new ObjectMap<String, LTexture>(_texture_lazys);
			LTexture texture = texs.get(key);
			if (texture == null) {
				for (LTexture tex : texs.values()) {
					if (tex.tmpLazy != null && tex.tmpLazy.toLowerCase().equals(key.toLowerCase())) {
						texture = tex;
						break;
					}
				}
			}
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = Image.createImageNicePatch(fileName, x, y, w, h).onHaveToClose(true).createTexture(config);
			texture.tmpLazy = fileName;
			_texture_lazys.put(key, texture);
			LSystem.debug("Texture : " + fileName + " Loaded");
			return texture;
		}
	}

	public LTexture loadTexture(String fileName, Format config) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		synchronized (_texture_lazys) {
			String key = fileName.trim().toLowerCase();
			ObjectMap<String, LTexture> texs = new ObjectMap<String, LTexture>(_texture_lazys);
			LTexture texture = texs.get(key);
			if (texture == null) {
				for (LTexture tex : texs.values()) {
					if (tex.tmpLazy != null && tex.tmpLazy.toLowerCase().equals(key.toLowerCase())) {
						texture = tex;
						break;
					}
				}
			}
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).onHaveToClose(true).createTexture(config);
			texture.tmpLazy = fileName;
			_texture_lazys.put(key, texture);
			LSystem.debug("Texture : " + fileName + " Loaded");
			return texture;
		}
	}

	public LTexture loadTexture(String fileName) {
		return loadTexture(fileName, Format.LINEAR);
	}

	protected LTexture removeTexture(LTexture tex) {
		if (tex == null) {
			return null;
		}
		String key = tex.src().trim().toLowerCase();
		LTexture tex2d = _texture_lazys.remove(key);
		if (tex2d == null) {
			tex2d = _texture_lazys.remove(tex.tmpLazy);
		}
		return tex2d;
	}

	public void destroySourceAllCache() {
		if (_texture_lazys.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(_texture_lazys.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed() && tex2d.getSource() != null
						&& tex2d.getSource().indexOf("<canvas>") == -1) {
					tex2d.refCount = 0;
					tex2d.close(true);
					tex2d = null;
				}
			}
		}
		_texture_lazys.clear();
	}

	public void destroyAllCache() {
		if (_texture_lazys.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(_texture_lazys.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed()) {
					tex2d.refCount = 0;
					tex2d.close(true);
					tex2d = null;
				}
			}
		}
		_texture_lazys.clear();
	}

	public void disposeTextureAll() {
		destroyAllCache();
		closeAllTexture();
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
