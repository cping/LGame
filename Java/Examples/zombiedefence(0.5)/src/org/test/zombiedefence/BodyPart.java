package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class BodyPart extends DrawableObject
{
	public float accelY;
	private java.util.ArrayList<BloodSpill> bloodSpillList;
	public boolean isFlying;
	public boolean isVisable;
	public float speedAngle;
	public float speedX;
	public float speedY;

	public BodyPart(LTexture texture, Vector2f position)
	{
		super(texture, position);
		this.bloodSpillList = new java.util.ArrayList<BloodSpill>();
		this.isVisable = true;
		this.isFlying = false;
		this.speedAngle = 0.1047198f;
		this.speedX = (-(MathUtils.random()) * 2f) - 2f;
		this.speedY = -(MathUtils.random()) * 2f;
		this.accelY = 0.1f;
		super.life = 60;
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		if (this.isVisable)
		{
			for (BloodSpill spill : this.bloodSpillList)
			{
				spill.Draw(batch);
			}
			super.Draw(batch);
		}
	}

	public final void Fly()
	{
		super.angle += this.speedAngle;
		this.position.x += this.speedX;
		this.position.y += this.speedY;
		this.speedY += this.accelY;
	}

	@Override
	public void Update()
	{
		if (this.isFlying)
		{
			this.Fly();
			this.bloodSpillList.add(new BloodSpill(ScreenGameplay.t2DBloodSpill, super.position, 0f));
		}
		for (int i = 0; i < this.bloodSpillList.size(); i++)
		{
			this.bloodSpillList.get(i).Update();
			if (this.bloodSpillList.get(i).isDead)
			{
				this.bloodSpillList.remove(i);
			}
		}
	}
}