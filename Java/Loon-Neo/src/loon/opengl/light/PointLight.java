package loon.opengl.light;

import loon.canvas.LColor;
import loon.geom.Vector3f;

public class PointLight extends BaseLight {
	public final Vector3f position = new Vector3f();
	public float intensity;

	public PointLight set(final PointLight copyFrom) {
		return set(copyFrom.color, copyFrom.position, copyFrom.intensity);
	}

	public PointLight set(final LColor color, final Vector3f position,
			final float intensity) {
		if (color != null) {
			this.color.setColor(color);
		}
		if (position != null) {
			this.position.set(position);
		}
		this.intensity = intensity;
		return this;
	}

	public PointLight set(final float r, final float g, final float b,
			final Vector3f position, final float intensity) {
		this.color.setColor(r, g, b, 1f);
		if (position != null){
			this.position.set(position);
		}
		this.intensity = intensity;
		return this;
	}

	public PointLight set(final LColor color, final float x, final float y,
			final float z, final float intensity) {
		if (color != null){
			this.color.setColor(color);
		}
		this.position.set(x, y, z);
		this.intensity = intensity;
		return this;
	}

	public PointLight set(final float r, final float g, final float b,
			final float x, final float y, final float z, final float intensity) {
		this.color.setColor(r, g, b, 1f);
		this.position.set(x, y, z);
		this.intensity = intensity;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PointLight) ? equals((PointLight) obj) : false;
	}

	public boolean equals(PointLight other) {
		return (other != null && (other == this || (color.equals(other.color)
				&& position.equals(other.position) && intensity == other.intensity)));
	}
}
