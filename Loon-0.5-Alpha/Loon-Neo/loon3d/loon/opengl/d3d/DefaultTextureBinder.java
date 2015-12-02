package loon.opengl.d3d;

import java.nio.IntBuffer;

import loon.LSystem;
import loon.LTexture;
import loon.opengl.GL20;
import loon.opengl.d3d.materials.TextureDescriptor;

public final class DefaultTextureBinder implements TextureBinder {

	public final static int ROUNDROBIN = 0;
	public final static int WEIGHTED = 1;

	public final static int MAX_GLES_UNITS = 32;

	private final int offset;

	private final int count;

	private final int reuseWeight;

	private final TextureDescriptor[] textures;

	private final int[] weights;

	private final int method;

	private boolean reused;

	private int reuseCount = 0;
	private int bindCount = 0;

	public DefaultTextureBinder(final int method) {
		this(method, 0);
	}

	public DefaultTextureBinder(final int method, final int offset) {
		this(method, offset, Math.min(getMaxTextureUnits(), MAX_GLES_UNITS)
				- offset);
	}

	public DefaultTextureBinder(final int method, final int offset,
			final int count) {
		this(method, offset, count, 10);
	}

	public DefaultTextureBinder(final int method, final int offset,
			final int count, final int reuseWeight) {
		final int max = Math.min(getMaxTextureUnits(), MAX_GLES_UNITS);
		if (offset < 0 || count < 0 || (offset + count) > max
				|| reuseWeight < 1)
			throw new RuntimeException("Illegal arguments");
		this.method = method;
		this.offset = offset;
		this.count = count;
		this.textures = new TextureDescriptor[count];
		for (int i = 0; i < count; i++)
			this.textures[i] = new TextureDescriptor();
		this.reuseWeight = reuseWeight;
		this.weights = (method == WEIGHTED) ? new int[count] : null;
	}

	private static int getMaxTextureUnits() {
		IntBuffer buffer = LSystem.base().support().newIntBuffer(16);
		LSystem.base().graphics().gl.glGetIntegerv(
				GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
		return buffer.get(0);
	}

	@Override
	public void begin() {
		for (int i = 0; i < count; i++) {
			textures[i].reset();
			if (weights != null)
				weights[i] = 0;
		}
	}

	@Override
	public void end() {
		for (int i = 0; i < count; i++) {
			if (textures[i].texture != null) {
				LSystem.base().graphics().gl.glActiveTexture(GL20.GL_TEXTURE0 + offset + i);
				LSystem.base().graphics().gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
				textures[i].texture = null;
			}
		}
		LSystem.base().graphics().gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

	/** Binds the texture if needed and sets it active, returns the unit */
	@Override
	public final int bind(final TextureDescriptor textureDesc) {
		return bindTexture(textureDesc, false);
	}

	private final int bindTexture(final TextureDescriptor textureDesc,
			final boolean rebind) {
		int idx, result;
		reused = false;

		switch (method) {
		case ROUNDROBIN:
			result = offset
					+ (idx = bindTextureRoundRobin(textureDesc.texture));
			break;
		case WEIGHTED:
			result = offset + (idx = bindTextureWeighted(textureDesc.texture));
			break;
		default:
			return -1;
		}

		if (reused) {
			reuseCount++;
			if (rebind)
				textureDesc.texture.bind(result);
			else
				LSystem.base().graphics().gl.glActiveTexture(GL20.GL_TEXTURE0 + result);
		} else
			bindCount++;
		if (textureDesc.minFilter != GL20.GL_INVALID_VALUE
				&& textureDesc.minFilter != textures[idx].minFilter)
			LSystem.base().graphics().gl.glTexParameterf(GL20.GL_TEXTURE_2D,
					GL20.GL_TEXTURE_MIN_FILTER,
					textures[idx].minFilter = textureDesc.minFilter);
		if (textureDesc.magFilter != GL20.GL_INVALID_VALUE
				&& textureDesc.magFilter != textures[idx].magFilter)
			LSystem.base().graphics().gl.glTexParameterf(GL20.GL_TEXTURE_2D,
					GL20.GL_TEXTURE_MAG_FILTER,
					textures[idx].magFilter = textureDesc.magFilter);
		return result;
	}

	private int currentTexture = 0;

	private final int bindTextureRoundRobin(final LTexture texture) {
		for (int i = 0; i < count; i++) {
			final int idx = (currentTexture + i) % count;
			if (textures[idx].texture == texture) {
				reused = true;
				return idx;
			}
		}
		currentTexture = (currentTexture + 1) % count;
		textures[currentTexture].texture = texture;
		texture.bind(offset + currentTexture);
		return currentTexture;
	}

	private final int bindTextureWeighted(final LTexture texture) {
		int result = -1;
		int weight = weights[0];
		int windex = 0;
		for (int i = 0; i < count; i++) {
			if (textures[i].texture == texture) {
				result = i;
				weights[i] += reuseWeight;
			} else if (weights[i] < 0 || --weights[i] < weight) {
				weight = weights[i];
				windex = i;
			}
		}
		if (result < 0) {
			textures[windex].texture = texture;
			weights[windex] = 100;
			texture.bind(offset + (result = windex));
		} else
			reused = true;
		return result;
	}

	@Override
	public final int getBindCount() {
		return bindCount;
	}

	@Override
	public final int getReuseCount() {
		return reuseCount;
	}

	@Override
	public final void resetCounts() {
		bindCount = reuseCount = 0;
	}
}
