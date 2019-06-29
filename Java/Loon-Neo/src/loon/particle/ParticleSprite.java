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
package loon.particle;

import loon.LTexture;
import loon.LSystem;
import loon.action.ColorTo;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.utils.MathUtils;

public class ParticleSprite extends Entity {

	private float currentLife, lifespan, gravityX, gravityY, velocityX,
			velocityY, rotationAmount, opacity;
	private boolean fadeOut, remove;
	private ColorTo colorEffects;

	public ParticleSprite(String path, float lifespan) {
		this(LSystem.loadTexture(path), lifespan);
	}

	public ParticleSprite(LTexture texture, float lifespan) {
		this(texture, texture.width(), texture.height(), lifespan);
	}

	public ParticleSprite(LTexture texture, float width, float height,
			float lifespan) {
		super(texture);
		setSize(width, height);
		setup(lifespan, 0, 0, 0, 0, 0, 1, true);
	}

	public ParticleSprite(LTexture texture, float x, float y, float width,
			float height, float lifespan, float gravityX, float gravityY,
			float velocityX, float velocityY, float rotationAmount,
			float opacity, boolean fadeOut) {
		super(texture);
		setSize(width, height);
		setLocation(x, y);
		setup(lifespan, gravityX, gravityY, velocityX, velocityY,
				rotationAmount, opacity, fadeOut);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		float delta = MathUtils.max(elapsedTime / 1000f, LSystem.MIN_SECONE_SPEED_FIXED);
		currentLife += delta;
		if (currentLife >= lifespan) {
			currentLife = lifespan;
			remove = true;
		}
		velocityX += gravityX * delta;
		velocityY += gravityY * delta;
		this.setX(getX() + velocityX * delta);
		this.setY(getY() + velocityY * delta);
		this.setRotation(rotationAmount * currentLife / lifespan);
		if (fadeOut && colorEffects != null) {
			colorEffects.update(elapsedTime);
			this.setColor(colorEffects.getCurrentRed(),
					colorEffects.getCurrentGreen(),
					colorEffects.getCurrentBlue(),
					colorEffects.getCurrentAlpha());
		}
	}

	public void setup(float lifespan, float gravityX, float gravityY,
			float velocityX, float velocityY, float rotationAmount,
			float opacity, boolean fadeOut) {

		this.remove = false;
		this.currentLife = 0;
		this.lifespan = lifespan;
		this.gravityX = gravityX;
		this.gravityY = gravityY;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.rotationAmount = rotationAmount;
		this.opacity = opacity;
		this.fadeOut = fadeOut;
		if (fadeOut) {
			colorEffects = new ColorTo(new LColor(1f, 1f, 1f, 1f), new LColor(
					1f, 1f, 1f, 0.0f), lifespan);
		}
	}

	public float getCurrentLife() {
		return currentLife;
	}

	public void setCurrentLife(float currentLife) {
		this.currentLife = currentLife;
	}

	public float getLifespan() {
		return lifespan;
	}

	public void setLifespan(float lifespan) {
		this.lifespan = lifespan;
	}

	public float getGravityX() {
		return gravityX;
	}

	public void setGravityX(float gravityX) {
		this.gravityX = gravityX;
	}

	public float getGravityY() {
		return gravityY;
	}

	public void setGravityY(float gravityY) {
		this.gravityY = gravityY;
	}

	public float getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}

	public float getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}

	public float getRotationAmount() {
		return rotationAmount;
	}

	public void setRotationAmount(float rotationAmount) {
		this.rotationAmount = rotationAmount;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public boolean isFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(boolean fadeOut) {
		this.fadeOut = fadeOut;
	}

	public boolean needToRemove() {
		return remove;
	}
}
