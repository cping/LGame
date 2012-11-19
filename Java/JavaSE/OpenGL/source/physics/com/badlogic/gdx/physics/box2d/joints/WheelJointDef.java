
package com.badlogic.gdx.physics.box2d.joints;

import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

/** Wheel joint definition. This requires defining a line of motion using an axis and an anchor point. The definition uses local
 * anchor points and a local axis so that the initial configuration can violate the constraint slightly. The joint translation is
 * zero when the local anchor points coincide in world space. Using local anchors and a local axis helps when saving and loading a
 * game. */
public class WheelJointDef extends JointDef {
	public WheelJointDef () {
		type = JointType.WheelJoint;
	}

	public void initialize (Body bodyA, Body bodyB, Vector2f anchor, Vector2f axis) {
		this.bodyA = bodyA;
		this.bodyB = bodyB;
		localAnchorA.set(bodyA.getLocalPoint(anchor));
		localAnchorB.set(bodyB.getLocalPoint(anchor));
		localAxisA.set(bodyA.getLocalVector(axis));
	}

	/** The local anchor point relative to body1's origin. **/
	public final Vector2f localAnchorA = new Vector2f();

	/** The local anchor point relative to body2's origin. **/
	public final Vector2f localAnchorB = new Vector2f();

	/** The local translation axis in body1. **/
	public final Vector2f localAxisA = new Vector2f(1, 0);

	/** Enable/disable the joint motor. **/
	public boolean enableMotor = false;

	/** The maximum motor torque, usually in N-m. */
	public float maxMotorTorque = 0;

	/** The desired motor speed in radians per second. */
	public float motorSpeed = 0;

	/** Suspension frequency, zero indicates no suspension */
	public float frequencyHz = 2;

	/** Suspension damping ratio, one indicates critical damping */
	public float dampingRatio = 0.7f;
}
