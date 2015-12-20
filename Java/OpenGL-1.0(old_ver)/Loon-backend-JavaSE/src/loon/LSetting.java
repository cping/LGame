/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import java.awt.Canvas;

public class LSetting {

	public int width = LSystem.MAX_SCREEN_WIDTH;

	public int height = LSystem.MAX_SCREEN_HEIGHT;

	public int fps = LSystem.DEFAULT_MAX_FPS;

	public int appX = -1, appY = -1;

	public String title;

	public boolean resizable;

	public boolean showFPS;

	public boolean showMemory;

	public boolean showLogo;

	public GameType gametype = GameType.UNKOWN;

	public Listener listener;

	public Canvas javaCanvas;

	public void setConfigFile(String file) {
		LSystem._configFile = file;
	}

	public String getConfigFile() {
		return LSystem._configFile;
	}

	public void setConfig(LConfig c) {
		LSystem._config = c;
	}

	public LConfig getConfig() {
		return LSystem._config;
	}

	public GameType getGameType() {
		return gametype;
	}

	public interface Listener {

		void onPause();

		void onResume();

		void onExit();
	}

}
