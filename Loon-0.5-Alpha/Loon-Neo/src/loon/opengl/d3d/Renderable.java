package loon.opengl.d3d;

import loon.geom.Matrix4;
import loon.opengl.Mesh;
import loon.opengl.d3d.materials.Material;
import loon.opengl.light.Lights;

public class Renderable {

	public final Matrix4 worldTransform = new Matrix4();

	public Mesh mesh;

	public int meshPartOffset;

	public int meshPartSize;

	public int primitiveType;

	public Material material;

	public Matrix4 bones[];

	public Lights lights;

	public Object userData;
}
