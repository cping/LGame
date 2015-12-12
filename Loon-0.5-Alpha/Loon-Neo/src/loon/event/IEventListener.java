package loon.event;

public interface IEventListener {
	
	void onReciveEvent(int type, EventDispatcher dispatcher, Object data);	
	
}
