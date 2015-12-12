package loon.utils.res;

import loon.Json;
import loon.canvas.Image;
import loon.event.EventDispatcher;

public abstract class ResourceGetter extends EventDispatcher {
	
	abstract public Json.Object getJson(String name);

	abstract public Texture getTexture(String name);

	abstract public Image getImage(String name);
	
	abstract public SpriteSheet getSheet(String name);

	abstract public FontSheet getFontSheet(String name);

	abstract public String getURL(String name);

	abstract public void release(String name);
}
