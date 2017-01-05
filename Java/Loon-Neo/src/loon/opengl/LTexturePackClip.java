package loon.opengl;

import loon.geom.RectBox;

public class LTexturePackClip {

	public String name = "unkown";

	public int id = 0;

	public RectBox rect = new RectBox();

	public LTexturePackClip(int id, String name, float x, float y, float w, float h) {
		this(id, name, new RectBox(x, y, w + x, h + y));
	}

	public LTexturePackClip(int id, String name, RectBox rect) {
		this.id = id;
		this.name = name;
		this.rect = rect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RectBox getRect() {
		return rect;
	}

	public void setRect(RectBox rect) {
		this.rect = rect;
	}

}
