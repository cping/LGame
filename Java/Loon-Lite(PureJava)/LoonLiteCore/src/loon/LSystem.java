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

import loon.Log.Level;
import loon.action.sprite.Sprites;
import loon.component.DefUI;
import loon.component.Desktop;
import loon.events.KeyMake;
import loon.events.SysInput;
import loon.events.Updateable;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Dimension;
import loon.opengl.LSTRFont;
import loon.opengl.Mesh;
import loon.utils.NumberUtils;
import loon.utils.Scale;
import loon.utils.json.JsonImpl;
import loon.utils.reply.Act;

public class LSystem {

	private LSystem() {
	}

	// 版本号(正在不断完善中,试图把此版做成API以及功能基本稳定的版本,以后只优化与扩展api,而不替换删除api,所以0.5会持续的比较长……)
	private static final String _version = "0.5-beta";

	// 默认的字符串打印完毕flag
	public static final String FLAG_TAG = "▼";

	public static final String FLAG_SELECT_TAG = "◆";

	/** 表示空值和无效的占位用字符串 **/
	public static final String EMPTY = "";

	public static final String NULL = "null";

	public static final String UNKNOWN = "unknown";

	/** 常见字符串操作用符号 **/
	public static final char DOT = '.';

	public static final char SLASH = '/';

	public static final char BACKSLASH = '\\';

	public static final char CR = '\r';

	public static final char LF = '\n';

	public static final char TF = '\t';

	public static final char UNDERLINE = '_';

	public static final char DASHED = '-';
	
	public static final char EQUAL = '=';

	public static final char COMMA = ',';

	public static final char DELIM_START = '{';

	public static final char DELIM_END = '}';
	
	public static final char PAREN_START = '(';

	public static final char PAREN_END = ')';

	public static final char BRACKET_START = '[';

	public static final char BRACKET_END = ']';

	public static final char COLON = ':';

	public static final char DOUBLE_QUOTES = '"';

	public static final char SINGLE_QUOTE = '\'';

	public static final char AMP = '&';

	public static final char SPACE = ' ';

	public static final char TAB = '	';

	// 默认最大预加载数量
	public static final float DEFAULT_MAX_PRE_SIZE = 10000f;

	// 默认缓存数量
	public static final int DEFAULT_MAX_CACHE_SIZE = 32;

	// 默认缓动函数延迟
	public static final float DEFAULT_EASE_DELAY = 1f / 60f;

	public static final float MIN_SECONE_SPEED_FIXED = 0.008f;
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

	public static final String ENCODING = "UTF-8";

	public static boolean PAUSED = false;

	// 是否允许屏幕画面刷新
	protected static boolean _auto_repaint = true;

	private static float _scaleWidth = 1f;

	private static float _scaleHeight = 1f;

	public static final Platform platform() {
		return LGame._platform;
	}

	public static final LGame base() {
		LGame game = LGame._base;
		if (game != null) {
			return game;
		} else if (platform() != null) {
			game = platform().getGame();
		}
		return game;
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
		if (base() != null) {
			return base().setting.systemImgPath;
		}
		return "loon_";
	}

	/**
	 * 获得Loon系统自带的默认文件前缀
	 *
	 * @param path
	 * @return
	 */
	public static final String getSystemImagePath(String path) {
		return getSystemImagePath() + path;
	}

	/**
	 * 获得系统画面log中显示的字体(如果未设置则默认为本地字体渲染,字体大小16)
	 *
	 * @return
	 */
	public static final IFont getSystemLogFont() {
		if (base() != null) {
			if (base().setting.defaultLogFont == null) {
				base().setting.defaultLogFont = LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
			}
			return base().setting.defaultLogFont;
		}
		return LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
	}

	/**
	 * 设定游戏画面log中显示的字体文字
	 *
	 * @param font
	 */
	public static void setSystemLogFont(IFont font) {
		if (base() != null) {
			base().setting.defaultLogFont = font;
		}
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log,如果未设置则默认为本地字体渲染)
	 *
	 * @return
	 */
	public static final IFont getSystemGameFont() {
		if (base() != null) {
			if (base().setting.defaultGameFont == null) {
				base().setting.defaultGameFont = LFont.getFont(20);
			}
			return base().setting.defaultGameFont;
		}
		return LFont.getFont(20);
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log)
	 *
	 * @param font
	 */
	public static void setSystemGameFont(IFont font) {
		if (base() != null) {
			base().setting.defaultGameFont = font;
		}
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
		if (base() != null) {
			return base().setting.lockAllTouchEvent;
		}
		return false;

	}

