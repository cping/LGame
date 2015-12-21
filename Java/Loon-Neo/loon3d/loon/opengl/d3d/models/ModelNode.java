package loon.opengl.d3d.models;

import loon.geom.Quaternion;
import loon.geom.Vector3f;

public class ModelNode {
	public String id;
	public int boneId = -1;
	public Vector3f translation;
	public Quaternion rotation;
	public Vector3f scale;
	public String meshId;
	public ModelNodePart[] parts;
	public ModelNode[] children;
}
