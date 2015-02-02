package loon.core.graphics.opengl;

import loon.core.graphics.opengl.Mesh.VertexDataType;
import loon.core.graphics.opengl.VertexAttributes.Usage;

public class MeshDefault {

	private final static int size = 1024;
	
	private static ShaderProgram shader = null;

	private static Mesh mesh = null;

	public static ShaderProgram getShader() {
		if (shader == null) {
			shader = GLEx.createDefaultShader();
		}
		return shader;
	}

	public static Mesh getMesh() {
		if (mesh == null) {
			mesh = new Mesh(VertexDataType.VertexArray, false, size * 4,
					size * 6, new VertexAttribute(Usage.Position, 2,
							ShaderProgram.POSITION_ATTRIBUTE),
					new VertexAttribute(Usage.ColorPacked, 4,
							ShaderProgram.COLOR_ATTRIBUTE),
					new VertexAttribute(Usage.TextureCoordinates, 2,
							ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
			int len = size * 6;
			short[] indices = new short[len];
			short j = 0;
			for (int i = 0; i < len; i += 6, j += 4) {
				indices[i] = j;
				indices[i + 1] = (short) (j + 1);
				indices[i + 2] = (short) (j + 2);
				indices[i + 3] = (short) (j + 2);
				indices[i + 4] = (short) (j + 3);
				indices[i + 5] = j;
			}
			mesh.setIndices(indices);
		}
		return mesh;
	}

	public static void post(ShaderProgram shader, float[] vertices,
			int vertexIdx, int count) {
		if (mesh == null) {
			getMesh();
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);
		mesh.render(shader, GL20.GL_TRIANGLES, 0, count);
	}
}
