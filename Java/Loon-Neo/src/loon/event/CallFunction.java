package loon.event;

import loon.component.LComponent;

public abstract class CallFunction implements Updateable {

	@Override
	public void action(Object a) {
		if (a instanceof LComponent) {
			call((LComponent) a);
		} else {
			call(null);
		}
	}

	public abstract void call(LComponent comp);

}
