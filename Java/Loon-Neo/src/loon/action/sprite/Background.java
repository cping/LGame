package loon.action.sprite;

public abstract class Background extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Background(float x, float y, float w, float h) {
		this.setLocation(x, y);
		this.setSize(w, h);
	}


}
