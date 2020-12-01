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
package loon.events;

import loon.geom.PointI;

public interface SysInput {

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

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;
	
	void setKeyDown(int code);

	void setKeyUp(int code);

	boolean isMoving();

	int getRepaintMode();

	void setRepaintMode(int mode);

	PointI getTouch();

	int getWidth();

	int getHeight();

	void refresh();

	int getTouchX();

	int getTouchY();

	int getTouchDX();

	int getTouchDY();

	int getTouchReleased();

	boolean isTouchReleased(int i);

	int getTouchPressed();

	boolean isTouchPressed(int i);

	boolean isTouchType(int i);

	int getKeyReleased();

	boolean isKeyReleased(int i);

	int getKeyPressed();

	boolean isKeyPressed(int i);

	boolean isKeyType(int i);

}
