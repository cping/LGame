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

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Shape;
import loon.utils.TArray;

/**
 * Shader光源(真实光源)用类
 */
public class Lights implements LRelease {

	public final LColor ambientLight = new LColor(0f, 0f, 0f);

	private boolean _active;

	private boolean _visibleLights;

	public final LColor fog = new LColor(0f, 0f, 0f);

	protected final TArray<BaseLight> lights = new TArray<BaseLight>();

	public Lights() {
		this(LColor.white);
	}

	public Lights(final LColor ambient) {
		ambientLight.setColor(ambient);
	}

	public Lights(final float ambientRed, final float ambientGreen, final float ambientBlue) {
		ambientLight.setColor(ambientRed, ambientGreen, ambientBlue, 1f);
	}

	public Lights(final LColor ambient, final BaseLight... lights) {
		this(ambient);
		add(lights);
	}

	public Lights setAmbientColor(float r, float g, float b) {
		this.ambientLight.setColor(r, g, b);
		return this;
	}

	public Lights setAmbientColor(LColor color) {
		this.ambientLight.setColor(color);
		return this;
	}

	public int getLightCount() {
		return this.lights.size;
	}

	public Lights clear() {
		ambientLight.setColor(0f, 0f, 0f);
		lights.clear();
		return this;
	}

	public Lights add(final BaseLight... lights) {
		for (final BaseLight light : lights) {
			add(light);
		}
		return this;
	}

	public Lights add(final TArray<BaseLight> lights) {
		for (final BaseLight light : lights) {
			add(light);
		}
		return this;
	}

	public Lights add(BaseLight light) {
		lights.add(light);
		return this;
	}

	public Lights addLight(LColor color, float x, float y, float radius) {
		return addLight(color, x, y, 0f, radius, 1f, 1f);
	}

	public Lights addLight(LColor color, float x, float y, float radius, float intensity) {
		return addLight(color, x, y, 0f, radius, 1f, 1f);
	}

	public Lights addLight(LColor color, float x, float y, float radius, float intensity, float attenuation) {
		return addLight(color, x, y, 0f, radius, intensity, attenuation);
	}

	public Lights addLight(LColor color, float x, float y, float z, float radius, float intensity, float attenuation) {
		lights.add(new PointLight(color, x, y, z, radius, intensity, attenuation));
		return this;
	}

	public Lights removeLight(BaseLight light) {
		lights.remove(light);
		return this;
	}
	
	public TArray<PointLight> getPointLights(){
		TArray<PointLight> lightList = new TArray<PointLight>();
		for (BaseLight light : lights) {
			if (light instanceof PointLight) {
				lightList.add((PointLight)light);
			}
		}
		return lightList;
	}

	public TArray<DirectionalLight> getDirectionalLights(){
		TArray<DirectionalLight> lightList = new TArray<DirectionalLight>();
		for (BaseLight light : lights) {
			if (light instanceof DirectionalLight) {
				lightList.add((DirectionalLight)light);
			}
		}
		return lightList;
	}
	
	public TArray<BaseLight> getLights() {
		return getLights(LSystem.viewSize.getRect());
	}

	public TArray<BaseLight> getLights(Shape shape) {
		if (shape == null) {
			return lights.cpy();
		}
		TArray<BaseLight> lightList = new TArray<BaseLight>();
		for (BaseLight light : lights) {
			if (shape.contains(light.position)) {
				lightList.add(light);
			}
		}
		return lightList;
	}

	public Lights enable() {
		this._active = true;
		return this;
	}

	public Lights disable() {
		this._active = false;
		return this;
	}

	public boolean isActive() {
		return _active;
	}

	public Lights setActive(boolean a) {
		this._active = a;
		return this;
	}

	public boolean isVisibleLights() {
		return _visibleLights;
	}

	public Lights setVisibleLights(boolean v) {
		this._visibleLights = v;
		return this;
	}

	public LColor getFog() {
		return fog;
	}

	public Lights setFog(LColor fog) {
		this.fog.setColor(fog);
		return this;
	}

	@Override
	public void close() {
		clear();
		setActive(false);
	}

}
