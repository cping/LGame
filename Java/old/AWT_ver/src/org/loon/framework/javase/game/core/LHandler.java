package org.loon.framework.javase.game.core;

import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import org.loon.framework.javase.game.GameDeploy;
import org.loon.framework.javase.game.GameScene;
import org.loon.framework.javase.game.GameView;
import org.loon.framework.javase.game.core.graphics.LColor;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.Screen;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.timer.LTimerContext;

/**
 * Copyright 2008 - 2011
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
 * @email ceponline ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LHandler implements MouseListener, MouseMotionListener,
		KeyListener, FocusListener {

	private GameScene scene;

	private GameDeploy deploy;

	private final LinkedList<Screen> screens;

	private Screen currentControl;

	private boolean isInstance;

	private int id, width, height;

	private Point textOrigin;

	private LTransition transition;

	private boolean waitTransition;

	public LHandler(GameScene scene, int width, int height) {
		this.width = width;
		this.height = height;
		this.scene = scene;
		this.textOrigin = new Point(0, 0);
		this.screens = new LinkedList<Screen>();
	}

	public LHandler(int width, int height) {
		this.width = width;
		this.height = height;
		this.textOrigin = new Point(0, 0);
		this.screens = new LinkedList<Screen>();
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public int getRepaintMode() {
		if (isInstance) {
			return currentControl.getRepaintMode();
		}
		return Screen.SCREEN_CANVAS_REPAINT;
	}

	public Image getBackground() {
		if (isInstance) {
			return currentControl.getBackground();
		}
		return null;
	}

	public boolean next() {
		if (isInstance) {
			if (currentControl.next()) {
				return true;
			}
		}
		return false;
	}

	public void calls() {
		if (isInstance) {
			currentControl.callEvents();
		}
	}

	public void runTimer(LTimerContext context) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {

					switch (transition.code) {
					default:
						if (!currentControl.isOnLoadComplete()) {
							transition.update(context.timeSinceLastUpdate);
						}
						break;
					case 1:
						if (!transition.completed()) {
							transition.update(context.timeSinceLastUpdate);
						} else {
							endTransition();
						}
						break;
					}
				}
			} else {
				currentControl.runTimer(context);
				return;
			}
		}
	}

	public void draw(LGraphics g) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {
					if (transition.isDisplayGameUI) {
						currentControl.createUI(g);
					}
					switch (transition.code) {
					default:
						if (!currentControl.isOnLoadComplete()) {
							transition.draw(g);
						}
						break;
					case 1:
						if (!transition.completed()) {
							transition.draw(g);
						}
						break;
					}
				}
			} else {
				currentControl.createUI(g);
				return;
			}
		}
	}

	public final void setTransition(LTransition t) {
		this.transition = t;
	}

	public final LTransition getTransition() {
		return this.transition;
	}

	private final void startTransition() {
		if (transition != null) {
			waitTransition = true;
			if (isInstance) {
				currentControl.setLock(true);
			}
		}
	}

	private final void endTransition() {
		if (transition != null) {
			switch (transition.code) {
			default:
				waitTransition = false;
				transition.dispose();
				break;
			case 1:
				if (transition.completed()) {
					waitTransition = false;
					transition.dispose();
				}
				break;
			}
			if (isInstance) {
				currentControl.setLock(false);
			}
		} else {
			waitTransition = false;
		}
	}

	public float getX() {
		if (isInstance) {
			return currentControl.getX();
		}
		return 0;
	}

	public float getY() {
		if (isInstance) {
			return currentControl.getY();
		}
		return 0;
	}

	public synchronized Screen getScreen() {
		return currentControl;
	}

	public void runFirstScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getFirst();
			if (o != currentControl) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getLast();
			if (o != currentControl) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentControl == screens.get(i)) {
					if (i - 1 > -1) {
						setScreen((Screen) screens.get(i - 1), false);
						return;
					}
				}
			}
		}
	}

	public void runNextScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentControl == screens.get(i)) {
					if (i + 1 < size) {
						setScreen((Screen) screens.get(i + 1), false);
						return;
					}
				}
			}
		}
	}

	public void runIndexScreen(int index) {
		int size = screens.size();
		if (size > 0 && index > -1 && index < size) {
			Object o = screens.get(index);
			if (currentControl != o) {
				setScreen((Screen) screens.get(index), false);
			}
		}
	}

	public void addScreen(final Screen screen) {
		if (screen == null) {
			throw new RuntimeException("Cannot create a [IScreen] instance !");
		}
		screens.add(screen);
	}

	public LinkedList<Screen> getScreens() {
		return screens;
	}

	public int getScreenCount() {
		return screens.size();
	}

	public void setScreen(final Screen screen) {
		setScreen(screen, true);
	}

	private void setScreen(final Screen screen, boolean put) {
		synchronized (this) {
			if (screen == null) {
				this.isInstance = false;
				throw new RuntimeException(
						"Cannot create a [Screen] instance !");
			}
			if (currentControl != null) {
				setTransition(screen.onTransition());
			} else {
				LTransition transition = screen.onTransition();
				if (transition == null) {
					switch (LSystem.getRandomBetWeen(0, 3)) {
					case 0:
						transition = LTransition.newFadeIn();
						break;
					case 1:
						transition = LTransition.newArc();
						break;
					case 2:
						transition = LTransition.newSplitRandom(LColor.black);
						break;
					case 3:
						transition = LTransition.newCrossRandom(LColor.black);
						break;
					}
				}
				setTransition(transition);
			}
		}
		screen.setOnLoadState(false);
		if (currentControl == null) {
			currentControl = screen;
		} else {
			synchronized (currentControl) {
				currentControl.destroy();
				currentControl = screen;
			}
		}
		this.isInstance = true;
		if (screen instanceof EmulatorListener) {
			if (deploy.getView() instanceof GameView) {
				GameView l2d = (GameView) deploy.getView();
				l2d.update();
				l2d.setEmulatorListener((EmulatorListener) screen);
			}
		} else {
			if (deploy.getView() instanceof GameView) {
				((GameView) deploy.getView()).setEmulatorListener(null);
			}
		}
		startTransition();
		screen.onCreate(LSystem.screenRect.width, LSystem.screenRect.height);
		Thread load = null;
		try {
			load = new Thread() {
				public void run() {
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
					}
					screen.setClose(false);
					screen.onLoad();
					screen.setOnLoadState(true);
					screen.onLoaded();
					endTransition();
				}
			};
			load.setPriority(Thread.NORM_PRIORITY);
			load.start();
		} catch (Exception ex) {
			throw new RuntimeException(currentControl.getName() + " onLoad:"
					+ ex.getMessage());
		} finally {
			load = null;
		}
		if (put) {
			screens.add(screen);
		}
		Thread.yield();
	}

	public void keyPressed(KeyEvent e) {
		if (isInstance) {
			currentControl.keyPressed(e);
		}
	}

	public void keyReleased(KeyEvent e) {
		if (isInstance) {
			currentControl.keyReleased(e);
		}
	}

	public void keyTyped(KeyEvent e) {
		if (isInstance) {
			currentControl.keyTyped(e);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (isInstance) {
			currentControl.mouseClicked(e);
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (isInstance) {
			currentControl.mouseEntered(e);
		}
	}

	public void mouseExited(MouseEvent e) {
		if (isInstance) {
			currentControl.mouseExited(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isInstance) {
			if (deploy.getEmulatorButtons() != null) {
				deploy.getEmulatorButtons().hit(e.getX(), e.getY());
			}
			currentControl.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isInstance) {
			if (deploy.getEmulatorButtons() != null) {
				deploy.getEmulatorButtons().unhit();
			}
			currentControl.mouseReleased(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (isInstance) {
			currentControl.mouseDragged(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (isInstance) {
			currentControl.mouseMoved(e);
		}
	}

	public void focusGained(FocusEvent e) {
		if (isInstance) {
			currentControl.focusGained(e);
		}
	}

	public void focusLost(FocusEvent e) {
		if (isInstance) {
			currentControl.focusLost(e);
		}
	}

	public GameScene getScene() {
		return scene;
	}

	public void setScene(GameScene scene) {
		this.scene = scene;
	}

	public GameDeploy getDeploy() {
		return deploy;
	}

	public void setDeploy(GameDeploy deploy) {
		this.deploy = deploy;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Window getWindow() {
		return scene.getWindow();
	}

	public Point getTextOrigin() {
		return textOrigin;
	}

	public Image getImage() {
		GameView view = deploy.getView();
		if (view != null) {
			Image tmp = view.getAwtImage();
			if (tmp != null) {
				return tmp;
			} else {
				return null;
			}
		}
		return null;
	}

	public void destroy() {
		endTransition();
		if (isInstance) {
			isInstance = false;
			if (currentControl != null) {
				currentControl.destroy();
				currentControl = null;
			}
			LImage.disposeAll();
		}
	}

}
