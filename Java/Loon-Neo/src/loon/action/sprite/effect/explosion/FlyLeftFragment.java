/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.sprite.effect.explosion;

import loon.LTexture;
import loon.geom.RectI;
import loon.utils.MathUtils;

public class FlyLeftFragment extends Fragment {

	public FlyLeftFragment(int color, float x, float y, RectI bound,LTexture tex) {
		super(color, x, y, bound,tex);
	}

	@Override
	protected void caculate(float factor) {
		if (ox > parBound.centerX()) {
			cx = cx + factor * MathUtils.nextInt(parBound.width) * (MathUtils.random());
		} else {
			cx = cx - factor * MathUtils.nextInt(parBound.width) * (MathUtils.random());
		}
		if (factor <= 0.5f) {
			cy = cy - factor * MathUtils.nextInt(parBound.height / 2);
		} else {
			cy = cy + factor * MathUtils.nextInt(parBound.height / 2);
		}
		update(factor);
	}
}
