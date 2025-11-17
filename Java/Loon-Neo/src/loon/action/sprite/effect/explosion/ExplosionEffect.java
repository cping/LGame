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
package loon.action.sprite.effect.explosion;

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.Entity;
import loon.action.sprite.effect.BaseEffect;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.opengl.GLEx;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;
import loon.utils.MathUtils;

/**
 * 像素风爆炸特效,让指定Image以指定的爆炸方式离开Screen画面
 */
public class ExplosionEffect extends Entity implements BaseEffect {

	public static enum Mode {

		Tattered, Explode, FlyLeft, FlyLeftDown, FlyRight, FlyRightDown;

	}

	private boolean _startExplision;

	private Mode _lastMode;

	private LTexture _ovalTexture;

	private Fragment[][] _fragments;

	private boolean _packed = false;

	private int _blockWidth;

	private int _blockHeight;

	private Mode _mode;

	private Image _pixmap;

	private boolean _autoRemoved;

	private EasingMode _easingMode;

	private EaseTimer _timer;

	private RectBox _imageRect;

	public ExplosionEffect(Mode m, String path) {
		this(m, BaseIO.loadImage(path));
	}

	public ExplosionEffect(Mode m, Image pix) {
		this(m, pix, null, 8, 8, EasingMode.Linear, 1f);
	}

	public ExplosionEffect(Mode m, String path, RectBox rect) {
		this(m, path, rect, 1f);
	}

	public ExplosionEffect(Mode m, String path, RectBox rect, float duration) {
		this(m, BaseIO.loadImage(path), rect, duration);
	}

	public ExplosionEffect(Mode m, Image pix, RectBox rect, float duration) {
		this(m, pix, rect, 8, 8, EasingMode.Linear, duration);
	}

	public ExplosionEffect(Mode m, Image pix, EasingMode ease) {
		this(m, pix, null, 8, 8, ease, 1f);
	}

	public ExplosionEffect(Mode m, Image pix, EasingMode ease, float duration) {
		this(m, pix, null, 8, 8, ease, duration);
	}

	public ExplosionEffect(Mode m, Image pix, RectBox imageSize, EasingMode ease, float duration) {
		this(m, pix, imageSize, 8, 8, ease, duration);
	}

	public ExplosionEffect(Mode m, Image pix, int tw, int th, EasingMode ease, float duration) {
		this(m, pix, null, tw, th, ease, duration);
	}

	public ExplosionEffect(Mode m, Image pix, RectBox imageSize, int tw, int th, EasingMode ease, float duration) {
		this._mode = m;
		this._pixmap = pix;
		if (imageSize == null) {
			this._imageRect = new RectBox(0, 0, pix.getWidth(), pix.getHeight());
		} else {
			this._imageRect = imageSize;
		}
		this.setBounds(_imageRect);
		this._blockWidth = tw;
		this._blockHeight = th;
		this._easingMode = ease;
		this._timer = new EaseTimer(duration, ease);
		setRepaint(true);
	}

	public void pack() {
		if (!_packed || _mode != _lastMode) {
			createOvalImage();
			if (_fragments == null || _mode != _lastMode) {
				_fragments = createFrags(
						new RectI(_imageRect.x(), _imageRect.y(), _imageRect.width, _imageRect.height));
			} else {
				for (int i = 0; i < _fragments.length; i++) {
					for (int j = 0; j < _fragments[i].length; j++) {
						_fragments[i][j].reset();
					}
				}
			}
			if (_image == null) {
				_image = _pixmap.texture();
			}
			_packed = true;
		}
	}

	@Override
	public ExplosionEffect reset() {
		super.reset();
		this.stop();
		this._packed = false;
		return this;
	}

