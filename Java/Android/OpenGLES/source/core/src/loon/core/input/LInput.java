package loon.core.input;

import loon.core.geom.Point.Point2i;

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
public interface LInput {

	public interface TextEvent {

		public void input(String text);

		public void cancel();

	}

	public interface SelectEvent {

		public void item(int index);

		public void cancel();

	}

	public interface ClickEvent {

		public void clicked();

		public void cancel();

	}


	public final static int NO_BUTTON = -1;

	public final static int NO_KEY = -1;
	
	public abstract void setKeyDown(int code);

	public abstract void setKeyUp(int code);

	public abstract boolean isMoving();

	public abstract int getRepaintMode();

	public abstract void setRepaintMode(int mode);

	public abstract Point2i getTouch();

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
