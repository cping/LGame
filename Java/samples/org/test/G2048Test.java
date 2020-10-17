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

import loon.Screen;
import loon.canvas.LColor;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.font.BMFont;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class G2048Test extends Screen {

	private int freeCount;
	private IFont font;
	private short[][] state = new short[4][4];

	// 2 4 8 16 32 64 128 256 512 1024 2048 4096...
	private final int[] backgroudColors = { 0xEEE4DA, 0xEDE0C8, 0xF2B179, 0xF59563, 0xF67C5F, 0xF65E3B, 0xEDCF72,
			0xEDCC61, 0xEDC850, 0xedc53f, 0xedc22e, 0x3c3a32 };

	@Override
	public void onLoad() {
		freeCount = 16;
		BMFont bmfont = new BMFont("assets/info.fnt", "assets/info.png");
		bmfont.setFontScale(2f);
		font = bmfont;
		for (int i = 0; i < 2; i++) {
			insertCreate();
		}
	}

	private boolean moveDown() {
		boolean result = false;
		int[] maxCheck = { 3, 3, 3, 3 };
		for (int i = 2; i >= 0; i--) {
			for (int j = 0; j < 4; j++) {
				if (state[i][j] == 0) {
					continue;
				}

				int nextFilled = -1;
				for (int k = i + 1; k <= maxCheck[j]; k++) {
					if (state[k][j] != 0) {
						nextFilled = k;
						break;
					}
				}

				if (nextFilled == -1) {
					if (maxCheck[j] != i) {
						state[maxCheck[j]][j] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				} else if (state[i][j] == state[nextFilled][j]) {
					state[nextFilled][j] *= 2;
					state[i][j] = 0;
					maxCheck[j] = nextFilled - 1;
					freeCount++;
					result = true;
				} else {
					if (nextFilled - 1 != i) {
						state[nextFilled - 1][j] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				}
			}
		}
		return result;
	}

	private boolean moveRight() {
		boolean result = false;
		int[] maxCheck = { 3, 3, 3, 3 };
		for (int j = 2; j >= 0; j--) {
			for (int i = 0; i < 4; i++) {

				if (state[i][j] == 0) {
					continue;
				}

				int nextFilled = -1;
				for (int k = j + 1; k <= maxCheck[i]; k++) {
					if (state[i][k] != 0) {
						nextFilled = k;
						break;
					}
				}

				if (nextFilled == -1) {
					if (maxCheck[i] != j) {
						state[i][maxCheck[i]] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				} else if (state[i][j] == state[i][nextFilled]) {

					state[i][nextFilled] *= 2;
					state[i][j] = 0;
					maxCheck[i] = nextFilled - 1;
					freeCount++;
					result = true;
				} else {

					if (nextFilled - 1 != j) {
						state[i][nextFilled - 1] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				}
			}
		}
		return result;
	}

	private boolean moveLeft() {
		boolean result = false;
		int[] minCheck = { 0, 0, 0, 0 };
		for (int j = 1; j < 4; j++) {
			for (int i = 0; i < 4; i++) {

				if (state[i][j] == 0) {
					continue;
				}

				int nextFilled = -1;
				for (int k = j - 1; k >= minCheck[i]; k--) {
					if (state[i][k] != 0) {
						nextFilled = k;
						break;
					}
				}

				if (nextFilled == -1) {
					if (minCheck[i] != j) {
						state[i][minCheck[i]] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				} else if (state[i][j] == state[i][nextFilled]) {
					state[i][nextFilled] *= 2;
					state[i][j] = 0;
					minCheck[i] = nextFilled + 1;
					freeCount++;
					result = true;
				} else {
					if (nextFilled + 1 != j) {
						state[i][nextFilled + 1] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				}
			}
		}
		return result;
	}

	private boolean moveUp() {
		boolean result = false;
		int[] minCheck = { 0, 0, 0, 0 };
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (state[i][j] == 0) {
					continue;
				}

				int nextFilled = -1;
				for (int k = i - 1; k >= minCheck[j]; k--) {
					if (state[k][j] != 0) {
						nextFilled = k;
						break;
					}
				}

				if (nextFilled == -1) {

					if (minCheck[j] != i) {
						state[minCheck[j]][j] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				} else if (state[i][j] == state[nextFilled][j]) {
					state[nextFilled][j] *= 2;
					state[i][j] = 0;
					minCheck[j] = nextFilled + 1;
					freeCount++;
					result = true;
				} else {
					if (nextFilled + 1 != i) {
						state[nextFilled + 1][j] = state[i][j];
						state[i][j] = 0;
						result = true;
					}
				}
			}
		}
		return result;
	}

	private void insertCreate() {
		int cond = MathUtils.nextInt(10);
		short next = (short) (cond < 9 ? 2 : 4);
		int pos = MathUtils.nextInt(freeCount);
		freeCount--;
		int currentFreePos = 0;
		for (int i = 0; i < state.length; i++) {
			short[] ses = state[i];
			for (int j = 0; j < ses.length; j++) {
				short s = ses[j];
				if (s == 0) {
					if (currentFreePos == pos) {
						state[i][j] = next;
						return;
					} else {
						currentFreePos++;
					}
				}
			}
		}
	}

	private LColor tmpColor = LColor.white.cpy();

	@Override
	public void draw(GLEx g) {

		g.setColor(205, 192, 180);
		int width = getWidth();
		int height = getHeight();

		g.fillRect(0, 0, width, height);

		g.setColor(0xBB, 0xAD, 0xA0);
		int widthOne = (width - 3) / 4;
		int heightOne = (height - 3) / 4;

		g.drawLine(widthOne, 0, widthOne, height);
		g.drawLine(2 * widthOne + 1, 0, 2 * widthOne + 1, height);
		g.drawLine(3 * widthOne + 2, 0, 3 * widthOne + 2, height);

		g.drawLine(0, heightOne, width, heightOne);
		g.drawLine(0, 2 * heightOne + 1, width, 2 * heightOne + 1);
		g.drawLine(0, 3 * heightOne + 2, width, 3 * heightOne + 2);

		g.setFont(font);
		for (int i = 0; i < state.length; i++) {
			short[] ses = state[i];
			for (int j = 0; j < ses.length; j++) {
				short val = ses[j];
				if (val != 0) {
					String text = Integer.toString(val);
					int colorIndex = 0;
					val = (short) (val >> 2);
					while (val != 0) {
						val = (short) (val >> 1);
						colorIndex++;
					}
					if (colorIndex >= backgroudColors.length) {
						colorIndex = backgroudColors.length - 1;
					}
					tmpColor = tmpColor.setColorRGB(backgroudColors[colorIndex]);
					g.fillRect(j * (widthOne + 1), i * (heightOne + 1), widthOne - 1, heightOne - 1, tmpColor);
					g.drawString(text, j * widthOne + j + font.getSize() / 2, i * heightOne + i + font.getHeight() / 2,
							LColor.red);
				}
			}
		}
	}

	@Override
	public void alter(LTimerContext context) {

	}

	@Override
	public void resize(int width, int height) {

	}

	public void onKeyDown(GameKey key) {
		int code = key.getKeyCode();
		boolean isModified = false;
		switch (code) {
		case SysKey.LEFT:
		case SysKey.NUM_4:
			isModified = moveLeft();
			break;
		case SysKey.UP:
		case SysKey.NUM_2:
			isModified = moveUp();
			break;
		case SysKey.RIGHT:
		case SysKey.NUM_6:
			isModified = moveRight();
			break;
		case SysKey.DOWN:
		case SysKey.NUM_8:
			isModified = moveDown();
			break;
		}
		if (!isModified) {
			return;
		}
		insertCreate();
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
