package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.timer.GameTime;

public class ParticleEngine
{
	protected java.util.ArrayList<ParticleSystem> particleSystems = new java.util.ArrayList<ParticleSystem>();

	public final void AddSystem(ParticleSystem particleSystem)
	{
		this.particleSystems.add(particleSystem);
	}

	public final void Draw(SpriteBatch spriteBatch)
	{
		for (int i = 0; i < this.particleSystems.size(); i++)
		{
			this.particleSystems.get(i).Draw(spriteBatch);
		}
	}

	public final void Reset()
	{
		for (int i = 0; i < this.particleSystems.size(); i++)
		{
			this.particleSystems.get(i).Reset();
		}
	}

	public final void Update(GameTime gameTime)
	{
		for (int i = 0; i < this.particleSystems.size(); i++)
		{
			this.particleSystems.get(i).Update(gameTime);
		}
	}
}