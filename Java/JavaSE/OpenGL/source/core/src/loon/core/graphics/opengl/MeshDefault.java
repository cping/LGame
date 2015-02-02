package loon.core.graphics.opengl;

import java.util.HashMap;

import loon.core.graphics.opengl.GLAttributes.Usage;
import loon.core.graphics.opengl.GLMesh.VertexDataType;

public class MeshDefault {

	private final static HashMap<Integer, GLMesh> meshLazy = new HashMap<Integer, GLMesh>(
			10);

	public static GLMesh getMesh(final int size) {
		GLMesh mesh = meshLazy.get(size);
		if (mesh == null) {
			mesh = new GLMesh(VertexDataType.VertexArray, false, size * 4,
					size * 6, new GLAttributes.VertexAttribute(Usage.Position,
							2, "POSITION"), new GLAttributes.VertexAttribute(
							Usage.ColorPacked, 4, "COLOR"),
					new GLAttributes.VertexAttribute(Usage.TextureCoordinates,
							2, "TEXCOORD"));
		}
		return mesh;
	}

	public static void post(final int size, float[] vertices, int vertexIdx,
			int count) {
		GLMesh mesh = getMesh(size);
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);
		mesh.render(GL10.GL_TRIANGLES, 0, count);
	}
}
