package loon.opengl;

import loon.LTexture;
import loon.LSystem;
import loon.geom.RectBox;
import loon.utils.TArray;

public final class LTexturePackClip {

	public static final TArray<LTexturePackClip> getTextureSplit(String path, int tileWidth, int tileHeight,
			int offsetX, int offsetY) {
		return getTextureSplit(LSystem.loadTexture(path), tileWidth, tileHeight, offsetX, offsetY);
	}

	public static final TArray<LTexturePackClip> getTextureSplit(String path, int tileWidth, int tileHeight) {
		return getTextureSplit(LSystem.loadTexture(path), tileWidth, tileHeight, 0, 0);
	}

	public static final TArray<LTexturePackClip> getTextureSplit(LTexture texture, int tileWidth, int tileHeight) {
		return getTextureSplit(texture, tileWidth, tileHeight, 0, 0);
	}

	public static final TArray<LTexturePackClip> getTextureSplit(LTexture texture, int tileWidth, int tileHeight,
			int offsetX, int offsetY) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int frame = 0;
		int wlength = width / tileWidth;
		int hlength = height / tileHeight;
		TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(wlength * hlength);
		for (int y = 0; y < hlength; y++) {
			for (int x = 0; x < wlength; x++) {
				clips.add(new LTexturePackClip(frame, String.valueOf(frame), offsetX + (x * tileWidth),
						offsetY + (y * tileHeight), tileWidth, tileHeight));
				frame++;
			}
		}
		return clips;
	}

	public String name;

	public int id = 0;

	public final RectBox rect;

	public LTexturePackClip(int id, String name, float x, float y, float size) {
		this(id, name, x, y, size, size);
	}

	public LTexturePackClip(int id, String name, float x, float y, float w, float h) {
		this(id, name, new RectBox(x, y, w + x, h + y));
	}

	public LTexturePackClip(int id, float x, float y, float size) {
		this(id, x, y, size, size);
	}

	public LTexturePackClip(int id, float x, float y, float w, float h) {
		this(id, LSystem.UNKNOWN, new RectBox(x, y, w + x, h + y));
	}

	public LTexturePackClip(int id, String name, RectBox rect) {
		this.id = id;
		this.name = name;
		this.rect = rect;
	}

	public String getName() {
		return name;
	}

	public LTexturePackClip setName(String name) {
		this.name = name;
		return this;
	}

	public int getId() {
		return id;
	}

	public LTexturePackClip setId(int id) {
		this.id = id;
		return this;
	}

	public RectBox getRect() {
		return rect;
	}

	public LTexturePackClip setRect(RectBox rect) {
		this.rect.setBounds(rect);
		return this;
	}

}
