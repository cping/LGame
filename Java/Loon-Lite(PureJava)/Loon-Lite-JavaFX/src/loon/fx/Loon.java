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

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.event.KeyMake;
import loon.event.SysInput;

public class Loon extends Application implements Platform {

	private JavaFXGame game;

	private Scene fxScene;

	public Loon(LSetting config) {
		this.game = new JavaFXGame(this, config);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group group = new Group();
		group.getChildren().add(game.gameCanvas);
		this.fxScene = new Scene(group, game.setting.width_zoom, game.setting.height_zoom);
		primaryStage.setTitle(game.setting.appName);
		primaryStage.setScene(fxScene);
		primaryStage.show();
	}

	public static void register(LSetting setting, LazyLoading.Data lazy) {
		register(setting, null, lazy);
	}

	public static void register(LSetting setting, Stage stage, LazyLoading.Data lazy) {
		Loon plat = new Loon(setting);
		plat.game.register(lazy.onScreen());
		plat.game.reset();
		if (System.getProperty("javafx.platform") == null) {
			System.setProperty("javafx.platform", "Desktop");
		}
		String[] args = plat.game.setting.args;
		if (stage == null) {
			launch(args);
		} else {
			try {
				plat.start(stage);
			} catch (Throwable cause) {
				System.out.println("Register Exception:");
				cause.printStackTrace();
				System.out.println("Loon will now exit");
				System.exit(-1);
			}
		}
	}

	@Override
	public int getContainerWidth() {
		return fxScene == null ? game.setting.width_zoom : (int) fxScene.getWidth();
	}

	@Override
	public int getContainerHeight() {
		return fxScene == null ? game.setting.height_zoom : (int) fxScene.getHeight();
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

	@Override
	public LGame getGame() {
		return game;
	}

}
