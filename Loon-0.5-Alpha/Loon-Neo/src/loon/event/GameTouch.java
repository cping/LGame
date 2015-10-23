package loon.event;



public class GameTouch {

	int type;

	float x, y;

	int button;

	int pointer;

	int id;

	GameTouch() {
		
	}
	
	public GameTouch(float x, float y, int pointer, int id) {
		this.set(x, y, pointer, id);
	}
	
	public void set(float x, float y, int pointer, int id) {
		this.x = x;
		this.y = y;
		this.pointer = pointer;
		this.id = id;
	}
	
	GameTouch(GameTouch touch) {
		this.type = touch.type;
		this.x = touch.x;
		this.y = touch.y;
		this.button = touch.button;
		this.pointer = touch.pointer;
		this.id = touch.id;
	}

	public void offset(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void offsetX(float x) {
		this.x += x;
	}

	public void offsetY(float y) {
		this.y += y;
	}

	public boolean equals(GameTouch e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.type == type && e.x == x && e.y == y && e.button == button
				&& e.pointer == pointer && e.id == id) {
			return true;
		}
		return false;
	}

	public int getButton() {
		return button;
	}

	public int getPointer() {
		return pointer;
	}

	public int getType() {
		return type;
	}

	public int getID() {
		return id;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	static boolean isDraging;
	
	public boolean isDown() {
		return button == SysTouch.TOUCH_DOWN;
	}

	public boolean isUp() {
		return button == SysTouch.TOUCH_UP;
	}

	public boolean isMove() {
		return button == SysTouch.TOUCH_MOVE;
	}

	public boolean isDrag() {
		return isDraging;
	}
}
