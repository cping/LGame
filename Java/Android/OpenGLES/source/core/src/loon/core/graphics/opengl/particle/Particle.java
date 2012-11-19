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

import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;


public class Particle {

	public static final int INHERIT_POINTS = 1;

	public static final int USE_POINTS = 2;

	public static final int USE_QUADS = 3;

	protected float x;

	protected float y;

	protected float velx;

	protected float vely;

	protected float size = 10;

	protected LColor color = LColor.white;

	protected float life;

	protected float originalLife;

	private ParticleSystem engine;

	private ParticleEmitter emitter;

	protected LTexture image;

	protected int type;

	protected int usePoints = INHERIT_POINTS;

	protected boolean oriented = false;

	protected float scaleY = 1.0f;

	public Particle(ParticleSystem engine) {
		this.engine = engine;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public float getSize() {
		return size;
	}

	public LColor getColor() {
		return color;
	}

	public void setImage(LTexture image) {
		this.image = image;
	}

	public float getOriginalLife() {
		return originalLife;
	}

	public float getLife() {
		return life;
	}

	public boolean inUse() {
		return life > 0;
	}

	public void render() {
		if ((engine.usePoints() && (usePoints == INHERIT_POINTS))
				|| (usePoints == USE_POINTS)) {
			GLEx gl = GLEx.self;
			gl.glBegin(GL.GL_POINTS);
			gl.glColor(color);
			gl.glVertex2f(x, y);
			gl.glEnd();
		} else {
			float angle = 0;
			if (oriented) {
				angle = MathUtils.atan2(y, x) * 180 / MathUtils.PI;
			}
			image.draw((x - (size / 2)), (y - (size / 2)), size, size, angle,
					color);
		}
	}

	public void update(long delta) {
		emitter.updateParticle(this, delta);
		life -= delta;
		if (life > 0) {
			x += delta * velx;
			y += delta * vely;
		} else {
			engine.release(this);
		}
	}

	public void init(ParticleEmitter emitter, float life) {
		x = 0;
		this.emitter = emitter;
		y = 0;
		velx = 0;
		vely = 0;
		size = 10;
		type = 0;
		this.originalLife = this.life = life;
		oriented = false;
		scaleY = 1.0f;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUsePoint(int usePoints) {
		this.usePoints = usePoints;
	}

	public int getType() {
		return type;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void adjustSize(float delta) {
		size += delta;
		size = Math.max(0, size);
	}

	public void setLife(float life) {
		this.life = life;
	}

	public void adjustLife(float delta) {
		life += delta;
	}

	public void kill() {
		life = 1;
	}

	public void setColor(float r, float g, float b, float a) {
		if (color == LColor.white) {
			color = new LColor(r, g, b, a);
		} else {
			color.r = r;
			color.g = g;
			color.b = b;
			color.a = a;
		}
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setVelocity(float dirx, float diry, float speed) {
		this.velx = dirx * speed;
		this.vely = diry * speed;
	}

	public void setSpeed(float speed) {
		float currentSpeed =  MathUtils.sqrt((velx * velx) + (vely * vely));
		velx *= speed;
		vely *= speed;
		velx /= currentSpeed;
		vely /= currentSpeed;
	}

	public void setVelocity(float velx, float vely) {
		setVelocity(velx, vely, 1);
	}

	public void adjustPosition(float dx, float dy) {
		x += dx;
		y += dy;
	}

	public void adjustColor(float r, float g, float b, float a) {
		if (color == null) {
			color = new LColor(1, 1, 1, 1f);
		}
		color.r += r;
		color.g += g;
		color.b += b;
		color.a += a;
	}

	public void adjustColor(int r, int g, int b, int a) {
		if (color == null) {
			color = new LColor(1, 1, 1, 1f);
		}

		color.r += (r / 255.0f);
		color.g += (g / 255.0f);
		color.b += (b / 255.0f);
		color.a += (a / 255.0f);
	}

	public void adjustVelocity(float dx, float dy) {
		velx += dx;
		vely += dy;
	}

	public ParticleEmitter getEmitter() {
		return emitter;
	}

	public boolean isOriented() {
		return oriented;
	}

	public void setOriented(boolean oriented) {
		this.oriented = oriented;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
}
