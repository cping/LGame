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
import loon.particle.ParticleBuffer.Effector;
import loon.utils.Easing;

public class ParticleAlpha {

	public static Effector byAge(Easing e) {
		return byAge(e, 1, 0);
	}

	public static Effector byAge(final Easing e, final float startAlpha,
			float endAlpha) {
		final float rangeAlpha = endAlpha - startAlpha;
		return new Effector() {
			@Override
			public void apply(int index, float[] data, int start, float now,
					float dt) {
				float alpha = e.apply(startAlpha, rangeAlpha, now
						- data[start + ParticleBuffer.BIRTH], data[start
						+ ParticleBuffer.LIFESPAN]);
				float red = LColor.decodeLower(data[start
						+ ParticleBuffer.ALPHA_RED]);
				data[start + ParticleBuffer.ALPHA_RED] = LColor.encode(alpha,
						red);
			}
		};
	}
}
