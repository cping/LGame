package loon.action;

public interface ActionCallback {

	public void onEvent(int type, ActionTweenBase<?> source);
	
}