package loon.component;

import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.opengl.GLEx;

public class LSpriteUI extends LContainer {

	private ISprite _sprite;

	public LSpriteUI(ISprite sprite) {
		super(sprite.x(), sprite.y(), (int) sprite.getWidth(), (int) sprite
				.getHeight());
		this.customRendering = true;
		this.setBackground(sprite.getBitmap());
		this.setElastic(true);
		this.setLocked(false);
		this.setLayer(100);
	}

	public void syncSprite() {
		if (_sprite != null) {
			this.setRotation(_sprite.getRotation());
			this.setAlpha(_sprite.getAlpha());
			this.setBackground(_sprite.getBitmap());
			this.setColor(_sprite.getColor());
			this.setVisible(_sprite.isVisible());
			this.setScale(_sprite.getScaleX(), _sprite.getScaleY());
			this.setState(_sprite.getState());
			this.setTag(_sprite.getTag());
			this.setLocation(_sprite.getX(), _sprite.getY());
			this.setLayer(_sprite.getLayer());
		}
	}

	public void syncComponent() {
		if (_sprite != null) {
			_sprite.setRotation(getRotation());
			_sprite.setAlpha(getAlpha());
			_sprite.setColor(getColor());
			_sprite.setVisible(isVisible());
			_sprite.setScale(getScaleX(), getScaleY());
			_sprite.setState(getState());
			_sprite.setLocation(getX(), getY());
			_sprite.setLayer(getLayer());
		}
	}

	public ISprite getSprite() {
		return this._sprite;
	}

	@Override
	protected void processTouchClicked() {
		if (!input.isMoving()) {
			this.doClick();
		}
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	@Override
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (_sprite != null) {
			_sprite.createUI(g, x, y);
		}
	}

	@Override
	protected void processTouchDragged() {
		if (!locked) {
			if (_sprite != null) {
				setLocation(_sprite.getX(), _sprite.getY());
			}
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
			if (_sprite != null) {
				_sprite.setLocation(getX(), getY());
			}
		}
		super.dragClick();
	}

	@Override
	protected void processTouchPressed() {
		if (!input.isMoving()) {
			this.downClick();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!input.isMoving()) {
			this.upClick();
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	@Override
	public String getUIName() {
		return "LSprite:" + _sprite == null ? "unkown" : _sprite.getName();
	}

}
