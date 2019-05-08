package loon.opengl;

import loon.Graphics;
import loon.LTexture;

public class LTextureImage extends GLEx {

	public final LTexture texture;

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch, float width,
			float height, boolean alltex) {
		this(gfx, defaultBatch, gfx.createTexture(width, height,
				LTexture.Format.LINEAR), alltex);
	}

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch,
			LTexture texture, boolean alltex) {
		super(gfx, RenderTarget.create(gfx, texture), defaultBatch, alltex);
		this.texture = texture;
	}

	@Override
	public void close() {
		super.close();
	}
}
