package loon.action.camera;

import loon.LSystem;
import loon.geom.Matrix4;

public class EmptyCamera extends BaseCamera {

	protected Matrix4 _projMatrix4;

	protected Matrix4 _viewMatrix4;

	public EmptyCamera() {
		this(LSystem.viewSize.getMatrix().cpy(), new Matrix4());
	}

	public EmptyCamera(Matrix4 p, Matrix4 v) {
		_projMatrix4 = p;
		_viewMatrix4 = v;
	}

	@Override
	public Matrix4 getView() {
		return _viewMatrix4;
	}

	@Override
	public Matrix4 getProjection() {
		return _projMatrix4;
	}

	@Override
	public Matrix4 getCombine() {
		return _projMatrix4.mul(_viewMatrix4);
	}

}
