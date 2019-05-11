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
package loon.fx;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.event.KeyMake;
import loon.event.SysInput;

public class Loon implements Platform {

	private JavaFXGame game;

	public Loon(LSetting config) {
		this.game = new JavaFXGame(this, config);
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		Loon plat = new Loon(setting);
		plat.game.register(lazy.onScreen());
		plat.game.reset();
	}

	@Override
	public int getContainerWidth() {
		return game.getWidth();
	}

	@Override
	public int getContainerHeight() {
		return game.getHeight();
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
	public void sysText(final SysInput.TextEvent event, final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {

				TextInputDialog dialog = new TextInputDialog(initVal);
				dialog.setTitle(label);
				dialog.setHeaderText(label);
				dialog.setContentText(label);
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					event.input(result.get());
				} else {
					event.cancel();
				}

			}
		});
	}

	@Override
	public void sysDialog(final SysInput.ClickEvent event, String title, String text, String ok, String cancel) {
		if (game == null) {
			event.cancel();
			return;
		}
		game.invokeAsync(new Runnable() {

			@Override
			public void run() {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(title);
				alert.setHeaderText(text);
				alert.setContentText(text);

				ButtonType okBtn = new ButtonType(ok);
				ButtonType cancelBtn = new ButtonType(cancel);
				alert.getButtonTypes().setAll(okBtn, cancelBtn);

				Optional<ButtonType> result = alert.showAndWait();

				if (result.get() == okBtn) {
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
