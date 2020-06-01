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
package loon.canvas;

import loon.BaseIO;
import loon.LTexture;
import loon.geom.RectBox;

/**
 * 手动设置的9grid构建类
 */
public class NineBuilder {

	private Pixmap _baseImage;

	private Pixmap _topLeft;
	private Pixmap _top;
	private Pixmap _topRight;
	private Pixmap _right;
	private Pixmap _botRight;
	private Pixmap _bot;
	private Pixmap _botLeft;
	private Pixmap _left;
	private Pixmap _center;

	public NineBuilder(Pixmap tex) {
		this._baseImage = tex;
	}

	public NineBuilder(String path) {
		this(BaseIO.loadImage(path).getPixmap());
	}

	public NineBuilder top(RectBox rect) {
		return top(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder top(int x, int y, int w, int h) {
		_top = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder topLeft(RectBox rect) {
		return topLeft(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder topLeft(int x, int y, int w, int h) {
		_topLeft = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder topRight(RectBox rect) {
		return topRight(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder topRight(int x, int y, int w, int h) {
		_topRight = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder left(RectBox rect) {
		return left(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder left(int x, int y, int w, int h) {
		_left = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder right(RectBox rect) {
		return right(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder right(int x, int y, int w, int h) {
		_right = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder bot(RectBox rect) {
		return bot(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder bot(int x, int y, int w, int h) {
		_bot = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder botLeft(RectBox rect) {
		return botLeft(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder botLeft(int x, int y, int w, int h) {
		_botLeft = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder botRight(RectBox rect) {
		return botRight(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder botRight(int x, int y, int w, int h) {
		_botRight = _baseImage.copy(x, y, w, h);
		return this;
	}

	public NineBuilder center(RectBox rect) {
		return center(rect.x(), rect.y(), rect.width(), rect.height());
	}

	public NineBuilder center(int x, int y, int w, int h) {
		_center = _baseImage.copy(x, y, w, h);
		return this;
	}

	public LTexture build(int width, int height) {
		return createImage(width, height).onHaveToClose(true).texture();
	}

	public Image createImage(int width, int height) {
		return createPixmap(width, height).getImage();
	}

	public Pixmap createPixmap(int width, int height) {
		int w = width - _left.getWidth() - _right.getWidth();
		int h = height - _top.getHeight() - _bot.getHeight();

		Pixmap newTop = _top;

		if (_top.getWidth() > w) {
			newTop = _top.copy(0, 0, w, _top.getHeight());
		} else if (_top.getWidth() < w) {
			int times = w / _top.getWidth();
			int rem = w % _top.getWidth();

			for (int i = 1; i < times; i++) {
				newTop = newTop.blendHorizontal(_top);
			}

			if (rem > 0) {
				newTop = newTop.blendHorizontal(_top.copy(0, 0, rem, _top.getHeight()));
			}
		}

		Pixmap row0 = _topLeft.blendHorizontal(newTop).blendHorizontal(_topRight);

		Pixmap newCenter = _center;

		if (newCenter.getWidth() > w) {
			newCenter = newCenter.copy(0, 0, w, newCenter.getHeight());
		} else if (newCenter.getWidth() < w) {
			int times = w / newCenter.getWidth();
			int rem = w % newCenter.getWidth();

			for (int i = 1; i < times; i++) {
				newCenter = newCenter.blendHorizontal(_center);
			}

			if (rem > 0) {
				newCenter = newCenter.blendHorizontal(_center.copy(0, 0, rem, _center.getHeight()));
			}
		}

		Pixmap center2 = newCenter;

		if (newCenter.getHeight() > h) {
			newCenter = newCenter.copy(0, 0, newCenter.getWidth(), h);
		} else if (newCenter.getHeight() < h) {
			int times = h / newCenter.getHeight();
			int rem = h % newCenter.getHeight();

			for (int i = 1; i < times; i++) {
				newCenter = newCenter.blendVertical(center2, true);
			}

			if (rem > 0) {
				newCenter = newCenter.blendVertical(center2.copy(0, 0, center2.getWidth(), rem), true);
			}
		}

		Pixmap newLeft = _left;
		if (newLeft.getHeight() > h) {
			newLeft = _left.copy(0, 0, _left.getWidth(), h);
		} else if (newLeft.getHeight() < h) {
			int times = h / _left.getHeight();
			int rem = h % _left.getHeight();

			for (int i = 1; i < times; i++) {
				newLeft = newLeft.blendVertical(_left, true);
			}

			if (rem > 0) {
				newLeft = newLeft.blendVertical(_left.copy(0, 0, _left.getWidth(), rem), true);
			}
		}

		Pixmap newRight = _right;
		if (newRight.getHeight() > h) {
			newRight = _right.copy(0, 0, _right.getWidth(), h);
		} else if (newRight.getHeight() < h) {
			int times = h / _right.getHeight();
			int rem = h % _right.getHeight();

			for (int i = 1; i < times; i++) {
				newRight = newRight.blendVertical(_right, true);
			}

			if (rem > 0) {
				newRight = newRight.blendVertical(_right.copy(0, 0, _right.getWidth(), rem), true);
			}
		}

		Pixmap row1 = newLeft.blendHorizontal(newCenter).blendHorizontal(newRight);

		Pixmap newBot = _bot;
		if (newBot.getWidth() > w) {
			newBot = _bot.copy(0, 0, w, _bot.getHeight());
		} else if (newBot.getWidth() < w) {
			int times = w / _top.getWidth();
			int rem = w % _top.getWidth();
			for (int i = 1; i < times; i++) {
				newBot = newBot.blendHorizontal(_bot);
			}

			if (rem > 0) {
				newBot = newBot.blendHorizontal(_bot.copy(0, 0, rem, _bot.getHeight()));
			}
		}

		Pixmap row2 = _botLeft.blendHorizontal(newBot).blendHorizontal(_botRight);

		return row0.blendVertical(row1, true).blendVertical(row2, true);
	}

	public Pixmap baseImage() {
		return _baseImage;
	}

	public Pixmap topImage() {
		return _top;
	}

	public Pixmap leftImage() {
		return _left;
	}

	public Pixmap rightImage() {
		return _right;
	}

	public Pixmap centerImage() {
		return _center;
	}

	public Pixmap topLeftImage() {
		return _topLeft;
	}

	public Pixmap topRightImage() {
		return _topRight;
	}

	public Pixmap botImage() {
		return _bot;
	}

	public Pixmap botLeftImage() {
		return _botLeft;
	}

	public Pixmap botRightImage() {
		return _botRight;
	}
}
