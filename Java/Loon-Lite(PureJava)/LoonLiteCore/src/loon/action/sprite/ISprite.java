/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action.sprite;

import loon.LObject.State;
import loon.LRelease;
import loon.LTexture;
import loon.Screen;
import loon.Visible;
import loon.ZIndex;
import loon.action.ActionBind;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.SetXY;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;

public interface ISprite extends ActionBind, Visible, LRelease, ZIndex, XY, SetXY {

	public static final int TYPE_FADE_IN = 0;

	public static final int TYPE_FADE_OUT = 1;

	@Override
	float getWidth();

	@Override
	float getHeight();

	@Override
	float getAlpha();

	@Override
	int x();

	@Override
	int y();

	@Override
	float getX();

	@Override
	float getY();

	@Override
	void setX(float x);

	@Override
	void setY(float y);

	@Override
	void setColor(LColor c);

	@Override
	LColor getColor();

	boolean showShadow();

	boolean autoXYSort();

	void createUI(GLEx g);

	void createUI(GLEx g, float offsetX, float offsetY);

	void update(long elapsedTime);

	@Override
	ISprite setSize(float w, float h);

	void onResize();

	void onCollision(ISprite coll, int dir);

	@Override
	int getLayer();

	void setLayer(int layer);

	RectBox getCollisionBox();

	LTexture getBitmap();

	String getName();

	Object getTag();

	ISprite getParent();

	void setParent(ISprite s);

	void setName(String s);

	void setState(State state);

	State getState();

	ISprite setSprites(Sprites ss);

	Sprites getSprites();

	Screen getScreen();

	ISprite setOffset(Vector2f v);

	float getOffsetX();

	float getOffsetY();

	float getFixedWidthOffset();

	ISprite setFixedWidthOffset(float widthOffset);

	float getFixedHeightOffset();

	ISprite setFixedHeightOffset(float heightOffset);

	boolean collides(ISprite other);

	boolean collidesX(ISprite other);

	boolean collidesY(ISprite other);

	boolean isDisposed();

}
