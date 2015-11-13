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
package loon;

import loon.action.map.Field2D;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.utils.MathUtils;

public abstract class LObject implements XY , ZIndex {

	public Object Tag;

	public float _alpha = 1f;

	protected RectBox rect;

	protected String name;

	protected Vector2f _location = new Vector2f(0, 0);

	protected int _layer;

	protected float _rotation;

	public void setTransparency(int _alpha) {
		setAlpha(_alpha / 255f);
	}

	public int getTransparency() {
		return (int) (_alpha * 255);
	}

	public void setAlpha(float a) {
		this._alpha = a;
	}

	public float getAlpha() {
		return this._alpha;
	}

	public void setRotation(float r) {
		this._rotation = r;
		if (rect != null) {
			rect = MathUtils.getBounds(_location.x, _location.y, getWidth(),
					getHeight(), r, rect);
		}
	}

	public float getRotation() {
		return _rotation;
	}

	public abstract void update(long elapsedTime);

	public void centerOnScreen() {
		LObject.centerOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void bottomOnScreen() {
		LObject.bottomOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void leftOnScreen() {
		LObject.leftOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void rightOnScreen() {
		LObject.rightOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public void topOnScreen() {
		LObject.topOn(this, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public RectBox getCollisionArea() {
		return getRect(getX(), getY(), getWidth(), getHeight());
	}

	protected RectBox getRect(float x, float y, float w, float h) {
		if (rect == null) {
			rect = new RectBox(x, y, w, h);
		} else {
			rect.setBounds(x, y, w, h);
		}
		return rect;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getLayer() {
		return _layer;
	}

	public void setLayer(int z) {
		this._layer = z;
	}

	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		_location.move_multiples(Field2D.UP, multiples);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		_location.move_multiples(Field2D.LEFT, multiples);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		_location.move_multiples(Field2D.RIGHT, multiples);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		_location.move_multiples(Field2D.DOWN, multiples);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		_location.move_multiples(Field2D.TUP, multiples);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		_location.move_multiples(Field2D.TLEFT, multiples);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		_location.move_multiples(Field2D.TRIGHT, multiples);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		_location.move_multiples(Field2D.TDOWN, multiples);
	}

	public void move(Vector2f v) {
		_location.move(v);
	}

	public void move(float x, float y) {
		_location.move(x, y);
	}

	public void setLocation(float x, float y) {
		_location.setLocation(x, y);
	}

	public int x() {
		return (int) _location.getX();
	}

	public int y() {
		return (int) _location.getY();
	}

	public float getX() {
		return _location.getX();
	}

	public float getY() {
		return _location.getY();
	}

	public void setX(Integer x) {
		_location.setX(x.intValue());
	}

	public void setX(float x) {
		_location.setX(x);
	}

	public void setY(Integer y) {
		_location.setY(y.intValue());
	}

	public void setY(float y) {
		_location.setY(y);
	}

	public Vector2f getLocation() {
		return _location;
	}

	public void setLocation(Vector2f _location) {
		this._location = _location;
	}

	public static void centerOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2,
				h / 2 - object.getHeight() / 2);
	}

	public static void topOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - h / 2, 0);
	}

	public static void leftOn(final LObject object, int w, int h) {
		object.setLocation(0, h / 2 - object.getHeight() / 2);
	}

	public static void rightOn(final LObject object, int w, int h) {
		object.setLocation(w - object.getWidth(), h / 2 - object.getHeight()
				/ 2);
	}

	public static void bottomOn(final LObject object, int w, int h) {
		object.setLocation(w / 2 - object.getWidth() / 2,
				h - object.getHeight());
	}

	public void centerOn(final LObject object) {
		centerOn(object, getWidth(), getHeight());
	}

	public void topOn(final LObject object) {
		topOn(object, getWidth(), getHeight());
	}

	public void leftOn(final LObject object) {
		leftOn(object, getWidth(), getHeight());
	}

	public void rightOn(final LObject object) {
		rightOn(object, getWidth(), getHeight());
	}

	public void bottomOn(final LObject object) {
		bottomOn(object, getWidth(), getHeight());
	}

	public abstract int getWidth();

	public abstract int getHeight();
}
