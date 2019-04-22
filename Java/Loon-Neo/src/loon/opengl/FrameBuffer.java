/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.LSystem;
import loon.LTexture;
import loon.LTexture.Format;

public class FrameBuffer extends GLFrameBuffer {

	FrameBuffer() {
	}

	protected FrameBuffer(GLFrameBufferBuilder<GLFrameBuffer> bufferBuilder) {
		super(bufferBuilder);
	}

	public FrameBuffer(int width, int height, int glFormat, int glType, boolean hasDepth) {
		this(width, height, glFormat, glType, hasDepth, false);
	}

	public FrameBuffer(int width, int height, boolean hasDepth, boolean hasStencil) {
		this(width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, hasDepth);
	}

	public FrameBuffer(int width, int height, int glFormat, int glType, boolean hasDepth, boolean hasStencil) {
		FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(width, height);
		frameBufferBuilder.addBasicColorTextureAttachment(glFormat, glType);
		if (hasDepth)
			frameBufferBuilder.addBasicDepthRenderBuffer();
		if (hasStencil)
			frameBufferBuilder.addBasicStencilRenderBuffer();
		this.bufferBuilder = frameBufferBuilder;

		build();
	}

	@Override
	protected LTexture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec) {
		LTexture texture = LTexture.createTexture(bufferBuilder.width, bufferBuilder.height, Format.LINEAR);
		return texture;
	}

	@Override
	protected void disposeColorTexture(LTexture colorTexture) {
		colorTexture.close();
	}

	@Override
	protected void attachFrameBufferColorTexture(LTexture texture) {
		LSystem.base().graphics().gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0,
				GL20.GL_TEXTURE_2D, texture.getID(), 0);
	}

	public static void unbind() {
		GLFrameBuffer.unbind();
	}
}
