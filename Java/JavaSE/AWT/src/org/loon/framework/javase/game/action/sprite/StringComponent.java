package org.loon.framework.javase.game.action.sprite;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LFont;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

public class StringComponent {
	
	private String text;

	private int breaks[] = new int[4];

	private boolean invertPaint = false;

	private int numOfBreaks;

	private int width;

	private int widthDecreaser;

	public StringComponent() {
		this(null);
	}

	public StringComponent(String text) {
		synchronized (this) {
			this.width = -1;
			this.widthDecreaser = 0;
			setText(text);
		}
	}

	public int getCharHeight() {
		return LFont.getDefaultFont().getHeight();
	}

	public int getCharPositionX(int num) {
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}

			int i, prevIndex = 0;
			LFont f = LFont.getDefaultFont();

			for (i = 0; i < numOfBreaks; i++) {
				if (num < breaks[i]) {
					break;
				}
				prevIndex = breaks[i];
			}

			return f.subStringWidth(text, prevIndex, num - prevIndex);
		}
	}

	public int getCharPositionY(int num) {
		int y = 0;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}

			LFont f = LFont.getDefaultFont();

			for (int i = 0; i < numOfBreaks; i++) {
				if (num < breaks[i]) {
					break;
				}
				y += f.getHeight();
			}
		}

		return y;
	}

	public int getHeight() {
		int height;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}

			LFont f = LFont.getDefaultFont();

			if (text == null) {
				return 0;
			}

			if (numOfBreaks == 0) {
				return f.getHeight();
			}

			height = numOfBreaks * f.getHeight();

			if (breaks[numOfBreaks - 1] == text.length() - 1
					&& text.charAt(text.length() - 1) == '\n') {
			} else {
				height += f.getHeight();
			}
		}

		return height;
	}

	public String getText() {
		return text;
	}

	public void invertPaint(boolean state) {
		synchronized (this) {
			invertPaint = state;
		}
	}

	public int paint(LGraphics g) {
		if (text == null) {
			return 0;
		}

		int y;
		synchronized (this) {
			if (numOfBreaks == -1) {
				updateBreaks();
			}

			int i, prevIndex;
			LFont f = LFont.getDefaultFont();

			for (i = prevIndex = y = 0; i < numOfBreaks; i++) {
				if (invertPaint) {
					g.setGrayScale(0);
				} else {
					g.setGrayScale(255);
				}
				g.fillRect(0, y, width, f.getHeight());
				if (invertPaint) {
					g.setGrayScale(255);
				} else {
					g.setGrayScale(0);
				}
				g
						.drawSubString(text, prevIndex, breaks[i] - prevIndex,
								0, y, 0);
				prevIndex = breaks[i];
				y += f.getHeight();
			}

			if (prevIndex != text.length() || text.length() == 0) {
				if (invertPaint) {
					g.setGrayScale(0);
				} else {
					g.setGrayScale(255);
				}
				g.fillRect(0, y, width, f.getHeight());
				if (invertPaint) {
					g.setGrayScale(255);
				} else {
					g.setGrayScale(0);
				}
				g.drawSubString(text, prevIndex, text.length() - prevIndex, 0,
						y, 0);
				y += f.getHeight();
			}
		}

		return y;
	}

	public void setText(String text) {
		synchronized (this) {
			this.text = text;
			this.numOfBreaks = -1;
		}
	}

	public void setWidthDecreaser(int widthDecreaser) {
		synchronized (this) {
			this.widthDecreaser = widthDecreaser;
			numOfBreaks = -1;
		}
	}

	private void insertBreak(int pos) {
		int i;

		for (i = 0; i < numOfBreaks; i++) {
			if (pos < breaks[i]) {
				break;
			}
		}
		if (numOfBreaks + 1 == breaks.length) {
			int newbreaks[] = new int[breaks.length + 4];
			System.arraycopy(breaks, 0, newbreaks, 0, numOfBreaks);
			breaks = newbreaks;
		}
		System.arraycopy(breaks, i, breaks, i + 1, numOfBreaks - i);
		breaks[i] = pos;
		numOfBreaks++;
	}

	private void updateBreaks() {
		if (text == null) {
			return;
		}

		width = LSystem.screenRect.getWidth() - widthDecreaser;

		int prevIndex = 0;
		int canBreak = 0;
		numOfBreaks = 0;
		LFont f = LFont.getDefaultFont();

		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				canBreak = i + 1;
			}
			if (text.charAt(i) == '\n') {
				insertBreak(i);
				canBreak = 0;
				prevIndex = i + 1;
				continue;
			}
			if (f.subStringWidth(text, prevIndex, i - prevIndex + 1) > width) {
				if (canBreak != 0) {
					insertBreak(canBreak);
					i = canBreak;
					prevIndex = i;
				} else {
					insertBreak(i);
					prevIndex = i + 1;
				}
				canBreak = 0;
			}
		}
	}

}
