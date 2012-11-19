package org.test;

import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;


public class Label
{
	public int alignment;
	public float alpha;
	public LColor color;
	public LFont font;
	public RectBox frame;
	public boolean isFramed;
	public Vector2f position;
	public String text;

	public Label(RectBox rectangle)
	{
		this.frame = new RectBox(rectangle);
		this.position = new Vector2f(rectangle.x, rectangle.y);
		this.alpha = 1f;
		this.isFramed = true;
	}

	public Label(Vector2f position)
	{
		this.position = position;
		this.alpha = 1f;
	}

	public final Vector2f textPosition()
	{
		if (this.alignment == 0)
		{
			return this.position;
		}
		if (this.alignment == 1)
		{
			return new Vector2f(this.frame.x + ((this.frame.getWidth() - this.font.stringWidth(this.text)) / 2f), (float) this.frame.y);
		}
		if (this.alignment == 2)
		{
			return new Vector2f(this.frame.x + (this.frame.getWidth() - this.font.stringWidth(this.text)), (float) this.frame.y);
		}
		return new Vector2f(this.frame.x + ((this.frame.getWidth() - this.font.stringWidth(this.text)) / 2f), this.frame.y + ((this.frame.getHeight() - this.font.getHeight()) / 2f));
	}
}