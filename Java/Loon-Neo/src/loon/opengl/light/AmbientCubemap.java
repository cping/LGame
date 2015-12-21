package loon.opengl.light;

import loon.canvas.LColor;
import loon.geom.Vector3f;
import loon.utils.MathUtils;

public class AmbientCubemap {

	private final static int limit = 3 * 6;

	public final float data[];

	public AmbientCubemap() {
		data = new float[limit];
	}

	public AmbientCubemap(final float copyFrom[]) {
		if (copyFrom.length != limit) {
			throw new RuntimeException("Incorrect array size");
		}
		data = new float[copyFrom.length];
		System.arraycopy(copyFrom, 0, data, 0, data.length);
	}

	public AmbientCubemap(final AmbientCubemap copyFrom) {
		this(copyFrom.data);
	}

	public AmbientCubemap set(final float values[]) {
		for (int i = 0; i < data.length; i++){
			data[i] = values[i];
		}
		return this;
	}

	public AmbientCubemap set(final AmbientCubemap other) {
		return set(other.data);
	}

	public AmbientCubemap set(final LColor color) {
		return set(color.r, color.g, color.b);
	}

	public AmbientCubemap set(float r, float g, float b) {
		for (int idx = 0; idx < data.length;) {
			data[idx++] = r;
			data[idx++] = g;
			data[idx++] = b;
		}
		return this;
	}

	public LColor getColor(final LColor out, int side) {
		side *= 3;
		return out.setColor(data[side], data[side + 1], data[side + 2], 1f);
	}

	public AmbientCubemap clear() {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0f;
		}
		return this;
	}

	public AmbientCubemap clamp() {
		for (int i = 0; i < data.length; i++) {
			data[i] = MathUtils.clamp(data[i]);
		}
		return this;
	}

	public AmbientCubemap add(float r, float g, float b) {
		for (int idx = 0; idx < data.length;) {
			data[idx++] += r;
			data[idx++] += g;
			data[idx++] += b;
		}
		return this;
	}

	public AmbientCubemap add(final LColor color) {
		return add(color.r, color.g, color.b);
	}

	public AmbientCubemap add(final float r, final float g, final float b,
			final float x, final float y, final float z) {
		final float x2 = x * x, y2 = y * y, z2 = z * z;
		float d = x2 + y2 + z2;
		if (d == 0f){
			return this;
		}
		d = 1f / d * (d + 1f);
		final float rd = r * d, gd = g * d, bd = b * d;
		int idx = x > 0 ? 0 : 3;
		data[idx] += x2 * rd;
		data[idx + 1] += x2 * gd;
		data[idx + 2] += x2 * bd;
		idx = y > 0 ? 6 : 9;
		data[idx] += y2 * rd;
		data[idx + 1] += y2 * gd;
		data[idx + 2] += y2 * bd;
		idx = z > 0 ? 12 : 15;
		data[idx] += z2 * rd;
		data[idx + 1] += z2 * gd;
		data[idx + 2] += z2 * bd;
		return this;
	}

	public AmbientCubemap add(final LColor color, final Vector3f direction) {
		return add(color.r, color.g, color.b, direction.x, direction.y,
				direction.z);
	}

	public AmbientCubemap add(final float r, final float g, final float b,
			final Vector3f direction) {
		return add(r, g, b, direction.x, direction.y, direction.z);
	}

	public AmbientCubemap add(final LColor color, final float x, final float y,
			final float z) {
		return add(color.r, color.g, color.b, x, y, z);
	}

	public AmbientCubemap add(final LColor color, final Vector3f point,
			final Vector3f target) {
		return add(color.r, color.g, color.b, target.x - point.x, target.y
				- point.y, target.z - point.z);
	}

	public AmbientCubemap add(final LColor color, final Vector3f point,
			final Vector3f target, final float intensity) {
		final float t = intensity / (1f + target.dst(point));
		return add(color.r * t, color.g * t, color.b * t, target.x - point.x,
				target.y - point.y, target.z - point.z);
	}
}
