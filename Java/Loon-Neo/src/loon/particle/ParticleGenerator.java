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

public abstract class ParticleGenerator {
	
	public static ParticleGenerator DEF = new ParticleGenerator() {
		@Override
		public boolean generate(ParticleEmitter emitter, float now, float dt) {
			return false;
		}
	};

	public static ParticleGenerator undulate(final int particles) {
		return new ParticleGenerator() {
			@Override
			public boolean generate(ParticleEmitter emitter, float now, float dt) {
				emitter.addParticles(particles);
				return true;
			}
		};
	}

	public static ParticleGenerator constant(final float particlesPerSecond) {

		return new ParticleGenerator() {
			protected final float secondsPerParticle = 1 / particlesPerSecond;
			protected float accum;

			@Override
			public boolean generate(ParticleEmitter emitter, float now, float dt) {
				accum += dt;
				int particles = (int) (accum / secondsPerParticle);
				accum -= particles * secondsPerParticle;
				emitter.addParticles(particles);
				return false;
			}

		};
	}

	public abstract boolean generate(ParticleEmitter emitter, float now, float dt);
}
