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
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.GameClock;
import loon.utils.timer.LTimerContext;

public class Display extends LSystemView {

	final static class Logo implements LRelease {

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

	private LTimerContext context = new LTimerContext();

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
		GL20 gl = game.graphics().gl;
		glEx = new GLEx(game.graphics(), game.graphics().defaultRenderTarget,
				gl);
		glEx.update();
		paint.connect(new Port<GameClock>() {
			public void onEmit(GameClock clock) {
				context.setTimeSinceLastUpdate(clock.dt);
				paint(context);
			}
		}).setPriority(-1);
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

	protected void paint(LTimerContext timerContext) {
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

			RealtimeProcessManager.get().tick(context.timeSinceLastUpdate);

			ActionControl.update(context.timeSinceLastUpdate);

			process.runTimer(timerContext);

			if (LSystem.AUTO_REPAINT) {

				int repaintMode = process.getRepaintMode();
				switch (repaintMode) {
				case Screen.SCREEN_BITMAP_REPAINT:
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
				case Screen.SCREEN_CANVAS_REPAINT:
					break;
				case Screen.SCREEN_NOT_REPAINT:
					break;
				default:
					if (process.getX() == 0 && process.getY() == 0) {
						glEx.draw(
								process.getBackground(),
								repaintMode / 2
										- LSystem.random.nextInt(repaintMode),
								repaintMode / 2
										- LSystem.random.nextInt(repaintMode));
					} else {
						glEx.draw(process.getBackground(),
								process.getX() + repaintMode / 2
										- LSystem.random.nextInt(repaintMode),
								process.getY() + repaintMode / 2
										- LSystem.random.nextInt(repaintMode));
					}
					break;
				}

				process.draw(glEx);
				process.drawable(context.timeSinceLastUpdate);

				if (setting.isFPS) {
					tickFrames();
					fpsFont.drawString("FPS:" + frameRate, 5, 5, 0,
							LColor.white);
				}

				process.drawEmulator(glEx);
				process.unload();

			}
		} finally {
			glEx.end();
			glEx.restoreTx();
		}
	}

	public Display resize(Scale scale, int viewWidth, int viewHeight) {
		process.resize(scale, viewWidth, viewHeight);
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
