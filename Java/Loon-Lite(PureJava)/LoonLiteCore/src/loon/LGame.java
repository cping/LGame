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

import loon.action.sprite.Sprites;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.component.Desktop;
import loon.events.EventActionFuture;
import loon.events.InputMake;
import loon.events.RunnableUpdate;
import loon.events.Updateable;
import loon.events.UpdateableRun;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.LSTRFont;
import loon.opengl.Mesh;
import loon.opengl.TextureSource;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.json.JsonImpl;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Act;
import loon.utils.reply.FutureResult;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;
import loon.utils.reply.Port;

/**
 * 此类为最主要的游戏功能类集合对象，所有Loon初始化由此开始，其中涵盖了Loon的基础对象实例。
 */
public abstract class LGame implements LRelease {

	/**
	 * 当前依赖的Java运行库
	 */
	public static enum Environment {
		JAVAFX, JAVASE, ANDROID, GWT
	}

	/**
	 * 当前运行的平台
	 */
	public static enum Sys {
		WINDOWS, MAC, LINUX, ANDROID, IOS, BROWSER, EMBEDDED
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

	private boolean _running = false;

	private boolean _stopGame = false;

	// 全部mesh
	private final TArray<Mesh> _mesh_all_pools;

	// 单独纹理批处理缓存
	private final IntMap<LTextureBatch> _texture_batch_pools;

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
		this._mesh_all_pools = new TArray<>(128);
		this._texture_batch_pools = new IntMap<>(12);
		this._texture_lazys = new ObjectMap<>(128);
		this._texture_all_list = new TArray<>(128);
		this._sprites_pools = new TArray<>(12);
		this._desktop_pools = new TArray<>(12);
		this._font_pools = new TArray<>(12);
		if (config == null) {
			config = new LSetting();
		}
		this.setting = config;
		this.jsonImpl = new JsonImpl();
		String appName = config.appName;
		if (StringUtils.isEmpty(appName)) {
			setting.appName = APP_NAME;
		}
		String fontName = config.fontName;
		if (StringUtils.isEmpty(fontName)) {
			setting.fontName = FONT_NAME;
		}
	}

	public LGame addStatus(Port<LGame> game) {
		frame.connect(game);
		status.connect(new Port<Status>() {

			@Override
			public void onEmit(Status event) {
				switch (event) {
				case EXIT:
					stop();
					break;
				case RESUME:
					LSystem.PAUSED = false;
					resume();
					break;
				case PAUSE:
					LSystem.PAUSED = true;
					pause();
					break;
				default:
					break;
				}
			}
		});
		return this;
	}

	public LGame removeStatus() {
		if (!errors.isClosed()) {
			errors.clearConnections();
		}
		if (!status.isClosed()) {
			status.clearConnections();
		}
		if (!frame.isClosed()) {
			frame.clearConnections();
		}
		return this;
	}

	public LGame pause() {
		try {
			synchronized (LGame.class) {
				if (displayImpl != null) {
					displayImpl.pause();
				}
			}
		} catch (Throwable cause) {
			LSystem.error("Pause Exception:", cause);
		}
		return this;
	}

	public LGame resume() {
		try {
			synchronized (LGame.class) {
				if (displayImpl != null) {
					displayImpl.resume();
				}
			}
		} catch (Throwable cause) {
			LSystem.error("Resume Exception:", cause);
		}
		return this;
	}

	public void stop() {
		if (!_stopGame) {
			try {
				synchronized (LGame.class) {
					LSystem.debug("The Loon Game Engine is End");
					LSystem.PAUSED = true;
					RealtimeProcessManager.get().dispose();
					LSystem.disposeTextureAll();
					LSystem.freeStaticObject();
					close();
					_stopGame = true;
				}
			} catch (Throwable cause) {
			}
		}
		this._running = false;
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
		this._stopGame = false;
		this.processImpl = new LProcess(game);
		this._running = true;
		log().debug("The Loon Game Engine is Begin");
	}

	/**
	 * 初始化LProcess组件,以处理游戏流程
	 *
	 * @return
	 */
	public LGame initProcess() {
		initProcess(this);
		return this;
	}

	public IFont setDefaultGameFont() {
		if (setting.defaultGameFont == null) {
			setting.defaultGameFont = LFont.getFont(setting.fontName, setting.fontSize);
		}
		return setting.defaultGameFont;
	}

