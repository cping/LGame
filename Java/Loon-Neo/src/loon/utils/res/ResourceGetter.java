package loon.utils.res;

import loon.Json;
import loon.canvas.Image;
import loon.event.EventDispatcher;
import loon.utils.TArray;
import loon.utils.ObjectMap.Keys;

public abstract class ResourceGetter extends EventDispatcher {
	
	abstract public Json.Object getJson(String name);

	abstract public Texture getTexture(String name);

	abstract public Image getImage(String name);
	
	abstract public MovieSpriteSheet getSheet(String name);

	abstract public FontSheet getFontSheet(String name);

	abstract public String getURL(String name);

	abstract public TArray<String> getGroupKeys(String name);
	
	abstract public Keys<String> getGroupNames(String name);
	
	abstract public void release(String name);
}
