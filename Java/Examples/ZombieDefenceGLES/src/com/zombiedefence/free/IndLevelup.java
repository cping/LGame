package com.zombiedefence.free;

import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;


public class IndLevelup extends DrawableObject
{
	private int i;
	private int period;

	public IndLevelup(LTexture texture, Vector2f position)
	{
		super(texture, position);
		this.period = 60;
	}

	@Override
	public void Update()
	{
		super.Update();
		if (Help.AvailSkillPoint > 0)
		{
			if (this.i > 60)
			{
				this.i = 0;
			}
			super.alpha = 0.7f + (0.3f * ((float) Math.sin((double)(((((float) this.i) / ((float) this.period)) * 3.141593f) * 2f))));
			this.i++;
		}
		else
		{
			super.alpha = 0.1f;
		}
	}
}