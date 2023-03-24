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
package loon.lwjgl;

import javax.swing.JOptionPane;

import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.events.KeyMake;
import loon.events.SysInput;

public class Loon implements Platform {

	private Lwjgl3Game game;

	public Loon(LSetting config) {
		this.game = new Lwjgl3Game(this, config);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		Loon plat = new Loon(setting);
		plat.game.register(lazy.onScreen());
		plat.game.reset();
	}

	@Override
	public int getContainerWidth() {
		return ((Lwjgl3Graphics) game.graphics()).screenSize().getWidth();
	}

	@Override
	public int getContainerHeight() {
		return ((Lwjgl3Graphics) game.graphics()).screenSize().getHeight();
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

	@Override
	public void sysText(final SysInput.TextEvent event,
			final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (Lwjgl3Game.isMacOS()) {
			return;
		}
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {
				final String output = (String) JOptionPane.showInputDialog(
						null, label, "", JOptionPane.QUESTION_MESSAGE, null,
						null, initVal);
				if (output != null) {
					event.input(output);
				} else {
					event.cancel();
				}
			}
		});
	}

	@Override
	public void sysDialog(final SysInput.ClickEvent event,final String title,
			final String text,final String ok,final String cancel) {
		if (Lwjgl3Game.isMacOS()) {
			return;
		}
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {
				int optType = JOptionPane.OK_CANCEL_OPTION;
				int msgType = cancel == null ? JOptionPane.INFORMATION_MESSAGE
						: JOptionPane.QUESTION_MESSAGE;
				Object[] options = (cancel == null) ? new Object[] { ok }
						: new Object[] { ok, cancel };
				Object defOption = (cancel == null) ? ok : cancel;
				int result = JOptionPane.showOptionDialog(null, text, title,
						optType, msgType, null, options, defOption);
				if (result == 0) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});
	}

	public LGame getGame() {
		return game;
	}

}
