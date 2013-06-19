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
package loon.core.graphics.opengl.particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;


public class ParticleSystem {

	public static final int BLEND_ADDITIVE = 1;

	public static final int BLEND_COMBINE = 2;

	private static final int DEFAULT_PARTICLES = 100;

	private ArrayList<ParticleEmitter> removeMe = new ArrayList<ParticleEmitter>();

	private class ParticlePool {
		public Particle[] particles;
		public ArrayList<Particle> available;

		public ParticlePool(ParticleSystem system, int maxParticles) {
			particles = new Particle[maxParticles];
			available = new ArrayList<Particle>();

			for (int i = 0; i < particles.length; i++) {
				particles[i] = createParticle(system);
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

	protected HashMap<ParticleEmitter, ParticlePool> particlesByEmitter = new HashMap<ParticleEmitter, ParticlePool>();

	protected int maxParticlesPerEmitter;

	protected ArrayList<ParticleEmitter> emitters = new ArrayList<ParticleEmitter>();

	protected Particle dummy;

	private int blendingMode = BLEND_COMBINE;

	private int pCount;

	private boolean usePoints;

	private float x;

	private float y;

	private boolean removeCompletedEmitters = true;

	private LTexture sprite;

	private boolean visible = true;

	private String defaultImageName;

	private LColor mask;

	public ParticleSystem(LTexture defaultSprite) {
		this(defaultSprite, DEFAULT_PARTICLES);
	}

	public ParticleSystem(String defaultSpriteRef) {
		this(defaultSpriteRef, DEFAULT_PARTICLES);
	}

	public void reset() {
		Iterator<ParticlePool> pools = particlesByEmitter.values().iterator();
		while (pools.hasNext()) {
			ParticlePool pool = pools.next();
			pool.reset(this);
		}

		for (int i = 0; i < emitters.size(); i++) {
			ParticleEmitter emitter = emitters.get(i);
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

	public ParticleSystem(String defaultSpriteRef, int maxParticles) {
		this(defaultSpriteRef, maxParticles, null);
	}

	public ParticleSystem(String defaultSpriteRef, int maxParticles, LColor mask) {
		this.maxParticlesPerEmitter = maxParticles;
		this.mask = mask;

		setDefaultImageName(defaultSpriteRef);
		dummy = createParticle(this);
	}

	public ParticleSystem(LTexture defaultSprite, int maxParticles) {
		this.maxParticlesPerEmitter = maxParticles;

		sprite = defaultSprite;
		dummy = createParticle(this);
	}

	public void setDefaultImageName(String ref) {
		defaultImageName = ref;
		sprite = null;
	}

	public int getBlendingMode() {
		return blendingMode;
	}

	protected Particle createParticle(ParticleSystem system) {
		return new Particle(system);
	}

	public void setBlendingMode(int mode) {
		this.blendingMode = mode;
	}

	public int getEmitterCount() {
		return emitters.size();
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
		for (int i = 0; i < emitters.size(); i++) {
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


		g.translate(x, y);

		if (blendingMode == BLEND_ADDITIVE) {
			GLEx.self.setBlendMode(GL.MODE_ALPHA_ONE);
		}
		if (usePoints()) {
			GLEx.gl10.glEnable(GL.GL_POINT_SMOOTH);
			g.glTex2DDisable();
		}

		for (int emitterIdx = 0; emitterIdx < emitters.size(); emitterIdx++) {

			ParticleEmitter emitter = emitters.get(emitterIdx);

			if (!emitter.isEnabled()) {
				continue;
			}

			if (emitter.useAdditive()) {
				g.setBlendMode(GL.MODE_ALPHA_ONE);
			}

			ParticlePool pool =  particlesByEmitter.get(emitter);
			LTexture image = emitter.getImage();
			if (image == null) {
				image = this.sprite;
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glBegin();
			}

			for (int i = 0; i < pool.particles.length; i++) {
				if (pool.particles[i].inUse()){
					pool.particles[i].render();
				}
			}

			if (!emitter.isOriented() && !emitter.usePoints(this)) {
				image.glEnd();
			}

			if (emitter.useAdditive()) {
				g.setBlendMode(GL.MODE_NORMAL);
			}
		}

		if (usePoints()) {
			GLEx.gl10.glDisable(GL.GL_POINT_SMOOTH);
		}
		if (blendingMode == BLEND_ADDITIVE) {
			g.setBlendMode(GL.MODE_NORMAL);
		}

		g.resetColor();
		g.translate(-x, -y);

	}

	private void loadSystemParticleImage() {

		try {
			if (mask != null) {
				sprite = TextureUtils.filterColor(defaultImageName, mask);
			} else {
				sprite = new LTexture(defaultImageName);
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
		ArrayList<ParticleEmitter> emitters = new ArrayList<ParticleEmitter>(
				this.emitters);
		for (int i = 0; i < emitters.size(); i++) {
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
			Iterator<ParticleEmitter> it = particlesByEmitter.keySet()
					.iterator();
			while (it.hasNext()) {
				ParticleEmitter emitter = it.next();
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

	public Particle getNewParticle(ParticleEmitter emitter, float life) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		ArrayList<Particle> available = pool.available;
		if (available.size() > 0) {
			Particle p = available.remove(available.size() - 1);
			p.init(emitter, life);
			p.setImage(sprite);

			return p;
		}
		return dummy;
	}

	public void release(Particle particle) {
		if (particle != dummy) {
			ParticlePool pool = particlesByEmitter.get(particle
					.getEmitter());
			pool.available.add(particle);
		}
	}

	public void releaseAll(ParticleEmitter emitter) {
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

	public void moveAll(ParticleEmitter emitter, float x, float y) {
		ParticlePool pool = particlesByEmitter.get(emitter);
		for (int i = 0; i < pool.particles.length; i++) {
			if (pool.particles[i].inUse()) {
				pool.particles[i].move(x, y);
			}
		}
	}

}
