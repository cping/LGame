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

import java.util.ArrayList;

import loon.core.geom.Shape;


import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsShellManager extends ArrayList<PhysicsShell> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7935419684080058194L;

	PhysicsShellManager() {

	}

	public void update(final float secondsElapsed) {
		final ArrayList<PhysicsShell> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			physicsConnectors.get(i).update(secondsElapsed);
		}
	}

	public void reset() {
		final ArrayList<PhysicsShell> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			physicsConnectors.get(i).reset();
		}
	}

	public Body findBodyByShape(final Shape s) {
		final ArrayList<PhysicsShell> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			final PhysicsShell physicsConnector = physicsConnectors.get(i);
			if (physicsConnector.shape == s) {
				return physicsConnector.body;
			}
		}
		return null;
	}

	public PhysicsShell findPhysicsConnectorByShape(final Shape s) {
		final ArrayList<PhysicsShell> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			final PhysicsShell physicsConnector = physicsConnectors.get(i);
			if (physicsConnector.shape == s) {
				return physicsConnector;
			}
		}
		return null;
	}

}
