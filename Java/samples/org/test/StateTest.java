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
package org.test;

import loon.Stage;
import loon.State;
import loon.event.Touched;
import loon.opengl.GLEx;

public class StateTest extends Stage {

	class TestState extends State {

		@Override
		public void close() {

		}

		@Override
		public void load() {

		}

		@Override
		public void update(float dt) {

		}

		@Override
		public void paint(GLEx g) {
			g.drawText("State1", 55, 55);
		}

	}

	class TestState2 extends State {

		@Override
		public void close() {

		}

		@Override
		public void load() {

		}

		@Override
		public void update(float dt) {

		}

		@Override
		public void paint(GLEx g) {
			g.drawText("State2", 55, 55);
		}

	}

	@Override
	public void create() {

		//构建一个State
		final TestState testState = new TestState();
		//偏移显示在坐标155,155
		testState.posCamera(15, 15);
		//缩放1.6f
		testState.scaleCamera(1.6f);

		final TestState2 testState2 = new TestState2();
		testState2.posCamera(15, 15);

		//把testState命名为A
		addState("A", testState);
		//把testState2命名为B
		addState("B", testState2);

		addButton("state1", 300, 100, 100, 35).up(new Touched() {

			@Override
			public void on(float x, float y) {
				//显示State A
				playState("A");
			}
		});
		addButton("state2", 300, 150, 100, 35).up(new Touched() {

			@Override
			public void on(float x, float y) {
				//显示State B
				playState("B");
			}
		});
	}

}
