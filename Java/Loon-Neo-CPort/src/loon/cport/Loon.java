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
package loon.cport;

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.cport.CGame.CSetting;
import loon.events.KeyMake.TextType;
import loon.events.SysInput.ClickEvent;
import loon.events.SysInput.TextEvent;
import loon.utils.Resolution;

public class Loon implements Platform {

	private CGame _game;

	public Loon(CSetting config) {
		Resolution
		this._game = new CGame(this, config);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		CSetting cset = null;
		if (setting == null) {
			cset = new CSetting();
		} else {
			if (setting instanceof CSetting) {
				cset = (CSetting) setting;
			} else {
				cset = new CSetting();
				cset.copy(setting);
			}
		}
		final Loon plat = new Loon(cset);
		plat._game.register(lazy.onScreen());
		plat._game.start();
	}

	@Override
	public void close() {
		if (_game != null) {
			_game.shutdown();
		}
	}

	@Override
	public int getContainerWidth() {
		if (_game != null) {
			return _game.graphics().width();
		}
		return LSystem.viewSize.getZoomWidth();
	}

	@Override
	public int getContainerHeight() {
		if (_game != null) {
			return _game.graphics().height();
		}
		return LSystem.viewSize.getZoomHeight();
	}

	@Override
	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	@Override
	public LGame getGame() {
		return _game;
	}

	@Override
	public void sysText(TextEvent event, TextType textType, String label, String initialValue) {

	}

	@Override
	public void sysDialog(ClickEvent event, String title, String text, String ok, String cancel) {

	}

}
