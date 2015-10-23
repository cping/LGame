package loon.opengl;

import java.nio.Buffer;
import java.nio.IntBuffer;

public interface GLExt {

	public String glGetActiveAttrib(int program, int index, IntBuffer size,
			Buffer type);

	public String glGetActiveUniform(int program, int index, IntBuffer size,
			Buffer type);
}
