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
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.BlendMethod;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.GLUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class ParticleSystem extends Entity {

	private static final int DEFAULT_PARTICLES = 100;

	private int state = BlendMethod.MODE_ADD;

	private TArray<ParticleEmitter> removeMe = new TArray<ParticleEmitter>();

	private static class ParticlePool {
		public ParticleParticle[] particles;
		public TArray<ParticleParticle> available;

		public ParticlePool(ParticleSystem system, int maxParticles) {
			particles = new ParticleParticle[maxParticles];
			available = new TArray<ParticleParticle>();

			for (int i = 0; i < particles.length; i++) {
				particles[i] = system.createParticle(system);
			}

			reset(system);
		}

		public void reset(ParticleSystem system) {
			available.clear();

			for (int i = 0; i < particles.length; i++) {
				available.add(particles[i]);
			}
		}
	}

	protected ObjectMap<ParticleEmitter, ParticlePool> particlesByEmitter = new ObjectMap<ParticleEmitter, ParticlePool>();

	protected int maxParticlesPerEmitter;

	protected TArray<ParticleEmitter> emitters = new TArray<ParticleEmitter>();

	protected ParticleParticle dummy;

	private int pCount;

	private boolean usePoints;

	private boolean removeCompletedEmitters = true;

	private LTexture sprite;

	private String defaultImageName;

	private LColor mask;

	public ParticleSystem(LTexture defaultSprite) {
		this(defaultSprite, DEFAULT_PARTICLES);
	}

	public ParticleSystem(String defaultSpriteRef) {
		this(defaultSpriteRef, DEFAULT_PARTICLES);
	}

	@Override
	public ParticleSystem reset() {
		Iterator<ParticlePool> pools = particlesByEmitter.values().iterator();
		while (pools.hasNext()) {
			ParticlePool pool = pools.next();
			pool.reset(this);
		}

		for (int i = 0; i < emitters.size; i++) {
			ParticleEmitter emitter = emitters.get(i);
			emitter.resetState();
		}
		super.reset();
		return this;
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

	public ParticleSystem(String defaultSpriteRef, int maxParticles) {
		this(defaultSpriteRef, maxParticles, null);
	}

	public ParticleSystem(String defaultSpriteRef, int maxParticles, LColor mask) {
		this.maxParticlesPerEmitter = maxParticles;
		this.mask = mask;
		this.setRepaint(true);
		setDefaultImageName(defaultSpriteRef);
		dummy = createParticle(this);
	}

	public ParticleSystem(LTexture defaultSprite, int maxParticles) {
		this.maxParticlesPerEmitter = maxParticles;
		this.setRepaint(true);
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

	protected ParticleParticle createParticle(ParticleSystem system) {
		return new ParticleParticle(system);
	}

	public int getEmitterCount() {
		return emitters.size;
	}

	public ParticleEmitter getEmitter(int index) {
		return emitters.get(index);
	}

	public void addEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);

		ParticlePool pool = new ParticlePool(this, maxParticlesPerEmitter);
		particlesByEmitter.put(emitter, pool);
	}

	public void removeEmitter(ParticleEmitter emitter) {
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
		return _objectLocation.x;
	}

	public float getPositionY() {
		return _objectLocation.y;
	}

	public void setPosition(float x, float y) {
		this.setLocation(x, y);
	}

	public void render(GLEx g) {
		repaint(g, _objectLocation.x, _objectLocation.y);
	}

	@Override
	public void repaint(GLEx g, float x, float y) {

		if ((sprite == null) && (defaultImageName != null)) {
			loadSystemParticleImage();
		}

		for (int emitterIdx = 0; emitterIdx < emitters.size; emitterIdx++) {

			ParticleEmitter emitter = emitters.get(emitterIdx);

			if (!emitter.isEnabled()) {
				continue;
			}

			int mode = g.getBlendMode();

			if (emitter.useAdditive()) {
				g.setBlendMode(BlendMethod.MODE_ADD);
			} else {
				g.setBlendMode(state);
			}

			ParticlePool pool = particlesByEmitter.get(emitter);
			LTexture image = emitter.getImage();
			if (image == null) {
				image = this.sprite;
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glBegin();
			}
			image.getTextureBatch().setLocation(x, y);
			image.getTextureBatch().setBlendMode(BlendMethod.MODE_NORMAL);

			for (int i = 0; i < pool.particles.length; i++) {
				if (pool.particles[i].inUse()) {
					pool.particles[i].paint(g);
				}
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glEnd();
			}

			g.setBlendMode(mode);

		}

	}

	private void loadSystemParticleImage() {
		try {
			if (mask != null) {
				sprite = TextureUtils.filterColor(defaultImageName, mask);
			} else {
				sprite = LSystem.loadTexture(defaultImageName);
			}
		} catch (Throwable e) {
			LSystem.error("Particle System load exception", e);
			defaultImageName = null;
		}
	}

	@Override
	public void onUpdate(long delta) {
		if ((sprite == null) && (defaultImageName != null)) {
			loadSystemParticleImage();
		}

		removeMe.clear();
		TArray<ParticleEmitter> emitters = new TArray<ParticleEmitter>(this.emitters);
		for (int i = 0; i < emitters.size; i++) {
			ParticleEmitter emitter = emitters.get(i);
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
			for (ParticleEmitter emitter : particlesByEmitter.keys()) {
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

	public ParticleParticle getNewParticle(ParticleEmitter emitter, float life) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		TArray<ParticleParticle> available = pool.available;
		if (available.size > 0) {
			ParticleParticle p = available.removeIndex(available.size - 1);
			p.init(emitter, life);
			p.setImage(sprite);

			return p;
		}
		return dummy;
	}

	public ParticleSystem release(ParticleParticle particle) {
		if (particle != dummy) {
			ParticlePool pool = particlesByEmitter.get(particle.getEmitter());
			pool.available.add(particle);
		}
		return this;
	}

	public ParticleSystem releaseAll(ParticleEmitter emitter) {
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
		return this;
	}

	public ParticleSystem moveAll(ParticleEmitter emitter, float x, float y) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		for (int i = 0; i < pool.particles.length; i++) {
			if (pool.particles[i].inUse()) {
				pool.particles[i].move(x, y);
			}
		}
		return this;
	}

	public int getBlendingState() {
		return state;
	}

	public ParticleSystem setBlendingState(int s) {
		this.state = s;
		return this;
	}

}
