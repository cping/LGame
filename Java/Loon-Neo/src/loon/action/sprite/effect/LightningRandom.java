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
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 在指定范围内创造随机数量闪电(就是野比饭初次变超赛2那种效果)
 */
public class LightningRandom implements ILightning {

	private LTimer _timer = new LTimer(0);
	private TArray<Vector2f> _particles = new TArray<Vector2f>();
	private TArray<LightningBranch> _bolts = new TArray<LightningBranch>();
	private float _hue = 4.5f;
	private float[] _noise = null;
	private LColor _color = null;
	private boolean _closed;

	public LightningRandom(int count, Vector2f source, Vector2f dest) {
		this(count, source.x, source.y, dest.x, dest.y, null);
	}

	public LightningRandom(int count, Vector2f source, Vector2f dest, LColor c) {
		this(count, source.x, source.y, dest.x, dest.y, c);
	}

	public LightningRandom(int count, float x, float y, float w, float h) {
		this(count, x, y, w, h, null);
	}

	public LightningRandom(int count, float x, float y, float w, float h, LColor c) {
		this._color = c;
		for (int i = 0; i < count; i++) {
			Vector2f v = new Vector2f(MathUtils.random(x, w), MathUtils.random(y, h));
			_particles.add(v);
		}
		_noise = new float[count];
		for (int i = 0; i < count; i++) {
			_noise[i] = MathUtils.random();
		}
	}

	public LightningRandom(Image image, Vector2f pos, LColor c) {
		this(image, pos.x, pos.y, c);
	}

	public LightningRandom(Image image, float x, float y, LColor c) {
		this(createParticle(image), x, y, c);
	}

	public LightningRandom(TArray<Vector2f> pars, Vector2f pos, LColor c) {
		this(pars, pos.x, pos.y, c);
	}

	public LightningRandom(TArray<Vector2f> pars, float x, float y, LColor c) {
		this(pars, x, y, 35, c);
	}

	public LightningRandom(TArray<Vector2f> pars, float x, float y, int len, LColor c) {
		for (int i = 0; i < pars.size; i++) {
			pars.get(i).addSelf(x, y);
		}
		this._particles.addAll(pars);
		this._color = c;
		this._noise = new float[len];
		for (int i = 0; i < len; i++) {
			_noise[i] = MathUtils.random();
		}
	}

	public float getHue() {
		return _hue;
	}

	public void setHue(float h) {
		_hue = h;
	}

	final static TArray<Vector2f> createParticle(Image img) {
		return createParticle(img, 2, 1.5f);
	}

	final static TArray<Vector2f> createParticle(Image img, int interval, float scale) {
		Vector2f size = Vector2f.at(img.getWidth() / 2, img.getHeight() / 2);
		TArray<Vector2f> points = img.getPoints(size, interval, scale);
		return points;
	}

	@Override
	public void draw(GLEx g, float x, float y) {
		for (LightningBranch bolt : _bolts) {
			bolt.draw(g, x, y);
		}
	}

	public void setDelay(long delay) {
		_timer.setDelay(delay);
	}

	public long getDelay() {
		return _timer.getDelay();
	}

	@Override
	public void update(long elapsedTime) {
		if (_timer.action(elapsedTime)) {
			_bolts.clear();
			_hue += 0.01f;
			if (_hue >= 6) {
				_hue -= 6;
			}
			int size = LSystem.viewSize.getWidth();
			for (Vector2f particle : _particles) {
				float x = particle.x / size;
				int boltChance = (int) (20 * MathUtils.sin(3 * _hue * MathUtils.PI - x + 1 * getNoise(_hue + x)) + 52);
				if (MathUtils.nextInt(boltChance) == 0) {
					Vector2f nearestParticle = Vector2f.ZERO();
					float nearestDist = Float.MAX_VALUE;
					for (int i = 0; i < 50; i++) {
						Vector2f other = _particles.get(MathUtils.nextInt(_particles.size - 1));
						float dist = Vector2f.dst(particle, other);
						if (dist < nearestDist && dist > 10 * 10) {
							nearestDist = dist;
							nearestParticle = other;
						}
					}
					if (nearestDist < 200 * 200 && nearestDist > 10 * 10) {
						_bolts.add(new LightningBranch(particle, nearestParticle,
								_color == null ? LColor.hsvToColor(_hue, 0.5f, 1f) : _color));
					}
				}
			}
		}

	}

	private final float getNoise(float x) {
		x = MathUtils.max(x, 0);
		int length = _noise.length;
		int i = ((int) (length * x)) % length;
		int j = (i + 1) % length;
		return MathUtils.smoothStep(_noise[i], _noise[j], x - (int) x);
	}

	@Override
	public boolean isComplete() {
		return false;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_particles != null) {
			_particles.clear();
		}
		if (_bolts != null) {
			_bolts.clear();
		}
		_closed = true;
	}

}
