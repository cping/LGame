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

	private boolean startExplision;

	private Mode lastMode;

	private LTexture ovalTexture;

	private Fragment[][] fragments;

	private boolean packed = false;

	private int blockWidth;

	private int blockHeight;

	private Mode mode;

	private Image pixmap;

	private boolean autoRemoved;

	private EasingMode easingMode;

	private EaseTimer timer;

	private RectBox imageRect;

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
		this.mode = m;
		this.pixmap = pix;
		if (imageSize == null) {
			this.imageRect = new RectBox(0, 0, pix.getWidth(), pix.getHeight());
		} else {
			this.imageRect = imageSize;
		}
		this.setBounds(imageRect);
		this.blockWidth = tw;
		this.blockHeight = th;
		this.easingMode = ease;
		this.timer = new EaseTimer(duration, ease);
		setRepaint(true);
	}

	public void pack() {
		if (!packed || mode != lastMode) {
			createOvalImage();
			if (fragments == null || mode != lastMode) {
				fragments = createFrags(new RectI(imageRect.x(), imageRect.y(), imageRect.width, imageRect.height));
			} else {
				for (int i = 0; i < fragments.length; i++) {
					for (int j = 0; j < fragments[i].length; j++) {
						fragments[i][j].reset();
					}
				}
			}
			if (_image == null) {
				_image = pixmap.texture();
			}
			packed = true;
		}
	}

	@Override
	public ExplosionEffect reset() {
		super.reset();
		this.stop();
		this.packed = false;
		return this;
	}

	@Override
	protected void onUpdate(final long elapsedTime) {
		if (startExplision) {
			timer.action(elapsedTime);
		}
		if (this.isCompleted()) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public ExplosionEffect start() {
		return start(this.mode);
	}

	public ExplosionEffect start(Mode m) {
		this.timer.reset();
		this.startExplision = true;
		this.mode = m;
		this.lastMode = null;
		return this;
	}

	public ExplosionEffect stop() {
		this.timer.reset();
		this.startExplision = false;
		this.lastMode = null;
		_baseColor.reset();
		return this;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		pack();
		float x = drawX(offsetX);
		float y = drawY(offsetY);
		if (startExplision) {
			float process = timer.getProgress();
			float alpha = 1f - (process * 3f);
			if (alpha < 0) {
				alpha = 0;
			}
			if (alpha > 1f) {
				alpha = 1f;
			}
			_baseColor.setAlpha(alpha);
			g.draw(_image, x, y, _baseColor);
			for (Fragment[] frag : fragments) {
				for (Fragment p : frag) {
					p.draw(g, x, y, process);
				}
			}
		} else {
			g.draw(_image, x, y, _baseColor);
		}
	}

	public Fragment[][] createFrags(RectI bound) {
		return createFrags(pixmap, bound);
	}

	public Fragment[][] createFrags(Image img, RectI bound) {
		Fragment[][] fragments = null;
		switch (mode) {
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
		this.lastMode = mode;
		return fragments;
	}

	public Mode getLastMode() {
		return this.lastMode;
	}

	LTexture createOvalImage() {
		if (ovalTexture == null) {
			Pixmap pixmap = new Pixmap(blockWidth + 1, blockHeight + 1, true);
			pixmap.setColor(LColor.white);
			pixmap.fillOval(0, 0, blockWidth, blockHeight);
			ovalTexture = pixmap.texture();
		}
		return ovalTexture;
	}

	protected Fragment[][] createTatteredFrags(Image img, RectI bound) {

		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {

				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + blockWidth * col;
				float y = bound.y + blockHeight * row;
				Fragment frag = new TatteredFragment(color, x, y, bound, ovalTexture);
				frag.width = blockWidth;
				frag.height = blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createExplodeFrags(Image img, RectI bound) {
		RectI bounds = new RectI(bound);
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				Fragment frag = createExplodeFrag(color, bounds);
				frag.width = blockWidth;
				frag.height = blockHeight;
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

		ExplodeFragment frag = new ExplodeFragment(color, 0, 0, bounds, nv, nv, end, ovalTexture);
		frag.color = color;
		frag.width = nv;
		frag.height = nv;
		if (MathUtils.random() < 0.2f) {
			frag.baseRadius = nv + ((dotSize - nv) * MathUtils.random());
		} else {
			frag.baseRadius = nw + ((nv - nw) * MathUtils.random());
		}
		float nextFloat = MathUtils.random();
		frag.top = bounds.height * ((0.18f * MathUtils.random()) + 0.2f);
		frag.top = nextFloat < 0.2f ? frag.top : frag.top + ((frag.top * 0.2f) * MathUtils.random());
		frag.bottom = (bounds.height * (MathUtils.random() - 0.5f)) * 1.8f;
		float f = nextFloat < 0.2f ? frag.bottom : nextFloat < 0.8f ? frag.bottom * 0.6f : frag.bottom * 0.3f;
		frag.bottom = f;
		frag.mag = 4f * frag.top / frag.bottom;
		frag.neg = (-frag.mag) / frag.bottom;
		f = bounds.centerX() + (ny * (MathUtils.random() - 0.5f));
		frag.baseCx = f;
		frag.cx = f;
		f = bounds.centerY() + (ny * (MathUtils.random() - 0.5f));
		frag.baseCy = f;
		frag.cy = f;
		frag.life = end / 10f * MathUtils.random();
		frag.overflow = 0.4f * MathUtils.random();
		frag.alpha = 1f;
		return frag;
	}

	protected Fragment[][] createFlyLeftDownFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + blockWidth * col;
				float y = bound.y + blockHeight * row;
				Fragment frag = new FlyLeftDownFragment(color, x, y, bound, ovalTexture);
				frag.width = blockWidth;
				frag.height = blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyRightFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;

		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;

		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;

		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);

				float x = bound.x + blockWidth * col;
				float y = bound.y + blockHeight * row;
				Fragment frag = new FlyRightFragment(color, x, y, bound, ovalTexture);
				frag.width = blockWidth;
				frag.height = blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyRightDownFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;
		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;
		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];

		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + blockWidth * col;
				float y = bound.y + blockHeight * row;
				Fragment frag = new FlayRightDownFragment(color, x, y, bound, ovalTexture);
				frag.width = blockWidth;
				frag.height = blockHeight;
				fragments[row][col] = frag;
			}
		}

		return fragments;
	}

	protected Fragment[][] createFlyLeftFrags(Image img, RectI bound) {
		int w = bound.width;
		int h = bound.height;
		int partWidthSize = w / blockWidth;
		int partHeightSize = h / blockHeight;
		int imgWidth = img.getWidth() / partWidthSize;
		int imgHeight = img.getHeight() / partHeightSize;
		Fragment[][] fragments = new Fragment[partHeightSize][partWidthSize];
		for (int row = 0; row < partHeightSize; row++) {
			for (int col = 0; col < partWidthSize; col++) {
				int color = img.getPixel(col * imgWidth, row * imgHeight);
				float x = bound.x + blockWidth * col;
				float y = bound.y + blockHeight * row;
				Fragment frag = new FlyLeftFragment(color, x, y, bound, ovalTexture);
				frag.width = blockWidth;
				frag.height = blockHeight;
				fragments[row][col] = frag;
			}
		}
		return fragments;
	}

	public int getBlockWidth() {
		return blockWidth;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	@Override
	public boolean isCompleted() {
		return timer.getProgress() >= 0.99f;
	}

	@Override
	public ExplosionEffect setStop(boolean c) {
		if (c) {
			timer.add(LSystem.MINUTE);
		} else {
			timer.reset();
		}
		return this;
	}

	public Mode getMode() {
		return mode;
	}

	public ExplosionEffect setMode(Mode m) {
		this.mode = m;
		return this;
	}

	public EasingMode getEasingMode() {
		return easingMode;
	}

	public ExplosionEffect setEasingMode(EasingMode easingMode) {
		this.easingMode = easingMode;
		return this;
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public ExplosionEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		this.stop();
		this.fragments = null;
		this.packed = false;
		if (ovalTexture != null) {
			ovalTexture.close();
			ovalTexture = null;
		}
	}
}
