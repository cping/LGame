package loon.opengl.d3d;

import loon.LObject;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.PreBoxViewer3D;

public class Object3D extends LObject implements ISprite {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3332614785354460231L;

	private boolean visible;

	private boolean isPreview = false;

	//轮廓预览模式
	private PreBoxViewer3D preview;

	public Object3D(boolean pre) {
		preview = null;
		isPreview = pre;
		visible = true;
	}

	public void load(String filename) {
		if (isPreview) {
			preview = PreBoxViewer3D.load(filename);
			if (null != preview) {
				preview.calculateFaceNormals();
				preview.rotate(1.0f, 1.0f, 5f, 0f, 0f, 0f);
			}
		}
	}

	public void rotate(float x, float y, float z, float ax, float ay, float az) {
		if (isPreview) {
			if (null != preview) {
				preview.rotate(x, y, z, ax, ay, az);
			}
		}
	}

	public void draw(GLEx g) {
		if (isPreview) {
			if (null != preview) {
				preview.draw(g);
			}
		}
	}

	public void setAmbientLight(float x, float y, float z, float f) {
		if (isPreview) {
			if (null != preview) {
				preview.setAmbientLight(x, y, z, f);
			}
		}
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		draw(g);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public float getWidth() {
		return getContainerWidth();
	}

	@Override
	public float getHeight() {
		return getContainerHeight();
	}

	@Override
	public void close() {

	}

}
