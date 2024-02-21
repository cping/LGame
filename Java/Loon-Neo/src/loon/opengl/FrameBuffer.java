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

/**
 * 图像缓冲区构建用类
 *
 * <pre>
 *
 * &#64;Override
 * public void create() {
 * 	tex = loadTexture("assets/ccc.png");
 * 	// 构建一个纹理缓冲器
 * 	buffer = new FrameBuffer(getWidth(), getHeight());
 * }
 *
 * &#64;Override
 * public void draw(GLEx g) {
 * 	// 如果缓冲区没有锁定
 * 	if (!buffer.isLocked()) {
 * 		// 开始缓冲屏幕纹理到缓冲器
 * 		buffer.begin();
 * 		g.draw(tex, 55, 55, LColor.red);
 * 		// 结束缓冲
 * 		buffer.end();
 * 		// 锁定纹理不再缓冲
 * 		buffer.lock();
 * 	}
 * 	// 显示缓冲的数据
 * 	g.drawFlip(buffer.texture(), 0, 0);
 * }
 *
 * </pre>
 */
public class FrameBuffer extends GLFrameBuffer {

	FrameBuffer() {
	}

	protected FrameBuffer(GLFrameBufferBuilder<GLFrameBuffer> bufferBuilder) {
		super(bufferBuilder);
	}

	public FrameBuffer(int width, int height) {
		this(width, height, false);
	}

	public FrameBuffer(int width, int height, int glFormat, int glType, boolean hasDepth) {
		this(width, height, glFormat, glType, hasDepth, false);
	}

	public FrameBuffer(int width, int height, boolean hasDepth) {
		this(width, height, hasDepth, false);
	}

	public FrameBuffer(int width, int height, boolean hasDepth, boolean hasStencil) {
		this(width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, hasDepth);
	}

	public FrameBuffer(int width, int height, int glFormat, int glType, boolean hasDepth, boolean hasStencil) {
		FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(width, height);
		frameBufferBuilder.addBasicColorTextureAttachment(glFormat, glType);
		if (hasDepth) {
			frameBufferBuilder.addBasicDepthRenderBuffer();
		}
		if (hasStencil) {
			frameBufferBuilder.addBasicStencilRenderBuffer();
		}
		this.bufferBuilder = frameBufferBuilder;
		build();
	}

	@Override
	protected LTexture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec) {
		return LTexture.createTexture(bufferBuilder.width, bufferBuilder.height, Format.LINEAR);
	}

	@Override
	protected void disposeColorTexture(LTexture colorTexture) {
		if (colorTexture != null) {
			colorTexture.close();
		}
	}

	protected void bindTextureID(int id) {
		LSystem.base().graphics().gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0,
				GL20.GL_TEXTURE_2D, id, 0);
	}

	@Override
	protected void attachFrameBufferColorTexture(LTexture texture) {
		bindTextureID(texture.getID());
	}
}
