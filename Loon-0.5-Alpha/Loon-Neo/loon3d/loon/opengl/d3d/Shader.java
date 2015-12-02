package loon.opengl.d3d;

import loon.LRelease;
import loon.action.camera.BaseCamera;

public interface Shader extends LRelease {

	void init();

	int compareTo(Shader other);

	boolean canRender(Renderable instance);

	void begin(BaseCamera camera, RenderContext context);

	void render(final Renderable renderable);

	void end();
}
