/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.core.graphics.d3d.parse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import loon.core.geom.Matrix;
import loon.core.graphics.device.LImage;

public class D3DMaterial {
	public ByteBuffer mTextureData;
	public ByteBuffer mSecondaryTextureData;
	public int mWidth;
	public int mHeight;
	public int mTextureName[] = new int[2];
	public FloatBuffer mColors;
	public boolean alphaBlending;
	public boolean alphaTest;
	public boolean renderLight;
	public int mColorName;
	private String mShaderName;

	public static D3DMaterial fromFile(String file) throws IOException {
		D3DMaterial material = new D3DMaterial();
		material.loadTexture(file);
		return material;
	}

	public void setshaderName(String shaderName) {
		this.mShaderName = shaderName;
	}

	public String getShaderName() {
		return this.mShaderName;
	}

	public boolean isRenderLight() {
		return renderLight;
	}

	public void setRenderLight(boolean renderLight) {
		this.renderLight = renderLight;
	}

	public boolean isAlphaTest() {
		return alphaTest;
	}

	public void setAlphaTest(boolean alphaTest) {
		this.alphaTest = alphaTest;
	}

	public boolean useAlphaBlending() {
		return alphaBlending;
	}

	public void setAlphaBlending(boolean useAlphaBlending) {
		this.alphaBlending = useAlphaBlending;
	}

	public void generateCircleTexture(int radius, int width, byte r, byte g,
			byte b, byte a) {
		this.mWidth = width;
		this.mHeight = width;
		mTextureData = ByteBuffer.allocateDirect(width * width * 4);
		mTextureData.order(ByteOrder.BIG_ENDIAN);

		float centerX = width / 2.f;
		float centerY = width / 2.f;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < width; j++) {
				if (Matrix.distance2d(i, j, centerX, centerY) < radius) {
					mTextureData.put(r);
					mTextureData.put(g);
					mTextureData.put(b);
					mTextureData.put(a);
				} else {
					mTextureData.put((byte) 0);
					mTextureData.put((byte) 0);
					mTextureData.put((byte) 0);
					mTextureData.put((byte) 0);
				}
			}

		mTextureData.position(0);
	}

	public void loadTexture(String filename) throws IOException {
		LImage bmp = LImage.createImage(filename);
		mTextureData = ByteBuffer.allocateDirect(bmp.getHeight()
				* bmp.getWidth() * 4);
		mTextureData.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = mTextureData.asIntBuffer();
		for (int y = bmp.getHeight() - 1; y > -1; y--) {
			for (int x = 0; x < bmp.getWidth(); x++) {
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				int alpha = ((pix >> 24) & 0xFF);
				int red = ((pix >> 16) & 0xFF);
				int green = ((pix >> 8) & 0xFF);
				int blue = ((pix) & 0xFF);

				ib.put(red << 24 | green << 16 | blue << 8 | alpha);
			}
		}
		mTextureData.position(0);
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();
		bmp.dispose();
	}

	public void init(int verticeCount) {
		mColors = FloatBuffer.allocate(verticeCount * 4);
	}

	public void setColor(float r, float g, float b, float a) {
		for (int i = 0; i < mColors.capacity() / 4; i++) {

			mColors.put(r);
			mColors.put(g);
			mColors.put(b);
			mColors.put(a);

		}
		mColors.position(0);
	}

	public void copy(D3DMaterial material) {
		mColors = FloatBuffer.allocate(material.mColors.position());

		for (int j = 0; j < mColors.capacity(); j++) {
			mColors.put(material.mColors.get(j));
		}

		mColors.position(0);

		if (material.mTextureData != null) {
			this.mTextureData = ByteBuffer.allocate(material.mTextureData
					.capacity());

			for (int j = 0; j < mTextureData.capacity(); j++) {
				mTextureData.put(material.mTextureData.get(j));
			}
			mTextureData.position(0);
		}

		this.mHeight = material.mHeight;

		this.mWidth = material.mWidth;

	}

}
