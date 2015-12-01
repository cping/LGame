package loon.opengl.d3d;

import loon.utils.TArray;


public abstract class BaseShaderProvider implements ShaderProvider {
	protected TArray<Shader> shaders = new TArray<Shader>();
	
	@Override
	public Shader getShader (Renderable renderable) {
		Shader suggestedShader = renderable.shader;
		if (suggestedShader != null && suggestedShader.canRender(renderable))
			return suggestedShader;
		for (Shader shader : shaders) {
			if (shader.canRender(renderable))
				return shader;
		}
		final Shader shader = createShader(renderable);
		shader.init();
		shaders.add(shader);
		return shader;
	}
	
	protected abstract Shader createShader(final Renderable renderable);

	@Override
	public void close () {
		for(Shader shader: shaders) {
			shader.close();
		}
	}
}