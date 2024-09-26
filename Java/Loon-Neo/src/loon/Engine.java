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
package loon;

import loon.action.behaviors.BehaviorBuilder;
import loon.events.EventActionT;
import loon.utils.reply.Emitter;

public final class Engine extends PlayerUtils implements EventActionT<Screen>, LRelease {

	private static Engine instance;
	
	private Engine() {
	}

	public static void freeStatic() {
		instance = null;
	}

	public static final Engine get() {
		synchronized (Engine.class) {
			if (instance == null) {
				instance = new Engine();
			}
		}
		return instance;
	}

	private Screen _mainScreen;

	private Screen _menuScreen;

	private Screen _introScreen;

	private Screen _loadingScreen;

	private Screen _gameScreen;

	private BehaviorBuilder<Screen> _builder;

	private Emitter<Screen> _emitter;

	public BehaviorBuilder<Screen> getBehaviorBuilder(Screen s) {
		_builder = BehaviorBuilder.begin(s);
		return _builder;
	}

	public Emitter<Screen> getEmitter() {
		_emitter = new Emitter<Screen>();
		return _emitter;
	}

	@Override
	public void update(Screen obj) {
		if (_emitter != null) {
			_emitter.update(obj);
		}
		if (_builder != null) {
			_builder.tick();
		}
	}

	public int getWidth() {
		return getProcess().getWidth();
	}

	public int getHeight() {
		return getProcess().getHeight();
	}

	public LGame getGame() {
		return LSystem.base();
	}

	public LProcess getProcess() {
		return LSystem.getProcess();
	}

	public Engine gotoMainScreen() {
		getProcess().setScreen(_mainScreen);
		return this;
	}

	public Engine gotoMenuScreen() {
		getProcess().setScreen(_menuScreen);
		return this;
	}

	public Engine gotoGameScreen() {
		getProcess().setScreen(_gameScreen);
		return this;
	}

	public Engine gotoIntroScreen() {
		getProcess().setScreen(_introScreen);
		return this;
	}

	public Engine gotoLoadingScreen() {
		getProcess().setScreen(_loadingScreen);
		return this;
	}

	public Screen getMainScreen() {
		return _mainScreen;
	}

	public Engine setMainScreen(Screen m) {
		this._mainScreen = m;
		return this;
	}

	public Screen getIntroScreen() {
		return _introScreen;
	}

	public Engine setIntroScreen(Screen i) {
		this._introScreen = i;
		return this;
	}

	public Screen getMenuScreen() {
		return _menuScreen;
	}

	public Engine setMenuScreen(Screen m) {
		this._menuScreen = m;
		return this;
	}

	public Screen getGameScreen() {
		return _gameScreen;
	}

	public Engine setGameScreen(Screen g) {
		this._gameScreen = g;
		return this;
	}

	public Screen getLoadingScreen() {
		return _loadingScreen;
	}

	public Engine setLoadingScreen(Screen l) {
		this._loadingScreen = l;
		return this;
	}

	@Override
	public void close() {
		if (_introScreen != null) {
			_introScreen.close();
		}
		if (_menuScreen != null) {
			_menuScreen.close();
		}
		if (_gameScreen != null) {
			_gameScreen.close();
		}
		if (_loadingScreen != null) {
			_loadingScreen.close();
		}
		if (_mainScreen != null) {
			_mainScreen.close();
		}
		if (_emitter != null) {
			_emitter.close();
		}
		if (_builder != null) {
			_builder.close();
		}
	}

}
