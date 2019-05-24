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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.control.TextInputDialog;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import loon.LGame;
import loon.LSetting;
import loon.LazyLoading;
import loon.Platform;
import loon.event.KeyMake;
import loon.event.SysInput;
import loon.fx.JavaFXGame.JavaFXSetting;
import loon.utils.StringUtils;

public class Loon extends Application implements Platform {

	private final static class JavaFXInit {

		private Loon app;

		private LSetting appSetting;

		private LazyLoading.Data lazyData;

		public void start(Stage stage) throws Exception {
			Rectangle2D rect = null;
			if (appSetting.fullscreen && JavaFXGame.isJavaFXDesktop()) {
				rect = Screen.getPrimary().getBounds();
			} else {
				rect = Screen.getPrimary().getVisualBounds();
			}
			// 如果javafx在android或ios上跑强制全屏
			String property = JavaFXGame.getJavaFXProperty();
			if (property.indexOf("android") != -1 || property.indexOf("ios") != -1 || appSetting.fullscreen) {
				appSetting.width_zoom = (int) rect.getWidth();
				appSetting.height_zoom = (int) rect.getHeight();
			}
			app = new Loon(appSetting);
			app.game.register(lazyData.onScreen());
			app.game.reset();
			app.start(stage);
		}

		public void stop() throws Exception {
			if (app != null) {
				app.stop();
			}
		}
	}

	public static class GameApp extends Application {

		private static JavaFXInit initData;

		@Override
		public void start(Stage stage) throws Exception {
			initData.start(stage);
		}

		@Override
		public void stop() throws Exception {
			super.stop();
			initData.stop();
		}

		public static void register(LSetting setting, LazyLoading.Data lazy) {
			initData = new JavaFXInit();
			initData.lazyData = lazy;
			initData.appSetting = setting;
			launch(GameApp.class, setting.args);
		}

	}

	private DoubleProperty scaledWidth = new SimpleDoubleProperty();
	private DoubleProperty scaledHeight = new SimpleDoubleProperty();
	private DoubleProperty scaleRatioX = new SimpleDoubleProperty();
	private DoubleProperty scaleRatioY = new SimpleDoubleProperty();

	private double windowBorderWidth = 0d;
	private double windowBorderHeight = 0d;

	protected JavaFXGame game;

	protected Scene fxScene;

	public Loon(LSetting setting) {
		this.game = new JavaFXGame(this, setting);
	}

	@Override
	public void stop() throws Exception {
		this.game.shutdown();
	}

	public void start(Stage primaryStage) throws Exception {

		float newWidth = game.setting.getShowWidth();
		float newHeight = game.setting.getShowHeight();

		Group group = new Group();

		Canvas canvas = game.getFxCanvas();

		if (canvas == null) {
			canvas = new Canvas(newWidth, newHeight);
		}
		canvas.setCache(true);
		canvas.setCacheHint(CacheHint.SPEED);
		GraphicsContext ctx = canvas.getGraphicsContext2D();

		Paint paint = ctx.getFill();
		ctx.setFill(Color.BLACK);
		ctx.fillRect(0, 0, newWidth, newHeight);
		ctx.setFill(paint);

		group.getChildren().add(canvas);

		primaryStage.setScene(createScene(group, newWidth, newHeight));
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent e) {
				game.shutdown();
			}
		});
		primaryStage.setTitle(game.setting.appName);
		if (game.setting instanceof JavaFXSetting) {
			JavaFXSetting fxsetting = ((JavaFXSetting) game.setting);
			primaryStage.setResizable(fxsetting.resizable);
			if (fxsetting.iconPaths != null) {
				String[] paths = fxsetting.iconPaths;
				for (String path : paths) {
					if (!StringUtils.isEmpty(path)) {
						try {
							JavaFXImage img = (JavaFXImage) game.assets().getImageSync(path);
							if (img != null) {
								primaryStage.getIcons().add(img.fxImage());
							}
						} catch (Exception e) {
							systemLog(e.getMessage());
						}
					}
				}
			}
		}
		if (game.setting.fullscreen && game.isDesktop()) {
			primaryStage.setFullScreen(true);
			primaryStage.setFullScreenExitHint("");
		}
		primaryStage.show();

		windowBorderWidth = primaryStage.getWidth() - scaledWidth.getValue();
		windowBorderHeight = primaryStage.getHeight() - scaledHeight.getValue();

		if (windowBorderHeight < 0.5 && System.getProperty("os.name").contains("nux")) {
			windowBorderHeight = 35.0d;
		}

		scaledWidth.bind(primaryStage.widthProperty().add(windowBorderWidth));
		scaledHeight.bind(primaryStage.heightProperty().add(windowBorderHeight));

		scaleRatioX.bind(scaledWidth.divide(newWidth));
		scaleRatioY.bind(scaledHeight.divide(newHeight));
		fxScene.getRoot().prefWidth(scaledWidth.doubleValue());
		fxScene.getRoot().prefHeight(scaledHeight.doubleValue());

		Scale scale = new Scale();
		scale.xProperty().bind(scaleRatioX);
		scale.yProperty().bind(scaleRatioY);
		fxScene.getRoot().getTransforms().setAll(scale);

	}

	protected Scene createScene(Group group, float width, float height) {
		LSetting setting = game.setting;
		Rectangle2D rect = null;
		if (setting.fullscreen && game.isDesktop()) {
			rect = Screen.getPrimary().getBounds();
		} else {
			rect = Screen.getPrimary().getVisualBounds();
		}
		if ((width > rect.getWidth()) || (height > rect.getHeight())) {
			float extraMargin = 25f;
			float ratio = width / height;
			for (int i = 0; i < rect.getWidth(); i++) {
				if (width / ratio <= rect.getHeight()) {
					width = i - extraMargin;
					height = i / ratio;
					break;
				}
			}
		}
		this.fxScene = new Scene(group, width, height);
		scaledWidth.set(width);
		scaledHeight.set(height);
		scaleRatioX.set(scaledWidth.getValue() / setting.getShowWidth());
		scaleRatioY.set(scaledHeight.getValue() / setting.getShowHeight());
		return fxScene;
	}

	public static void systemLog(String message) {
		System.out.println(message);
	}

	@Override
	public int getContainerWidth() {
		return fxScene == null ? game.setting.getShowWidth() : (int) fxScene.getWidth();
	}

	@Override
	public int getContainerHeight() {
		return fxScene == null ? game.setting.getShowHeight() : (int) fxScene.getHeight();
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
