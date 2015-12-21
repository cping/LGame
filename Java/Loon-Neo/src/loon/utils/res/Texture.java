package loon.utils.res;

import loon.LTexture;
import loon.LTextures;

public class Texture {

	private TextureData _texData = null;

	private LTexture _img = null;

	private String _path = null;

	public Texture(String path) {
		_path = path;
		_texData = new TextureData();
	}

	public Texture(LTexture tex, TextureData td) {
		_path = tex.getSource();
		_img = tex;
		_texData = td;
	}

	public TextureData getTextureData() {
		if (_img == null || _img.disposed()) {
			_img = LTextures.loadTexture(_path);
			_texData.w = _img.getWidth();
			_texData.h = _img.getHeight();
			_texData.sourceW = _texData.w;
			_texData.sourceH = _texData.h;
		}
		return _texData;
	}

	public LTexture img() {
		if (_img == null || _img.disposed()) {
			_img = LTextures.loadTexture(_path);
			_texData.w = _img.getWidth();
			_texData.h = _img.getHeight();
			_texData.sourceW = _texData.w;
			_texData.sourceH = _texData.h;
		}
		return _img;
	}

	public TextureData data() {
		return _texData;
	}

	public void close() {
		if (_img != null) {
			_img.close();
		}
	}

}
