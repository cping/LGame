package loon.action.sprite;

public abstract class Background extends Entity {

	public Background(float x, float y, float w, float h) {
		this.setLocation(x, y);
		this.setSize(w, h);
	}


}
