package loon.opengl.d3d.models;

import loon.geom.Matrix4;
import loon.opengl.d3d.Renderable;
import loon.opengl.d3d.materials.Material;
import loon.utils.ListMap;

public class NodePart {
	
	public MeshPart meshPart;
	public Material material;
	public ListMap<Node, Matrix4> invBoneBindTransforms;
	public Matrix4[] bones;
	
	public NodePart() {}
	
	public NodePart(final MeshPart meshPart, final Material material) {
		this.meshPart = meshPart;
		this.material = material;
	}
	
	public Renderable setRenderable(final Renderable out) {
		out.material = material;
		out.mesh = meshPart.mesh;
		out.meshPartOffset = meshPart.indexOffset;
		out.meshPartSize = meshPart.numVertices;
		out.primitiveType = meshPart.primitiveType;
		out.bones = bones;
		return out;
	}
}
