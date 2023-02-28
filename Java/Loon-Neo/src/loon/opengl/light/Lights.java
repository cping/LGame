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

import loon.LSysException;
import loon.canvas.LColor;
import loon.utils.TArray;

public class Lights {

	public final LColor ambientLight = new LColor(0f, 0f, 0f, 1f);

	public LColor fog;

	public final TArray<DirectionalLight> directionalLights = new TArray<DirectionalLight>();

	public final TArray<PointLight> pointLights = new TArray<PointLight>();

	public Lights() {
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

	public Lights clear() {
		ambientLight.setColor(0f, 0f, 0f, 1f);
		directionalLights.clear();
		pointLights.clear();
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
		if (light instanceof DirectionalLight) {
			directionalLights.add((DirectionalLight) light);
		} else if (light instanceof PointLight) {
			pointLights.add((PointLight) light);
		} else {
			throw new LSysException("Unknown light type");
		}
		return this;
	}
}
