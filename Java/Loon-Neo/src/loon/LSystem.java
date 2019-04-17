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
import loon.Log.Level;
import loon.canvas.NinePatchAbstract.Repeat;
import loon.event.KeyMake;
import loon.event.SysInput;
import loon.event.Updateable;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Dimension;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.Mesh;
import loon.opengl.ShaderCmd;
import loon.opengl.ShaderProgram;
import loon.utils.NumberUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.json.JsonImpl;
import loon.utils.reply.Act;

public class LSystem {

	private LSystem() {
	}

	// 版本号(正在不断完善中,试图把此版做成API以及功能基本稳定的版本,以后只优化与扩展api,而不替换删除api,所以0.5会持续的比较长……)
	private static final String _version = "0.5-beta";

	// 默认的字符串打印完毕flag
	public static String FLAG_TAG = "▼";

	public static String FLAG_SELECT_TAG = "◆";

	// 默认缓存数量
	public static int DEFAULT_MAX_CACHE_SIZE = 32;

	// 默认缓动函数延迟
	public static float DEFAULT_EASE_DELAY = 1f / 60f;

	// 行分隔符
	public static final String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	public static final String FS = System.getProperty("file.separator", "/");

	// 换行标记
	public static final String NL = "\r\n";

	// 屏幕大小
	public static final Dimension viewSize = new Dimension(480, 320);

	public static final int MODE_NORMAL = 1;

	public static final int MODE_ALPHA_MAP = 2;

	public static final int MODE_ALPHA_BLEND = 3;

	public static final int MODE_COLOR_MULTIPLY = 4;

	public static final int MODE_ADD = 5;

	public static final int MODE_SCREEN = 6;

	public static final int MODE_ALPHA = 7;

	public static final int MODE_SPEED = 8;

	public static final int MODE_ALPHA_ONE = 9;

	public static final int MODE_NONE = 10;

	public static final int MODE_MASK = 11;

	public static final int MODE_LIGHT = 12;

	public static final int MODE_ALPHA_ADD = 13;

	public static final int MODE_MULTIPLY = 14;
	// 兆秒
	public static final long MSEC = 1;

	// 秒
	public static final long SECOND = 1000;

	// 分
	public static final long MINUTE = SECOND * 60;

	// 小时
	public static final long HOUR = MINUTE * 60;

	// 天
	public static final long DAY = HOUR * 24;

	// 周
	public static final long WEEK = DAY * 7;

	// 理论上一年
	public static final long YEAR = DAY * 365;

	public static String ENCODING = "UTF-8";

	public static boolean PAUSED = false;

	// 是否允许屏幕画面刷新
	protected static boolean _auto_repaint = true;
	// 包内默认的图片路径
	private static String _systemDefaultImgPath = "loon_";

	private static float _scaleWidth = 1f;

	private static float _scaleHeight = 1f;

	private static IFont _defaultLogFont = null;

	private static IFont _defaultGameFont = null;

