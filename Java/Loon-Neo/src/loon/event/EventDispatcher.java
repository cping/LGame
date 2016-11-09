package loon.event;

import loon.LObject;
import loon.action.sprite.ISprite;
import loon.utils.ListMap;
import loon.utils.TArray;

public class EventDispatcher extends LObject<ISprite> {

	public class EventType {

		/**
		 * 舞台进入新的一帧
		 */
		static public final byte EVENT_STAGE_ENTER_FRAME = 1;

		/**
		 * 键盘按下事件
		 */
		static public final byte EVENT_KEY_PRESSED = 2;

		/**
		 * 键盘弹起事件
		 */
		static public final byte EVENT_KEY_RELEASEED = 3;

		/**
		 * Tween结束事件
		 */
		static public final byte EVENT_TWEEN_COMPLETE = 4;

		/**
		 * MC又从头开始播放了
		 */
		static public final byte EVENT_MOVIE_CLIP_RESTART = 5;

		/**
		 * 时间，Loader加载完成
		 */
		static public final byte EVENT_LOAD_COMPLETE = 6;

	}

	private ListMap<Integer, TArray<IEventListener>> _events = new ListMap<Integer, TArray<IEventListener>>();

	public EventDispatcher() {

	}

	public void dispatchEvent(int type) {
		dispatchEvent(type, null);
	}

	public void dispatchEvent(int type, Object data) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners == null) {
			return;
		}
		for (IEventListener listener : listeners) {
			listener.onReciveEvent(type, this, data);
		}
	}

	public void addEventListener(int type, IEventListener listener) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners == null) {
			listeners = new TArray<IEventListener>();
			_events.put(type, listeners);
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(int type, IEventListener listener) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}
}
