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

import loon.LSetting;
import loon.LazyLoading;
import loon.html5.gwt.GWTGame.Config;
import loon.html5.gwt.soundmanager2.SoundManager;
import loon.html5.gwt.preloader.Preloader;
import loon.html5.gwt.preloader.Preloader.PreloaderCallback;
import loon.html5.gwt.preloader.Preloader.PreloaderState;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class Loon implements EntryPoint, LazyLoading {

	public interface LoadingListener {

		public void beforeSetup();

		public void afterSetup();
	}

	protected LoadingListener loadingListener;
	protected Preloader preloader;

	protected Panel root;

	protected static Loon self;
	private LSetting setting;

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
		Config config = null;
		if (this.setting instanceof Config) {
			config = (Config) this.setting;
		} else {
			config = new Config();
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
						final PreloaderCallback callback = getPreloaderCallback();
						preloader = createPreloader();
						preloader.preload("assets.txt",
								new PreloaderCallback() {
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
					}

					@Override
					public void ontimeout(String status, String errorType) {
						System.err.println("SoundManager:" + status + " "
								+ errorType);
					}

				});
	}

	void mainLoop() {
		this.resources = new GWTResources(preloader);
		this.createGame().start();
	}

	public abstract void onMain();

	public String getPreloaderBaseURL() {
		return GWT.getHostPageBaseURL() + "assets/";
	}

	public Preloader createPreloader() {
		return new Preloader(getPreloaderBaseURL());
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
		return this.game = new GWTGame(this, root, (Config) this.setting);
	}

	protected GWTGame getGame() {
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

}