	public static final String getGLExVertexShader() {
		ShaderCmd cmd = ShaderCmd.getCmd("glex_vertex");
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putAttributeVec4(ShaderProgram.POSITION_ATTRIBUTE);
			cmd.putAttributeVec4(ShaderProgram.COLOR_ATTRIBUTE);
			cmd.putAttributeVec2(ShaderProgram.TEXCOORD_ATTRIBUTE + "0");
			cmd.putUniformMat4("u_projTrans");
			cmd.putVaryingVec4("v_color");
			cmd.putVaryingVec2("v_texCoords");
			cmd.putMainCmd("   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
					+ "   v_color.a = v_color.a * (255.0/254.0);\n" + "   v_texCoords = "
					+ ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "   gl_Position =  u_projTrans * "
					+ ShaderProgram.POSITION_ATTRIBUTE + ";");
			return cmd.getShader();
		}
	}

	public static final String getGLExFragmentShader() {
		ShaderCmd cmd = ShaderCmd.getCmd("glex_fragment");
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putVarying("LOWP vec4", "v_color");
			cmd.putVaryingVec2("v_texCoords");
			cmd.putUniform("sampler2D", "u_texture");
			cmd.putMainLowpCmd("  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);");
			return cmd.getShader();
		}
	}

	public static final String getColorFragmentShader() {
		ShaderCmd cmd = ShaderCmd.getCmd("color_fragment");
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putUniform("LOWP vec4", "v_color");
			cmd.putVaryingVec2("v_texCoords");
			cmd.putUniform("sampler2D", "u_texture");
			cmd.putMainLowpCmd("  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);");
			return cmd.getShader();
		}
	}

	protected static LGame _base = null;

	protected static LProcess _process = null;

	protected static Platform _platform = null;

	protected static JsonImpl _json_instance = null;

	// 是否手机环境
	protected static boolean _on_mobile = false;

	// 是否HTML5环境
	protected static boolean _on_html5 = false;

	// 游戏字体(仅限LFont实现IFont接口时有效)
	protected static String _font_name = "Dialog";

	// 当前应用名称
	protected static String _app_name = "Loon";

	public static final Platform platform() {
		return _platform;
	}

	public static final LGame base() {
		if (_base != null) {
			return _base;
		} else if (_platform != null) {
			_base = _platform.getGame();
		}
		return _base;
	}

	public static final boolean landscape() {
		return viewSize.height < viewSize.width;
	}

	/**
	 * 获得Loon系统自带的默认文件前缀
	 * 
	 * @return
	 */
	public static final String getSystemImagePath() {
		return _systemDefaultImgPath;
	}

	/**
	 * 获得Loon系统自带的默认文件前缀
	 * 
	 * @param path
	 * @return
	 */
	public static final String getSystemImagePath(String path) {
		return _systemDefaultImgPath + path;
	}

	/**
	 * 获得系统画面log中显示的字体(如果未设置则默认为本地字体渲染,字体大小16)
	 * 
	 * @return
	 */
	public static final IFont getSystemLogFont() {
		if (_defaultLogFont == null) {
			_defaultLogFont = LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
		}
		return _defaultLogFont;
	}

	/**
	 * 设定游戏画面log中显示的字体文字
	 * 
	 * @param font
	 */
	public static void setSystemLogFont(IFont font) {
		_defaultLogFont = font;
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log,如果未设置则默认为本地字体渲染)
	 * 
	 * @return
	 */
	public static final IFont getSystemGameFont() {
		if (_defaultGameFont == null) {
			_defaultGameFont = LFont.getDefaultFont();
		}
		return _defaultGameFont;
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log)
	 * 
	 * @param font
	 */
	public static void setSystemGameFont(IFont font) {
		_defaultGameFont = font;
	}

	/**
	 * 设定游戏全局默认使用的字体文字
	 * 
	 * @param font
	 */
	public static void setSystemGlobalFont(IFont font) {
		LSystem.setSystemLogFont(font);
		LSystem.setSystemGameFont(font);
	}

	public static final boolean isLockAllTouchEvent() {
		if (_base != null) {
			return _base.setting.lockAllTouchEvent;
		}
		return false;

	}

	public static final boolean isNotAllowDragAndMove() {
		if (_base != null) {
			return _base.setting.notAllowDragAndMove;
		}
		return false;

	}

	public static final float getEmulatorScale() {
		if (_base != null) {
			return _base.setting.emulatorScale;
		}
		return 1f;
	}

	public static final boolean isTrueFontClip() {
		if (_base != null) {
			return _base.setting.useTrueFontClip;
		}
		return true;
	}

	public static final boolean isConsoleLog() {
		if (_base != null) {
			return _base.setting.isConsoleLog;
		}
		return true;
	}

	public static final String getSystemGameFontName() {
		if (_base != null) {
			return _base.setting.fontName;
		}
		return _font_name;
	}

	public static final String getSystemAppName() {
		if (_base != null) {
			return _base.setting.appName;
		}
		return _app_name;
	}

	public static final String getVersion() {
		return _version;
	}

	public static void resetTextureRes() {
		resetTextureRes(base());
	}

	public static void resetTextureRes(final LGame game) {
		LGame loonMain = game;
		if (loonMain == null) {
			loonMain = base();
		}
		Mesh.invalidateAllMeshes(loonMain);
		ShaderProgram.invalidateAllShaderPrograms(loonMain);
		disposeMeshPool();
		disposeTextureAll();
	}

	protected static void initProcess(LGame game) {
		_base = game;
		if (_base == null && _platform != null) {
			_base = _platform.getGame();
		}
		LSetting setting = _base.setting;
		setting.updateScale();
		LSystem.viewSize.setSize(setting.width, setting.height);
		_process = new LProcess(game);
		_on_html5 = (_base.type() == LGame.Type.HTML5);
		_on_mobile = _base.isMobile();
		_base.log().debug("The Loon Game Engine is Begin");
	}

	public static void exit() {
		if (_platform != null) {
			_platform.close();
		}
	}

	public static void sysText(SysInput.TextEvent event, KeyMake.TextType textType, String label, String initialValue) {
		if (_platform != null) {
			_platform.sysText(event, textType, label, initialValue);
		}
	}

	public static void sysDialog(SysInput.ClickEvent event, String title, String text, String ok, String cancel) {
		if (_platform != null) {
			_platform.sysDialog(event, title, text, ok, cancel);
		}
	}

	public static Json json() {
		if (_json_instance == null) {
			_json_instance = new JsonImpl();
		}
		return _json_instance;
	}

	public static boolean isHTML5() {
		return _on_html5;
	}

	public static boolean isMobile() {
		return _on_mobile;
	}

	public static boolean isDesktop() {
		return !_on_mobile && !_on_html5;
	}

	public static float getScaleWidth() {
		return LSystem._scaleWidth;
	}

	public static float getScaleHeight() {
		return LSystem._scaleHeight;
	}

	public static void setScaleWidth(float sx) {
		LSystem._scaleWidth = sx;
	}

	public static void setScaleHeight(float sy) {
		LSystem._scaleHeight = sy;
	}

	public static Scale getScale() {
		Graphics graphics = null;
		if (LSystem.base() != null) {
			graphics = LSystem.base().graphics();
		}
		return graphics == null ? new Scale(1f) : graphics.scale();
	}

	public static float invXScaled(float length) {
		return length / LSystem.getScaleWidth();
	}

	public static float invYScaled(float length) {
		return length / LSystem.getScaleWidth();
	}

	public static String getAllFileName(String name) {
		if (name == null) {
			return "";
		}
		int idx = name.lastIndexOf('.');
		return idx == -1 ? name : name.substring(0, idx);
	}

	public static String getFileName(String name) {
		if (name == null) {
			return "";
		}
		int length = name.length();
		int idx = name.lastIndexOf('/');
		if (idx == -1) {
			idx = name.lastIndexOf('\\');
		}
		int size = idx + 1;
		if (size < length) {
			return name.substring(size, length);
		} else {
			return "";
		}
	}

	public static String getExtension(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(index + 1);
		}
	}

	public static boolean mainDrawRunning() {
		if (_base == null) {
			return false;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			return gl.running();
		}
		return false;
	}

	public static void mainBeginDraw() {
		if (_base == null) {
			return;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			if (!gl.running()) {
				gl.begin();
			}
		}
	}

	public static void mainEndDraw() {
		if (_base == null) {
			return;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			if (gl.running()) {
				gl.end();
			}
		}
	}

	public static final void close(LRelease rel) {
		if (rel != null) {
			try {
				rel.close();
				rel = null;
			} catch (Throwable e) {
			}
		}
	}

	public static final LProcess getProcess() {
		return _process;
	}

	public static final void load(Updateable u) {
		if (_process != null) {
			_process.addLoad(u);
		}
	}

	public static final void unload(Updateable u) {
		if (_process != null) {
			_process.addUnLoad(u);
		}
	}

	public static ShaderProgram createShader(String ver, String fragment) {
		ShaderProgram shader = new ShaderProgram(ver, fragment);
		if (shader.isCompiled() == false) {
			throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		}
		return shader;
	}

	public static final String format(float value) {
		String fmt = String.valueOf(value);
		return fmt.indexOf('.') == -1 ? (fmt + ".0") : fmt;
	}

	public static final int unite(int hashCode, boolean value) {
		int v = value ? 1231 : 1237;
		return unite(hashCode, v);
	}

	public static final int unite(int hashCode, long value) {
		int v = (int) (value ^ (value >>> 32));
		return unite(hashCode, v);
	}

	public static final int unite(int hashCode, float value) {
		int v = NumberUtils.floatToIntBits(value);
		return unite(hashCode, v);
	}

	public static final int unite(int hashCode, Object value) {
		return unite(hashCode, value.hashCode());
	}

	public static final int unite(int hashCode, int value) {
		return 31 * hashCode + value;
	}

	public static boolean isImage(String extension) {
		return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("bmp")
				|| extension.equals("gif");
	}

	public static boolean isText(String extension) {
		return extension.equals("json") || extension.equals("xml") || extension.equals("txt")
				|| extension.equals("glsl") || extension.equals("fnt") || extension.equals("pack")
				|| extension.equals("obj") || extension.equals("atlas") || extension.equals("g3dj")
				|| extension.equals("tmx") || extension.equals("an") || extension.equals("text")
				|| extension.equals("cfg") || extension.equals("cvs");
	}

	public static final boolean isAudio(String extension) {
		return extension.equals("mp3") || extension.equals("ogg") || extension.equals("wav") || extension.equals("mid");
	}

	public static final void stopRepaint() {
		LSystem._auto_repaint = false;
	}

	public static final void startRepaint() {
		LSystem._auto_repaint = true;
	}

	public static final <E> void dispatchEvent(Act<E> signal, E event) {
		if (_base != null) {
			_base.dispatchEvent(signal, event);
		}
	}

	public static final void invokeLater(Runnable runnable) {
		if (_base != null) {
			_base.invokeLater(runnable);
		}
	}

	public static final boolean isAsyncSupported() {
		return _base != null ? false : _base.isAsyncSupported();
	}

	public static final void invokeAsync(Runnable action) {
		if (_base != null) {
			_base.invokeAsync(action);
		}
	}

	public static final int batchCacheSize() {
		if (_base != null) {
			return _base.batchCacheSize();
		}
		return 0;
	}

	public static final void clearBatchCaches() {
		if (_base != null) {
			_base.clearBatchCaches();
		}
	}

	public static final LTextureBatch getBatchCache(LTexture texture) {
		if (_base != null) {
			return _base.getBatchCache(texture);
		}
		return null;
	}

	public static final LTextureBatch bindBatchCache(LTextureBatch batch) {
		if (_base != null) {
			return _base.bindBatchCache(batch);
		}
		return null;
	}

	public static final LTextureBatch disposeBatchCache(LTextureBatch batch) {
		if (_base != null) {
			return _base.disposeBatchCache(batch);
		}
		return null;
	}

	public static final LTextureBatch disposeBatchCache(LTextureBatch batch, boolean closed) {
		if (_base != null) {
			return _base.disposeBatchCache(batch, closed);
		}
		return null;
	}

	public static final void resetIndices(int size, Mesh mesh) {
		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);
	}

	public static final Mesh getMeshPool(String n, int size) {
		if (_base != null) {
			return _base.getMeshPool(n, size);
		}
		return null;
	}

	public static final void resetMeshPool(String n, int size) {
		if (_base != null) {
			_base.resetMeshPool(n, size);
		}
	}

	public static final int getMeshPoolSize() {
		if (_base != null) {
			_base.getMeshPoolSize();
		}
		return 0;
	}

	public static final void disposeMeshPool(String name, int size) {
		if (_base != null) {
			_base.disposeMeshPool(name, size);
		}
	}

	public static final void disposeMeshPool() {
		if (_base != null) {
			_base.disposeMeshPool();
		}
	}

	public static final boolean containsTexture(int id) {
		if (_base != null) {
			return _base.containsTexture(id);
		}
		return false;
	}

	public static final void reloadTexture() {
		if (_base != null) {
			_base.reloadTexture();
		}
	}

	public static final int getTextureMemSize() {
		if (_base != null) {
			return _base.getTextureMemSize();
		}
		return 0;
	}

	public static final void closeAllTexture() {
		if (_base != null) {
			_base.closeAllTexture();
		}
	}

	public static final int countTexture() {
		if (_base != null) {
			return _base.countTexture();
		}
		return 0;
	}

	public static final boolean containsTextureValue(LTexture texture) {
		if (_base != null) {
			return _base.containsTextureValue(texture);
		}
		return false;
	}

	public static final int getRefTextureCount(String fileName) {
		if (_base != null) {
			return _base.getRefTextureCount(fileName);
		}
		return 0;
	}

	public static final LTexture createTexture(int width, int height, Format config) {
		if (_base != null) {
			return _base.createTexture(width, height, config);
		}
		return null;
	}

	public static final LTexture newTexture(String path) {
		if (_base != null) {
			return _base.newTexture(path);
		}
		return null;
	}

	public static final LTexture newTexture(String path, Format config) {
		if (_base != null) {
			return _base.newTexture(path, config);
		}
		return null;
	}

	public static final LTexture loadNinePatchTexture(String fileName, int x, int y, int w, int h) {
		if (_base != null) {
			return _base.loadNinePatchTexture(fileName, x, y, w, h);
		}
		return null;
	}

	public static final LTexture loadNinePatchTexture(String fileName, Repeat repeat, int x, int y, int w, int h,
			Format config) {
		if (_base != null) {
			return _base.loadNinePatchTexture(fileName, repeat, x, y, w, h, config);
		}
		return null;
	}

	public static final LTexture loadTexture(String fileName, Format config) {
		if (_base != null) {
			return _base.loadTexture(fileName, config);
		}
		return null;
	}

	public static final LTexture loadTexture(String fileName) {
		if (_base != null) {
			return _base.loadTexture(fileName);
		}
		return null;
	}

	public static final void destroySourceAllCache() {
		if (_base != null) {
			_base.destroySourceAllCache();
		}
	}

	public static final void destroyAllCache() {
		if (_base != null) {
			_base.destroyAllCache();
		}
	}

	public static final void disposeTextureAll() {
		if (_base != null) {
			_base.disposeTextureAll();
		}
	}

	protected static final void putTexture(LTexture texture) {
		if (_base != null) {
			_base.putTexture(texture);
		}
	}

	protected static final void removeTexture(LTexture texture) {
		if (_base != null) {
			_base.removeTexture(texture);
		}
	}

	protected static final boolean delTexture(int texID) {
		if (_base != null) {
			return _base.delTexture(texID);
		}
		return false;
	}

	public static final void debug(String msg) {
		if (_base != null) {
			_base.log().debug(msg);
		}
	}

	public static final void debug(String msg, Object... args) {
		if (_base != null) {
			_base.log().debug(msg, args);
		}
	}

	public static final void debug(String msg, Throwable throwable) {
		if (_base != null) {
			_base.log().debug(msg, throwable);
		}
	}

	public static final void info(String msg) {
		if (_base != null) {
			_base.log().info(msg);
		}
	}

	public static final void info(String msg, Object... args) {
		if (_base != null) {
			_base.log().info(msg, args);
		}
	}

	public static final void info(String msg, Throwable throwable) {
		if (_base != null) {
			_base.log().info(msg, throwable);
		}
	}

	public static final void warn(String msg) {
		if (_base != null) {
			_base.log().warn(msg);
		}
	}

	public static final void warn(String msg, Object... args) {
		if (_base != null) {
			_base.log().warn(msg, args);
		}
	}

	public static final void warn(String msg, Throwable throwable) {
		if (_base != null) {
			_base.log().warn(msg, throwable);
		}
	}

	public static final void error(String msg) {
		if (_base != null) {
			_base.log().error(msg);
		}
	}

	public static final void error(String msg, Object... args) {
		if (_base != null) {
			_base.log().error(msg, args);
		}
	}

	public static final void error(String msg, Throwable throwable) {
		if (_base != null) {
			_base.log().error(msg, throwable);
		}
	}

	public static final void reportError(String msg, Throwable throwable) {
		if (_base != null) {
			_base.reportError(msg, throwable);
		}
	}

	public static final RuntimeException runThrow(String msg) {
		error(msg);
		return new RuntimeException(msg);
	}

	public static final RuntimeException runThrow(String msg, Throwable thr) {
		error(msg, thr);
		return new RuntimeException(msg, thr);
	}

	public static final RuntimeException runThrow(String msg, Object... args) {
		error(msg, args);
		return new RuntimeException(StringUtils.format(msg, args));
	}

	public static final void d(String msg) {
		debug(msg);
	}

	public static final void d(String msg, Object... args) {
		debug(msg, args);
	}

	public static final void d(String msg, Throwable throwable) {
		debug(msg, throwable);
	}

	public static final void i(String msg) {
		info(msg);
	}

	public static final void i(String msg, Object... args) {
		info(msg, args);
	}

	public static final void i(String msg, Throwable throwable) {
		info(msg, throwable);
	}

	public static final void w(String msg) {
		warn(msg);
	}

	public static final void w(String msg, Object... args) {
		warn(msg, args);
	}

	public static final void w(String msg, Throwable throwable) {
		warn(msg, throwable);
	}

	public static final void e(String msg) {
		error(msg);
	}

	public static final void e(String msg, Object... args) {
		error(msg, args);
	}

	public static final void e(String msg, Throwable throwable) {
		error(msg, throwable);
	}

	public static final RuntimeException re(String msg) {
		return runThrow(msg);
	}

	public static final RuntimeException re(String msg, Throwable thr) {
		return runThrow(msg, thr);
	}

	public static final RuntimeException re(String msg, Object... args) {
		return runThrow(msg, args);
	}

	public static final void setLogMinLevel(Level level) {
		if (_base != null) {
			_base.log().setMinLevel(level);
		}
	}

}
