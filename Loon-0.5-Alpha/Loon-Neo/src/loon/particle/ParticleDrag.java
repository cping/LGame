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

import loon.particle.ParticleBuffer.Effector;

public class ParticleDrag extends Effector {

	protected final float _dragX, _dragY;

	public ParticleDrag(float drag) {
		this(drag, drag);
	}

	public ParticleDrag(float dragX, float dragY) {
		_dragX = dragX;
		_dragY = dragY;
	}

	@Override
	public void apply(int index, float[] data, int start, float now, float dt) {
		data[start + ParticleBuffer.VEL_X] *= _dragX;
		data[start + ParticleBuffer.VEL_Y] *= _dragY;
	}

}
