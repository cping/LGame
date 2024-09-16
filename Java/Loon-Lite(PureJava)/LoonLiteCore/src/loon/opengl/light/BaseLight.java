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

public abstract class BaseLight {

	public final Vector3f position = new Vector3f();

	public final LColor color = new LColor(0f, 0f, 0f, 1f);

	public float radius;

	public float intensity;

	public float attenuation;

	public LColor getColor() {
		return color;
	}

	public float getRadius() {
		return radius;
	}

	public BaseLight setRadius(float radius) {
		this.radius = radius;
		return this;
	}

	public float getIntensity() {
		return intensity;
	}

	public BaseLight setIntensity(float intensity) {
		this.intensity = intensity;
		return this;
	}

	public float getAttenuation() {
		return attenuation;
	}

	public BaseLight setAttenuation(float attenuation) {
		this.attenuation = attenuation;
		return this;
	}

}