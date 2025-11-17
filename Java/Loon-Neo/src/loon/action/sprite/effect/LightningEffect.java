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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.BaseBatch;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.TArray;

public class LightningEffect extends BaseAbstractEffect {

	private LTexture _lightningSegment, _halfCircle, _pixel;

	private LTexturePack _pack;

	private int _countCompleted;

	private static LightningEffect instance;

	public final static void freeStatic() {
		instance = null;
	}

	final static LightningEffect make() {
		return new LightningEffect();
	}

	final static LightningEffect get() {
		synchronized (LightningEffect.class) {
			if (instance == null || instance.isDestroyed()) {
				instance = make();
			}
			return instance;
		}
	}

	private TArray<ILightning> lists = new TArray<ILightning>(10);

	private LightningEffect() {
	}

	public void loadLightning(LTexturePack p) {
		this._countCompleted = 0;
		this._completed = false;
		this._pack = p;
		this._lightningSegment = _pack.getTexture("loon_lightning");
		this._halfCircle = _pack.getTexture("loon_halfcircle");
		this._pixel = _pack.getTexture("loon_pixel");
	}

	public static LightningEffect addBolt(Vector2f s, Vector2f e, LColor c) {
		return addBolt(new Vector2f[] { s }, new Vector2f[] { e }, c);
	}

	// screen.add(LightningEffect.addBolt(Vector2f.at(33, 33),
	// Vector2f.at(300, 300),LColor.red));
	public static LightningEffect addBolt(Vector2f[] s, Vector2f[] e, LColor c) {
		TArray<ILightning> lights = new TArray<ILightning>();
		for (int i = 0; i < e.length; i++) {
			LightningBolt branch = new LightningBolt(s[i], e[i], c);
			lights.add(branch);
		}
		return new LightningEffect(lights);
	}

	public static LightningEffect addBranch(Vector2f s, Vector2f e, LColor c) {
		return addBranch(new Vector2f[] { s }, new Vector2f[] { e }, c);
	}

	// screen.add(LightningEffect.addBranch(Vector2f.at(33, 33),
	// Vector2f.at(300, 300),LColor.red));
	public static LightningEffect addBranch(Vector2f[] s, Vector2f[] e, LColor c) {
		TArray<ILightning> lights = new TArray<ILightning>();
		for (int i = 0; i < e.length; i++) {
			LightningBranch branch = new LightningBranch(s[i], e[i], c);
			lights.add(branch);
		}
		return new LightningEffect(lights);
	}

	public static LightningEffect addRandom(int count, Vector2f s, Vector2f e, LColor c) {
		return addRandom(count, new Vector2f[] { s }, new Vector2f[] { e }, c);
	}

	// screen.add(LightningEffect.addRandom(30,Vector2f.at(33, 33),
	// Vector2f.at(300, 300),LColor.red));
	public static LightningEffect addRandom(int count, Vector2f[] s, Vector2f[] e, LColor c) {
		TArray<ILightning> lights = new TArray<ILightning>();
		for (int i = 0; i < e.length; i++) {
			LightningRandom branch = new LightningRandom(count, s[i], e[i], c);
			lights.add(branch);
		}
		return new LightningEffect(lights);
	}

	public LightningEffect(TArray<ILightning> lights) {
		this.setLocation(0, 0);
		this.setRepaint(true);
		this.lists.addAll(lights);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		for (ILightning bolt : lists) {
			bolt.update(elapsedTime);
			if (bolt.isComplete()) {
				_countCompleted++;
			}
		}
		if (_countCompleted >= lists.size) {
			_completed = true;
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (isDisposed()) {
			return;
		}
		BaseBatch batch = g.batch();
		batch.setBlendMode(MODE_ALPHA_ONE);
		for (ILightning bolt : lists) {
			bolt.draw(g, drawX(offsetX), drawY(offsetY));
		}
		batch.flush();
		batch.setBlendMode(-1);
	}

	public LTexture getLightningSegment() {
		if (_lightningSegment == null || _lightningSegment.isClosed()) {
			loadLightning();
		}
		return _lightningSegment;
	}

	public LTexture getHalfCircle() {
		if (_halfCircle == null || _halfCircle.isClosed()) {
			loadLightning();
		}
		return _halfCircle;
	}

	public LTexture getPixel() {
		if (_pixel == null || _pixel.isClosed()) {
			loadLightning();
		}
		return _pixel;
	}

	public LightningEffect loadLightning() {
		loadLightning(new LTexturePack(LSystem.getSystemImagePath() + "natural.txt"));
		return this;
	}

	@Override
	public LightningEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (lists != null) {
			for (ILightning light : lists) {
				if (light != null) {
					light.close();
				}
			}
			lists.clear();
		}
		if (this._lightningSegment != null) {
			this._lightningSegment.close();
			this._lightningSegment = null;
		}
		if (this._halfCircle != null) {
			this._halfCircle.close();
			this._halfCircle = null;
		}
		if (this._pixel != null) {
			this._pixel.close();
			this._pixel = null;
		}
	}

}
