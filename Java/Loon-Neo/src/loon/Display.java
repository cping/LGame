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

import java.io.OutputStream;

import loon.action.ActionControl;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.opengl.ShaderSource;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteOutput;
import loon.utils.GLUtils;
import loon.utils.GifEncoder;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

/**
 * Loon显示实际载体用类,主渲染器GLEx于此类中被构建,但用户无需直接使用此类,细节操作已被融入Screen中
 */
public class Display extends LSystemView {

	private final static String FPS_STR = "FPS:";

	private final static String MEMORY_STR = "MEMORY:";

	private final static String SPRITE_STR = "SPRITE:";

	private final static String DESKTOP_STR = "DESKTOP:";

	private String displayMemony = MEMORY_STR;

	private String displaySprites = SPRITE_STR;

	private StrBuilder displayMessage = new StrBuilder(32);

	private GifEncoder gifEncoder;

	private boolean videoScreenToGif;

	private ArrayByteOutput videoCache;

	private final LTimer videoDelay = new LTimer();

	/**
	 * 返回video的缓存结果(不设置out对象时才会有效)
	 * 
	 * @return
	 */
	public ArrayByte getVideoCache() {
		return videoCache.getArrayByte();
	}

	/**
	 * 开始录像(默认使用ArrayByte缓存录像结果到内存中)
	 * 
	 * @return
	 */
	public GifEncoder startVideo() {
		return startVideo(videoCache = new ArrayByteOutput());
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @return
	 */
	public GifEncoder startVideo(OutputStream output) {
		return startVideo(output, LSystem.isDesktop() ? LSystem.SECOND : LSystem.SECOND + LSystem.SECOND / 2);
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @param delay
	 * @return
	 */
	public GifEncoder startVideo(OutputStream output, long delay) {
		stopVideo();
		videoDelay.setDelay(delay);
		gifEncoder = new GifEncoder();
		gifEncoder.start(output);
		gifEncoder.setDelay((int) delay);
		videoScreenToGif = true;
		return gifEncoder;
	}

	/**
	 * 结束录像
	 * 
	 * @return
	 */
	public GifEncoder stopVideo() {
		if (gifEncoder != null) {
			gifEncoder.finish();
		}
		videoScreenToGif = false;
		return gifEncoder;
	}

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出具体类来……
	// PS:别提delegate，委托那玩意写出来太不优雅了(对于凭空实现某接口或抽象，而非局部重载来说)，而且大多数J2C#的工具也不能直接转换过去……
	private final class PaintPort extends Port<LTimerContext> {

		private final Display _display;

		PaintPort(Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					RealtimeProcessManager.get().tick(clock);
					_display.draw(clock);
				}
			}
		}

	}

	private final class PaintAllPort extends Port<LTimerContext> {

		private final Display _display;

