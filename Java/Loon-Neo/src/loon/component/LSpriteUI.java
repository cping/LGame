/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.component;

import loon.LSystem;
import loon.action.sprite.ISprite;
import loon.opengl.GLEx;

/**
 * 用于将一个精灵当作UI注入Screen
 */
public class LSpriteUI extends LContainer {

	private ISprite _sprite;

	public LSpriteUI(ISprite sprite) {
		super(sprite.x(), sprite.y(), (int) sprite.getWidth(), (int) sprite.getHeight());
		this.customRendering = true;
		this.setBackground(sprite.getBitmap());
		this.setElastic(true);
		this.setLocked(false);
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
			super.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!input.isMoving()) {
			super.processTouchReleased();
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "LSprite:" + _sprite == null ? LSystem.UNKNOWN : _sprite.getName();
	}

	@Override
	public void destory() {
		if (_sprite != null) {
			_sprite.close();
		}
	}

}
