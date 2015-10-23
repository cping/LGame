/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.opengl;

import static loon.opengl.GL20.*;
import loon.Graphics;
import loon.LRelease;
import loon.LTexture;

/**
 * Encapsulates an OpenGL render target (i.e. a framebuffer).
 * @see Graphics#defaultRenderTarget
 */
public abstract class RenderTarget implements LRelease {

  /** Creates a render target that renders to {@code texture}. */
  public static RenderTarget create (Graphics gfx, final LTexture tex) {
    GL20 gl = gfx.gl;
    final int fb = gl.glGenFramebuffer();
    if (fb == 0) throw new RuntimeException("Failed to gen framebuffer: " + gl.glGetError());
    gl.glBindFramebuffer(GL_FRAMEBUFFER, fb);
    gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex.getID(), 0);
    gl.checkError("RenderTarget.create");
    return new RenderTarget (gfx) {
      public int id () { return fb; }
      public int width () { return tex.pixelWidth(); }
      public int height () { return tex.pixelHeight(); }
      public float xscale () { return tex.pixelWidth() / tex.width(); }
      public float yscale () { return tex.pixelHeight() / tex.height(); }
      public boolean flip () { return false; }
    };
  }

  /** A handle on our graphics services. */
  public final Graphics gfx;

  public RenderTarget (Graphics gfx) {
    this.gfx = gfx;
  }

  /** The framebuffer id. */
  public abstract int id ();

  /** The width of the framebuffer in pixels. */
  public abstract int width ();
  /** The height of the framebuffer in pixels. */
  public abstract int height ();

  /** The x-scale between display units and pixels for this target. */
  public abstract float xscale ();
  /** The y-scale between display units and pixels for this target. */
  public abstract float yscale ();

  /** Whether or not to flip the y-axis when rendering to this target. When rendering to textures
    * we do not want to flip the y-axis, but when rendering to the screen we do (so that the origin
    * is at the upper-left of the screen). */
  public abstract boolean flip ();

  /** Binds the framebuffer. */
  public void bind () {
    gfx.gl.glBindFramebuffer(GL_FRAMEBUFFER, id());
    gfx.gl.glViewport(0, 0, width(), height());
  }

  /** Deletes the framebuffer associated with this render target. */
  @Override public void close () {
    if (!disposed) {
      disposed = true;
      gfx.gl.glDeleteFramebuffer(id());
    }
  }

  @Override public String toString () {
    return "[id=" + id() + ", size=" + width() + "x" + height() + " @ " +
      xscale() + "x" + yscale() + ", flip=" + flip() + "]";
  }

  @Override protected void finalize () {
    if (!disposed) gfx.queueForDispose(this);
  }

  private boolean disposed;
}
