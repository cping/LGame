package loon.utils.res;

import loon.Json;
import loon.LRelease;
import loon.LTexture;
import loon.utils.ListMap;
import loon.utils.TArray;

public class MovieSpriteSheet implements LRelease {

	protected TextureData[] _datas = null;

	private TextureAtlas _ta = null;

	protected MovieSpriteSheet(TextureAtlas ta, String[] frameNames) {
		init(ta, frameNames);
	}

	protected MovieSpriteSheet(Json.Object jsonObj, String[] frameNames,
			LTexture sheet) {
		TextureAtlas ta = new TextureAtlas(sheet, jsonObj);
		init(ta, frameNames);
	}

	protected MovieSpriteSheet(Json.Object jsonObj, LTexture sheet) {
		TextureAtlas ta = new TextureAtlas(sheet, jsonObj);
		init(ta, ta.names);
	}

	public TextureAtlas textureAtlas() {
		return _ta;
	}

	public LTexture sheet() {
		return _ta.img();
	}

	public TextureData[] datas() {
		return _datas;
	}

	public TextureData getSSD(String name) {
		return _ta.getFrame(name);
	}

	protected void init(TextureAtlas ta, TArray<String> frameNames) {
		_ta = ta;
		_datas = new TextureData[frameNames.size];
		for (int i = 0; i < frameNames.size; i++) {
			_datas[i] = _ta.getFrame(frameNames.get(i));
		}
	}

	protected void init(TextureAtlas ta, String[] frameNames) {
		_ta = ta;
		_datas = new TextureData[frameNames.length];
		for (int i = 0; i < frameNames.length; i++) {
			_datas[i] = _ta.getFrame(frameNames[i]);
		}
	}

	public LTexture[] getTextures() {
		LTexture tex = _ta.img();
		int size = _datas.length;
		LTexture[] texList = new LTexture[size];
		for (int i = 0; i < size; i++) {
			TextureData data = _datas[i];
			texList[i] = tex.copy(data.x, data.y, data.w, data.h);
		}
		return texList;
	}

	public Texture getTexture(String name) {
		return _ta.getTexture(name);
	}

	public MovieSpriteSheet getSpriteSheet(String prefix) {
		TArray<TextureData> list = new TArray<TextureData>();
		for (int i = 0; i < _datas.length; i++) {
			TextureData td = _datas[i];
			if (td.name.startsWith(prefix)) {
				list.add(td);
			}
		}
		ListMap<String, TextureData> frames = new ListMap<String, TextureData>(
				list.size);
		String[] frameNames = new String[list.size];
		for (int i = 0; i < frameNames.length; i++) {
			TextureData td = list.get(i);
			frameNames[i] = td.name;
			frames.put(td.name, td);
		}
		TextureAtlas ta = new TextureAtlas(_ta.img(), frames);
		MovieSpriteSheet ss = new MovieSpriteSheet(ta, frameNames);
		return ss;
	}

	@Override
	public void close() {
		if (_ta != null) {
			_ta.close();
		}
	}
}
