package loon.opengl.d3d;

import loon.utils.TArray;
import loon.utils.cache.Pool;

public interface RenderableProvider {

	public void getRenderables(TArray<Renderable> renderables, Pool<Renderable> pool);
}