	public static final boolean isNotAllowDragAndMove() {
		if (base() != null) {
			return base().setting.notAllowDragAndMove;
		}
		return false;

	}

	public static final float getEmulatorScale() {
		if (base() != null) {
			return base().setting.emulatorScale;
		}
		return 1f;
	}

	public static final boolean isTrueFontClip() {
		if (base() != null) {
			return base().setting.useTrueFontClip;
		}
		return true;
	}

	public static final boolean isConsoleLog() {
		if (base() != null) {
			return base().setting.isConsoleLog;
		}
		return true;
	}

	public static final String getSystemGameFontName() {
		if (base() != null) {
			return base().setting.fontName;
		}
		return LGame.FONT_NAME;
	}

	public static final String getSystemAppName() {
		if (base() != null) {
			return base().setting.appName;
		}
		return LGame.APP_NAME;
	}

	public static final String getVersion() {
		return _version;
	}

	public static void resetTextureRes() {
		resetTextureRes(base());
	}

	public static void resetTextureRes(final LGame game) {
		disposeTextureAll();
	}

	public static void exit() {
		if (platform() != null) {
			platform().close();
		}
	}

	public static void sysText(SysInput.TextEvent event, KeyMake.TextType textType, String label, String initialValue) {
		if (platform() != null) {
			platform().sysText(event, textType, label, initialValue);
		}
	}

	public static void sysDialog(SysInput.ClickEvent event, String title, String text, String ok, String cancel) {
		if (platform() != null) {
			platform().sysDialog(event, title, text, ok, cancel);
		}
	}

	public static final void setSize(int w, int h) {
		if (w < 0 || h < 0) {
			return;
		}
		viewSize.setSize(w, h);
	}

	public static Json json() {
		if (base() != null) {
			return base().json();
		}
		return new JsonImpl();
	}

	public static boolean isBrowser() {
		if (base() != null) {
			return base().isBrowser();
		}
		return false;
	}

	public static boolean isMobile() {
		if (base() != null) {
			return base().isMobile();
		}
		return false;
	}

	public static boolean isDesktop() {
		if (base() != null) {
			return base().isDesktop();
		}
		return false;
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
		if (base() != null) {
			return base().processImpl;
		}
		return null;
	}

	public static final boolean addResume(Updateable u) {
		if (getProcess() != null) {
			return getProcess().addResume(u);
		}
		return false;
	}

	public static final boolean removeResume(Updateable u) {
		if (getProcess() != null) {
			return getProcess().removeResume(u);
		}
		return false;
	}

	public static final boolean load(Updateable u) {
		if (getProcess() != null) {
			return getProcess().addLoad(u);
		}
		return false;
	}

	public static final boolean removeLoad(Updateable u) {
		if (getProcess() != null) {
			return getProcess().removeLoad(u);
		}
		return false;
	}

	public static final boolean containsLoad(Updateable u) {
		if (getProcess() != null) {
			return getProcess().containsLoad(u);
		}
		return false;
	}

	public static final boolean unload(Updateable u) {
		if (getProcess() != null) {
			return getProcess().addUnLoad(u);
		}
		return false;
	}

	public static final boolean removeUnLoad(Updateable u) {
		if (getProcess() != null) {
			return getProcess().removeUnLoad(u);
		}
		return false;
	}

