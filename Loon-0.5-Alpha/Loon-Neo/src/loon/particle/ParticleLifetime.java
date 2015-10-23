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

import loon.particle.ParticleBuffer.Initializer;
import loon.utils.MathUtils;

public class ParticleLifetime {
	
    public static Initializer constant (final float lifespan) {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start+ParticleBuffer.LIFESPAN] = lifespan;
            }
        };
    }

    public static Initializer random (final float min, final float max) {
        return new Initializer() {
            @Override public void init (int index, float[] data, int start) {
                data[start+ParticleBuffer.LIFESPAN] = MathUtils.random(min, max);
            }
        };
    }
}