	public IFont setDefaultLogFont() {
		if (setting.defaultLogFont == null) {
			if (setting.fontSize <= LSystem.DEFAULT_SYS_FONT_SIZE) {
				setting.defaultLogFont = LSTRFont.getFont(
						LSystem.isDesktop() ? LSystem.DEFAULT_SYS_FONT_SIZE - 4 : LSystem.DEFAULT_SYS_FONT_SIZE);
			} else {
				setting.defaultLogFont = LSTRFont.getFont(setting.fontSize);
			}
		}
		return setting.defaultLogFont;
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
			}
		}
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
	 * 返回游戏是否正在运行
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return this._running;
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
			if (displayImpl != null) {
				displayImpl.stopRepaint();
			}
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
	 * 预加载数据并返回结果
	 * 
	 * @param <T>
	 * @param call
	 * @return
	 */
	public <T> GoFuture<T> loadFuture(final FutureResult<T> call) {
		final Asyn asyn = asyn();
		if (asyn == null || call == null) {
			return GoFuture.failure(new LSysException("The Asyn object is null !"));
		}
		if (asyn.isAsyncSupported()) {
			return asyn.deferredPromise(call);
		}
		GoPromise<T> result = GoPromise.create();
		if (processImpl != null) {
			processImpl.addLoad(new RunnableUpdate(new EventActionFuture<T>(result, call)));
		}
		return result;
	}

	/**
	 * 如果本地支持异步提交Runnable,则使用本地的,如果不支持,则转换为process循环中提交
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
		} else if (processImpl != null) {
			processImpl.addLoad(new RunnableUpdate(action));
		}
		return this;
	}

	/**
	 * 如果本地支持异步提交Updateable,则使用本地的,如果不支持,则转换为process循环中提交
	 * 
	 * @param update
	 * @return
	 */
	public LGame invokeAsync(final Updateable update) {
		if (update == null) {
			return this;
		}
		if (isAsyncSupported()) {
			asyn().invokeAsync(new UpdateableRun(update));
		} else if (processImpl != null) {
			processImpl.addLoad(update);
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
			batchCaches = new IntMap<>(_texture_batch_pools);
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
			texs = new TArray<>(_texture_all_list);
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
				final int v = tex.getMemSize();
				if (v > 0) {
					memTotal += v;
				}
			}
		}
		return memTotal;
	}

	/**
	 * 关闭所有纹理
	 */
	public void closeAllTexture() {
		if (_texture_all_list.size > 0) {
			TArray<LTexture> tex2d = new TArray<>(_texture_all_list);
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
			return texture._referenceCount;
		}
		for (int i = 0, size = _texture_all_list.size; i < size; i++) {
			LTexture tex2d = _texture_all_list.get(i);
			String source = tex2d.getSource();
			if (tex2d != null && source.indexOf(TextureSource.RenderCanvas) == -1) {
				if (key.equalsIgnoreCase(source) || key.equalsIgnoreCase(tex2d.tmpLazy)) {
					return tex2d._referenceCount;
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
			refCount = texture._referenceCount--;
		} else {
			for (int i = 0; i < _texture_all_list.size; i++) {
				LTexture tex = _texture_all_list.get(i);
				if (tex != null && tex.tmpLazy.equals(name)) {
					texture = tex;
					refCount = tex._referenceCount--;
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
	 * @return
	 */
	public LTexture createTexture(int width, int height) {
		return graphics().createTexture(width, height);
	}

	/**
	 * 以指定位置图片和格式创建一个新纹理
	 *
	 * @param path
	 * @return
	 */
	public LTexture newTexture(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		log().debug("Texture : New " + path + " Loaded");
		return BaseIO.loadImage(path).createTexture();
	}

	/**
	 * 从缓存加载一个指定文件名纹理为指定格式(存在缓存时会得到缓存图片)
	 *
	 * @param fileName
	 * @return
	 */
	public LTexture loadTexture(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		synchronized (_texture_lazys) {
			String key = fileName.trim().toLowerCase();
			ObjectMap<String, LTexture> texs = new ObjectMap<>(_texture_lazys);
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
				texture._referenceCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).createTexture();
			texture.tmpLazy = fileName;
			_texture_lazys.put(key, texture);
			log().debug("Texture : " + fileName + " Loaded");
			return texture;
		}
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
	 * 以纹理id获得具体纹理
	 *
	 * @param id
	 * @return
	 */
	public LTexture getTexture(int id) {
		synchronized (_texture_all_list) {
			for (LTexture tex : _texture_all_list) {
				if (tex != null && !tex.isClosed() && tex.getID() == id) {
					return tex;
				}
			}
			return null;
		}
	}

	/**
	 * 删除所有从路径加载的纹理图片并强制销毁纹理(但是手动生成的纹理此处不销毁,仅删除)
	 */
	public void destroySourceAllCache() {
		if (_texture_lazys.size > 0) {
			TArray<LTexture> textures = new TArray<>(_texture_lazys.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed() && tex2d.getSource() != null
						&& tex2d.getSource().indexOf(TextureSource.RenderCanvas) == -1) {
					tex2d._referenceCount = 0;
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
			TArray<LTexture> textures = new TArray<>(_texture_lazys.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed()) {
					tex2d._referenceCount = 0;
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

	public void clearSpritesPool() {
		_sprites_pools.clear();
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

	public void clearDesktopPool() {
		_desktop_pools.clear();
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

	public abstract Image snapshot();

	public abstract Mesh makeMesh(Canvas canvas);

	public abstract Environment env();

	public abstract double time();

	public abstract int tick();

	public abstract Assets assets();

	public abstract Asyn asyn();

	public abstract Graphics graphics();

	public abstract InputMake input();

	public abstract Clipboard clipboard();

	public abstract Log log();

	public abstract Save save();

	public abstract Accelerometer accel();

	public LProcess process() {
		return processImpl;
	}

	public Json json() {
		return jsonImpl;
	}

	public Display display() {
		return displayImpl;
	}

	public LGame startRepaint() {
		if (displayImpl != null) {
			displayImpl.startRepaint();
		}
		return this;
	}

	public LGame stopRepaint() {
		if (displayImpl != null) {
			displayImpl.stopRepaint();
		}
		return this;
	}

	public abstract Sys getPlatform();

	public abstract boolean isMobile();

	public abstract boolean isDesktop();

	public abstract boolean isBrowser();

	@Override
	public void close() {
		removeStatus();
		if (assets() != null) {
			assets().close();
		}
		if (displayImpl != null) {
			displayImpl.close();
		}
	}

	public void shutdown() {
		if (status.isClosed()) {
			return;
		}
		status.emit(Status.EXIT);
	}
}
