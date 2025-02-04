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
package loon.action.camera;

import loon.LRelease;
import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.opengl.GLEx;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimerContext;

public interface ViewportEffect extends LRelease {

	void start();

	void stop();

	void update(LTimerContext timer);

	void draw(GLEx g, Affine2f view);

	void reset();

	boolean isRunning();

	ViewportEffect setUpdate(Updateable u);

	EaseTimer getEaseTimer();

}
