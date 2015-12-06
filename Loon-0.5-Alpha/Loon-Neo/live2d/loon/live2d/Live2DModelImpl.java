package loon.live2d;

import loon.LTexture;
import loon.live2d.framework.L2DModelMatrix;
import loon.live2d.graphics.*;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;

public class Live2DModelImpl extends ALive2DModel {

	DrawParamImpl impl;

	Live2DModelImpl() {
		this.impl = new DrawParamImpl();
	}

	@Override
	public void draw(L2DModelMatrix matrix,GLEx g) {
		this.modeContext.draw(matrix,g, this.impl);
	}

	@Override
	public void deleteTextures() {
		impl.deleteTextures();
	}

	public void setTexture(final int textureNo, final LTexture tex) {
		this.impl.loadTexture(textureNo, tex);
	}

	@Override
	public void releaseModelTextureNo(final int no) {
		this.impl.releaseModelTextureNo(no);
	}

	public static Live2DModelImpl loadModel(final String filepath) {
		final Live2DModelImpl ret = new Live2DModelImpl();
		ALive2DModel.loadModel_exe(ret, filepath);
		return ret;
	}

	public static Live2DModelImpl loadModel(final ArrayByte bin) {
		final Live2DModelImpl ret = new Live2DModelImpl();
		ALive2DModel.loadModel_exe(ret, bin);
		return ret;
	}

	public static Live2DModelImpl loadModel(final byte[] data) {
		final ArrayByte bin = new ArrayByte(data);
		final Live2DModelImpl ret = new Live2DModelImpl();
		ALive2DModel.loadModel_exe(ret, bin);
		return ret;
	}

	@Override
	public DrawParam getDrawParam() {
		return this.impl;
	}

	public void setTextureColor(final int textureNo, final float r,
			final float impl, final float b) {
		this.getDrawParam().setTextureColor(textureNo, r, impl, b, 1.0f);
	}
}
