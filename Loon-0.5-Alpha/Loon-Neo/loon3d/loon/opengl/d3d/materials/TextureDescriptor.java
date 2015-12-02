package loon.opengl.d3d.materials;

import loon.LTexture;
import loon.opengl.GL20;

public class TextureDescriptor {

	public LTexture texture = null;
	public int minFilter = GL20.GL_LINEAR;
	public int magFilter = GL20.GL_LINEAR;

	public TextureDescriptor(final LTexture texture, final int minFilter, final int magFilter) {
		set(texture, minFilter, magFilter);
	}
	
	public TextureDescriptor(final LTexture texture) {
		this.texture = texture;
	}
	
	public TextureDescriptor() {
	}
	
	public void set(final LTexture texture, final int minFilter, final int magFilter) {
		this.texture = texture;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}
	
	public void set(final TextureDescriptor other) {
		texture = other.texture;
		minFilter = other.minFilter;
		magFilter = other.magFilter;
	}
	
	public void reset() {
		texture = null;
		minFilter = GL20.GL_LINEAR;
		magFilter = GL20.GL_LINEAR;
	}
	
	@Override
	public boolean equals (Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof TextureDescriptor)) return false;
		final TextureDescriptor other = (TextureDescriptor)obj;
		return other.texture == texture && other.minFilter == minFilter && other.magFilter == magFilter;
	}
}
