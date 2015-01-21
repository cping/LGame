package loon.core.graphics.opengl;

import java.nio.FloatBuffer;

import loon.core.LRelease;

public interface VertexData extends LRelease {

	public int getNumVertices();

	public int getNumMaxVertices();

	public VertexAttributes getAttributes();

	public void setVertices(float[] vertices, int offset, int count);

	public void updateVertices(int targetOffset, float[] vertices,
			int sourceOffset, int count);

	public FloatBuffer getBuffer();

	public void bind(ShaderProgram shader);

	public void bind(ShaderProgram shader, int[] locations);

	public void unbind(ShaderProgram shader);

	public void unbind(ShaderProgram shader, int[] locations);

	public void invalidate();

	public void dispose();
}
