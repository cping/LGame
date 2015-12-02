package loon.opengl.d3d;

import loon.action.camera.BaseCamera;
import loon.utils.TArray;

public interface RenderableSorter {

	public void sort(BaseCamera camera, TArray<Renderable> renderables);
	
}
