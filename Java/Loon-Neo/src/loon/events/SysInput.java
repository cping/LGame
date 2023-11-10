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

	boolean isTouchReleased(String keyName);

	int getTouchPressed();

	boolean isTouchPressed(int i);

	boolean isTouchPressed(String keyName);

	boolean isTouchType(int i);

	int getKeyReleased();

	boolean isKeyReleased(String keyName);

	boolean isKeyReleased(int i);

	int getKeyPressed();

	boolean isKeyPressed(String keyName);

	boolean isKeyPressed(int i);

	boolean isKeyType(int i);

	float getCurrentTimer();

	boolean isLongPressed();

	boolean isLongPressed(float seconds);

	boolean isAxisTouchPressed(String... keys);

	boolean isAxisTouchReleased(String... keys);

	boolean isAxisKeyPressed(String... keys);

	boolean isAxisKeyReleased(String... keys);
}
