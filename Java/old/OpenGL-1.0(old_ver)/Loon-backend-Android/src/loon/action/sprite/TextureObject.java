package loon.action.sprite;

import loon.core.graphics.opengl.LTexture;

public class TextureObject extends SimpleObject{

	public TextureObject(float x, float y,
			LTexture tex2d) {
		super(x, y, tex2d.getWidth(), tex2d.getHeight(), Animation.getDefaultAnimation(tex2d), null);
	}
	
	public TextureObject(float x, float y,
			String file) {
		super(x, y, Animation.getDefaultAnimation(file), null);
	}
	
}
