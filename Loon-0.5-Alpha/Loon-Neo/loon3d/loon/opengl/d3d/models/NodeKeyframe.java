package loon.opengl.d3d.models;

import loon.geom.Quaternion;
import loon.geom.Vector3f;

public class NodeKeyframe {

	public float keytime;

	public final Vector3f translation = new Vector3f();

	public final Vector3f scale = new Vector3f(1f,1f,1f);

	public final Quaternion rotation = new Quaternion();
}
