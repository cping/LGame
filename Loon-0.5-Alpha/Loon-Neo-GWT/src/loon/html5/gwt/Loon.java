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
package loon.html5.gwt;

import loon.LGame;
import loon.LSetting;
import loon.LSystem;
import loon.LazyLoading;
import loon.Platform;
import loon.event.KeyMake;
import loon.event.SysInput;
import loon.event.Updateable;
import loon.html5.gwt.GWTGame.GWTSetting;
import loon.html5.gwt.soundmanager2.SoundManager;
import loon.html5.gwt.preloader.LocalAssetResources;
import loon.html5.gwt.preloader.Preloader;
import loon.html5.gwt.preloader.Preloader.PreloaderCallback;
import loon.html5.gwt.preloader.Preloader.PreloaderState;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class Loon implements Platform, EntryPoint, LazyLoading {

	static final class JsMap extends JavaScriptObject {
		protected JsMap() {
		}

		public native String getKey() /*-{
			return this["k"];
		}-*/;

		public native String getValue() /*-{
			return this["v"];
		}-*/;
	}

	public interface LoadingListener {

		public void beforeSetup();

		public void afterSetup();
	}

	protected LoadingListener loadingListener;

	protected Preloader preloader;

	protected Panel root;

	protected static Loon self;

	private LSetting setting;

	private GWTSetting config;

	private LazyLoading.Data mainData;

	protected GWTResources resources;

	public String getBaseUrl() {
		return preloader.baseUrl;
	}

	public Preloader getPreloader() {
		return preloader;
	}

	@Override
	public void onModuleLoad() {
		Loon.self = this;
		onMain();
		if (this.setting instanceof GWTSetting) {
			config = (GWTSetting) this.setting;
		} else {
			config = new GWTSetting();
			config.copy(this.setting);
		}
		this.setting = config;

		Element element = Document.get().getElementById(config.rootId);
		if (element == null) {
			VerticalPanel panel = new VerticalPanel();
			panel.setWidth("" + config.width + "px");
			panel.setHeight("" + config.height + "px");
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			RootPanel.get().add(panel);
			RootPanel.get().setWidth("" + config.width + "px");
			RootPanel.get().setHeight("" + config.height + "px");
			this.root = panel;
		} else {
			VerticalPanel panel = new VerticalPanel();
			panel.setWidth("" + config.width + "px");
			panel.setHeight("" + config.height + "px");
			panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			element.appendChild(panel.getElement());
			this.root = panel;
		}

		SoundManager.init(GWT.getModuleBaseURL(), 9, config.preferFlash,
				new SoundManager.SoundManagerCallback() {

					@Override
					public void onready() {
						boolean internalExist = config.internalRes != null;
						boolean jsres = config.jsloadRes || internalExist;
						if (jsres) {
							final LocalAssetResources localRes = internalExist ? config.internalRes
									: new LocalAssetResources();
							ScriptInjector
									.fromUrl("assets/resources.js")
									.setCallback(
											new Callback<Void, Exception>() {
												public void onFailure(
														Exception reason) {
													consoleLog("resources script load failed.");
												}

												public void onSuccess(
														Void result) {
													JsArray<JsMap> list = loadJavaScriptResources(
															localRes).cast();

													int size = list.length();

													for (int i = 0; i < size; i++) {
														JsMap res = list.get(i);
														String key = res
																.getKey();
														String value = res
																.getValue();
														String ext = LSystem
																.getExtension(key);

														if (LSystem.isText(ext)) {
															localRes.putText(
																	key, value);
														} else if (LSystem
																.isImage(ext)) {

															localRes.putImage(
																	key, value);
														} else if (LSystem
																.isAudio(ext)) {
															// noop
														} else {
															localRes.putBlobString(
																	key, value);
														}
													}

													localRes.commit();
													loadResources(
															getPreloaderCallback(),
															localRes);

												}
											})
									.setWindow(ScriptInjector.TOP_WINDOW)
									.inject();
						} else {
							loadResources(getPreloaderCallback(), null);
						}
					}

					@Override
					public void ontimeout(String status, String errorType) {
						consoleLog("SoundManager:" + status + " " + errorType);
					}

				});

	}

	private native JsArray<JavaScriptObject> loadJavaScriptResources(
			LocalAssetResources res) /*-{
		return new $wnd.LocalResources().running(res);
	}-*/;

	Preloader loadResources(final PreloaderCallback callback,
			final LocalAssetResources localRes) {
		this.preloader = createPreloader(localRes);
		this.preloader.preload("assets.txt", new PreloaderCallback() {
			@Override
			public void error(String file) {
				callback.error(file);
			}

			@Override
			public void update(PreloaderState state) {
				callback.update(state);
				if (state.hasEnded()) {
					getRootPanel().clear();
					if (loadingListener != null) {
						loadingListener.beforeSetup();
					}
					mainLoop();
					if (loadingListener != null) {
						loadingListener.afterSetup();
					}
				}
			}
		});
		return this.preloader;
	}

	void mainLoop() {
		this.resources = new GWTResources(preloader);
		this.createGame().start();
	}

	public abstract void onMain();

	public String getPreloaderBaseURL() {
		return GWT.getHostPageBaseURL() + "assets/";
	}

	public Preloader createPreloader(LocalAssetResources res) {
		return new Preloader(getPreloaderBaseURL(), res);
	}

	public PreloaderCallback getPreloaderCallback() {
		final Panel preloaderPanel = new VerticalPanel();
		preloaderPanel.setStyleName("loon-preloader");

		final Image logo = new Image(GWT.getModuleBaseURL() + "logo.png");
		logo.setStyleName("logo");
		preloaderPanel.add(logo);
		final Panel meterPanel = new SimplePanel();
		meterPanel.setStyleName("loon-meter");
		meterPanel.addStyleName("red");
		final InlineHTML meter = new InlineHTML();
		final Style meterStyle = meter.getElement().getStyle();
		meterStyle.setWidth(0, Unit.PCT);
		meterPanel.add(meter);
		preloaderPanel.add(meterPanel);
		getRootPanel().add(preloaderPanel);
		return new PreloaderCallback() {

			@Override
			public void error(String file) {
				System.out.println("error: " + file);
			}

			@Override
			public void update(PreloaderState state) {
				meterStyle.setWidth(100f * state.getProgress(), Unit.PCT);
			}

		};
	}

	private GWTGame game;

	public Panel getRootPanel() {
		return root;
	}

	protected GWTGame createGame() {
		return this.game = new GWTGame(this, root, (GWTSetting) this.setting);
	}

	public LGame getGame() {
		return game;
	}

	protected GWTGame initialize() {
		if (game != null) {
			game.register(mainData.onScreen());
		}
		return game;
	}

	public void register(LSetting s, LazyLoading.Data data) {
		this.setting = s;
		this.mainData = data;
	}

	public int getContainerWidth() {
		return Window.getClientWidth();
	}

	public int getContainerHeight() {
		return Window.getClientHeight();
	}

	public Orientation getOrientation() {
		if (getContainerHeight() > getContainerWidth()) {
			return Orientation.Portrait;
		} else {
			return Orientation.Landscape;
		}
	}

	public void close() {
		closeImpl();
	}

	private static native void closeImpl()
	/*-{
		$wnd.close();
	}-*/;

	public native static void consoleLog(String message) /*-{
		console.log("GWT: " + message);
	}-*/;

	@Override
	public void sysText(final SysInput.TextEvent event,
			final KeyMake.TextType textType, final String label,
			final String initVal) {
		if (game == null) {
			event.cancel();
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {

				String result = Window.prompt(label, initVal);
				if (game.input() instanceof GWTInputMake) {
					((GWTInputMake) game.input()).emitFakeMouseUp();
				}
				if (result != null) {
					event.input(result);
				} else {
					event.cancel();
				}
			}
		});

	}

	@Override
	public void sysDialog(final SysInput.ClickEvent event, final String title,
			final String text, final String ok, final String cancel) {
		if (game == null) {
			event.cancel();
			return;
		}
		LSystem.load(new Updateable() {

			@Override
			public void action(Object a) {
				boolean result;
				if (cancel != null)
					result = Window.confirm(text);
				else {
					Window.alert(text);
					result = true;
				}
				if (game.input() instanceof GWTInputMake) {
					((GWTInputMake) game.input()).emitFakeMouseUp();
				}
				if (result) {
					event.clicked();
				} else {
					event.cancel();
				}
			}
		});
	}

}
