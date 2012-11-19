package org.test;

public class CBulletType
{
	public CAnimObject anim;
	public boolean homing;
	public float rotationSpeed;
	public float speed;
	public float splashRadius;
	public CAnimObject tail;

	public CBulletType(CAnimObject anim, float speed, boolean homing, float rotationSpeed, CAnimObject tail, float splashRadius)
	{
		this.speed = speed;
		this.anim = anim;
		this.rotationSpeed = rotationSpeed;
		this.tail = tail;
		this.homing = homing;
		this.splashRadius = splashRadius;
	}
}