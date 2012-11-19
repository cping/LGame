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

import java.util.Iterator;
import java.util.List;

import loon.core.geom.Vector2f;
import loon.core.timer.LTimerContext;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.DestructionListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsWorld {

	protected final PhysicsShellManager pPhysicsConnectorManager = new PhysicsShellManager();

	protected final World globalWorld;

	protected int pVelocityIterations = PhysicsScreen.VELOCITY_ITERATIONS_DEFAULT;

	protected int pPositionIterations = PhysicsScreen.POSITION_ITERATIONS_DEFAULT;

	public PhysicsWorld(final Vector2f g, final boolean a) {
		this(g, a, PhysicsScreen.VELOCITY_ITERATIONS_DEFAULT,
				PhysicsScreen.POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsWorld(final Vector2f g, final boolean a, final int v,
			final int p) {
		this.globalWorld = new World(g, a);
		this.pVelocityIterations = v;
		this.pPositionIterations = p;
	}

	public int getPositionIterations() {
		return this.pPositionIterations;
	}

	public void setPositionIterations(final int pPositionIterations) {
		this.pPositionIterations = pPositionIterations;
	}

	public int getVelocityIterations() {
		return this.pVelocityIterations;
	}

	public void setVelocityIterations(final int pVelocityIterations) {
		this.pVelocityIterations = pVelocityIterations;
	}

	public PhysicsShellManager getPhysicsConnectorManager() {
		return this.pPhysicsConnectorManager;
	}

	public void clearPhysicsConnectors() {
		this.pPhysicsConnectorManager.clear();
	}

	public void registerPhysicsConnector(final PhysicsShell pPhysicsConnector) {
		this.pPhysicsConnectorManager.add(pPhysicsConnector);
	}

	public void unregisterPhysicsConnector(final PhysicsShell pPhysicsConnector) {
		this.pPhysicsConnectorManager.remove(pPhysicsConnector);
	}

	public void update(final float secondsElapsed) {
		this.globalWorld.step(secondsElapsed, this.pVelocityIterations,
				this.pPositionIterations);
		this.pPhysicsConnectorManager.update(secondsElapsed);
	}

	public void update(LTimerContext timer) {
		this.update(timer.timeSinceLastUpdate / 10f);
	}

	public void reset() {
		this.pPhysicsConnectorManager.reset();
	}

	public void clearForces() {
		this.globalWorld.clearForces();
	}

	public Body createBody(final BodyDef pDef) {
		return this.globalWorld.createBody(pDef);
	}

	public Joint createJoint(final JointDef pDef) {
		return this.globalWorld.createJoint(pDef);
	}

	public void destroyBody(final Body pBody) {
		this.globalWorld.destroyBody(pBody);
	}

	public void destroyJoint(final Joint pJoint) {
		this.globalWorld.destroyJoint(pJoint);
	}

	public void dispose() {
		this.globalWorld.dispose();
	}

	public boolean getAutoClearForces() {
		return this.globalWorld.getAutoClearForces();
	}

	public Iterator<Body> getBodies() {
		return this.globalWorld.getBodies();
	}

	public int getBodyCount() {
		return this.globalWorld.getBodyCount();
	}

	public int getContactCount() {
		return this.globalWorld.getContactCount();
	}

	public List<Contact> getContactList() {
		return this.globalWorld.getContactList();
	}

	public Vector2f getGravity() {
		return this.globalWorld.getGravity();
	}

	public Iterator<Joint> getJoints() {
		return this.globalWorld.getJoints();
	}

	public int getJointCount() {
		return this.globalWorld.getJointCount();
	}

	public int getProxyCount() {
		return this.globalWorld.getProxyCount();
	}

	public boolean isLocked() {
		return this.globalWorld.isLocked();
	}

	public void QueryAABB(final QueryCallback pCallback, final float pLowerX,
			final float pLowerY, final float pUpperX, final float pUpperY) {
		this.globalWorld.QueryAABB(pCallback, pLowerX, pLowerY, pUpperX, pUpperY);
	}

	public void setAutoClearForces(final boolean pFlag) {
		this.globalWorld.setAutoClearForces(pFlag);
	}

	public void setContactFilter(final ContactFilter pFilter) {
		this.globalWorld.setContactFilter(pFilter);
	}

	public void setContactListener(final ContactListener pListener) {
		this.globalWorld.setContactListener(pListener);
	}

	public void setContinuousPhysics(final boolean pFlag) {
		this.globalWorld.setContinuousPhysics(pFlag);
	}

	public void setDestructionListener(final DestructionListener pListener) {
		this.globalWorld.setDestructionListener(pListener);
	}

	public void setGravity(final Vector2f pGravity) {
		this.globalWorld.setGravity(pGravity);
	}

	public void setWarmStarting(final boolean pFlag) {
		this.globalWorld.setWarmStarting(pFlag);
	}

	public void rayCast(final RayCastCallback pRayCastCallback,
			final Vector2f pPoint1, final Vector2f pPoint2) {
		this.globalWorld.rayCast(pRayCastCallback, pPoint1, pPoint2);
	}
}
