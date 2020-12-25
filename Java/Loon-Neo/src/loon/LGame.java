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
import loon.action.sprite.Sprites;
import loon.component.Desktop;
import loon.events.InputMake;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.FrameBuffer;
import loon.opengl.GLFrameBuffer;
import loon.opengl.LSTRFont;
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.VertexAttribute;
import loon.opengl.Mesh.VertexDataType;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.json.JsonImpl;
import loon.utils.reply.Act;

/**
 * 此类为最主要的游戏功能类集合对象，所有Loon初始化由此开始，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame {

	/**
	 * 支持的运行库(Java版不支持的会由C++版和C#版实现)
	 */
	public static enum Type {
		JAVASE, MONO, ANDROID, IOS, WP, HTML5, UNITY, SWITCH, PS, XBOX, STUB
	}

	/**
	 * 基本游戏状态
	 */
	public static enum Status {
		PAUSE, RESUME, EXIT
	}

	/**
	 * 简单的错误信息存储
	 *
	 */
	public static class Error {

		public String message;
		public Throwable cause;

		public Error(String message, Throwable cause) {
			this.message = message;
			this.cause = cause;
		}
	}

	protected static final String FONT_NAME = "Dialog";

	protected static final String APP_NAME = "Loon";

	protected static LGame _base = null;

	protected static Platform _platform = null;

	// 全部mesh
	private final TArray<Mesh> _mesh_all_pools;

	// 全部shader
	private final TArray<ShaderProgram> _shader_all_pools;

	// 全部framebuffer
	private final TArray<GLFrameBuffer> _framebuffer_all_pools;

	// 单独纹理批处理缓存
	private final IntMap<LTextureBatch> _texture_batch_pools;

	// mesh缓存
	private final ObjectMap<String, Mesh> _texture_mesh_pools;

	// 纹理惰性加载缓存
	private final ObjectMap<String, LTexture> _texture_lazys;

	// 全部纹理数据
	private final TArray<LTexture> _texture_all_list;

	// 精灵group缓存
	private final TArray<Sprites> _sprites_pools;

	// 桌面group缓存
	private final TArray<Desktop> _desktop_pools;

	// ifnot缓存
	private final TArray<IFont> _font_pools;

	// 错误接口
	public Act<Error> errors = Act.create();

	// 状态接口
	public Act<Status> status = Act.create();

	// 游戏窗体刷新接口
	public Act<LGame> frame = Act.create();

	// 游戏基本设置
	public LSetting setting;

	protected LProcess processImpl;

	protected Display displayImpl;

	protected JsonImpl jsonImpl;

	public LGame(LSetting config, Platform plat) {
		LGame._platform = plat;
		this._mesh_all_pools = new TArray<Mesh>(128);
		this._shader_all_pools = new TArray<ShaderProgram>(128);
		this._framebuffer_all_pools = new TArray<GLFrameBuffer>(12);
		this._texture_batch_pools = new IntMap<LTextureBatch>(12);
		this._texture_mesh_pools = new ObjectMap<String, Mesh>(12);
		this._texture_lazys = new ObjectMap<String, LTexture>(128);
		this._texture_all_list = new TArray<LTexture>(128);
		this._sprites_pools = new TArray<Sprites>(12);
		this._desktop_pools = new TArray<Desktop>(12);
		this._font_pools = new TArray<IFont>(12);
		if (config == null) {
			config = new LSetting();
		}
		this.setting = config;
		String appName = config.appName;
		if (StringUtils.isEmpty(appName)) {
			setting.appName = APP_NAME;
		}
		String fontName = config.fontName;
		if (StringUtils.isEmpty(fontName)) {
			setting.fontName = FONT_NAME;
		}
	}

	/**
	 * 注册Screen到游戏显示器中
	 * 
	 * @param screen
	 * @return
	 */
	public final Display register(Screen screen) {
		this.displayImpl = new Display(this, setting.fps);
		this.displayImpl.setScreen(screen);
		return displayImpl;
	}

	protected final void initProcess(LGame game) {
		LGame._base = game;
		if (LGame._base == null && LGame._platform != null) {
			LGame._base = LGame._platform.getGame();
		}
		processImpl = new LProcess(game);
		log().debug("The Loon Game Engine is Begin");
	}

	/**
	 * 初始化LProcess组件,以处理游戏流程
	 * 
	 * @return
	 */
	public LGame initProcess() {
		initProcess(this);
		if (setting.defaultGameFont == null) {
			setting.defaultGameFont = LFont.getFont(setting.fontName, 20);
		}
		if (setting.defaultLogFont == null) {
			setting.defaultLogFont = LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
		}
		if (jsonImpl == null) {
			jsonImpl = new JsonImpl();
		}
		return this;
	}

	/**
	 * 设定LGame绑定的运行平台(主要就是换Activity)
	 * 
	 * @param plat
	 */
	public void setPlatform(Platform plat) {
		if (plat != null) {
			LGame._platform = plat;
			LGame game = plat.getGame();
			if (game != null) {
				LGame._base = game;
				LGame._base.resetShader();
			}
		}
	}

	/**
	 * 刷新Shader数据
	 * 
	 * @param game
	 */
	public void resetShader() {
		Mesh.invalidate(this);
		ShaderProgram.invalidate(this);
		FrameBuffer.invalidate(this);
	}

	/**
	 * 检查LGame对象的静态地址和实际地址的变化,防止被虚拟机乱分配导致错误
	 * 
	 * @param game
	 */
	protected final LGame checkBaseGame(LGame game) {
		LGame oldGame = _base;
		if (game != oldGame && game != null) {
			oldGame = game;
		} else if (game == null) {
			if (oldGame != null && _platform != null && _platform.getGame() != null) {
				if (oldGame != _platform.getGame()) {
					oldGame = _platform.getGame();
				}
			}
		}
		if (_base != game || _base != oldGame) {
			_base = oldGame;
		}
		return LSystem.base();
	}

	/**
	 * 检查是否手机环境
	 * 
	 * @return
	 */
	public boolean isMobile() {
		Type type = this.type();
		return (type == LGame.Type.ANDROID || type == LGame.Type.IOS || type == LGame.Type.WP
				|| type == LGame.Type.SWITCH);
	}

	/**
	 * 检查是否HTML5(JS)环境
	 * 
	 * @return
	 */
	public boolean isHTML5() {
		Type type = this.type();
		return type == LGame.Type.HTML5;
	}

	/**
	 * 是否桌面
	 * 
	 * @return
	 */
	public boolean isDesktop() {
		return !isMobile() && !isHTML5();
	}

	/**
	 * 上报异常到异常池中
	 * 
	 * @param message
	 * @param cause
	 * @return
	 */
	public LGame reportError(String message, Throwable cause) {
		return reportError(message, cause, true);
	}

	/**
	 * 上报异常到异常池中
	 * 
	 * @param message
	 * @param cause
	 * @param logError
	 * @return
	 */
	public LGame reportError(String message, Throwable cause, boolean logError) {
		errors.emit(new Error(message, cause));
		if (logError) {
			log().error(message, cause);
		}
		return this;
	}

	/**
	 * 绑定事件和Act状态
	 * 
	 * @param signal
	 * @param event
	 */
	public <E> void dispatchEvent(Act<E> signal, E event) {
		try {
			signal.emit(event);
		} catch (Throwable cause) {
			reportError("Event dispatch failure", cause);
		}
	}

	/**
	 * 此处负责绑定主渲染器循环,如果渲染器异常则停止游戏画面刷新(否则后台会不停报错,根本停不下来)
	 */
	protected void emitFrame() {
		try {
			frame.emit(this);
		} catch (Throwable cause) {
			log().error("Frame tick exception :", cause);
			LSystem.stopRepaint();
		}
	}

	/**
	 * 提交一个Runnable到Loon本身进程中
	 * 
	 * @param runnable
	 * @return
	 */
	public LGame invokeLater(Runnable runnable) {
		if (runnable == null) {
			return this;
		}
		asyn().invokeLater(runnable);
		return this;
	}

	/**
	 * 当前环境是否支持异步提交Runnable
	 * 
	 * @return
	 */
	public boolean isAsyncSupported() {
		return asyn().isAsyncSupported();
	}

	/**
	 * 如果本地支持异步提交Runnable,则使用本地的,如果不支持,则转换为invokeLater
	 * 
	 * @param action
	 * @return
	 */
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

	/**
	 * 单独纹理批处理渲染器的数量
	 * 
	 * @return
	 */
	public int batchCacheSize() {
		return _texture_batch_pools.size;
	}

	/**
	 * 清空所有单独纹理批处理渲染器
	 */
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

	/**
	 * 获得指定纹理的单独纹理批处理渲染器
	 * 
	 * @param texture
	 * @return
	 */
	public LTextureBatch getBatchCache(LTexture texture) {
		if (texture == null) {
			return null;
		}
		return _texture_batch_pools.get(texture.getID());
	}

	/**
	 * 绑定单独纹理批处理和生成它的纹理到缓存池中
	 * 
	 * @param batch
	 * @return
	 */
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

	/**
	 * 从池中注销一个单独纹理批处理渲染器
	 * 
	 * @param batch
	 * @return
	 */
	public LTextureBatch disposeBatchCache(LTextureBatch batch) {
		return disposeBatchCache(batch, true);
	}

	/**
	 * 从池中注销一个单独纹理批处理渲染器,如果closed为true则纹理处理器也被注销
	 * 
	 * @param batch
	 * @param closed
	 * @return
	 */
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

	/**
	 * 刷新指定的Mesh池中Mesh数据
	 * 
	 * @param n
	 * @param size
	 */
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

	/**
	 * 获得指定名称大小的MeshPool池中对象
	 * 
	 * @param n
	 * @param size
	 * @return
	 */
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

	/**
	 * 获得指定名称大小的MeshPool池中对象
	 * 
	 * @param n
	 * @param size
	 * @return
	 */
	public Mesh getMeshTrianglePool(String n, int size, int trisize) {
		int code = 1;
		code = LSystem.unite(code, size);
		code = LSystem.unite(code, trisize);
		String name = n + "tri" + code;
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.get(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size, trisize * 3,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				_texture_mesh_pools.put(name, mesh);
			}
			return mesh;
		}
	}

	/**
	 * 刷新指定的Mesh池中Mesh数据
	 * 
	 * @param n
	 * @param size
	 */
	public void resetMeshTrianglePool(String n, int size, int trisize) {
		int code = 1;
		code = LSystem.unite(code, size);
		code = LSystem.unite(code, trisize);
		String name = n + "tri" + code;
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.get(name);
			if (mesh != null) {
				mesh.close();
				mesh = null;
			}
			_texture_mesh_pools.remove(name);
			if (mesh == null || mesh.isClosed()) {
				mesh = new Mesh(VertexDataType.VertexArray, false, size, trisize * 3,
						new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
				_texture_mesh_pools.put(name, mesh);
			}
		}
	}

	/**
	 * 获得MeshPool大小
	 * 
	 * @return
	 */
	public int getMeshPoolSize() {
		return _texture_mesh_pools.size;
	}

	/**
	 * 注销一个指定名称大小的MeshPool中对象
	 * 
	 * @param name
	 * @param size
	 */
	public void disposeMeshPool(String name, int size) {
		String key = name + size;
		synchronized (_texture_mesh_pools) {
			Mesh mesh = _texture_mesh_pools.remove(key);
			if (mesh != null) {
				mesh.close();
			}
		}
	}

	/**
	 * 注销全部MeshPool池中对象
	 */
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

	/**
	 * 查看纹理池中是否存在指定id对象
	 * 
	 * @param id
	 * @return
	 */
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

	/**
	 * 单纯从池中删除指定的纹理对象
	 * 
	 * @param id
	 * @return
	 */
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

	/**
	 * 提交一个纹理到纹理池
	 * 
	 * @param tex2d
	 */
	protected void putTexture(LTexture tex2d) {
		if (tex2d != null && !tex2d.isClosed() && !tex2d.isChild() && !_texture_all_list.contains(tex2d)) {
			synchronized (_texture_all_list) {
				_texture_all_list.add(tex2d);
			}
		}
	}

	/**
	 * 重载全部纹理
	 */
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

	/**
	 * 获得当前所有纹理池中对象累加占用资源大小
	 * 
	 * @return
	 */
	public int getTextureMemSize() {
		int memTotal = 0;
		for (LTexture tex : _texture_all_list) {
			if (tex != null && !tex.isChild() && !tex.isClosed()) {
				memTotal += tex.getMemSize();
			}
		}
		return memTotal;
	}

	/**
	 * 关闭所有纹理
	 */
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

	/**
	 * 纹理池中纹理数量
	 * 
	 * @return
	 */
	public int countTexture() {
		return _texture_all_list.size;
	}

	/**
	 * 查看纹理池中是否包含指定纹理
	 * 
	 * @param texture
	 * @return
	 */
	public boolean containsTextureValue(LTexture texture) {
		return _texture_all_list.contains(texture);
	}

	/**
	 * 查看纹理池中对象被引用次数
	 * 
	 * @param fileName
	 * @return
	 */
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

	/**
	 * 删除指定缓存名称的纹理对象并返回引用次数,若remove为true,则会强制删除查找到的池中纹理
	 * 
	 * @param name
	 * @param remove
	 * @return
	 */
	public int removeTextureRef(String name, final boolean remove) {
		if (StringUtils.isEmpty(name)) {
			return 0;
		}
		int refCount = -1;
		LTexture texture = _texture_lazys.get(name);
		if (texture != null) {
			refCount = texture.refCount--;
		} else {
			for (int i = 0; i < _texture_all_list.size; i++) {
				LTexture tex = _texture_all_list.get(i);
				if (tex != null && tex.tmpLazy.equals(name)) {
					texture = tex;
					refCount = tex.refCount--;
					break;
				}
			}
		}
		if (remove && texture != null) {
			texture.close(true);
		}
		return refCount;
	}

	/**
	 * 删除指定纹理对象并返回引用次数,如果remove为true,同时强制删除指定纹理
	 * 
	 * @param texture
	 * @param remove
	 * @return
	 */
	public int removeTextureRef(LTexture texture, final boolean remove) {
		if (texture == null) {
			return -1;
		}
		return removeTextureRef(texture.tmpLazy, remove);
	}

	/**
	 * 创建一个指定大小格式的空白新纹理
	 * 
	 * @param width
	 * @param height
	 * @param config
	 * @return
	 */
	public LTexture createTexture(int width, int height, Format config) {
		return graphics().createTexture(width, height, config);
	}

	/**
	 * 以指定位置图片创建一个新纹理
	 * 
	 * @param path
	 * @return
	 */
	public LTexture newTexture(String path) {
		return newTexture(path, Format.LINEAR);
	}

	/**
	 * 以指定位置图片和格式创建一个新纹理
	 * 
	 * @param path
	 * @param config
	 * @return
	 */
	public LTexture newTexture(String path, Format config) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		log().debug("Texture : New " + path + " Loaded");
		return BaseIO.loadImage(path).onHaveToClose(true).createTexture(config);
	}

	/**
	 * 从缓存加载一个指定文件名纹理为指定格式(存在缓存时会得到缓存图片)
	 * 
	 * @param fileName
	 * @param config
	 * @return
	 */
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
			log().debug("Texture : " + fileName + " Loaded");
			return texture;
		}
	}

	/**
	 * 从缓存加载一个指定位置图片
	 * 
	 * @param fileName
	 * @return
	 */
	public LTexture loadTexture(String fileName) {
		return loadTexture(fileName, Format.LINEAR);
	}

	/**
	 * 从纹理缓存池删除一个缓存的纹理图片
	 * 
	 * @param tex
	 * @return
	 */
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

	/**
	 * 删除所有从路径加载的纹理图片并强制销毁纹理(但是手动生成的纹理此处不销毁,仅删除)
	 */
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

	/**
	 * 强制销毁全部缓存的纹理图片(所有都不放过)
	 */
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

	/**
	 * 只要是纹理,无论类型,全部销毁
	 */
	public void disposeTextureAll() {
		destroyAllCache();
		closeAllTexture();
	}

	public void addMesh(Mesh mesh) {
		_mesh_all_pools.add(mesh);
	}

	public void removeMesh(Mesh mesh) {
		_mesh_all_pools.remove(mesh);
	}

	public TArray<Mesh> getMeshAll() {
		return _mesh_all_pools;
	}

	public void clearMesh() {
		_mesh_all_pools.clear();
	}

	public void addShader(ShaderProgram shader) {
		_shader_all_pools.add(shader);
	}

	public void removeShader(ShaderProgram shader) {
		_shader_all_pools.remove(shader);
	}

	public TArray<ShaderProgram> getShaderAll() {
		return _shader_all_pools;
	}

	public void clearShader() {
		_shader_all_pools.clear();
	}

	public void addFrameBuffer(GLFrameBuffer buffer) {
		_framebuffer_all_pools.add(buffer);
	}

	public void removeFrameBuffer(GLFrameBuffer buffer) {
		_framebuffer_all_pools.remove(buffer);
	}

	public TArray<GLFrameBuffer> getFrameBufferAll() {
		return _framebuffer_all_pools;
	}

	public void clearFramebuffer() {
		_framebuffer_all_pools.clear();
	}

	public int getSpritesSize() {
		return _sprites_pools.size;
	}

	public int allSpritesCount() {
		int size = 0;
		for (int i = _sprites_pools.size - 1; i > -1; i--) {
			size += _sprites_pools.get(i).size();
		}
		return size;
	}

	public boolean pushSpritesPool(Sprites sprites) {
		if (!_sprites_pools.contains(sprites)) {
			return _sprites_pools.add(sprites);
		}
		return false;
	}

	public boolean popSpritesPool(Sprites sprites) {
		return _sprites_pools.remove(sprites);
	}

	public void closeSpritesPool() {
		for (int i = _sprites_pools.size - 1; i > -1; i--) {
			Sprites sprites = _sprites_pools.get(i);
			if (sprites != null) {
				sprites.close();
			}
		}
		_sprites_pools.clear();
	}

	public int getDesktopSize() {
		return _desktop_pools.size;
	}

	public int allDesktopCount() {
		int size = 0;
		for (int i = _desktop_pools.size - 1; i > -1; i--) {
			size += _desktop_pools.get(i).size();
		}
		return size;
	}

	public boolean pushDesktopPool(Desktop desktop) {
		if (!_desktop_pools.contains(desktop)) {
			return _desktop_pools.add(desktop);
		}
		return false;
	}

	public boolean popDesktopPool(Desktop desktop) {
		return _desktop_pools.remove(desktop);
	}

	public void closeDesktopPool() {
		for (int i = _desktop_pools.size - 1; i > -1; i--) {
			Desktop desktop = _desktop_pools.get(i);
			if (desktop != null) {
				desktop.close();
			}
		}
		_desktop_pools.clear();
	}

	public int getFontSize() {
		return _font_pools.size;
	}

	public boolean pushFontPool(IFont font) {
		if (!_font_pools.contains(font)) {
			return _font_pools.add(font);
		}
		return false;
	}

	public boolean popFontPool(IFont font) {
		return _font_pools.remove(font);
	}

	public IFont serachFontPool(String className, String fontName, int size) {
		if (className == null) {
			return null;
		}
		for (int i = _font_pools.size - 1; i > -1; i--) {
			IFont font = _font_pools.get(i);
			if (font != null && font.getSize() == size && font.getClass().getName().equals(className)
					&& font.getFontName().equals(fontName)) {
				return font;
			}
		}
		return null;
	}

	protected final void closeFontTempTexture() {
		for (int i = _font_pools.size - 1; i > -1; i--) {
			IFont font = _font_pools.get(i);
			if (font != null && font instanceof LFont) {
				((LFont) font).closeTempTexture();
			}
		}
	}

	public void closeFontPool() {
		for (int i = _font_pools.size - 1; i > -1; i--) {
			IFont font = _font_pools.get(i);
			if (font != null) {
				font.close();
			}
		}
		_font_pools.clear();
	}

	public static void freeStatic() {
		LGame._platform = null;
		LGame._base = null;
	}

	public abstract LGame.Type type();

	public abstract double time();

	public abstract int tick();

	public abstract void openURL(String url);

	public abstract Assets assets();

	public abstract Asyn asyn();

	public abstract Graphics graphics();

	public abstract InputMake input();

	public abstract Clipboard clipboard();

	public abstract Log log();

	public abstract Save save();

	public abstract Accelerometer accel();

	public abstract Support support();

	public LProcess process() {
		return processImpl;
	}

	public Json json() {
		return jsonImpl;
	}

	public Display display() {
		return displayImpl;
	}

	public void close() {
		if (!errors.isClosed()) {
			errors.clearConnections();
		}
		if (!status.isClosed()) {
			status.clearConnections();
		}
		if (!frame.isClosed()) {
			frame.clearConnections();
		}
	}

	/**
	 * 由于GWT不支持真实的反射，而完全模拟反射需要耗费大量资源，精确反射又难以控制用户具体使用的类，所以统一放弃外部反射方法，
	 * 不让用户有机会使用自定义的类操作。
	 */
	/*
	 * private Class<?> getType(Object o) { if (o instanceof Integer) { return
	 * Integer.TYPE; } else if (o instanceof Float) { return Float.TYPE; } else if
	 * (o instanceof Double) { return Double.TYPE; } else if (o instanceof Long) {
	 * return Long.TYPE; } else if (o instanceof Short) { return Short.TYPE; } else
	 * if (o instanceof Short) { return Short.TYPE; } else if (o instanceof Boolean)
	 * { return Boolean.TYPE; } else { return o.getClass(); } }
	 * 
	 * public Display register(Class<? extends Screen> clazz, Object... args) {
	 * LSystem.viewSize.setSize(setting.width, setting.height); this.display = new
	 * Display(this, setting.fps); if (args == null) { args = new Object[0]; } if
	 * (clazz != null) { if (args != null) { try { int funs = args.length; if (funs
	 * == 0) { display.setScreen(ClassReflection.newInstance(clazz)); } else {
	 * Class<?>[] functions = new Class<?>[funs]; for (int i = 0; i < funs; i++) {
	 * functions[i] = getType(args[i]); } Constructor constructor = ClassReflection
	 * .getConstructor(clazz, functions); Object o = constructor.newInstance(args);
	 * 
	 * if (o != null && (o instanceof Screen)) { display.setScreen((Screen) o); } }
	 * } catch (Exception e) { e.printStackTrace(); } } } return display; }
	 */

}