	@Override
	protected void onUpdate(final long elapsedTime) {
		if (_startExplision) {
			_timer.action(elapsedTime);
		}
		if (this.isCompleted()) {
			if (_autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public ExplosionEffect start() {
		return start(this._mode);
	}

	public ExplosionEffect start(Mode m) {
		this._timer.reset();
		this._startExplision = true;
		this._mode = m;
		this._lastMode = null;
		return this;
	}

	public ExplosionEffect stop() {
		this._timer.reset();
		this._startExplision = false;
		this._lastMode = null;
		_baseColor.reset();
		return this;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		pack();
		float x = drawX(offsetX);
		float y = drawY(offsetY);
		if (_startExplision) {
			float process = _timer.getProgress();
			float alpha = 1f - (process * 3f);
			if (alpha < 0) {
				alpha = 0;
			}
			if (alpha > 1f) {
				alpha = 1f;
			}
			_baseColor.setAlpha(alpha);
			g.draw(_image, x, y, _baseColor);
			for (Fragment[] frag : _fragments) {
				for (Fragment p : frag) {
					p.draw(g, x, y, process);
				}
			}
		} else {
			g.draw(_image, x, y, _baseColor);
		}
	}

	public Fragment[][] createFrags(RectI bound) {
		return createFrags(_pixmap, bound);
	}

	public Fragment[][] createFrags(Image img, RectI bound) {
		Fragment[][] fragments = null;
		switch (_mode) {
		case Tattered:
			fragments = createTatteredFrags(img, bound);
			break;
		case Explode:
			fragments = createExplodeFrags(img, bound);
			break;
		case FlyRight:
			fragments = createFlyRightFrags(img, bound);
			break;
		case FlyLeftDown:
			fragments = createFlyLeftDownFrags(img, bound);
			break;
		case FlyRightDown:
			fragments = createFlyRightDownFrags(img, bound);
			break;
		case FlyLeft:
			fragments = createFlyLeftFrags(img, bound);
			break;
		}
		this._lastMode = _mode;
		return fragments;
	}

	public Mode getLastMode() {
		return this._lastMode;
	}

	LTexture createOvalImage() {
		if (_ovalTexture == null) {
			Pixmap pixmap = new Pixmap(_blockWidth + 1, _blockHeight + 1, true);
			pixmap.setColor(LColor.white);
			pixmap.fillOval(0, 0, _blockWidth, _blockHeight);
			_ovalTexture = pixmap.texture();
		}
		return _ovalTexture;
	}

	protected Fragment[][] createTatteredFrags(Image img, RectI bound) {

		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {

				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + _blockWidth * col;
				float y = bound.y + _blockHeight * row;
				Fragment frag = new TatteredFragment(color, x, y, bound, _ovalTexture);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createExplodeFrags(Image img, RectI bound) {
		RectI bounds = new RectI(bound);
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				Fragment frag = createExplodeFrag(color, bounds);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}
		return fragments;
	}

	private Fragment createExplodeFrag(int color, RectI bound) {
		final float dotSize = 10;
		final float ny = bound.height / 2;
		final float nv = 4;
		final float nw = 1;
		final float end = 1.4f;
		RectI bounds = new RectI(bound);

		ExplodeFragment frag = new ExplodeFragment(color, 0, 0, bounds, nv, nv, end, _ovalTexture);
		frag._color = color;
		frag._width = nv;
		frag._height = nv;
		if (MathUtils.random() < 0.2f) {
			frag._baseRadius = nv + ((dotSize - nv) * MathUtils.random());
		} else {
			frag._baseRadius = nw + ((nv - nw) * MathUtils.random());
		}
		float nextFloat = MathUtils.random();
		frag._top = bounds.height * ((0.18f * MathUtils.random()) + 0.2f);
		frag._top = nextFloat < 0.2f ? frag._top : frag._top + ((frag._top * 0.2f) * MathUtils.random());
		frag._bottom = (bounds.height * (MathUtils.random() - 0.5f)) * 1.8f;
		float f = nextFloat < 0.2f ? frag._bottom : nextFloat < 0.8f ? frag._bottom * 0.6f : frag._bottom * 0.3f;
		frag._bottom = f;
		frag._mag = 4f * frag._top / frag._bottom;
		frag._neg = (-frag._mag) / frag._bottom;
		f = bounds.centerX() + (ny * (MathUtils.random() - 0.5f));
		frag._baseCx = f;
		frag._cx = f;
		f = bounds.centerY() + (ny * (MathUtils.random() - 0.5f));
		frag._baseCy = f;
		frag._cy = f;
		frag._life = end / 10f * MathUtils.random();
		frag._overflow = 0.4f * MathUtils.random();
		frag._alpha = 1f;
		return frag;
	}

	protected Fragment[][] createFlyLeftDownFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + _blockWidth * col;
				float y = bound.y + _blockHeight * row;
				Fragment frag = new FlyLeftDownFragment(color, x, y, bound, _ovalTexture);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyRightFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);

				float x = bound.x + _blockWidth * col;
				float y = bound.y + _blockHeight * row;
				Fragment frag = new FlyRightFragment(color, x, y, bound, _ovalTexture);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyRightDownFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;
		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;
		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + _blockWidth * col;
				float y = bound.y + _blockHeight * row;
				Fragment frag = new FlayRightDownFragment(color, x, y, bound, _ovalTexture);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyLeftFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / _blockWidth;
		int partHeightSize = h / _blockHeight;
		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;
		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + _blockWidth * col;
				float y = bound.y + _blockHeight * row;
				Fragment frag = new FlyLeftFragment(color, x, y, bound, _ovalTexture);
				frag._width = _blockWidth;
				frag._height = _blockHeight;
				fragments[row][col] = frag;
			}
		}
		return fragments;
	}

	public int getBlockWidth() {
		return _blockWidth;
	}

	public int getBlockHeight() {
		return _blockHeight;
	}

	@Override
	public boolean isCompleted() {
		return _timer.getProgress() >= 0.99f;
	}

	@Override
	public ExplosionEffect setStop(boolean c) {
		if (c) {
			_timer.add(LSystem.MINUTE);
		} else {
			_timer.reset();
		}
		return this;
	}

	public Mode getMode() {
		return _mode;
	}

	public ExplosionEffect setMode(Mode m) {
		this._mode = m;
		return this;
	}

	public EasingMode getEasingMode() {
		return _easingMode;
	}

	public ExplosionEffect setEasingMode(EasingMode easingMode) {
		this._easingMode = easingMode;
		return this;
	}

	public boolean isAutoRemoved() {
		return _autoRemoved;
	}

	public ExplosionEffect setAutoRemoved(boolean autoRemoved) {
		this._autoRemoved = autoRemoved;
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		this.stop();
		this._fragments = null;
		this._packed = false;
		if (_ovalTexture != null) {
			_ovalTexture.close();
			_ovalTexture = null;
		}
	}
}
