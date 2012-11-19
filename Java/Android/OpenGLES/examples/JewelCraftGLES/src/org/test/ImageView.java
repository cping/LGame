package org.test;

import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class ImageView
{
	public float alpha;
	public RectBox frame;
	public LTexture image;
	public boolean isFramed;
	public Vector2f position;
	public LTexture selectedImage;
	public int state;

	public ImageView(RectBox rectangle)
	{
		this.frame = new RectBox(rectangle);
		this.position = new Vector2f( rectangle.x,rectangle.y);
		this.alpha = 1f;
		this.isFramed = true;
	}

	public ImageView(Vector2f position)
	{
		this.position = position;
		this.alpha = 1f;
		this.isFramed = false;
	}
}