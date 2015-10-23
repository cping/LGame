package loon.action.sprite.effect;

import loon.LRelease;
import loon.LTexture;
import loon.opengl.GLEx;

public interface IKernel extends LRelease {

	public int id();

	public void draw(GLEx g);

	public void update();

	public LTexture get();

	public float getHeight();

	public float getWidth();

}
