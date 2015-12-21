package loon.particle;

import loon.geom.Vector2f;
import loon.particle.ParticleBuffer.Initializer;
import loon.utils.MathUtils;

public class ParticleVelocity {

	public static Initializer constant(final Vector2f velocity) {
		return new VelocityInitializer() {
			@Override
			protected void initVelocity(Vector2f vel) {
				vel.set(velocity);
			}
		};
	}

	public static Initializer randomSquare(float xRange, float yRange) {
		return randomSquare(-xRange / 2, xRange / 2, -yRange / 2, yRange / 2);
	}

	public static Initializer randomSquare(final float minX, final float maxX,
			final float minY, final float maxY) {
		return new VelocityInitializer() {
			@Override
			protected void initVelocity(Vector2f vel) {
				vel.set(MathUtils.random(minX, maxX),
						(MathUtils.random(minY, maxY)));
			}
		};
	}

	public static Initializer randomNormal(float mean, float dev) {
		return randomNormal(mean, dev, mean, dev);
	}

	public static Initializer randomNormal(final float xMean, final float xDev,
			final float yMean, final float yDev) {
		return new VelocityInitializer() {
			@Override
			protected void initVelocity(Vector2f vel) {
				vel.set(MathUtils.random(xMean, xDev),
						MathUtils.random(yMean, yDev));
			}
		};
	}

	public static Initializer randomCircle(float maximum) {
		return randomCircle(0, maximum);
	}

	public static Initializer randomCircle(final float min, final float max) {
		return new VelocityInitializer() {
			@Override
			protected void initVelocity(Vector2f vel) {
				float angle = MathUtils.random(MathUtils.TWO_PI);
				float magnitude = min + MathUtils.random(max - min);
				vel.set(MathUtils.sin(angle) * magnitude, MathUtils.cos(angle)
						* magnitude);
			}
		};
	}

	public static Initializer increment(final float dx, final float dy) {
		return new Initializer() {
			@Override
			public void init(int index, float[] data, int start) {
				data[start + ParticleBuffer.VEL_X] += dx;
				data[start + ParticleBuffer.VEL_Y] += dy;
			}
		};
	}

	protected static abstract class VelocityInitializer extends Initializer {
		protected final Vector2f vec = new Vector2f();

		@Override
		public void init(int index, float[] data, int start) {
			initVelocity(vec);
			data[start + ParticleBuffer.VEL_X] = vec.x;
			data[start + ParticleBuffer.VEL_Y] = vec.y;
		}

		protected abstract void initVelocity(Vector2f vel);

	}
}
