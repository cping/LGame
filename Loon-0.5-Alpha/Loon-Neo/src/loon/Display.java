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

import loon.action.ActionControl;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.opengl.GL20;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class Display extends LSystemView {

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
				this.centerX = (int) (LSystem.viewSize.width) / 2
						- logo.getWidth() / 2;
				this.centerY = (int) (LSystem.viewSize.height) / 2
						- logo.getHeight() / 2;
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

	private long frameCount;

	private int frameRate, frames;

	private LSTRFont fpsFont;

	private float cred, cgreen, cblue, calpha;

	private final GLEx glEx;

	private final LProcess process;

	private final String pFontString = " MEORYFPSB0123456789:.of";

	private LSetting setting;

	boolean showLogo = false;

	private Logo logoTex;

	private void newDefView(boolean show) {
		if (show && fpsFont == null) {
			this.fpsFont = new LSTRFont(LFont.getDefaultFont(), pFontString);
		}
		showLogo = setting.isLogo;
		if (showLogo && !StringUtils.isEmpty(setting.logoPath)) {
			logoTex = new Logo(newTexture(setting.logoPath));
		}
	}

	public Display(LGame game, int updateRate) {
		super(game, updateRate);
		setting = LSystem._base.setting;
		newDefView(setting.isFPS);
		process = LSystem._process;
		manager = RealtimeProcessManager.get();
		GL20 gl = game.graphics().gl;
		glEx = new GLEx(game.graphics(), game.graphics().defaultRenderTarget,
				gl);
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
		if (showLogo) {
			try {
				glEx.save();
				glEx.begin();
				glEx.clear(cred, cgreen, cblue, calpha);
				if (logoTex == null || logoTex.finish
						|| logoTex.logo.disposed()) {
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

		glEx.saveTx();
		glEx.begin();

		glEx.reset(cred, cgreen, cblue, calpha);
		try {
			process.load();
			process.calls();
			process.runTimer(clock);

			int repaintMode = process.getRepaintMode();
			switch (repaintMode) {
			case Screen.SCREEN_NOT_REPAINT:
				break;
			case Screen.SCREEN_TEXTURE_REPAINT:
				if (process.getX() == 0 && process.getY() == 0) {
					glEx.draw(process.getBackground(), 0, 0);
				} else {
					glEx.draw(process.getBackground(), process.getX(),
							process.getY());
				}
				break;
			case Screen.SCREEN_COLOR_REPAINT:
				LColor c = process.getColor();
				if (c != null) {
					glEx.clear(c);
				}
				break;
			default:
				if (process.getX() == 0 && process.getY() == 0) {
					glEx.draw(process.getBackground(), repaintMode / 2
							- MathUtils.random(repaintMode), repaintMode / 2
							- MathUtils.random(repaintMode));
				} else {
					glEx.draw(
							process.getBackground(),
							process.getX() + repaintMode / 2
									- MathUtils.random(repaintMode),
							process.getY() + repaintMode / 2
									- MathUtils.random(repaintMode));
				}
				break;
			}

			process.draw(glEx);
			process.drawable(clock.timeSinceLastUpdate);

			if (setting.isFPS) {
				tickFrames();
				fpsFont.drawString(glEx, "FPS:" + frameRate, 5, 5, 0,
						LColor.white);
			}

			process.drawEmulator(glEx);
			process.unload();

		} finally {
			glEx.end();
			glEx.restoreTx();
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

}
