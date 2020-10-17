/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package org.test;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.action.map.TetrisField;
import loon.action.map.TetrisField.TetrisListener;
import loon.canvas.LColor;
import loon.component.LPad;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class TetrisTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	final static class TetrisDrawImpl implements TetrisListener {

		private TetrisField field2d;

		public TetrisDrawImpl(TetrisField tf) {
			this.field2d = tf;
		}

		@Override
		public void draw(GLEx g, float x, float y, LTexture[] stones) {
			// 获得下一个方块编号
			int nextStone = field2d.getNextStone();
			LTexture tex = stones[nextStone];
			// 绘制下一个编号的方块在屏幕上
			switch (nextStone) {
			case 1:
				g.draw(tex, 240, 60);
				g.draw(tex, 260, 60);
				g.draw(tex, 240, 80);
				g.draw(tex, 260, 80);
				break;
			case 2:
				g.draw(tex, 220, 70);
				g.draw(tex, 240, 70);
				g.draw(tex, 260, 70);
				g.draw(tex, 280, 70);
				break;
			case 3:
				g.draw(tex, 250, 60);
				g.draw(tex, 230, 80);
				g.draw(tex, 250, 80);
				g.draw(tex, 270, 80);
				break;
			case 4:
				g.draw(tex, 270, 60);
				g.draw(tex, 230, 80);
				g.draw(tex, 250, 80);
				g.draw(tex, 270, 80);
				break;
			case 5:
				g.draw(tex, 230, 60);
				g.draw(tex, 230, 80);
				g.draw(tex, 250, 80);
				g.draw(tex, 270, 80);
				break;
			case 6:
				g.draw(tex, 230, 60);
				g.draw(tex, 250, 60);
				g.draw(tex, 250, 80);
				g.draw(tex, 270, 80);
				break;
			case 7:
				g.draw(tex, 250, 60);
				g.draw(tex, 270, 60);
				g.draw(tex, 230, 80);
				g.draw(tex, 250, 80);
			}

		}

	}

	private int curLevel = 1;

	private int blockSize = 20;

	private TetrisField field2d;

	private LTexture[] blocks;

	@Override
	public void draw(GLEx g) {
		if (!field2d.isGameOver()) {
			int x, y;
			int[][] arrayStones = field2d.getStonePosition();
			for (x = 0; x < field2d.getRows(); x++) {
				for (y = 0; y < field2d.getCols(); y++) {
					if (arrayStones[x][y] != 0) {
						g.draw(blocks[arrayStones[x][y]], x * blockSize, y * blockSize, blockSize, blockSize);
					}
				}
			}
			// 绘制下一个方块样式
			field2d.draw(g, blocks);
		} else {
			g.setColor(LColor.white);
			g.drawString("GAME OVER", 120, 160);
		}
		g.setColor(LColor.white);
		g.drawString("等级:" + Integer.toString(field2d.getLevel()), 380, 120);
		g.drawString("消层:" + Integer.toString(field2d.getLines()), 380, 160);
		g.drawString("得分:" + Integer.toString(field2d.getPoints()), 380, 200);
	}

	@Override
	public void onLoad() {
		// 最后绘制组件
		lastDesktopDraw();
		
		LTexture texture = loadTexture("tblock.png");
		
		// 拆分方块图片,大小20x20
		blocks = TextureUtils.getSplitTextures(texture, 20, 20);
		// 默认游戏区域（网格）大小为横15,竖16
		field2d = new TetrisField(15, 16);
		// 设定产生随机方块时索引取值范围
		field2d.setStoneValue(1, 6);
		// 设定渲染监听
		field2d.setListener(new TetrisDrawImpl(field2d));
		// 游戏开始
		field2d.start();
		
		//关闭Screen注销纹理
		putReleases(blocks);


		// 监听Screen的LTimerContext
		add(new Port<LTimerContext>() {

			final LTimer delay = new LTimer();

			@Override
			public void onEmit(LTimerContext timer) {
				if (delay.action(timer)) {
					if (field2d.isGameRunning()) {
						if (!field2d.incrementPositionY(true)) {
							field2d.createCurrentStone();
							if (field2d.hasLines()) {
								curLevel = field2d.getLevel();
							}
						}
						delay.setDelay(300 / curLevel);
					}
				}
			}
		}, true);

		// 四方向手柄
		LPad pad = new LPad(20, 170);
		// 限制点击次数,点击事件必须在松开鼠标或者触屏手指离开屏幕后才能再次触发，否则不触发
		pad.setLimitClick(true);

		pad.setListener(new LPad.ClickListener() {

			@Override
			public void up() {
				// 转换方块方向
				field2d.rotateStone();
			}

			@Override
			public void right() {
				field2d.rightPositionX();

			}

			@Override
			public void left() {
				field2d.leftPositionX();
			}

			@Override
			public void down() {
				field2d.downPositionX();

			}

			@Override
			public void other() {
			}

		});
		add(pad);

		add(MultiScreenTest.getBackButton(this, 1));
	}

	@Override
	public void alter(LTimerContext timer) {
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
