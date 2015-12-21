package loon.action.sprite.effect;

import loon.opengl.GLEx;

public interface BaseEffect {

	public void createUI(GLEx g) ;

	public void update(long elapsedTime) ;

	public boolean isCompleted() ;

	public boolean isVisible();

	public void setVisible(boolean visible) ;

}
