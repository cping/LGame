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
package loon;

import java.util.ArrayList;
import java.util.LinkedList;

import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionEvent;
import loon.action.collision.GravityHandler;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.action.sprite.Sprites.SpriteListener;
import loon.canvas.LColor;
import loon.component.Desktop;
import loon.component.LComponent;
import loon.component.LLayer;
import loon.event.Drawable;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.SysInput;
import loon.event.ScreenListener;
import loon.event.SysTouch;
import loon.event.Updateable;
import loon.geom.Point.Point2i;
import loon.geom.RectBox;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.stage.PlayerUtils;
import loon.stage.RootPlayer;
import loon.stage.Stage;
import loon.stage.StageSystem;
import loon.stage.StageTransition;
import loon.utils.Scale;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public abstract class Screen extends PlayerUtils implements SysInput, LRelease, XY {

	private ArrayList<ScreenListener> screens;

	private boolean useScreenListener;

	public Screen addScreenListener(ScreenListener l) {
		if (l != null) {
			if (screens == null) {
				screens = new ArrayList<ScreenListener>(10);
			}
			screens.add(l);
		}
		useScreenListener = (screens != null && screens.size() > 0);
		return this;
	}

	public Screen removeScreenListener(ScreenListener l) {
		if (screens == null) {
			return this;
		}
		if (l != null) {
			screens.remove(l);
		}
		useScreenListener = (screens != null && screens.size() > 0);
		return this;
	}

	private ArrayList<LRelease> releases;

	public Screen putRelease(LRelease r) {
		if (releases == null) {
			releases = new ArrayList<LRelease>(10);
		}
		releases.add(r);
		return this;
	}

	public Screen putReleases(LRelease... rs) {
		if (releases == null) {
			releases = new ArrayList<LRelease>(10);
		}
		final int size = rs.length;
		for (int i = 0; i < size; i++) {
			releases.add(rs[i]);
		}
		return this;
	}

	public Screen addAction(ActionEvent e, ActionBind act) {
		ActionControl.getInstance().addAction(e, act);
		return this;
	}

	public Screen removeAction(ActionEvent e) {
		ActionControl.getInstance().removeAction(e);
		return this;
	}

	public Screen removeAction(Object tag, ActionBind act) {
		ActionControl.getInstance().removeAction(tag, act);
		return this;
	}

	public Screen removeAllActions(ActionBind act) {
		ActionControl.getInstance().removeAllActions(act);
		return this;
	}

	public float getDeltaTime() {
		return elapsedTime / 1000f;
	}

	protected static Screen StaticCurrentSceen;

	public final static byte DRAW_USER = 0;

	public final static byte DRAW_SPRITE = 1;

	public final static byte DRAW_DESKTOP = 2;

	public final class PaintOrder {

		private byte type;

		private Screen screen;

		public PaintOrder(byte t, Screen s) {
			this.type = t;
			this.screen = s;
		}

		void paint(GLEx g) {
			switch (type) {
			case DRAW_USER:
				synchronized (this) {
					screen.draw(g);
				}
				break;
			case DRAW_SPRITE:
				synchronized (this) {
					if (spriteRun) {
						sprites.createUI(g);
					} else if (spriteRun = (sprites != null && sprites.size() > 0)) {
						sprites.createUI(g);
					}
				}
				break;
			case DRAW_DESKTOP:
				synchronized (this) {
					if (desktopRun) {
						desktop.createUI(g);
					} else if (desktopRun = (desktop != null && desktop.size() > 0)) {
						desktop.createUI(g);
					}
				}
				break;
			}
		}

		void update(LTimerContext c) {
			switch (type) {
			case DRAW_USER:
				synchronized (this) {
					screen.alter(c);
				}
				break;
			case DRAW_SPRITE:
				synchronized (this) {
					spriteRun = (sprites != null && sprites.size() > 0);
					if (spriteRun) {
						sprites.update(c.timeSinceLastUpdate);
					}
				}
				break;
			case DRAW_DESKTOP:
				synchronized (this) {
					desktopRun = (desktop != null && desktop.size() > 0);
					if (desktopRun) {
						desktop.update(c.timeSinceLastUpdate);
					}
				}
				break;
			}
		}

	}

	public LGame getGame() {
		return LSystem._base;
	}

	private boolean spriteRun, desktopRun;

	private boolean fristPaintFlag;

	private boolean secondPaintFlag;

	private boolean lastPaintFlag;

	public static enum SensorDirection {
		NONE, LEFT, RIGHT, UP, DOWN;
	}

	public static interface LEvent {

		public Screen call();

	}

	public abstract void draw(GLEx g);

	public final static int SCREEN_NOT_REPAINT = 0;

	public final static int SCREEN_BITMAP_REPAINT = -1;

	public final static int SCREEN_CANVAS_REPAINT = -2;

	public final static int SCREEN_COLOR_REPAINT = -3;

	// 0.3.2版新增的简易重力控制接口
	private GravityHandler gravityHandler;

	private LColor color;

	private float lastTouchX, lastTouchY, touchDX, touchDY;

	public long elapsedTime;

	private final static boolean[] touchType, keyType;

	private int touchButtonPressed = SysInput.NO_BUTTON,
			touchButtonReleased = SysInput.NO_BUTTON;

	private int keyButtonPressed = SysInput.NO_KEY,
			keyButtonReleased = SysInput.NO_KEY;

	boolean isNext;

	private int mode, frame;

	private LTexture currentScreen;

	protected LProcess handler;

	private int width, height, halfWidth, halfHeight;

	private SensorDirection direction = SensorDirection.NONE;

	private SysInput baseInput;

	// 精灵集合
	private Sprites sprites;

	// 桌面集合
	private Desktop desktop;

	private Point2i touch = new Point2i(0, 0);

	private boolean isLoad, isLock, isClose, isTranslate, isGravity;

	private float tx, ty;

	// 首先绘制的对象
	private PaintOrder fristOrder;

	// 其次绘制的对象
	private PaintOrder secondOrder;

	// 最后绘制的对象
	private PaintOrder lastOrder;

	private PaintOrder userOrder, spriteOrder, desktopOrder;

	private ArrayList<RectBox> limits = new ArrayList<RectBox>(10);

	private boolean replaceLoading;

	private int replaceScreenSpeed = 8;

	private LTimer replaceDelay = new LTimer(0);

	private Screen replaceDstScreen;

	private EmptyObject dstPos = new EmptyObject();

	private MoveMethod replaceMethod = MoveMethod.FROM_LEFT;

	// Screen切换方式
	public static enum MoveMethod {
		FROM_LEFT, FROM_UP, FROM_DOWN, FROM_RIGHT, FROM_UPPER_LEFT, FROM_UPPER_RIGHT, FROM_LOWER_LEFT, FROM_LOWER_RIGHT, OUT_LEFT, OUT_UP, OUT_DOWN, OUT_RIGHT, OUT_UPPER_LEFT, OUT_UPPER_RIGHT, OUT_LOWER_LEFT, OUT_LOWER_RIGHT
	}

	private boolean isScreenFrom = false;

	public Screen replaceScreen(final Screen screen, MoveMethod m) {
		if (screen != null && screen != this) {
			screen.setOnLoadState(false);
			setLock(true);
			screen.setLock(true);
			this.replaceMethod = m;
			this.replaceDstScreen = screen;

			screen.setRepaintMode(SCREEN_CANVAS_REPAINT);
			switch (m) {
			case FROM_LEFT:
				dstPos.setLocation(-getWidth(), 0);
				isScreenFrom = true;
				break;
			case FROM_RIGHT:
				dstPos.setLocation(getWidth(), 0);
				isScreenFrom = true;
				break;
			case FROM_UP:
				dstPos.setLocation(0, -getHeight());
				isScreenFrom = true;
				break;
			case FROM_DOWN:
				dstPos.setLocation(0, getHeight());
				isScreenFrom = true;
				break;
			case FROM_UPPER_LEFT:
				dstPos.setLocation(-getWidth(), -getHeight());
				isScreenFrom = true;
				break;
			case FROM_UPPER_RIGHT:
				dstPos.setLocation(getWidth(), -getHeight());
				isScreenFrom = true;
				break;
			case FROM_LOWER_LEFT:
				dstPos.setLocation(-getWidth(), getHeight());
				isScreenFrom = true;
				break;
			case FROM_LOWER_RIGHT:
				dstPos.setLocation(getWidth(), getHeight());
				isScreenFrom = true;
				break;
			default:
				dstPos.setLocation(0, 0);
				isScreenFrom = false;
				break;
			}

			RealtimeProcessManager.get().addProcess(new RealtimeProcess() {

				@Override
				public void run(long time) {
					screen.onCreate(LSystem.viewSize.getWidth(),
							LSystem.viewSize.getHeight());
					screen.setClose(false);
					screen.onLoad();
					screen.setRepaintMode(SCREEN_CANVAS_REPAINT);
					screen.onLoaded();
					screen.setOnLoadState(true);
					kill();
				}
			});

			replaceLoading = true;
		}
		
		return this;
	}

	public int getReplaceScreenSpeed() {
		return replaceScreenSpeed;
	}

	public Screen setReplaceScreenSpeed(int s) {
		this.replaceScreenSpeed = s;
		return this;
	}

	public Screen setReplaceScreenDelay(long d) {
		replaceDelay.setDelay(d);
		return this;
	}

	public long getReplaceScreenDelay() {
		return replaceDelay.getDelay();
	}

	private void submitReplaceScreen() {
		if (handler != null) {
			handler.setCurrentScreen(replaceDstScreen);
		}
		replaceLoading = false;
	}

	public Screen addTouchLimit(LObject c) {
		if (c != null) {
			limits.add(c.getCollisionArea());
		}
		return this;
	}

	public Screen addTouchLimit(RectBox r) {
		if (r != null) {
			limits.add(r);
		}
		return this;
	}

	public boolean isClickLimit(GameTouch e) {
		return isClickLimit(e.x(), e.y());
	}

	public boolean isClickLimit(int x, int y) {
		if (limits.size() == 0) {
			return false;
		}
		for (RectBox rect : limits) {
			if (rect.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	protected final PaintOrder DRAW_USER_PAINT() {
		if (userOrder == null) {
			userOrder = new PaintOrder(DRAW_USER, this);
		}
		return userOrder;
	}

	protected final PaintOrder DRAW_SPRITE_PAINT() {
		if (spriteOrder == null) {
			spriteOrder = new PaintOrder(DRAW_SPRITE, this);
		}
		return spriteOrder;
	}

	protected final PaintOrder DRAW_DESKTOP_PAINT() {
		if (desktopOrder == null) {
			desktopOrder = new PaintOrder(DRAW_DESKTOP, this);
		}
		return desktopOrder;
	}

	static {
		keyType = new boolean[15];
		touchType = new boolean[15];
	}

	public Screen() {
		resetBase();
	}
	
	final void resetBase(){
		Screen.StaticCurrentSceen = this;
		this.handler = LSystem._process;
		this.width = LSystem.viewSize.getWidth();
		this.height = LSystem.viewSize.getHeight();
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.fristOrder = DRAW_USER_PAINT();
		this.secondOrder = DRAW_SPRITE_PAINT();
		this.lastOrder = DRAW_DESKTOP_PAINT();
		this.fristPaintFlag = true;
		this.secondPaintFlag = true;
		this.lastPaintFlag = true;
	}

	public boolean contains(float x, float y) {
		return LSystem.viewSize.getRect().contains(x, y);
	}

	public boolean contains(float x, float y, float w, float h) {
		return LSystem.viewSize.getRect().contains(x, y, w, h);
	}

	/**
	 * 当Screen被创建(或再次加载)时将调用此函数
	 * 
	 * @param width
	 * @param height
	 */
	public void onCreate(int width, int height) {
		this.mode = SCREEN_CANVAS_REPAINT;
		this.baseInput = this;
		this.width = width;
		this.height = height;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.lastTouchX = lastTouchY = touchDX = touchDY = 0;
		this.isLoad = isLock = isClose = isTranslate = isGravity = false;
		if (sprites != null) {
			sprites.close();
			sprites = null;
		}
		this.sprites = new Sprites(width, height);
		if (desktop != null) {
			desktop.close();
			desktop = null;
		}
		this.desktop = new Desktop(baseInput, width, height);
		this.isNext = true;
	}

	public Screen addLoad(Updateable u) {
		if (handler != null) {
			handler.addLoad(u);
		}
		return this;
	}

	public Screen removeLoad(Updateable u) {
		if (handler != null) {
			handler.removeLoad(u);
		}
		return this;
	}

	public Screen removeAllLoad() {
		if (handler != null) {
			handler.removeAllLoad();
		}
		return this;
	}

	public Screen addUnLoad(Updateable u) {
		if (handler != null) {
			handler.addUnLoad(u);
		}
		return this;
	}

	public Screen removeUnLoad(Updateable u) {
		if (handler != null) {
			handler.removeUnLoad(u);
		}
		return this;
	}

	public Screen removeAllUnLoad() {
		if (handler != null) {
			handler.removeAllUnLoad();
		}
		return this;
	}

	public Screen addDrawing(Drawable d) {
		if (handler != null) {
			handler.addDrawing(d);
		}
		return this;
	}

	public Screen removeDrawing(Drawable d) {
		if (handler != null) {
			handler.removeDrawing(d);
		}
		return this;
	}

	public Screen removeAllDrawing() {
		if (handler != null) {
			handler.removeAllDrawing();
		}
		return this;
	}

	/**
	 * 当执行Screen转换时将调用此函数(如果返回的LTransition不为null，则渐变效果会被执行)
	 * 
	 * @return
	 */
	public LTransition onTransition() {
		return null;
	}

	/**
	 * 设定重力系统是否启动
	 * 
	 * @param g
	 * @return
	 */
	public synchronized GravityHandler setGravity(boolean g) {
		if (g && gravityHandler == null) {
			gravityHandler = new GravityHandler();
		}
		this.isGravity = g;
		return gravityHandler;
	}

	/**
	 * 判断重力系统是否启动
	 * 
	 * @return
	 */
	public synchronized boolean isGravity() {
		return this.isGravity;
	}

	/**
	 * 获得当前重力器句柄
	 * 
	 * @return
	 */
	public GravityHandler getGravityHandler() {
		return setGravity(true);
	}

	/**
	 * 获得当前游戏事务运算时间是否被锁定
	 * 
	 * @return
	 */
	public boolean isLock() {
		return isLock;
	}

	/**
	 * 锁定游戏事务运算时间
	 * 
	 * @param lock
	 */
	public Screen setLock(boolean lock) {
		this.isLock = lock;
		return this;
	}

	/**
	 * 关闭游戏
	 * 
	 * @param close
	 */
	public Screen setClose(boolean close) {
		this.isClose = close;
		return this;
	}

	/**
	 * 判断游戏是否被关闭
	 * 
	 * @return
	 */
	public boolean isClose() {
		return isClose;
	}

	/**
	 * 设定当前帧
	 * 
	 * @param frame
	 */
	public Screen setFrame(int frame) {
		this.frame = frame;
		return this;
	}

	/**
	 * 返回当前帧
	 * 
	 * @return
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * 移动当前帧
	 * 
	 * @return
	 */
	public synchronized boolean next() {
		this.frame++;
		return isNext;
	}

	/**
	 * 初始化时加载的数据
	 */
	public abstract void onLoad();

	/**
	 * 初始化加载完毕
	 * 
	 */
	public void onLoaded() {

	}

	/**
	 * 改变资源加载状态
	 */
	public Screen setOnLoadState(boolean flag) {
		this.isLoad = flag;
		return this;
	}

	/**
	 * 获得当前资源加载是否完成
	 */
	public boolean isOnLoadComplete() {
		return isLoad;
	}

	/**
	 * 取出第一个Screen并执行
	 * 
	 */
	public Screen runFirstScreen() {
		if (handler != null) {
			handler.runFirstScreen();
		}
		return this;
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public Screen runLastScreen() {
		if (handler != null) {
			handler.runLastScreen();
		}
		return this;
	}

	/**
	 * 运行指定位置的Screen
	 * 
	 * @param index
	 */
	public Screen runIndexScreen(int index) {
		if (handler != null) {
			handler.runIndexScreen(index);
		}
		return this;
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public Screen runPreviousScreen() {
		if (handler != null) {
			handler.runPreviousScreen();
		}
		return this;
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public Screen runNextScreen() {
		if (handler != null) {
			handler.runNextScreen();
		}
		return this;
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen
	 */
	public Screen addScreen(Screen screen) {
		if (handler != null) {
			handler.addScreen(screen);
		}
		return this;
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return
	 */
	public LinkedList<Screen> getScreens() {
		if (handler != null) {
			return handler.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 */
	public int getScreenCount() {
		if (handler != null) {
			return handler.getScreenCount();
		}
		return 0;
	}

	/**
	 * 返回精灵监听
	 * 
	 * @return
	 */

	public SpriteListener getSprListerner() {
		if (sprites == null) {
			return null;
		}
		return sprites.getSprListerner();
	}

	/**
	 * 监听Screen中精灵
	 * 
	 * @param sprListerner
	 */

	public Screen setSprListerner(SpriteListener sprListerner) {
		if (sprites == null) {
			return this;
		}
		sprites.setSprListerner(sprListerner);
		return this;
	}

	/**
	 * 获得当前Screen类名
	 */
	public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * 设定模拟按钮监听器
	 */

	public Screen setEmulatorListener(EmulatorListener emulator) {
		if (LSystem._process != null) {
			LSystem._process.setEmulatorListener(emulator);
		}
		return this;
	}

	/**
	 * 返回模拟按钮集合
	 * 
	 * @return
	 */

	public EmulatorButtons getEmulatorButtons() {
		if (LSystem._process != null) {
			return LSystem._process.getEmulatorButtons();
		}
		return null;
	}

	/**
	 * 设定模拟按钮组是否显示
	 * 
	 * @param visible
	 */

	public Screen emulatorButtonsVisible(boolean visible) {
		if (LSystem._process != null) {
			try {
				EmulatorButtons es = LSystem._process.getEmulatorButtons();
				es.setVisible(visible);
			} catch (Exception e) {
			}
		}
		return this;
	}

	/**
	 * 设定背景图像
	 * 
	 * @param screen
	 */
	public Screen setBackground(LTexture background) {
		if (background != null) {
			setRepaintMode(SCREEN_BITMAP_REPAINT);
			LTexture screen = null;
			if (background.getWidth() != getWidth()
					|| background.getHeight() != getHeight()) {
				screen = background.scale(getWidth(), getHeight());
			} else {
				screen = background;
			}
			LTexture tmp = currentScreen;
			currentScreen = screen;
			if (tmp != null) {
				tmp.close();
				tmp = null;
			}
		} else {
			setRepaintMode(SCREEN_CANVAS_REPAINT);
		}
		return this;
	}

	/**
	 * 设定背景图像
	 */
	public Screen setBackground(String fileName) {
		return this.setBackground(LTextures.loadTexture(fileName));
	}

	/**
	 * 设定背景颜色
	 * 
	 * @param c
	 */
	public Screen setBackground(LColor c) {
		setRepaintMode(SCREEN_COLOR_REPAINT);
		if (color == null) {
			color = new LColor(c);
		} else {
			color.setColor(c.r, c.g, c.b, c.a);
		}
		return this;
	}

	public LColor getColor() {
		return color;
	}

	/**
	 * 返回背景图像
	 * 
	 * @return
	 */
	public LTexture getBackground() {
		return currentScreen;
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public Sprites getSprites() {
		return sprites;
	}

	/**
	 * 返回位于屏幕顶部的组件
	 * 
	 * @return
	 */

	public LComponent getTopComponent() {
		if (desktop != null) {
			return desktop.getTopComponent();
		}
		return null;
	}

	/**
	 * 返回位于屏幕底部的组件
	 * 
	 * @return
	 */

	public LComponent getBottomComponent() {
		if (desktop != null) {
			return desktop.getBottomComponent();
		}
		return null;
	}

	public LLayer getTopLayer() {
		if (desktop != null) {
			return desktop.getTopLayer();
		}
		return null;
	}

	public LLayer getBottomLayer() {
		if (desktop != null) {
			return desktop.getBottomLayer();
		}
		return null;
	}


	/**
	 * 返回位于数据顶部的精灵
	 * 
	 */

	public ISprite getTopSprite() {
		if (sprites != null) {
			return sprites.getTopSprite();
		}
		return null;
	}

	/**
	 * 返回位于数据底部的精灵
	 * 
	 */

	public ISprite getBottomSprite() {
		if (sprites != null) {
			return sprites.getBottomSprite();
		}
		return null;
	}

	/**
	 * 添加游戏精灵
	 * 
	 * @param sprite
	 */

	public Screen add(ISprite sprite) {
		if (sprites != null) {
			sprites.add(sprite);
		}
		return this;
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param comp
	 */

	public Screen add(LComponent comp) {
		if (desktop != null) {
			desktop.add(comp);
		}
		return this;
	}

	public Screen remove(ISprite sprite) {
		if (sprites != null) {
			sprites.remove(sprite);
		}
		return this;
	}

	public Screen remove(LComponent comp) {
		if (desktop != null) {
			desktop.remove(comp);
		}
		return this;
	}


	public Screen removeAll() {
		if (sprites != null) {
			sprites.removeAll();
		}
		if (desktop != null) {
			desktop.getContentPane().clear();
		}
		return this;
	}

	/**
	 * 判断是否点中指定精灵
	 * 
	 * @param sprite
	 * @return
	 */

	public boolean onClick(ISprite sprite) {
		if (sprite == null) {
			return false;
		}
		if (sprite.isVisible()) {
			RectBox rect = sprite.getCollisionBox();
			if (rect.contains(SysTouch.getX(), SysTouch.getY())
					|| rect.intersects(SysTouch.getX(), SysTouch.getY())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否点中指定组件
	 * 
	 * @param component
	 * @return
	 */

	public boolean onClick(LComponent component) {
		if (component == null) {
			return false;
		}
		if (component.isVisible()) {
			RectBox rect = component.getCollisionBox();
			if (rect.contains(SysTouch.getX(), SysTouch.getY())
					|| rect.intersects(SysTouch.getX(), SysTouch.getY())) {
				return true;
			}
		}
		return false;
	}

	public Screen centerOn(final LObject object) {
		LObject.centerOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen topOn(final LObject object) {
		LObject.topOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen leftOn(final LObject object) {
		LObject.leftOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen rightOn(final LObject object) {
		LObject.rightOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen bottomOn(final LObject object) {
		LObject.bottomOn(object, getWidth(), getHeight());
		return this;
	}

	/**
	 * 获得背景显示模式
	 */
	public int getRepaintMode() {
		return mode;
	}

	/**
	 * 设定背景刷新模式
	 * 
	 * @param mode
	 */
	public void setRepaintMode(int mode) {
		this.mode = mode;
	}

	public Screen setLocation(float x, float y) {
		this.tx = x;
		this.ty = y;
		this.isTranslate = (tx != 0 || ty != 0);
		return this;
	}

	public Screen setX(float x) {
		setLocation(x, ty);
		return this;
	}

	public Screen setY(float y) {
		setLocation(tx, y);
		return this;
	}

	public float getX() {
		return this.tx;
	}

	public float getY() {
		return this.ty;
	}

	protected void afterUI(GLEx g) {

	}

	protected void beforeUI(GLEx g) {

	}

	private final void repaint(GLEx g) {
		if (!isClose) {
			if (isTranslate) {
				g.translate(tx, ty);
			}
			afterUI(g);
			if (fristPaintFlag) {
				fristOrder.paint(g);
			}
			if (secondPaintFlag) {
				secondOrder.paint(g);
			}
			if (lastPaintFlag) {
				lastOrder.paint(g);
			}
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.draw(g);
				}
			}
			LProcess process = LSystem._process;
			RootPlayer players = process.rootPlayer;
			try {
				g.save();
				players.update(elapsedTime);
				players.paint(g);
			} finally {
				g.restore();
			}
			if (isTranslate) {
				g.translate(-tx, -ty);
			}
		}
	}

	private int tmpColor = LColor.DEF_COLOR;

	public synchronized void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (replaceLoading) {
			if (replaceDstScreen == null
					|| !replaceDstScreen.isOnLoadComplete()) {
				repaint(g);
			} else if (replaceDstScreen.isOnLoadComplete()) {
				if (isScreenFrom) {
					repaint(g);
					if (replaceDstScreen.color != null) {
						tmpColor = g.color();
						g.setColor(replaceDstScreen.color);
						g.fillRect(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.setColor(tmpColor);
					}
					if (replaceDstScreen.currentScreen != null) {
						g.draw(replaceDstScreen.currentScreen, dstPos.x(),
								dstPos.y(), getWidth(), getHeight());
					}
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.setClip(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.translate(dstPos.x(), dstPos.y());
					}
					replaceDstScreen.createUI(g);
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.translate(-dstPos.x(), -dstPos.y());
						g.clearClip();
					}
				} else {
					if (replaceDstScreen.color != null) {
						tmpColor = g.color();
						g.setColor(replaceDstScreen.color);
						g.fillRect(0, 0, getWidth(), getHeight());
						g.setColor(tmpColor);
					}
					if (replaceDstScreen.currentScreen != null) {
						g.draw(replaceDstScreen.currentScreen, 0, 0,
								getWidth(), getHeight());
					}
					replaceDstScreen.createUI(g);
					if (color != null) {
						tmpColor = g.color();
						g.setColor(color);
						g.fillRect(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.setColor(tmpColor);
					}
					if (getBackground() != null) {
						g.draw(currentScreen, dstPos.x(), dstPos.y(),
								getWidth(), getHeight());
					}
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.setClip(dstPos.x(), dstPos.y(), getWidth(),
								getHeight());
						g.translate(dstPos.x(), dstPos.y());
					}
					repaint(g);
					if (dstPos.x() != 0 || dstPos.y() != 0) {
						g.translate(-dstPos.x(), -dstPos.y());
						g.clearClip();
					}
				}
			}
		} else {
			repaint(g);
		}
	}

	private final void process(final LTimerContext timer) {
		this.elapsedTime = timer.getTimeSinceLastUpdate();
		if (!isClose) {
			if (isGravity) {
				gravityHandler.update(elapsedTime);
			}
			if (fristPaintFlag) {
				fristOrder.update(timer);
			}
			if (secondPaintFlag) {
				secondOrder.update(timer);
			}
			if (lastPaintFlag) {
				lastOrder.update(timer);
			}
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.update(elapsedTime);
				}
			}
		}
		this.touchDX = SysTouch.getX() - lastTouchX;
		this.touchDY = SysTouch.getY() - lastTouchY;
		this.lastTouchX = SysTouch.getX();
		this.lastTouchY = SysTouch.getY();
		this.touchButtonReleased = NO_BUTTON;
	}

	public void runTimer(final LTimerContext timer) {
		if (isClose) {
			return;
		}
		if (replaceLoading) {
			if (replaceDstScreen == null
					|| !replaceDstScreen.isOnLoadComplete()) {
				process(timer);
			} else if (replaceDstScreen.isOnLoadComplete()) {
				process(timer);
				if (replaceDelay.action(timer)) {
					switch (replaceMethod) {
					case FROM_LEFT:
						dstPos.move_right(replaceScreenSpeed);
						if (dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_RIGHT:
						dstPos.move_left(replaceScreenSpeed);
						if (dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UP:
						dstPos.move_down(replaceScreenSpeed);
						if (dstPos.y() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_DOWN:
						dstPos.move_up(replaceScreenSpeed);
						if (dstPos.y() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LEFT:
						dstPos.move_left(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_RIGHT:
						dstPos.move_right(replaceScreenSpeed);
						if (dstPos.x() > getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UP:
						dstPos.move_up(replaceScreenSpeed);
						if (dstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_DOWN:
						dstPos.move_down(replaceScreenSpeed);
						if (dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_LEFT:
						if (dstPos.y() < 0) {
							dstPos.move_45D_right(replaceScreenSpeed);
						} else {
							dstPos.move_right(replaceScreenSpeed);
						}
						if (dstPos.y() >= 0 && dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_RIGHT:
						if (dstPos.y() < 0) {
							dstPos.move_45D_down(replaceScreenSpeed);
						} else {
							dstPos.move_left(replaceScreenSpeed);
						}
						if (dstPos.y() >= 0 && dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_LEFT:
						if (dstPos.y() > 0) {
							dstPos.move_45D_up(replaceScreenSpeed);
						} else {
							dstPos.move_right(replaceScreenSpeed);
						}
						if (dstPos.y() <= 0 && dstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_RIGHT:
						if (dstPos.y() > 0) {
							dstPos.move_45D_left(replaceScreenSpeed);
						} else {
							dstPos.move_left(replaceScreenSpeed);
						}
						if (dstPos.y() <= 0 && dstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_LEFT:
						dstPos.move_45D_left(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()
								|| dstPos.y() <= -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_RIGHT:
						dstPos.move_45D_up(replaceScreenSpeed);
						if (dstPos.x() > getWidth()
								|| dstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_LEFT:
						dstPos.move_45D_down(replaceScreenSpeed);
						if (dstPos.x() < -getWidth()
								|| dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_RIGHT:
						dstPos.move_45D_right(replaceScreenSpeed);
						if (dstPos.x() > getWidth() || dstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					default:
						break;
					}
					replaceDstScreen.runTimer(timer);
				}
			}
		} else {
			process(timer);
		}
	}

	public SysInput getInput() {
		return baseInput;
	}

	public Screen setNext(boolean next) {
		this.isNext = next;
		return this;
	}

	public abstract void alter(LTimerContext timer);

	/**
	 * 设定游戏窗体
	 */
	public Screen setScreen(Screen screen) {
		if (handler != null) {
			this.handler.setScreen(screen);
		}
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 刷新基础设置
	 */
	public void refresh() {
		for (int i = 0; i < touchType.length; i++) {
			touchType[i] = false;
		}
		touchDX = touchDY = 0;
		for (int i = 0; i < keyType.length; i++) {
			keyType[i] = false;
		}
	}

	public abstract void resize(Scale scale, int width, int height);

	public Point2i getTouch() {
		touch.set((int)SysTouch.getX(), (int)SysTouch.getY());
		return touch;
	}

	public boolean isPaused() {
		return LSystem.PAUSED;
	}

	public int getTouchPressed() {
		return touchButtonPressed > SysInput.NO_BUTTON ? touchButtonPressed
				: SysInput.NO_BUTTON;
	}

	public int getTouchReleased() {
		return touchButtonReleased > SysInput.NO_BUTTON ? touchButtonReleased
				: SysInput.NO_BUTTON;
	}

	public boolean isTouchPressed(int button) {
		return touchButtonPressed == button;
	}

	public boolean isTouchReleased(int button) {
		return touchButtonReleased == button;
	}

	public int getTouchX() {
		return (int) SysTouch.getX();
	}

	public int getTouchY() {
		return (int) SysTouch.getY();
	}

	public int getTouchDX() {
		return (int) touchDX;
	}

	public int getTouchDY() {
		return (int) touchDY;
	}

	public boolean isTouchType(int type) {
		return touchType[type];
	}

	public int getKeyPressed() {
		return keyButtonPressed > SysInput.NO_KEY ? keyButtonPressed
				: SysInput.NO_KEY;
	}

	public boolean isKeyPressed(int keyCode) {
		return keyButtonPressed == keyCode;
	}

	public int getKeyReleased() {
		return keyButtonReleased > SysInput.NO_KEY ? keyButtonReleased
				: SysInput.NO_KEY;
	}

	public boolean isKeyReleased(int keyCode) {
		return keyButtonReleased == keyCode;
	}

	public boolean isKeyType(int type) {
		return keyType[type];
	}

	public final void keyPressed(GameKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = e.getType();
		int code = e.getKeyCode();
		try {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.pressed(e);
				}
			}
			this.onKeyDown(e);
			keyType[type] = true;
			keyButtonPressed = code;
			keyButtonReleased = SysInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = SysInput.NO_KEY;
			keyButtonReleased = SysInput.NO_KEY;
			ex.printStackTrace();
		}
	}

	/**
	 * 设置键盘按下事件
	 * 
	 * @param code
	 */
	public void setKeyDown(int button) {
		try {
			keyButtonPressed = button;
			keyButtonReleased = SysInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	public final void keyReleased(GameKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		int type = e.getType();
		int code = e.getKeyCode();
		try {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.released(e);
				}
			}
			this.onKeyUp(e);
			keyType[type] = false;
			keyButtonReleased = code;
			keyButtonPressed = SysInput.NO_KEY;
		} catch (Exception ex) {
			keyButtonPressed = SysInput.NO_KEY;
			keyButtonReleased = SysInput.NO_KEY;
			ex.printStackTrace();
		}
	}

	public void setKeyUp(int button) {
		try {
			keyButtonReleased = button;
			keyButtonPressed = SysInput.NO_KEY;
		} catch (Exception e) {
		}
	}

	public void keyTyped(GameKey e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		onKeyTyped(e);
	}

	public void onKeyDown(GameKey e) {

	}

	public void onKeyUp(GameKey e) {

	}

	public void onKeyTyped(GameKey e) {

	}

	public final void mousePressed(GameTouch e) {

		if (isLock || isClose || !isLoad) {
			return;
		}

		int type = e.getType();
		int button = e.getButton();
		try {
			touchType[type] = true;
			touchButtonPressed = button;
			touchButtonReleased = SysInput.NO_BUTTON;
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.pressed(e);
				}
			}
			if (!isClickLimit(e)) {
				touchDown(e);
			}
		} catch (Exception ex) {
			touchButtonPressed = SysInput.NO_BUTTON;
			touchButtonReleased = SysInput.NO_BUTTON;
			ex.printStackTrace();
		}
	}

	public abstract void touchDown(GameTouch e);

	public void mouseReleased(GameTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		int type = e.getType();
		int button = e.getButton();
		try {
			touchType[type] = false;
			touchButtonReleased = button;
			touchButtonPressed = SysInput.NO_BUTTON;
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.released(e);
				}
			}
			if (!isClickLimit(e)) {
				touchUp(e);
			}
		} catch (Exception ex) {
			touchButtonPressed = SysInput.NO_BUTTON;
			touchButtonReleased = SysInput.NO_BUTTON;
			ex.printStackTrace();
		}
	}

	public abstract void touchUp(GameTouch e);

	public void mouseMoved(GameTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		if (useScreenListener) {
			for (ScreenListener t : screens) {
				t.move(e);
			}
		}
		if (!isClickLimit(e)) {
			touchMove(e);
		}
	}

	public abstract void touchMove(GameTouch e);

	public void mouseDragged(GameTouch e) {
		if (isLock || isClose || !isLoad) {
			return;
		}
		if (isTranslate) {
			e.offset(tx, ty);
		}
		if (useScreenListener) {
			for (ScreenListener t : screens) {
				t.drag(e);
			}
		}
		if (!isClickLimit(e)) {
			touchDrag(e);
		}
	}

	public abstract void touchDrag(GameTouch e);

	public boolean isMoving() {
		return SysTouch.isDrag();
	}

	public int getHalfWidth() {
		return halfWidth;
	}

	public int getHalfHeight() {
		return halfHeight;
	}

	public SensorDirection getSensorDirection() {
		return direction;
	}

	public PaintOrder getFristOrder() {
		return fristOrder;
	}

	public Screen setFristOrder(PaintOrder fristOrder) {
		if (fristOrder == null) {
			this.fristPaintFlag = false;
		} else {
			this.fristPaintFlag = true;
			this.fristOrder = fristOrder;
		}
		return this;
	}

	public PaintOrder getSecondOrder() {
		return secondOrder;
	}

	public Screen setSecondOrder(PaintOrder secondOrder) {
		if (secondOrder == null) {
			this.secondPaintFlag = false;
		} else {
			this.secondPaintFlag = true;
			this.secondOrder = secondOrder;
		}
		return this;
	}

	public PaintOrder getLastOrder() {
		return lastOrder;
	}

	public Screen setLastOrder(PaintOrder lastOrder) {
		if (lastOrder == null) {
			this.lastPaintFlag = false;
		} else {
			this.lastPaintFlag = true;
			this.lastOrder = lastOrder;
		}
		return this;
	}

	public final void destroy() {
		synchronized (this) {
			if (useScreenListener) {
				for (ScreenListener t : screens) {
					t.dispose();
				}
			}
			useScreenListener = false;
			replaceLoading = false;
			replaceDelay.setDelay(10);
			tx = ty = 0;
			isClose = true;
			isTranslate = false;
			isNext = false;
			isGravity = false;
			isLock = true;
			if (screens != null) {
				screens.clear();
				screens = null;
			}
			if (sprites != null) {
				sprites.close();
				sprites = null;
			}
			if (desktop != null) {
				desktop.close();
				desktop = null;
			}
			if (currentScreen != null) {
				LTexture parent = currentScreen.getParent();
				if (parent != null) {
					parent.closeChildAll();
					parent.close();
				} else {
					currentScreen.close();
				}
				currentScreen = null;
			}
			if (LSystem._base != null && LSystem._process.rootPlayer != null) {
				LSystem._process.rootPlayer.removeAll();
			}
			if (gravityHandler != null) {
				gravityHandler.close();
				gravityHandler = null;
			}
			if (releases != null) {
				for (LRelease r : releases) {
					if (r != null) {
						r.close();
					}
				}
				releases.clear();
			}
			close();
			System.gc();
		}
	}

	public Display getDisplay() {
		return LSystem.base().display();
	}

	public RootPlayer getRootPlayer() {
		return LSystem._process.rootPlayer;
	}

	public StageSystem getStageSystem() {
		return LSystem._process.stageSystem;
	}

	public Screen puspStageUp(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage,
					LSystem._process.stageSystem.newSlide().up());
		}
		return this;
	}

	public Screen puspStageRight(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage,
					LSystem._process.stageSystem.newSlide().right());
		}
		return this;
	}

	public Screen puspStageLeft(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage,
					LSystem._process.stageSystem.newSlide().left());
		}
		return this;
	}

	public Screen puspStageDown(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage,
					LSystem._process.stageSystem.newSlide().down());
		}
		return this;
	}

	public Screen puspStage(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage);
		}
		return this;
	}

	public Screen puspStage(Stage stage, StageTransition trans) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stage, trans);
		}
		return this;
	}

	public Screen puspStage(Iterable<? extends Stage> stages) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stages);
		}
		return this;
	}

	public Screen puspStage(Iterable<? extends Stage> stages,
			StageTransition trans) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.push(stages, trans);
		}
		return this;
	}

	public Screen popTo(Stage newTopStage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.popTo(newTopStage);
		}
		return this;
	}

	public Screen popTo(Stage newTopStage, StageTransition trans) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.popTo(newTopStage, trans);
		}
		return this;
	}

	public Screen replace(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.replace(stage);
		}
		return this;
	}

	public Screen replace(Stage stage, StageTransition trans) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.replace(stage, trans);
		}
		return this;
	}

	public boolean remove(Stage stage) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.popTo(stage);
		}
		return false;
	}

	public boolean remove(Stage stage, StageTransition trans) {
		if (LSystem._base != null) {
			LSystem._process.stageSystem.remove(stage);
		}
		return false;
	}

	public Screen setAutoDestory(final boolean a) {
		if (desktop != null) {
			desktop.setAutoDestory(a);
		}
		return this;
	}

	public boolean isAutoDestory() {
		if (desktop != null) {
			return desktop.isAutoDestory();
		}
		return false;
	}

	public abstract void resume();

	public abstract void pause();

	public void stop() {
	}// noop

	/**
	 * 释放函数内资源
	 * 
	 */
	public abstract void close();
}
