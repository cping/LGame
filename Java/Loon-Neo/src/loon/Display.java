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
import loon.action.sprite.Sprites;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.Desktop;
import loon.font.IFont;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteOutput;
import loon.utils.GLUtils;
import loon.utils.GifEncoder;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class Display extends LSystemView {

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

	private final RealtimeProcessManager manager;

	// 为了方便直接转码到C#和C++，无法使用匿名内部类(也就是在构造内直接构造实现的方式)，只能都写出具体类来……
	// PS:别提delegate，委托那玩意写出来太不优雅了(对于凭空实现某接口或抽象，而非局部重载来说)，而且大多数J2C#的工具也不能直接转换过去……
	private final class PaintPort extends Port<LTimerContext> {

		private final Display _display;

		PaintPort(Display d) {
			this._display = d;
		}

		@Override
		public void onEmit(LTimerContext clock) {
			_display.draw(clock);
		}

	}

	private final class UpdatePort extends Port<LTimerContext> {

		UpdatePort() {
		}

		@Override
		public void onEmit(LTimerContext clock) {
			manager.tick(clock);
			ActionControl.update(clock.timeSinceLastUpdate);
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
				this.centerX = (int) (LSystem.viewSize.width) / 2 - logo.getWidth() / 2;
				this.centerY = (int) (LSystem.viewSize.height) / 2 - logo.getHeight() / 2;
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
			gl.setAlpha(alpha);
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

	private long frameCount;

	private int frameRate, frames;

	private IFont fpsFont;

	private float cred, cgreen, cblue, calpha;

	private final GLEx glEx;

	private final LProcess process;

	private LSetting setting;

	boolean showLogo = false, initDrawConfig = false;;

	private Logo logoTex;

	private void newDefView(boolean show) {
		if (show && fpsFont == null) {
			this.fpsFont = LSystem.getSystemLogFont();
		}
		showLogo = setting.isLogo;
		if (showLogo && !StringUtils.isEmpty(setting.logoPath)) {
			logoTex = new Logo(newTexture(setting.logoPath));
		}
	}

	public Display(LGame game, int updateRate) {
		super(game, updateRate);
		setting = LSystem._base.setting;
		process = LSystem._process;
		manager = RealtimeProcessManager.get();
		GL20 gl = game.graphics().gl;
		glEx = new GLEx(game.graphics(), game.graphics().defaultRenderTarget, gl);
		glEx.update();
		paint.connect(new PaintPort(this)).setPriority(-1);
		update.connect(new UpdatePort()).setPriority(1);
		if (!setting.isLogo) {
			process.start();
		}
	}

	public void setScreen(Screen screen) {
		process.setScreen(screen);
	}

	public LProcess getProcess() {
		return process;
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

	protected void draw(LTimerContext clock) {

		// fix渲染时机，避免调用渲染在纹理构造前
		if (!initDrawConfig) {
			newDefView(setting.isFPS || setting.isLogo || setting.isMemory || setting.isSprites || setting.isDebug);
			initDrawConfig = true;
		}

		if (showLogo) {
			try {
				glEx.save();
				glEx.begin();
				glEx.clear(cred, cgreen, cblue, calpha);
				if (logoTex == null || logoTex.finish || logoTex.logo.disposed()) {
					showLogo = false;
					return;
				}
				logoTex.draw(glEx);
				if (logoTex.finish) {
					showLogo = false;
					logoTex.close();
					logoTex = null;
				}
			} finally {
				glEx.end();
				glEx.restore();
				if (!showLogo) {
					process.start();
				}
			}
			return;
		}

		if (!process.next()) {
			return;
		}
		try {
			glEx.saveTx();
			glEx.begin();
			glEx.reset(cred, cgreen, cblue, calpha);

			process.load();
			process.calls();
			process.runTimer(clock);
			process.draw(glEx);

			final boolean debug = setting.isDebug;
			// 显示fps速度
			if (debug || setting.isFPS) {
				tickFrames();
				fpsFont.drawString(glEx, "FPS:" + frameRate, 5, 5, 0, LColor.white);
			}
			// 显示内存
			if (debug || setting.isMemory) {
				if (runtime == null) {
					runtime = Runtime.getRuntime();
				}
				long totalMemory = runtime.totalMemory();
				long currentMemory = totalMemory - runtime.freeMemory();
				String memory = ((float) ((currentMemory * 10) >> 20) / 10) + " of "
						+ ((float) ((runtime.maxMemory() * 10) >> 20) / 10) + " MB";
				fpsFont.drawString(glEx, "MEMORY:" + memory, 5, 25, 0, LColor.white);
			}
			if (debug || setting.isSprites) {
				fpsFont.drawString(glEx,
						"SPRITE:" + Sprites.allSpritesCount() + "," + " DESKTOP:" + Desktop.allDesktopCount(), 5, 45, 0,
						LColor.white);
			}
			// 若打印日志到界面,很可能挡住游戏界面内容,所以isDisplayLog为true并且debug才显示
			if (debug && setting.isDisplayLog) {
				process.paintLog(glEx, 5, 65);
			}
			process.drawEmulator(glEx);
			process.unload();

			// 如果存在屏幕录像设置
			if (videoScreenToGif && gifEncoder != null) {
				if (videoDelay.action(clock)) {
					Image tmp = GLUtils.getScreenshot();
					Image image = null;
					if (LSystem.isDesktop()) {
						image = tmp;
					} else {
						// 因为内存和速度关系,考虑到全平台录制,因此默认只录屏幕大小的一半(否则在手机上绝对抗不了5分钟以上……)
						image = Image.getResize(tmp, (int) (process.getWidth() * 0.5f),
								(int) (process.getHeight() * 0.5f));
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
			glEx.end();
			glEx.restoreTx();
			process.resetTouch();
		}

	}

	public Display resize(int viewWidth, int viewHeight) {
		process.resize(viewWidth, viewHeight);
		return this;
	}

	private void tickFrames() {
		long time = System.currentTimeMillis();
		if (time - frameCount > 1000L) {
			frameRate = MathUtils.min(setting.fps, frames);
			frames = 0;
			frameCount = time;
		}
		frames++;
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
		return glEx;
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
