package loon.opengl;

import loon.Graphics;
import loon.LTexture;


/**
 * A {@link GLEx} which renders to a {@link LTexture} instead of to the default frame buffer.
 *
 * <p>Note: a {@code TextureSurface} makes use of three GPU resources: a framebuffer, a quad batch
 * and a texture. The framebuffer's lifecycle is tied to the lifecycle of the {@code
 * TextureSurface}. When you {@link close} it the framebuffer is disposed.
 *
 * <p>The quad batch's lifecycle is independent of the {@code TextureSurface}. Most likely you will
 * use the default quad batch for your game which lives for the lifetime of your game.
 *
 * <p>The texture's lifecycle is also independent of the {@code TextureSurface} and is managed by
 * reference counting. The texture is neither referenced, nor released by the {@code
 * TextureSurface}. It is assumed that the texture will be stuffed into an {@code ImageLayer} or
 * used for rendering elsewhere and that code will manage the texture's lifecycle (even if the
 * texture is created by {@code TextureSurface} in the first place).
 */
public class LTextureImage extends GLEx {

  /** The texture into which we're rendering. */
  public final LTexture texture;

  /** Creates a texture surface which is {@code width x height} in display units.
    * A managed backing texture will be automatically created. */
  public LTextureImage (Graphics gfx, BaseBatch defaultBatch, float width, float height) {
    this(gfx, defaultBatch, gfx.createTexture(width, height, LTexture.Format.LINEAR));
  }

  /** Creates a texture surface which renders to {@code texture}. */
  public LTextureImage (Graphics gfx, BaseBatch defaultBatch, LTexture texture) {
    super(gfx, RenderTarget.create(gfx, texture), defaultBatch);
    this.texture = texture;
  }

  @Override public void close () {
    super.close();
    target.close();
  }
}
