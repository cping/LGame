package loon.opengl.d3d;

import loon.LRelease;

public interface ShaderProvider extends LRelease{

	Shader getShader(Renderable renderable);

	public void close();
}