package loon.opengl;

import loon.Graphics;
import loon.LTexture;

public final class LTextureImage extends GLEx {

	public final LTexture texture;

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch, float width, float height, boolean alltex) {
		this(gfx, defaultBatch, gfx.createTexture(width, height, LTexture.Format.LINEAR), alltex);
	}

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch, float width, float height, boolean alltex,
			boolean saveFrameBuffer) {
		this(gfx, defaultBatch, gfx.createTexture(width, height, LTexture.Format.LINEAR), alltex, saveFrameBuffer);
	}

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch, LTexture texture, boolean alltex) {
		this(gfx, defaultBatch, texture, alltex, false);
	}

	public LTextureImage(Graphics gfx, BaseBatch defaultBatch, LTexture texture, boolean alltex,
			boolean saveFrameBuffer) {
		super(gfx, RenderTarget.create(gfx, texture), defaultBatch, alltex, saveFrameBuffer);
		this.texture = texture;
	}

	@Override
	public void close() {
		super.close();
		target.close();
	}
}
