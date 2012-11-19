package org.test;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTextures;
import loon.utils.MathUtils;

public class BubbleParticleEngine extends ParticleEngine {
	protected ParticleSystem simpleSystem = new ParticleSystem(500, 20,
			LFont.getFont(30), LTextures.loadTexture("assets/Balls.png"),
			new Vector2f(0f, 1500f), 800f);

	public BubbleParticleEngine() {
		super.AddSystem(this.simpleSystem);
	}

	public final void AddJokerEffect(Vector2f Position) {
		for (int i = 0; i < 2; i++) {
			this.simpleSystem
					.AddTextureParticle(new RectBox(50, 50, 50, 50), 0f, -3f,
							new Vector2f(20f, 20f), 1f, (-0.4f - (MathUtils
									.random())) - 0.5f,
							((MathUtils.random() - 0.5f) * 3.1415926535897931f),
							((MathUtils.random() - 0.5f) * 3.1415926535897931f),
							false, false, false,
							new Vector2f(((MathUtils.random()) - 0.5f) * 100f,
									((MathUtils.random() - 0.5f)) * 100f),
							Position, 0f);
		}
	}

	public final void AddJokerHitEffect(Vector2f Position) {
		for (int i = 0; i < 10; i++) {
			this.simpleSystem.AddTextureParticle(new RectBox(100, 50, 50, 50),
					30f, 20f, new Vector2f(20f, 20f), 0.8f, 0.2f + ((MathUtils
							.random()) * 0.1f),
					((MathUtils.random() - 0.5f) * 3.1415926535897931f),
					((MathUtils.random() - 0.5f) * 3.1415926535897931f), false,
					true, false,
					new Vector2f(((MathUtils.random()) - 0.5f) * 500f,
							((MathUtils.random() - 0.5f)) * 1000f), Position,
					0f);
		}
	}

	public final void BallAddToGritEffect(Vector2f Position) {
		this.simpleSystem.AddTextureParticle(new RectBox(0, 50, 50, 50), 12f,
				48f, new Vector2f(25f), 0.1f, 5f, 0f, 0f, false, false, false,
				Vector2f.Zero, Position, 1f);
	}

	public final void BallFallEffect(Vector2f Position, int colorIndex,
			float rotation) {
		this.simpleSystem.AddTextureParticle(new RectBox(50 * colorIndex, 0,
				50, 50), 1f, 0f, new Vector2f(25f), 1f, 0f, rotation, 0f,
				false, true, false, new Vector2f(
						((float) MathUtils.random()) - 0.5f, -150f
								- ((MathUtils.random()) * 400f)), Position, 1f);
	}
}