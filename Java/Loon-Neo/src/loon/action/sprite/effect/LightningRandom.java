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
import loon.action.sprite.SpriteBatch;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 在指定范围内创造随机数量闪电(就是野比饭初次变超赛2那种效果)
 */
public class LightningRandom implements ILightning {

	private LTimer timer = new LTimer(0);
	private TArray<Vector2f> particles = new TArray<Vector2f>();
	private TArray<LightningBranch> bolts = new TArray<LightningBranch>();
	private float hue = 4.5f;
	private float[] noise = null;
	private LColor color = null;

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
		this.color = c;
		for (int i = 0; i < count; i++) {
			Vector2f v = new Vector2f(MathUtils.random(x, w), MathUtils.random(y, h));
			particles.add(v);
		}
		noise = new float[count];
		for (int i = 0; i < count; i++) {
			noise[i] = MathUtils.random();
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
		int len = 35;
		for (int i = 0; i < pars.size; i++) {
			pars.get(i).addSelf(x, y);
		}
		this.particles.addAll(pars);
		this.color = c;
		this.noise = new float[len];
		for (int i = 0; i < len; i++) {
			noise[i] = MathUtils.random();
		}
	}

	final static TArray<Vector2f> createParticle(Image img) {
		Vector2f size = Vector2f.at(img.getWidth() / 2, img.getHeight() / 2);
		final int interval = 2;
		final float scale = 1.5f;
		TArray<Vector2f> points = img.getPoints(size, interval, scale);
		return points;
	}

	public void draw(SpriteBatch batch, float x, float y) {
		for (LightningBranch bolt : bolts) {
			bolt.draw(batch, x, y);
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			bolts.clear();
			hue += 0.01f;
			if (hue >= 6) {
				hue -= 6;
			}
			int size = LSystem.viewSize.getWidth();
			for (Vector2f particle : particles) {
				float x = particle.x / size;
				int boltChance = (int) (20 * MathUtils.sin(3 * hue * MathUtils.PI - x + 1 * getNoise(hue + x)) + 52);
				if (MathUtils.nextInt(boltChance) == 0) {
					Vector2f nearestParticle = Vector2f.ZERO();
					float nearestDist = Float.MAX_VALUE;
					for (int i = 0; i < 50; i++) {
						Vector2f other = particles.get(MathUtils.nextInt(particles.size));
						float dist = Vector2f.dst(particle, other);
						if (dist < nearestDist && dist > 10 * 10) {
							nearestDist = dist;
							nearestParticle = other;
						}
					}
					if (nearestDist < 200 * 200 && nearestDist > 10 * 10) {
						bolts.add(new LightningBranch(particle, nearestParticle,
								color == null ? LColor.hsvToColor(hue, 0.5f, 1f) : color));
					}
				}
			}
		}

	}

	private final float getNoise(float x) {
		x = MathUtils.max(x, 0);
		int length = noise.length;
		int i = ((int) (length * x)) % length;
		int j = (i + 1) % length;
		return MathUtils.smoothStep(noise[i], noise[j], x - (int) x);
	}

	@Override
	public boolean isComplete() {
		return false;
	}

	@Override
	public void close() {
		particles.clear();
		bolts.clear();
	}
}
