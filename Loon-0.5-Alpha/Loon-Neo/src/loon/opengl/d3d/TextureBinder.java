package loon.opengl.d3d;

import loon.opengl.d3d.materials.TextureDescriptor;

public interface TextureBinder {

	public void begin();
	
	public void end();
	
	public int bind(TextureDescriptor textureDescriptor);

	public int getBindCount();

	public int getReuseCount();

	public void resetCounts();
}
