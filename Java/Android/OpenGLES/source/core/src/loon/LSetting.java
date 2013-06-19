/**
 * Copyright 2013 The Loon Authors
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
 */
package loon;

import loon.LGame.LMode;
import loon.core.LSystem;

public class LSetting {

	public int width = LSystem.MAX_SCREEN_WIDTH;

	public int height = LSystem.MAX_SCREEN_HEIGHT;

	public int fps = LSystem.DEFAULT_MAX_FPS;

	public String title;

	public boolean showFPS;

	public boolean showMemory;

	public boolean showLogo;

	public boolean landscape;

	public LMode mode = LMode.Fill;

	public Listener listener;

	public interface Listener {

		void onPause();

		void onResume();

		void onExit();
	}

}
