package loon.opengl.d3d;

import loon.opengl.d3d.shaders.DefaultShader;

public class DefaultShaderProvider extends BaseShaderProvider {
	public String vertexShader;
	public String fragmentShader;

	public DefaultShaderProvider(final String vertexShader,
			final String fragmentShader) {
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
	}

	public DefaultShaderProvider() {
		this(DefaultShader.getDefaultVertexShader(), DefaultShader
				.getDefaultFragmentShader());
	}

	@Override
	protected Shader createShader(final Renderable renderable) {
		return new DefaultShader(vertexShader, fragmentShader,
				renderable.material, renderable.mesh.getVertexAttributes(),
				renderable.lights != null, renderable.lights != null
						&& renderable.lights.fog != null, 2, 5, 3,
				renderable.bones == null ? 0 : 12);
	}
}
