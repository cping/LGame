package loon.action.camera;

import loon.LSystem;
import loon.geom.Matrix4;

public class EmptyCamera extends BaseCamera {

	protected Matrix4 _viewMatrix4;

	public EmptyCamera() {
		_viewMatrix4 = new Matrix4();
		_viewMatrix4.setToOrtho2D(0, 0, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public EmptyCamera(Matrix4 v) {
		_viewMatrix4 = v;
	}

	@Override
	public Matrix4 getView() {
		return _viewMatrix4;
	}
}
