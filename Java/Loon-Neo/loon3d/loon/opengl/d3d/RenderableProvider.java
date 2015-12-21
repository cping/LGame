package loon.opengl.d3d;

import loon.utils.Pool;
import loon.utils.TArray;

public interface RenderableProvider {

	public void getRenderables(TArray<Renderable> renderables, Pool<Renderable> pool);
}
