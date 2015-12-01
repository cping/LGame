package loon.opengl.d3d;

import java.util.Comparator;

import loon.action.camera.BaseCamera;
import loon.geom.Vector3f;
import loon.opengl.d3d.materials.BlendingAttribute;
import loon.utils.TArray;

public class DefaultRenderableSorter implements RenderableSorter, Comparator<Renderable> {
	
	private BaseCamera camera;
	private final Vector3f tmpV1 = new Vector3f();
	private final Vector3f tmpV2 = new Vector3f();
	
	@Override
	public void sort (final BaseCamera camera, final TArray<Renderable> renderables) {
		this.camera = camera;
	//	renderables.sort(this);
	}
	
	@Override
	public int compare (final Renderable o1, final Renderable o2) {
		final boolean b1 = o1.material.has(BlendingAttribute.Type);
		final boolean b2 = o2.material.has(BlendingAttribute.Type);
		if (b1 != b2) {
			return b1 ? 1 : -1;
		}
		o1.worldTransform.getTranslation(tmpV1);
		o2.worldTransform.getTranslation(tmpV2);
	//final float dst = camera.position.dst2(tmpV1) - camera.position.dst2(tmpV2);
		//return dst < 0f ? -1 : (dst > 0f ? 1 : 0);
		return 0;
	}
}
