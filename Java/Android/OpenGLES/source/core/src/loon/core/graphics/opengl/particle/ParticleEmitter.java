/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.opengl.particle;

import loon.core.graphics.opengl.LTexture;

public interface ParticleEmitter {

	public void update(ParticleSystem system, long delta);

	public boolean completed();
	
	public void wrapUp();
	
	public void updateParticle(Particle particle, long delta);
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean useAdditive();
	
	public LTexture getImage();

	public boolean isOriented();
	
	public boolean usePoints(ParticleSystem system);
	
	public void resetState();
}
