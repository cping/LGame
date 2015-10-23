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

public class ParticleGravity extends Effector
{

    protected final float accel;
    
    public static final float EARTH_G = 9.81f;

    public ParticleGravity () {
        this(EARTH_G);
    }

    public ParticleGravity (float a) {
    	accel = a;
    }

    @Override public void apply (int index, float[] data, int start, float now, float dt) {
        data[start + ParticleBuffer.VEL_Y] += accel * dt;
    }

}
