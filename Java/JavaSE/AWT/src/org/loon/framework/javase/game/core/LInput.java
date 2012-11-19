package org.loon.framework.javase.game.core;

import java.awt.Point;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public interface LInput {

	public static final int NO_BUTTON = -1;

	public static final int NO_KEY = -1;

	public abstract void update(long time);

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;

	public abstract void setKeyDown(int code);

	public abstract void setKeyUp(int code);

	public abstract boolean isMoving();

	public abstract int getRepaintMode();

	public abstract void setRepaintMode(int mode);

	public abstract boolean isTouchClick();

	public abstract boolean isTouchClickUp();

	public abstract Point getTouch();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void refresh();

	public abstract int getTouchX();

	public abstract int getTouchY();

	public abstract int getTouchDX();

	public abstract int getTouchDY();

	public abstract int getTouchReleased();

	public abstract boolean isTouchReleased(int i);

	public abstract int getTouchPressed();

	public abstract boolean isTouchPressed(int i);

	public abstract boolean isTouchType(int i);

	public abstract int getKeyReleased();

	public abstract boolean isKeyReleased(int i);

	public abstract int getKeyPressed();

	public abstract boolean isKeyPressed(int i);

	public abstract boolean isKeyType(int i);
}