		PaintAllPort(Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					RealtimeProcessManager.get().tick(clock);
					ActionControl.get().call(clock.timeSinceLastUpdate);
					_display.draw(clock);
				}
			}
		}

	}

	private final class UpdatePort extends Port<LTimerContext> {

		UpdatePort() {
		}

		@Override
		public void onEmit(LTimerContext clock) {
			synchronized (clock) {
				if (!LSystem.PAUSED) {
					ActionControl.get().call(clock.timeSinceLastUpdate);
				}
			}
		}
	}

	private final class Logo implements LRelease {

		private int centerX = 0, centerY = 0;

		private float alpha = 0f;

		private float curFrame, curTime;

		boolean finish, inToOut;

		LTexture logo;

		public Logo(LTexture texture) {
			this.logo = texture;
			this.curTime = 60;
			this.curFrame = 0;
			this.inToOut = true;
		}

		public void draw(final GLEx gl) {
			if (logo == null || finish) {
				return;
			}
			if (!logo.isLoaded()) {
				this.logo.loadTexture();
			}
			if (centerX == 0 || centerY == 0) {
				this.centerX = (LSystem.viewSize.getWidth()) / 2 - logo.getWidth() / 2;
				this.centerY = (LSystem.viewSize.getHeight()) / 2 - logo.getHeight() / 2;
			}
			if (logo == null || !logo.isLoaded()) {
				return;
			}
			alpha = (curFrame / curTime);
			if (inToOut) {
				curFrame++;
				if (curFrame == curTime) {
					alpha = 1f;
					inToOut = false;
				}
			} else if (!inToOut) {
				curFrame--;
				if (curFrame == 0) {
					alpha = 0f;
					finish = true;
				}
			}
			gl.setAlpha(MathUtils.clamp(alpha, 0f, 0.98f));
			gl.draw(logo, centerX, centerY);
		}

		@Override
		public void close() {
			if (logo != null) {
				logo.close();
				logo = null;
			}
		}
	}

	private Runtime runtime;

	private long frameCount = 0l;

	private int frameRate = 0;

	private float frameDelta = 0f;

	private IFont fpsFont;

	private float cred, cgreen, cblue, calpha;

	private final GLEx _glEx;

	private final LProcess _process;

	private LSetting _setting;

	protected boolean showLogo = false, initDrawConfig = false;

	private Logo logoTex;

	private PaintAllPort paintAllPort;

	private PaintPort paintPort;

	private UpdatePort updatePort;

	protected void newDefView(boolean show) {
		if (show && (fpsFont == null || (fpsFont != LSystem.getSystemLogFont()))) {
			this.fpsFont = LSystem.getSystemLogFont();
		}
		showLogo = _setting.isLogo;
		if (showLogo && !StringUtils.isEmpty(_setting.logoPath)) {
			logoTex = new Logo(newTexture(_setting.logoPath));
		}
	}

	public Display(LGame game, int updateRate) {
		super(game, updateRate);
		Graphics graphics = game.graphics();
		GL20 gl = graphics.gl;
		_setting = game.setting;
		_process = game.process();
		_glEx = new GLEx(graphics, graphics.defaultRenderTarget, gl);
		_glEx.update();
		updateSyncTween(_setting.isSyncTween);
		this.displayMemony = MEMORY_STR + "0";
		this.displaySprites = SPRITE_STR + "0, " + DESKTOP_STR + "0";
		if (!_setting.isLogo) {
			_process.start();
		}
	}

	public void updateSyncTween(boolean sync) {
		if (paintAllPort != null) {
			paint.disconnect(paintAllPort);
		}
		if (paintPort != null) {
			paint.disconnect(paintPort);
		}
		if (update != null) {
			update.disconnect(updatePort);
		}
		if (sync) {
			paint.connect(paintAllPort = new PaintAllPort(this));
		} else {
			paint.connect(paintPort = new PaintPort(this));
			update.connect(updatePort = new UpdatePort());
		}
	}

	public void setScreen(Screen screen) {
		_process.setScreen(screen);
	}

	public LProcess getProcess() {
		return _process;
	}

	/**
	 * 清空当前游戏窗体内容为指定色彩
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void clearColor(float red, float green, float blue, float alpha) {
		cred = red;
		cgreen = green;
		cblue = blue;
		calpha = alpha;
	}

	/**
	 * 清空当前游戏窗体内容为指定色彩
	 * 
	 * @param color
	 */
	public void clearColor(LColor color) {
		this.clearColor(color.r, color.g, color.b, color.a);
	}

	/**
	 * 清空当前游戏窗体内容为纯黑色
	 */
	public void clearColor() {
		this.clearColor(0, 0, 0, 0);
	}

	/**
	 * 启用GLEx的全局渲染FrameBuffer(全局缓存渲染到纹理中去)
	 */
	public void enableFrameBuffer() {
		if (_glEx != null) {
			_glEx.enableFrameBuffer();
		}
	}

	/**
	 * 关闭GLEx的全局渲染FrameBuffer
	 */
	public void disableFrameBuffer() {
		if (_glEx != null) {
			_glEx.disableFrameBuffer();
		}
	}

	protected void draw(LTimerContext clock) {

		// fix渲染时机，避免调用渲染在纹理构造前
		if (!initDrawConfig) {
			newDefView(
					_setting.isFPS || _setting.isLogo || _setting.isMemory || _setting.isSprites || _setting.isDebug);
			initDrawConfig = true;
		}

		if (showLogo) {
			boolean saveBuffer = _glEx.isSaveFrameBuffer();
			try {
				if (saveBuffer) {
					_glEx.disableFrameBuffer();
				}
				_glEx.save();
				_glEx.begin();
				_glEx.clear(cred, cgreen, cblue, calpha);
				if (logoTex == null || logoTex.finish || logoTex.logo.disposed()) {
					showLogo = false;
					return;
				}
				logoTex.draw(_glEx);
				if (logoTex.finish) {
					showLogo = false;
					logoTex.close();
					logoTex = null;
				}
				if (saveBuffer) {
					_glEx.enableFrameBuffer();
				}
			} finally {
				_glEx.end();
				_glEx.restore();
				if (!showLogo) {
					_process.start();
				}
			}
			return;
		}

		if (!_process.next()) {
			return;
		}
		try {
			_glEx.saveTx();
			_glEx.reset(cred, cgreen, cblue, calpha);
			_glEx.begin();
			
			_process.drawFrist(_glEx);
			_process.load();
			_process.runTimer(clock);

			_process.draw(_glEx);
			// 渲染debug信息
			drawDebug(_glEx, _setting, clock.timeSinceLastUpdate);
			_process.drawEmulator(_glEx);
			// 最后渲染的内容
			_process.drawLast(_glEx);

			_process.unload();

			// 如果存在屏幕录像设置
			if (videoScreenToGif && !LSystem.PAUSED && gifEncoder != null) {
				if (videoDelay.action(clock)) {
					Image tmp = GLUtils.getScreenshot();
					Image image = null;
					if (LSystem.isDesktop()) {
						image = tmp;
					} else {
						// 因为内存和速度关系,考虑到全平台录制,因此默认只录屏幕大小的一半(否则在手机上绝对抗不了5分钟以上……)
						image = Image.getResize(tmp, (int) (_process.getWidth() * 0.5f),
								(int) (_process.getHeight() * 0.5f));
					}
					gifEncoder.addFrame(image);
					if (tmp != null) {
						tmp.close();
						tmp = null;
					}
					if (image != null) {
						image.close();
						image = null;
					}
				}
			}

		} finally {
			_glEx.end();
			_glEx.restoreTx();
			_glEx.clearFrame();
			_process.resetTouch();
		}

	}

	public void setShaderSource(ShaderSource src) {
		if (_glEx != null && src != null) {
			_glEx.setShaderSource(src);
		}
	}

	public ShaderSource getShaderSource() {
		if (_glEx != null) {
			return _glEx.getShaderSource();
		}
		return LSystem.DEF_SOURCE;
	}

	public Display resize(int viewWidth, int viewHeight) {
		_process.resize(viewWidth, viewHeight);
		return this;
	}

	/**
	 * 渲染debug信息到游戏画面
	 * 
	 * @param gl
	 * @param setting
	 * @param delta
	 */
	private final void drawDebug(final GLEx gl, final LSetting setting, final float delta) {

		final boolean debug = setting.isDebug;

		if (debug || setting.isFPS || setting.isMemory || setting.isSprites) {
			this.frameCount++;
			this.frameDelta += delta;

			if (frameCount % 60 == 0) {
				final int dstFPS = setting.fps;
				final int newFps = MathUtils.round((1000f * frameCount) / frameDelta) + 1;
				this.frameRate = MathUtils.clamp(newFps, 0, dstFPS);
				if (frameRate == dstFPS - 1) {
					frameRate = MathUtils.max(dstFPS, frameRate);
				}
				this.frameDelta = 0;
				this.frameCount = 0;

				if (runtime == null) {
					runtime = Runtime.getRuntime();
				}
				long totalMemory = runtime.totalMemory();
				long currentMemory = totalMemory - runtime.freeMemory();

				displayMessage.delete(0, displayMessage.length());
				displayMessage.append(MEMORY_STR);
				displayMessage.append(((float) ((currentMemory * 10) >> 20) / 10f));
				displayMessage.append(" of ");
				displayMessage.append(((float) ((runtime.maxMemory() * 10) >> 20) / 10f));
				displayMessage.append(" MB");

				displayMemony = displayMessage.toString();

				LGame game = getGame();

				displayMessage.delete(0, displayMessage.length());
				displayMessage.append(SPRITE_STR);
				displayMessage.append(game.allSpritesCount());
				displayMessage.append(", ");
				displayMessage.append(DESKTOP_STR);
				displayMessage.append(game.allDesktopCount());

				displaySprites = displayMessage.toString();

			}
			// 显示fps速度
			if (debug || setting.isFPS) {
				fpsFont.drawString(gl, FPS_STR + frameRate, 5, 5, 0, LColor.white);
			}
			// 显示内存占用
			if (debug || setting.isMemory) {
				fpsFont.drawString(gl, displayMemony, 5, 25, 0, LColor.white);
			}
			// 显示精灵与组件数量
			if (debug || setting.isSprites) {
				fpsFont.drawString(gl, displaySprites, 5, 45, 0, LColor.white);
			}
			// 若打印日志到界面,很可能挡住游戏界面内容,所以isDisplayLog为true并且debug才显示
			if (debug && setting.isDisplayLog) {
				_process.paintLog(gl, 5, 65);
			}
		}
	}

	public int getFPS() {
		return frameRate;
	}

	public float getAlpha() {
		return calpha;
	}

	public float getRed() {
		return cred;
	}

	public float getGreen() {
		return cgreen;
	}

	public float getBlue() {
		return cblue;
	}

	public GLEx GL() {
		return _glEx;
	}

	public float width() {
		return LSystem.viewSize.width();
	}

	public float height() {
		return LSystem.viewSize.height;
	}

	public void close() {
		if (this.fpsFont != null) {
			this.fpsFont.close();
			this.fpsFont = null;
		}
		if (this.logoTex != null) {
			this.logoTex.close();
			this.logoTex = null;
		}
		initDrawConfig = false;
	}
}
