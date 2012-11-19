package org.loon.framework.javase.game;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.List;

import org.loon.framework.javase.game.core.EmulatorButtons;
import org.loon.framework.javase.game.core.EmulatorListener;
import org.loon.framework.javase.game.core.LHandler;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.Screen;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1
 */

public class GameDeploy {

	private int left, top;

	private Window screen;

	private LHandler game;

	private GameView view;

	public GameDeploy(LHandler game) {
		LSystem.gc();
		game.setDeploy(this);
		this.game = game;
		this.screen = game.getScene().getWindow();
		initView(screen);
	}

	public EmulatorButtons getEmulatorButtons() {
		return view.getEmulatorButtons();
	}

	public void setEmulatorListener(EmulatorListener emulator) {
		view.setEmulatorListener(emulator);
	}

	public EmulatorListener getEmulatorListener() {
		return view.getEmulatorListener();
	}

	public void setShowMemory(boolean memory) {
		view.setShowMemory(memory);
	}

	public void setLogo(Image logo) {
		view.setLogo(logo);
	}

	public void setLogo(String fileName) {
		view.setLogo(fileName);
	}

	public void mainLoop() {
		view.mainLoop();
	}

	public void setShowFPS(boolean isFPS) {
		view.setShowFPS(isFPS);
	}

	public void setFPS(long frames) {
		view.setFPS(frames);
	}

	public long getCurrentFPS() {
		return view.getCurrentFPS();
	}

	public void runFirstScreen() {
		if (game != null) {
			game.runFirstScreen();
		}
	}

	public void runLastScreen() {
		if (game != null) {
			game.runLastScreen();
		}
	}

	public void runIndexScreen(int index) {
		if (game != null) {
			game.runIndexScreen(index);
		}
	}

	public void runPreviousScreen() {
		if (game != null) {
			game.runPreviousScreen();
		}
	}

	public void runNextScreen() {
		if (game != null) {
			game.runNextScreen();
		}
	}

	public void addScreen(final Screen screen) {
		if (game != null) {
			game.addScreen(screen);
		}
	}

	public List<?> getScreens() {
		return game.getScreens();
	}

	public int getScreenCount() {
		return game.getScreenCount();
	}

	public LHandler getGame() {
		return this.game;
	}

	public void setScreen(Screen screen) {
		if (screen == null) {
			throw new RuntimeException("Cannot create a [IScreen] instance !");
		}
		screen.setupHandler(game);
		this.game.setScreen(screen);
	}

	private void initView(final Window screen) {
		view = new GameView(game);
		view.startPaint();
		left = screen.getInsets().left;
		top = screen.getInsets().top;
		screen.add((Canvas) view);
		screen.pack();
		screen.invalidate();
		screen.validate();
		view.createScreen();
		screen.doLayout();
		view.requestFocus();
	}

	public synchronized boolean addComponent(Component component) {
		return addComponent(0, 0, component);
	}

	public synchronized boolean addComponent(int x, int y, Component component) {
		return addComponent(x, y, component.getWidth(), component.getHeight(),
				component);
	}

	public synchronized boolean addComponent(int x, int y, int w, int h,
			Component component) {
		if (component == null) {
			return false;
		}
		Component[] components = screen.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] == component) {
				return false;
			}
		}
		if (!LSystem.isApplet
				&& game.getScene().getIScene().getCodeType() == GameModel.Awt) {
			component.setBounds(x + left, y + top, w, h);
		} else {
			component.setBounds(x, y, w, h);
		}
		screen.setLayout(null);
		screen.add(component, 0);
		screen.repaint();
		screen.validate();
		return true;
	}

	public synchronized void removeComponent(Component component) {
		if (component == null) {
			return;
		}
		screen.remove(component);
		screen.repaint();
		screen.validate();
	}

	public synchronized void removeComponent(int index) {
		screen.remove(index);
		screen.repaint();
		screen.validate();
	}

	public Window getScreen() {
		return screen;
	}

	public GameView getView() {
		return view;
	}

	public void setGame(LHandler game) {
		this.game = game;
	}

	public void setLayout(LayoutManager layout) {
		this.screen.setLayout(layout);
	}

}
