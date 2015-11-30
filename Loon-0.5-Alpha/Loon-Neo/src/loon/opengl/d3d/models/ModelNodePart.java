package loon.opengl.d3d.models;

import loon.geom.Matrix4;
import loon.utils.ListMap;

public class ModelNodePart {
	
	public String materialId;
	public String meshPartId;
	public ListMap<String, Matrix4> bones;
	public int uvMapping[][];
	
}
