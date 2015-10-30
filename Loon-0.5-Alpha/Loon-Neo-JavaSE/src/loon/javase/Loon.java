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
package loon.javase;

import org.lwjgl.opengl.Display;

import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;

public class Loon implements Platform {

	private JavaSEGame game;

	public Loon(LSetting config) {
		this.game = new JavaSEGame(this, config);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		Loon plat = new Loon(setting);
		plat.game.register(lazy.onScreen());
		plat.game.reset();
	}

	@Override
	public int getContainerWidth() {
		return Display.getWidth();
	}

	@Override
	public int getContainerHeight() {
		return Display.getHeight();
	}

	@Override
	public void close() {
		System.exit(-1);
	}

	@Override
	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	public LGame getGame() {
		return game;
	}

}
