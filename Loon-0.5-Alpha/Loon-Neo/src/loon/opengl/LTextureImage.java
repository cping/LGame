package loon.opengl;

import loon.Graphics;
import loon.LTexture;


public class LTextureImage extends GLEx {

  public final LTexture texture;

  public LTextureImage (Graphics gfx, BaseBatch defaultBatch, float width, float height) {
    this(gfx, defaultBatch, gfx.createTexture(width, height, LTexture.Format.LINEAR));
  }

  public LTextureImage (Graphics gfx, BaseBatch defaultBatch, LTexture texture) {
    super(gfx, RenderTarget.create(gfx, texture), defaultBatch);
    this.texture = texture;
  }

  @Override public void close () {
    super.close();
    target.close();
  }
}
