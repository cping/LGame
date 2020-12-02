/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package org.test;

import loon.Counter;
import loon.Screen;
import loon.events.ActionUpdate;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.utils.reply.Emitter;
import loon.utils.timer.LTimerContext;

public class EmitterTest extends Screen {

	private Emitter<EmitterTest> emitter = new Emitter<EmitterTest>();

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		emitter.addObserver(this, new ActionUpdate() {

			Counter counter = new Counter();

			@Override
			public void action(Object a) {
				counter.increment();
			}

			@Override
			public boolean completed() {
				boolean over = counter.getValue() > 200;
				if (over) {
					System.out.println("remove a");
				}
				return over;
			}
		});
		emitter.addObserver(this, new ActionUpdate() {

			Counter counter = new Counter();

			@Override
			public void action(Object a) {
				counter.increment();
			}

			@Override
			public boolean completed() {
				boolean over = counter.getValue() > 400;
				if (over) {
					System.out.println("remove b");
				}
				return over;
			}
		});
	}

	@Override
	public void alter(LTimerContext context) {
		emitter.onEmit(this);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
