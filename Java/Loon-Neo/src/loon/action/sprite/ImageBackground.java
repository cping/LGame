package loon.action.sprite;

import loon.LTexture;
import loon.LTextures;

public class ImageBackground extends Background {

	public ImageBackground(LTexture tex, float x, float y, float w, float h) {
		super(x, y, w, h);
		setRepaint(true);
		_image = tex;
	}

	public ImageBackground(String path, float x, float y, float w, float h) {
		this(LTextures.loadTexture(path), x, y, w, h);
	}

	public ImageBackground(String path) {
		this(LTextures.loadTexture(path));
	}

	public ImageBackground(LTexture texture) {
		super(0, 0, texture.getWidth(), texture.getHeight());
		_image = texture;
	}

}
