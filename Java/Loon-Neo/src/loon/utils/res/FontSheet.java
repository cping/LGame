package loon.utils.res;

import loon.BaseIO;
import loon.Json;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;

public class FontSheet implements LRelease {

	private TextureAtlas _texAtlas = null;

	public LTexture sheet() {
		return _texAtlas.img();
	}

	public TextureData getCharData(char ch) {
		String str = String.valueOf(ch);
		return _texAtlas.getFrame(str);
	}

	protected FontSheet(String url) {
		Json.Object jsonObj = LSystem.base().json().parse(BaseIO.loadText(url));
		String imagePath = url;
		LTexture sheet = LTextures.loadTexture(imagePath);
		init(jsonObj, sheet);
	}

	protected FontSheet(Json.Object jsonObj, LTexture sheet) {
		init(jsonObj, sheet);
	}

	protected void init(Json.Object jsonObj, LTexture sheet) {
		_texAtlas = new TextureAtlas(sheet, jsonObj);
	}

	public void close() {
		if (_texAtlas != null) {
			_texAtlas.close();
		}
	}
}
