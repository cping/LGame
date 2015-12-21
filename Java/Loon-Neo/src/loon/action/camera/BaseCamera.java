package loon.action.camera;

import loon.geom.Matrix4;

public abstract class BaseCamera {
	
	public static BaseCamera DEF = new EmptyCamera();

	public void setup() {
		DEF = this;
	}

	public abstract Matrix4 getView();

    public abstract Matrix4 getProjection();
    
    public abstract Matrix4 getCombine();
}
