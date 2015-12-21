package loon;

import loon.event.KeyMake;
import loon.event.SysInput;

public interface Platform {

	public static enum Orientation {
		Portrait, Landscape;
	}

	public abstract void close();

	public abstract int getContainerWidth();

	public abstract int getContainerHeight();

	public abstract Orientation getOrientation();

	public abstract LGame getGame();

	public void sysText(SysInput.TextEvent event, KeyMake.TextType textType,
			String label, String initialValue);

	public void sysDialog(SysInput.ClickEvent event, String title, String text,
			String ok, String cancel);
}