	public static final boolean containsUnLoad(Updateable u) {
		if (getProcess() != null) {
			return getProcess().containsUnLoad(u);
		}
		return false;
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

	public static String getNotExtension(String name) {
		if (name == null) {
			return LSystem.EMPTY;
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return name;
		} else {
			return name.substring(0, index);
		}
	}

	public static final void stopRepaint() {
		LSystem._auto_repaint = false;
	}

	public static final void startRepaint() {
		LSystem._auto_repaint = true;
	}

	public static final boolean isEmulateFullVer() {
		return base() != null ? base().setting.emulateFullVer : false;
	}

	public static final <E> void dispatchEvent(Act<E> signal, E event) {
		if (base() != null) {
			base().dispatchEvent(signal, event);
		}
	}

	public static final void invokeLater(Runnable runnable) {
		if (base() != null) {
			base().invokeLater(runnable);
		}
	}

	public static final boolean isAsyncSupported() {
		return base() != null ? base().isAsyncSupported() : false;
	}

	public static final void invokeAsync(Runnable action) {
		if (base() != null) {
			base().invokeAsync(action);
		}
	}

	public static final int batchCacheSize() {
		if (base() != null) {
			return base().batchCacheSize();
		}
		return 0;
	}

	public static final void clearBatchCaches() {
		if (base() != null) {
			base().clearBatchCaches();
		}
	}

	public static final LTextureBatch getBatchCache(LTexture texture) {
		if (base() != null) {
			return base().getBatchCache(texture);
		}
		return null;
	}

	public static final LTextureBatch bindBatchCache(LTextureBatch batch) {
		if (base() != null) {
			return base().bindBatchCache(batch);
		}
		return null;
	}

	public static final LTextureBatch disposeBatchCache(LTextureBatch batch) {
		if (base() != null) {
			return base().disposeBatchCache(batch);
		}
		return null;
	}

	public static final LTextureBatch disposeBatchCache(LTextureBatch batch, boolean closed) {
		if (base() != null) {
			return base().disposeBatchCache(batch, closed);
		}
		return null;
	}

	public static final int[] resetIndices(int size, Mesh mesh) {
		int len = size * 6;
		int[] indices = new int[len];
		int j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (j + 1);
			indices[i + 2] = (j + 2);
			indices[i + 3] = (j + 2);
			indices[i + 4] = (j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);
		return indices;
	}

	public static final LTexture getTexture(int id) {
		if (base() != null) {
			return base().getTexture(id);
		}
		return null;
	}

	public static final boolean containsTexture(int id) {
		if (base() != null) {
			return base().containsTexture(id);
		}
		return false;
	}

	public static final void reloadTexture() {
		if (base() != null) {
			base().reloadTexture();
		}
	}

	public static final int getTextureMemSize() {
		if (base() != null) {
			return base().getTextureMemSize();
		}
		return 0;
	}

	public static final void closeAllTexture() {
		if (base() != null) {
			base().closeAllTexture();
		}
	}

	public static final int countTexture() {
		if (base() != null) {
			return base().countTexture();
		}
		return 0;
	}

	public static final boolean containsTextureValue(LTexture texture) {
		if (base() != null) {
			return base().containsTextureValue(texture);
		}
		return false;
	}

	public static final int getRefTextureCount(String fileName) {
		if (base() != null) {
			return base().getRefTextureCount(fileName);
		}
		return 0;
	}

	public static final LTexture createTexture(int width, int height) {
		if (base() != null) {
			return base().createTexture(width, height);
		}
		return null;
	}

	public static final LTexture newTexture(String path) {
		if (base() != null) {
			return base().newTexture(path);
		}
		return null;
	}

	public static final LTexture loadTexture(String fileName) {
		if (base() != null) {
			return base().loadTexture(fileName);
		}
		return null;
	}

	public static final void destroySourceAllCache() {
		if (base() != null) {
			base().destroySourceAllCache();
		}
	}

	public static final void destroyAllCache() {
		if (base() != null) {
			base().destroyAllCache();
		}
	}

	public static final void disposeTextureAll() {
		if (base() != null) {
			base().disposeTextureAll();
		}
	}

	protected static final void putTexture(LTexture texture) {
		if (base() != null) {
			base().putTexture(texture);
		}
	}

	protected static final void removeTexture(LTexture texture) {
		if (base() != null) {
			base().removeTexture(texture);
		}
	}

	protected static final boolean delTexture(int texID) {
		if (base() != null) {
			return base().delTexture(texID);
		}
		return false;
	}

	public static final int getSpritesSize() {
		if (base() != null) {
			return base().getSpritesSize();
		}
		return 0;
	}

	public static final int allSpritesCount() {
		if (base() != null) {
			return base().allSpritesCount();
		}
		return 0;
	}

	public static final boolean pushSpritesPool(Sprites sprites) {
		if (base() != null) {
			return base().pushSpritesPool(sprites);
		}
		return false;
	}

	public static final boolean popSpritesPool(Sprites sprites) {
		if (base() != null) {
			return base().popSpritesPool(sprites);
		}
		return false;
	}

	public static final void clearSpritesPool() {
		if (base() != null) {
			base().clearSpritesPool();
		}
	}

	public static final void closeSpritesPool() {
		if (base() != null) {
			base().closeSpritesPool();
		}
	}

	public static final int allDesktopCount() {
		if (base() != null) {
			return base().allDesktopCount();
		}
		return 0;
	}

	public static final boolean pushDesktopPool(Desktop desktop) {
		if (base() != null) {
			return base().pushDesktopPool(desktop);
		}
		return false;
	}

	public static final boolean popDesktopPool(Desktop desktop) {
		if (base() != null) {
			return base().popDesktopPool(desktop);
		}
		return false;
	}

	public static final int getDesktopSize() {
		if (base() != null) {
			return base().getDesktopSize();
		}
		return 0;
	}

	public static final void clearDesktopPool() {
		if (base() != null) {
			base().clearDesktopPool();
		}
	}

	public static final void closeDesktopPool() {
		if (base() != null) {
			base().closeDesktopPool();
		}
	}

	public static final int getFontSize() {
		if (base() != null) {
			return base().getFontSize();
		}
		return 0;
	}

	public static final boolean pushFontPool(IFont font) {
		if (base() != null) {
			return base().pushFontPool(font);
		}
		return false;
	}

	public static final boolean popFontPool(IFont font) {
		if (base() != null) {
			return base().popFontPool(font);
		}
		return false;
	}

	public static final void closeFontPool() {
		if (base() != null) {
			base().closeFontPool();
		}
	}

	public static final IFont serachFontPool(String className, String fontName, int size) {
		if (base() != null) {
			return base().serachFontPool(className, fontName, size);
		}
		return null;
	}

	public static final void debug(String msg) {
		if (base() != null) {
			base().log().debug(msg);
		}
	}

	public static final void debug(String msg, Object... args) {
		if (base() != null) {
			base().log().debug(msg, args);
		}
	}

	public static final void debug(String msg, Throwable throwable) {
		if (base() != null) {
			base().log().debug(msg, throwable);
		}
	}

	public static final void info(String msg) {
		if (base() != null) {
			base().log().info(msg);
		}
	}

	public static final void info(String msg, Object... args) {
		if (base() != null) {
			base().log().info(msg, args);
		}
	}

	public static final void info(String msg, Throwable throwable) {
		if (base() != null) {
			base().log().info(msg, throwable);
		}
	}

	public static final void warn(String msg) {
		if (base() != null) {
			base().log().warn(msg);
		}
	}

	public static final void warn(String msg, Object... args) {
		if (base() != null) {
			base().log().warn(msg, args);
		}
	}

	public static final void warn(String msg, Throwable throwable) {
		if (base() != null) {
			base().log().warn(msg, throwable);
		}
	}

	public static final void error(String msg) {
		if (base() != null) {
			base().log().error(msg);
		}
	}

	public static final void error(String msg, Object... args) {
		if (base() != null) {
			base().log().error(msg, args);
		}
	}

	public static final void error(String msg, Throwable throwable) {
		if (base() != null) {
			base().log().error(msg, throwable);
		}
	}

	public static final void reportError(String msg, Throwable throwable) {
		if (base() != null) {
			base().reportError(msg, throwable);
		}
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

	public static final void setLogMinLevel(Level level) {
		if (base() != null) {
			base().log().setMinLevel(level);
		}
	}

	public static final boolean equals(final Object o1, final Object o2) {
		return (o1 == null) ? (o2 == null) : o1.equals(o2);
	}

	public static final void closeTemp() {
		clearSpritesPool();
		clearDesktopPool();
		DefUI.selfClear();
	}
}
