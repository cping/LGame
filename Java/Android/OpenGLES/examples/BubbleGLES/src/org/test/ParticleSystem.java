package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class ParticleSystem {
	protected java.util.ArrayList<TextParticle> activeTextParticles;
	protected java.util.ArrayList<TextureParticle> activeTextureParticles;
	protected LFont font;
	protected java.util.ArrayList<TextParticle> freeTextParticles;
	protected java.util.ArrayList<TextureParticle> freeTextureParticles;
	protected Vector2f gravity;
	protected float groundY;
	protected LTexture texture;

	public ParticleSystem(int maximumTextureParticles,
			int maximumTextParticles, LFont font, LTexture myTexture,
			Vector2f gravity, float groundY) {
		int num;
		this.activeTextureParticles = new java.util.ArrayList<TextureParticle>();
		this.freeTextureParticles = new java.util.ArrayList<TextureParticle>();
		this.activeTextParticles = new java.util.ArrayList<TextParticle>();
		this.freeTextParticles = new java.util.ArrayList<TextParticle>();
		for (num = 0; num < maximumTextureParticles; num++) {
			this.freeTextureParticles.add(new TextureParticle());
		}
		for (num = 0; num < maximumTextParticles; num++) {
			this.freeTextParticles.add(new TextParticle());
		}
		this.gravity = gravity;
		this.groundY = groundY;
		this.texture = myTexture;
		this.font = font;
	}

	public final void AddTextParticle(String text, float alpha,
			float reduceAlpha, Vector2f origin, float scale,
			float growPerSecond, float angle, float rotation,
			boolean setAngleFromDirection, boolean withGravity,
			boolean bounceBack, Vector2f direction, Vector2f position, float depth) {
		this.AddTextParticle(text, LColor.white, alpha, reduceAlpha, origin,
				scale, growPerSecond, angle, rotation, setAngleFromDirection,
				withGravity, bounceBack, direction, position, depth);
	}

	public final void AddTextParticle(String text, LColor color, float alpha,
			float reduceAlpha, Vector2f origin, float scale,
			float growPerSecond, float angle, float rotation,
			boolean setAngleFromDirection, boolean withGravity,
			boolean bounceBack, Vector2f direction, Vector2f position, float depth) {
		if (this.freeTextParticles.size() > 0) {
			TextParticle item = this.freeTextParticles.get(0);
			item.active = true;
			item.color = color;
			item.timeAlive = 0.0;
			item.delay = 0.0;
			item.spriteEffect = SpriteEffects.None;
			item.text = text;
			item.alpha = alpha;
			item.reduceAlphaPerSecond = reduceAlpha;
			item.origin = origin;
			item.scale = scale;
			item.growPerSecond = growPerSecond;
			item.angle = angle;
			item.rotationPerSecond = rotation;
			item.setAngleFromDirection = setAngleFromDirection;
			item.gravity = withGravity;
			item.bounceFromGround = bounceBack;
			item.position = position;
			item.direction = direction;
			item.depth = depth;
			this.activeTextParticles.add(item);
			this.freeTextParticles.remove(0);
		}
	}

	public final void AddTextureParticle(RectBox source, float alpha,
			float reduceAlpha, Vector2f origin, float scale,
			float growPerSecond, float angle, float rotation,
			boolean setAngleFromDirection, boolean withGravity,
			boolean bounceBack, Vector2f direction, Vector2f position, float depth) {
		if (this.freeTextureParticles.size() > 0) {
			TextureParticle item = this.freeTextureParticles.get(0);
			item.active = true;
			item.color = LColor.white;
			item.timeAlive = 0.0;
			item.delay = 0.0;
			item.spriteEffect = SpriteEffects.None;
			item.sourceRectangle = source;
			item.alpha = alpha;
			item.reduceAlphaPerSecond = reduceAlpha;
			item.origin = origin;
			item.scale = scale;
			item.growPerSecond = growPerSecond;
			item.angle = angle;
			item.rotationPerSecond = rotation;
			item.setAngleFromDirection = setAngleFromDirection;
			item.gravity = withGravity;
			item.bounceFromGround = bounceBack;
			item.position = position;
			item.direction = direction;
			item.depth = depth;
			this.activeTextureParticles.add(item);
			this.freeTextureParticles.remove(0);
		}
	}

	public void Draw(SpriteBatch spriteBatch) {
		int num;
		for (num = 0; num < this.activeTextureParticles.size(); num++) {
			TextureParticle particle = this.activeTextureParticles.get(num);
			spriteBatch.draw(this.texture, particle.position, new RectBox(
					particle.sourceRectangle), LColor.white, particle.angle, particle.origin,
					particle.scale, particle.spriteEffect);
		}
		for (num = 0; num < this.activeTextParticles.size(); num++) {
		
		}
	}

	public final void Reset() {
		while (this.activeTextureParticles.size() > 0) {
			this.freeTextureParticles.add(this.activeTextureParticles.get(0));
			this.activeTextureParticles.remove(0);
		}
		while (this.activeTextParticles.size() > 0) {
			this.freeTextParticles.add(this.activeTextParticles.get(0));
			this.activeTextParticles.remove(0);
		}
	}

	public final void Update(GameTime gameTime) {
		int num;
		this.UpdateParticles(gameTime);
		for (num = 0; num < this.activeTextureParticles.size(); num++) {
			if (!this.activeTextureParticles.get(num).active) {
				this.freeTextureParticles.add(this.activeTextureParticles
						.get(num));
				this.activeTextureParticles.remove(num);
				num--;
			}
		}
		for (num = 0; num < this.activeTextParticles.size(); num++) {
			if (!this.activeTextParticles.get(num).active) {
				this.freeTextParticles.add(this.activeTextParticles.get(num));
				this.activeTextParticles.remove(num);
				num--;
			}
		}
	}

	protected void UpdateParticles(GameTime gameTime) {
		Particle particle;
		int num2;
		float totalSeconds = gameTime.getElapsedGameTime();
		for (num2 = 0; num2 < this.activeTextureParticles.size(); num2++) {
			particle = this.activeTextureParticles.get(num2);
			particle.alpha -= particle.reduceAlphaPerSecond * totalSeconds;
			particle.scale += particle.growPerSecond * totalSeconds;
			particle.timeAlive += totalSeconds;
			if (((particle.alpha <= 0f) || (particle.scale <= 0f))
					|| (particle.position.y > 1000f)) {
				particle.active = false;
			} else {
				if (particle.setAngleFromDirection) {
					particle.angle = Trigonometry.getAngle(particle.direction) - 1.570796f;
				} else {
					particle.angle += particle.rotationPerSecond * totalSeconds;
				}
				if (particle.gravity) {
					particle.direction.addLocal(this.gravity.mul(totalSeconds));
				}
				particle.position.addLocal(particle.direction.mul(totalSeconds));
				if (particle.bounceFromGround
						&& ((particle.direction.y > 0f) && (particle.position.y > this.groundY))) {
					particle.direction.y *= -0.8f;
				}
			}
		}
		for (num2 = 0; num2 < this.activeTextParticles.size(); num2++) {
			particle = this.activeTextParticles.get(num2);
			particle.alpha -= particle.reduceAlphaPerSecond * totalSeconds;
			particle.scale += particle.growPerSecond * totalSeconds;
			particle.timeAlive += totalSeconds;
			if (((particle.alpha <= 0f) || (particle.scale <= 0f))
					|| (particle.position.y > 1000f)) {
				particle.active = false;
			} else {
				if (particle.setAngleFromDirection) {
					particle.angle = Trigonometry.getAngle(particle.direction) - 1.570796f;
				} else {
					particle.angle += particle.rotationPerSecond * totalSeconds;
				}
				if (particle.gravity) {
					particle.direction.addLocal(this.gravity.mul(totalSeconds));
				}
				particle.position.addLocal(particle.direction.mul(totalSeconds));
				if (particle.bounceFromGround
						&& ((particle.direction.y > 0f) && (particle.position.y > this.groundY))) {
					particle.direction.y *= -0.8f;
				}
			}
		}
	}
}