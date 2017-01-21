package loon.action.sprite.effect;

import loon.LRelease;
import loon.action.sprite.SpriteBatch;

public interface ILightning extends LRelease {
	
	boolean isComplete();

	void update(long elapsedTime);
	
	void draw(SpriteBatch spriteBatch);
}
