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

import loon.canvas.LColor;
import loon.particle.ParticleBuffer.Initializer;

public class ParticleColor {

	public static Initializer constant(int argb) {
		return constant(((argb >> 16) & 0xFF) / 255f,
				((argb >> 8) & 0xFF) / 255f, ((argb >> 0) & 0xFF) / 255f,
				((argb >> 24) & 0xFF) / 255f);
	}

	public static Initializer constant(final float r, final float g,
			final float b, final float a) {
		return new Initializer() {
			@Override
			public void init(int index, float[] data, int start) {
				data[start + ParticleBuffer.ALPHA_RED] = LColor.encode(a, r);
				data[start + ParticleBuffer.GREEN_BLUE] = LColor.encode(g, b);
			}
		};
	}
}
