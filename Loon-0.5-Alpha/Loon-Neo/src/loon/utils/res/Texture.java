package loon.utils.res;

import loon.LTexture;

public class Texture {

	protected TextureData _texData = null;

	protected LTexture _img = null;

	public Texture(LTexture tex) {
		_img = tex;
		_texData = new TextureData();
		_texData.w = _img.getWidth();
		_texData.h = _img.getHeight();
		_texData.sourceW = _texData.w;
		_texData.sourceH = _texData.h;
	}

	public Texture(LTexture img, TextureData td) {
		_img = img;
		_texData = td;
	}

	public LTexture img() {
		return _img;
	}

	public TextureData data() {
		return _texData;
	}

}
