package loon.action.sprite;

import loon.opengl.GLEx;

public class Scene extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long secondsElapsedTotal;

	protected Scene _parentScene;
	protected Scene _childScene;
	private boolean _childSceneModalDraw;
	private boolean _childSceneModalUpdate;

	private boolean mBackgroundEnabled = true;

	public Scene() {

	}

	public Scene(final int count) {
		for (int i = 0; i < count; i++) {
			this.attachChild(new Entity());
		}
	}

	public float getSecondsElapsedTotal() {
		return this.secondsElapsedTotal;
	}

	private void setParentScene(final Scene pParentScene) {
		this._parentScene = pParentScene;
	}

	@Override
	public void reset() {
		super.reset();
		this.clearChildScene();
	}

	protected void onManagedPaint(final GLEx gl) {
		final Scene childScene = this._childScene;
		if (childScene == null || !this._childSceneModalDraw) {
			if (this.mBackgroundEnabled) {
				super.onManagedPaint(gl);
			}
		}
		if (childScene != null) {
			childScene.createUI(gl);
		}
	}

	public void back() {
		this.clearChildScene();
		if (this._parentScene != null) {
			this._parentScene.clearChildScene();
			this._parentScene = null;
		}
	}

	public boolean hasChildScene() {
		return this._childScene != null;
	}

	public Scene getChildScene() {
		return this._childScene;
	}

	public void setChildSceneModal(final Scene child) {
		this.setChildScene(child, true, true);
	}

	public void setChildScene(final Scene child) {
		this.setChildScene(child, false, false);
	}

	public void setChildScene(final Scene child, final boolean modalDraw,
			final boolean modalUpdate) {
		child.setParentScene(this);
		this._childScene = child;
		this._childSceneModalDraw = modalDraw;
		this._childSceneModalUpdate = modalUpdate;
	}

	public void clearChildScene() {
		this._childScene = null;
	}

	protected void onManagedUpdate(final long elapsedTime) {
		this.secondsElapsedTotal += elapsedTime;
		final Scene childScene = this._childScene;
		if (childScene == null || !this._childSceneModalUpdate) {
			super.onManagedUpdate(elapsedTime);
		}
		if (childScene != null) {
			childScene.update(elapsedTime);
		}
	}

}
