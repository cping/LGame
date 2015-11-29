package loon.opengl.d3d;

import loon.geom.Vector3f;

public class Face {

	public Vector3f vertexIndex;
	public Vector3f normalIndex;
	public Vector3f texcoordIndex;

	public Face() {
		vertexIndex = new Vector3f();
		normalIndex = new Vector3f();
		texcoordIndex = new Vector3f();
	}

	void setVertices(int v1, int v2, int v3) {
		this.vertexIndex.set(v1, v2, v3);
	}

	void setTextureVertices(int t1, int t2, int t3) {
		this.texcoordIndex.set(t1, t2, t3);
	}

	void setNormalVertices(int n1, int n2, int n3) {
		this.normalIndex.set(n1, n2, n3);
	}
}
