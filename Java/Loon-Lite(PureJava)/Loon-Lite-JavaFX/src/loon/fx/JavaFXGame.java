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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Platform;
import loon.Save;
import loon.Support;
import loon.event.InputMake;

public class JavaFXGame extends LGame {

	public static class JavaFXSetting extends LSetting {

	}

	protected final Canvas gameCanvas;
	private final JavaFXAppliaction app;
	private final JavaFXAccelerometer accelerometer;
	private final JavaFXSave save;
	private final JavaFXGraphics graphics;
	private final JavaFXAssets assets;
	private final ExecutorService pool = Executors.newFixedThreadPool(4);
	private final Support support;

	private final JavaFXLog log;
	private final Asyn asyn;

	private final long start = System.nanoTime();

	public JavaFXGame(Platform plat, LSetting config) {
		super(config, plat);
		this.app = new JavaFXAppliaction(this);
		this.assets = new JavaFXAssets(this);
		this.log = new JavaFXLog();
		this.support = new NativeSupport();
		this.save = new JavaFXSave(log, config.appName);
		this.accelerometer = new JavaFXAccelerometer();
		this.graphics = new JavaFXGraphics(this);
		this.asyn = new JavaFXAsyn(pool, log, frame);
		this.gameCanvas = new Canvas(setting.getShowWidth(), setting.getShowHeight());
	}

	public void reset() {

	}

	public int getWidth() {
		return app.getWidth();
	}

	public int getHeight() {
		return app.getHeight();
	}

	@Override
	public Type type() {
		return Type.JAVAFX;
	}

	@Override
	public double time() {
		return System.currentTimeMillis();
	}

	@Override
	public int tick() {
		return (int) ((System.nanoTime() - start) / 1000000L);
	}

	@Override
	public void openURL(final String url) {

		invokeAsync(new Runnable() {

			@Override
			public void run() {

				final Scene scene = new Scene(new Group());

				final Stage newStage = new Stage();
				final WebView view = new WebView();
				final WebEngine engine = view.getEngine();

				final ScrollPane scrollPane = new ScrollPane();
				scrollPane.setContent(view);

				engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

					@Override
					public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {

						if (newState == Worker.State.SUCCEEDED) {
							newStage.setTitle(engine.getLocation());
						}
						if (newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
							newStage.close();
						}

					}

				});
				engine.load(url);

				scene.setRoot(scrollPane);

				newStage.setScene(scene);
				newStage.show();

			}
		});

	}

	@Override
	public Assets assets() {
		return this.assets;
	}

	@Override
	public Asyn asyn() {
		return this.asyn;
	}

	@Override
	public Graphics graphics() {
		return this.graphics;
	}

	@Override
	public InputMake input() {
		return null;
	}

	@Override
	public Log log() {
		return this.log;
	}

	@Override
	public Save save() {
		return this.save;
	}

	@Override
	public Accelerometer accel() {
		return this.accelerometer;
	}

	@Override
	public Support support() {
		return support;
	}

	@Override
	public boolean isMobile() {
		return false;
	}

	@Override
	public boolean isDesktop() {
		return true;
	}

}
