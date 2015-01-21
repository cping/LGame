
package loon.core.graphics.opengl;

import java.nio.ShortBuffer;

import loon.core.LRelease;


public interface IndexData extends LRelease {

	public int getNumIndices();

	public int getNumMaxIndices();

	public void setIndices(short[] indices, int offset, int count);

	public void setIndices(ShortBuffer indices);

	public ShortBuffer getBuffer();

	public void bind();

	public void unbind();

	public void invalidate();

	public void dispose();
}
