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

/**
 * LGame通用的基本配置类,当配置此类后,注入LGame相关入口函数即可启动游戏.
 * <p>
 * <h3>构建基本参数,用以启动游戏</h3>
 * <p>
 * 
 * <pre class="prettyprint">
 * LSetting setting = new LSetting();
 * </pre>
 */
public class LSetting {

	public int width = LSystem.MAX_SCREEN_WIDTH;

	public int height = LSystem.MAX_SCREEN_HEIGHT;

	public int fps = LSystem.DEFAULT_MAX_FPS;

	public String title;

	public boolean showFPS;

	public boolean showMemory;

	public boolean showLogo;

	public boolean landscape;

	public GameType gametype = GameType.UNKOWN;

	public LMode mode = LMode.Fill;

	public Listener listener;

	public GameType getGameType() {
		return gametype;
	}

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

	public interface Listener {

		void onPause();

		void onResume();

		void onExit();
	}

}
