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
package loon.physics;

import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.World;

public class PhysicsFixedWorld extends PhysicsWorld {

	private final float pTimeStep;

	private final int pMaximumStepsPerUpdate;

	private float pSecondsElapsedAccumulator;

	public PhysicsFixedWorld(final int s, final Vector2f g, final boolean a) {
		this(s, Integer.MAX_VALUE, g, a);
	}

	public PhysicsFixedWorld(final int s, final int m, final Vector2f g,
			final boolean a) {
		super(g, a);
		this.pTimeStep = 1.0f / s;
		this.pMaximumStepsPerUpdate = m;
	}

	public PhysicsFixedWorld(final int s, final Vector2f g, final boolean a,
			final int v, final int p) {
		this(s, Integer.MAX_VALUE, g, a, v, p);
	}

	public PhysicsFixedWorld(final int s, final int m, final Vector2f g,
			final boolean a, final int v, final int p) {
		super(g, a, v, p);
		this.pTimeStep = 1.0f / s;
		this.pMaximumStepsPerUpdate = m;
	}

	public void update(final float secondsElapsed) {

		this.pSecondsElapsedAccumulator += secondsElapsed;

		final int velocityIterations = this.pVelocityIterations;
		final int positionIterations = this.pPositionIterations;

		final World world = this.globalWorld;
		final float stepLength = this.pTimeStep;

		int stepsAllowed = this.pMaximumStepsPerUpdate;

		while (this.pSecondsElapsedAccumulator >= stepLength
				&& stepsAllowed > 0) {
			world.step(stepLength, velocityIterations, positionIterations);
			this.pSecondsElapsedAccumulator -= stepLength;
			stepsAllowed--;
		}
		this.pPhysicsConnectorManager.update(secondsElapsed);
	}

}
