package loon.physics;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class PhysicsCollisionEvent {

	private Body bodyA;

	private Body bodyB;

	public PhysicsCollisionEvent(Body bodyA, Body bodyB) {
		this.bodyA = bodyA;
		this.bodyB = bodyB;
	}

	public Body getBodyA() {
		return this.bodyA;
	}

	public Body getBodyB() {
		return this.bodyB;
	}

	public boolean contains(Body current) {
		return (this.bodyA == current) || (this.bodyB == current);
	}

}
