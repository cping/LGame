package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class Weapon extends DrawableObject
{
	public float accuracy;
	public float currentAccuracy;
	public int currentMagSize;
	public int currentReloadLength;

	public int framesPerFire;
	public int iDelay;
	public int iFire;
	public int iReload;
	public boolean isFirable;
	public boolean isReloading;
	public boolean isTriggerPulled;
	public int magSize;
	public String name;
	public int numBullet;
	public int power;
	public int price;
	public int reloadLength;
	public SoundEffect firingSound;

	public Weapon(LTexture weaponTexture,SoundEffect eff, Vector2f position, String name, int magSize, int reloadLength, int framesPerFire, int power, float accuracy, int price)
	{
		super(weaponTexture, position);
		super.texture = weaponTexture;
		super.position = position.cpy();
		this.firingSound = eff;
		this.name = name;
		this.magSize = magSize;
		this.reloadLength = reloadLength;
		this.framesPerFire = framesPerFire;
		this.power = power;
		this.accuracy = accuracy;
		this.price = price;
		this.iReload = 0;
		this.iFire = 0;
		this.isReloading = false;
		this.isFirable = true;
		this.isTriggerPulled = false;
		this.numBullet = magSize;
		this.currentReloadLength = reloadLength;
		this.currentMagSize = magSize;
		this.currentAccuracy = accuracy;
		this.iDelay =  (int) (ScreenGameplay.rand.NextDouble() * 5.0);
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}

	public final void Fire(float angle)
	{
		this.isFirable = false;
		this.iFire = 1;
	}

	public final void Reload()
	{
		this.iReload++;
		this.numBullet = (int)((this.currentMagSize * this.iReload) / ((float) this.currentReloadLength));
		if (this.iReload > this.currentReloadLength)
		{
			this.isReloading = false;
			this.iReload = 0;
			this.numBullet = this.currentMagSize;
		}
	}

	@Override
	public void Update()
	{
		super.Update();
		if (!this.isFirable)
		{
			this.iFire++;
		}
		if (((this.iFire >= this.framesPerFire) && !this.isReloading) && (this.iDelay == 0))
		{
			this.isFirable = true;
		}
		if (this.numBullet == 0)
		{
			this.isReloading = true;
			this.iDelay =  (int) (ScreenGameplay.rand.NextDouble() * 5.0);
		}
		if (this.isReloading)
		{
			this.Reload();
		}
		else if (this.iDelay != 0)
		{
			this.iDelay--;
		}
	}
}