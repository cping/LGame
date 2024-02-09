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

import loon.ScreenSystem;
import loon.ScreenSystemManager;
import loon.Stage;

public class ScreenSystemTest extends Stage {

	class Test1System extends ScreenSystem {

		@Override
		public void init() {
			System.out.println("init1");
		}

		@Override
		public void loop() {
			System.out.println("loop1");
		}

		@Override
		public void exit() {
			System.out.println("exit1");
		}

	}

	class Test2System extends ScreenSystem {

		@Override
		public void init() {
			System.out.println("init2");
		}

		@Override
		public void loop() {
			System.out.println("loop2");
		}

		@Override
		public void exit() {
			System.out.println("exit2");
		}

	}

	@Override
	public void system(ScreenSystemManager m) {
		m.addSystem(new Test1System());
		m.addSystem(new Test2System());
	}

	@Override
	public void create() {
		// 获得系统类管理
		// getSystemManager();
	}

}
