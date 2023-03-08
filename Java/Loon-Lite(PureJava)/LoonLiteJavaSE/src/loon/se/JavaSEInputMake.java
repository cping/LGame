/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.se;

import java.awt.event.MouseEvent;
import java.util.List;

import loon.LObject;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.KeyMake.KeyEvent;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.TouchMake;
import loon.geom.Vector2f;

public class JavaSEInputMake extends InputMake {

	private final JavaSEGame game;

	private final Vector2f lastMousePt = new Vector2f();

	private boolean inDragSequence = false;
	private boolean isRequestingMouseLock;

	private boolean inTouchSequence = false;

	private float touchDX = -1, touchDY = -1;

	public float getTouchDX() {
		return touchDX;
	}

	public float getTouchDY() {
		return touchDY;
	}


	public JavaSEInputMake(JavaSEGame game) {
		this.game = game;
	

	}

	@Override
	public void setMouseLocked(boolean locked) {
		if (locked) {
			if (hasMouseLock()) {
				isRequestingMouseLock = true;
				game.log().debug("Requesting mouse lock (supported)");
			} else {
				game.log().debug("Requesting mouse lock -- but unsupported");
			}
		} else {
			game.log().debug("Requesting mouse unlock");
			if (hasMouseLock()) {
				isRequestingMouseLock = false;
			}
		}
	}

	@Override
	public void callback(LObject<?> o) {

	}
}
