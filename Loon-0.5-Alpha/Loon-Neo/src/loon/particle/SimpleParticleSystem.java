/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.particle;

import java.util.Iterator;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.GLUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class SimpleParticleSystem {

	private static final int DEFAULT_PARTICLES = 100;

	private TArray<SimpleEmitter> removeMe = new TArray<SimpleEmitter>();

	private class ParticlePool {
		public SimpleParticle[] particles;
		public TArray<SimpleParticle> available;

		public ParticlePool(SimpleParticleSystem system, int maxParticles) {
			particles = new SimpleParticle[maxParticles];
			available = new TArray<SimpleParticle>();

			for (int i = 0; i < particles.length; i++) {
				particles[i] = createParticle(system);
			}

			reset(system);
		}

		public void reset(SimpleParticleSystem system) {
			available.clear();

			for (int i = 0; i < particles.length; i++) {
				available.add(particles[i]);
			}
		}
	}

	protected ObjectMap<SimpleEmitter, ParticlePool> particlesByEmitter = new ObjectMap<SimpleEmitter, ParticlePool>();

	protected int maxParticlesPerEmitter;

	protected TArray<SimpleEmitter> emitters = new TArray<SimpleEmitter>();

	protected SimpleParticle dummy;

	private int pCount;

	private boolean usePoints;

	private float x;

	private float y;

	private boolean removeCompletedEmitters = true;

	private LTexture sprite;

	private boolean visible = true;

	private String defaultImageName;

	private LColor mask;

	public SimpleParticleSystem(LTexture defaultSprite) {
		this(defaultSprite, DEFAULT_PARTICLES);
	}

	public SimpleParticleSystem(String defaultSpriteRef) {
		this(defaultSpriteRef, DEFAULT_PARTICLES);
	}

	public void reset() {
		Iterator<ParticlePool> pools = particlesByEmitter.values().iterator();
		while (pools.hasNext()) {
			ParticlePool pool = pools.next();
			pool.reset(this);
		}

		for (int i = 0; i < emitters.size; i++) {
			SimpleEmitter emitter = emitters.get(i);
			emitter.resetState();
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setRemoveCompletedEmitters(boolean remove) {
		removeCompletedEmitters = remove;
	}

	public void setUsePoints(boolean usePoints) {
		this.usePoints = usePoints;
	}

	public boolean usePoints() {
		return usePoints;
	}

	public SimpleParticleSystem(String defaultSpriteRef, int maxParticles) {
		this(defaultSpriteRef, maxParticles, null);
	}

	public SimpleParticleSystem(String defaultSpriteRef, int maxParticles,
			LColor mask) {
		this.maxParticlesPerEmitter = maxParticles;
		this.mask = mask;

		setDefaultImageName(defaultSpriteRef);
		dummy = createParticle(this);
	}

	public SimpleParticleSystem(LTexture defaultSprite, int maxParticles) {
		this.maxParticlesPerEmitter = maxParticles;

		sprite = defaultSprite;
		dummy = createParticle(this);
	}

	public void setDefaultImageName(String ref) {
		defaultImageName = ref;
		sprite = null;
	}

	public int getBlendingMode() {
		return GLUtils.getBlendMode();
	}

	protected SimpleParticle createParticle(SimpleParticleSystem system) {
		return new SimpleParticle(system);
	}

	public int getEmitterCount() {
		return emitters.size;
	}

	public SimpleEmitter getEmitter(int index) {
		return emitters.get(index);
	}

	public void addEmitter(SimpleEmitter emitter) {
		emitters.add(emitter);

		ParticlePool pool = new ParticlePool(this, maxParticlesPerEmitter);
		particlesByEmitter.put(emitter, pool);
	}

	public void removeEmitter(SimpleEmitter emitter) {
		emitters.remove(emitter);
		particlesByEmitter.remove(emitter);
	}

	public void removeAllEmitters() {
		for (int i = 0; i < emitters.size; i++) {
			removeEmitter(emitters.get(i));
			i--;
		}
	}

	public float getPositionX() {
		return x;
	}

	public float getPositionY() {
		return y;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void render(GLEx g) {
		render(g, x, y);
	}

	public void render(GLEx g, float x, float y) {

		if (!visible) {
			return;
		}

		if ((sprite == null) && (defaultImageName != null)) {
			loadSystemParticleImage();
		}

		g.save();
		g.translate(x, y);

		for (int emitterIdx = 0; emitterIdx < emitters.size; emitterIdx++) {

			SimpleEmitter emitter = emitters.get(emitterIdx);

			if (!emitter.isEnabled()) {
				continue;
			}

			int mode = g.getBlendMode();

			if (emitter.useAdditive()) {
				g.setBlendMode(LSystem.MODE_ADD);
			}

			ParticlePool pool = particlesByEmitter.get(emitter);
			LTexture image = emitter.getImage();
			if (image == null) {
				image = this.sprite;
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glBegin();
			}

			for (int i = 0; i < pool.particles.length; i++) {
				if (pool.particles[i].inUse()) {
					pool.particles[i].paint(g);
				}
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glEnd();
			}

			if (emitter.useAdditive()) {
				g.setBlendMode(mode);
			}
		}

		g.translate(-x, -y);
		g.restore();

	}

	private void loadSystemParticleImage() {
		try {
			if (mask != null) {
				sprite = TextureUtils.filterColor(defaultImageName, mask);
			} else {
				sprite = LTextures.loadTexture(defaultImageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			defaultImageName = null;
		}
	}

	public void update(long delta) {
		if ((sprite == null) && (defaultImageName != null)) {
			loadSystemParticleImage();
		}

		removeMe.clear();
		TArray<SimpleEmitter> emitters = new TArray<SimpleEmitter>(
				this.emitters);
		for (int i = 0; i < emitters.size; i++) {
			SimpleEmitter emitter = emitters.get(i);
			if (emitter.isEnabled()) {
				emitter.update(this, delta);
				if (removeCompletedEmitters) {
					if (emitter.completed()) {
						removeMe.add(emitter);
						particlesByEmitter.remove(emitter);
					}
				}
			}
		}
		this.emitters.removeAll(removeMe);

		pCount = 0;

		if (!particlesByEmitter.isEmpty()) {
			for (SimpleEmitter emitter : particlesByEmitter.keys()) {
				if (emitter.isEnabled()) {
					ParticlePool pool = particlesByEmitter.get(emitter);
					for (int i = 0; i < pool.particles.length; i++) {
						if (pool.particles[i].life > 0) {
							pool.particles[i].update(delta);
							pCount++;
						}
					}
				}
			}
		}
	}

	public int getParticleCount() {
		return pCount;
	}

	public SimpleParticle getNewParticle(SimpleEmitter emitter, float life) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		TArray<SimpleParticle> available = pool.available;
		if (available.size > 0) {
			SimpleParticle p = available.removeIndex(available.size - 1);
			p.init(emitter, life);
			p.setImage(sprite);

			return p;
		}
		return dummy;
	}

	public void release(SimpleParticle particle) {
		if (particle != dummy) {
			ParticlePool pool = particlesByEmitter.get(particle.getEmitter());
			pool.available.add(particle);
		}
	}

	public void releaseAll(SimpleEmitter emitter) {
		if (!particlesByEmitter.isEmpty()) {
			Iterator<ParticlePool> it = particlesByEmitter.values().iterator();
			while (it.hasNext()) {
				ParticlePool pool = it.next();
				for (int i = 0; i < pool.particles.length; i++) {
					if (pool.particles[i].inUse()) {
						if (pool.particles[i].getEmitter() == emitter) {
							pool.particles[i].setLife(-1);
							release(pool.particles[i]);
						}
					}
				}
			}
		}
	}

	public void moveAll(SimpleEmitter emitter, float x, float y) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		for (int i = 0; i < pool.particles.length; i++) {
			if (pool.particles[i].inUse()) {
				pool.particles[i].move(x, y);
			}
		}
	}

}
