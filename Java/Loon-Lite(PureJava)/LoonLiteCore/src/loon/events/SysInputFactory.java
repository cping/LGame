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
package loon.events;

public abstract class SysInputFactory {

	protected final static GameTouch finalTouch = new GameTouch();

	protected final static GameKey finalKey = new GameKey();

	protected final static ActionKey onlyKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

	protected static boolean isDraging;

	private static OnscreenKeyboard _defkeyboard = new DefaultOnscreenKeyboard();

	static public interface OnscreenKeyboard {
		public void show(boolean visible);
	}

	static public class DefaultOnscreenKeyboard implements OnscreenKeyboard {
		@Override
		public void show(boolean visible) {
			// todo
		}
	}

	public static void setKeyBoard(OnscreenKeyboard keyboard) {
		_defkeyboard = keyboard;
	}

	public static OnscreenKeyboard getKeyBoard() {
		return _defkeyboard;
	}

	public static ActionKey getOnlyKey() {
		return onlyKey;
	}

	public abstract void startTouchCollection();

	public abstract void stopTouchCollection();

	public abstract LTouchCollection getTouchState() ;

	public abstract void reset();

	public abstract void resetTouch() ;

	public abstract void resetSysTouch() ;

	public abstract void callKey(KeyMake.KeyEvent e) ;

	/**
	 * 鼠标移动事件监听(仅在有鼠标的设备上生效,没有触屏的设备上(主要是台式机)触屏会由此监听模拟)
	 */
	public abstract void callMouse(MouseMake.ButtonEvent event);

	/**
	 * 触屏事件监听
	 *
	 * @param events
	 */
	public abstract void callTouch(TouchMake.Event[] events);

}
