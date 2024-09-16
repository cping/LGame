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
package loon.opengl.light;

import loon.canvas.LColor;
import loon.geom.Vector3f;

public class PointLight extends BaseLight {

	public PointLight() {

	}

	public PointLight(LColor color, float x, float y, float radius) {
		this(color, x, y, 0f, radius, 1f, 1f);
	}

	public PointLight(LColor color, float x, float y, float radius, float intensity) {
		this(color, x, y, 0f, radius, intensity, 1f);
	}

	public PointLight(LColor color, float x, float y, float radius, float intensity, float attenuation) {
		this(color, x, y, 0f, radius, intensity, attenuation);
	}

	public PointLight(LColor color, float x, float y, float z, float radius, float intensity, float attenuation) {
		this.set(color, x, y, z, radius, intensity, attenuation);
	}

	public PointLight set(final PointLight copyFrom) {
		return set(copyFrom.color, copyFrom.position, copyFrom.radius, copyFrom.intensity, copyFrom.attenuation);
	}

	public PointLight set(final LColor color, final Vector3f position, final float radius, final float intensity,
			final float attenuation) {
		if (color != null) {
			this.color.setColor(color);
		}
		if (position != null) {
			this.position.set(position);
		}
		this.radius = radius;
		this.intensity = intensity;
		this.attenuation = attenuation;
		return this;
	}

	public PointLight set(final float r, final float g, final float b, final Vector3f position, final float radius,
			final float intensity, final float attenuation) {
		this.color.setColor(r, g, b);
		if (position != null) {
			this.position.set(position);
		}
		this.radius = radius;
		this.intensity = intensity;
		this.attenuation = attenuation;
		return this;
	}

	public PointLight set(final LColor color, final float x, final float y, final float z, final float radius,
			final float intensity, final float attenuation) {
		if (color != null) {
			this.color.setColor(color);
		}
		if (position != null) {
			this.position.set(x, y, z);
		}
		this.radius = radius;
		this.intensity = intensity;
		this.attenuation = attenuation;
		return this;
	}

	public PointLight set(final float r, final float g, final float b, final float x, final float y, final float z,
			final float radius, final float intensity, final float attenuation) {
		if (color != null) {
			this.color.setColor(color);
		}
		if (position != null) {
			this.position.set(x, y, z);
		}
		this.radius = radius;
		this.intensity = intensity;
		this.attenuation = attenuation;
		return this;
	}

	public PointLight set(final float r, final float g, final float b, final float x, final float y, final float z,
			final float intensity) {
		if (color != null) {
			this.color.setColor(r, g, b);
		}
		if (position != null) {
			this.position.set(x, y, z);
		}
		this.intensity = intensity;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PointLight) ? equals((PointLight) obj) : false;
	}

	public boolean equals(PointLight other) {
		return (other != null && (other == this || (color.equals(other.color) && position.equals(other.position)
				&& intensity == other.intensity && radius == other.radius && attenuation == other.attenuation)));
	}
}
