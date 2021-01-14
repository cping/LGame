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
package loon.utils.res;

import loon.Json;
import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.Pixmap;
import loon.events.EventDispatcher;
import loon.font.BMFont;
import loon.utils.TArray;
import loon.utils.ObjectMap.Keys;
import loon.utils.xml.XMLDocument;

public abstract class ResourceGetter extends EventDispatcher {
	
	abstract public Json.Object getJson(String name);

	abstract public XMLDocument getXml(String name);

	abstract public String getText(String name);
	
	abstract public Texture getTexture(String name);

	abstract public LTexture getTextureData(String name);
	
	abstract public Image getImage(String name);

	abstract public Pixmap getPixmap(String name);
	
	abstract public MovieSpriteSheet getSheet(String name);

	abstract public FontSheet getFontSheet(String name);

	abstract public BMFont getBMFont(String name);
	
	abstract public String getURL(String name);

	abstract public TArray<String> getGroupKeys(String name);
	
	abstract public Keys<String> getGroupNames(String name);
	
	abstract public void release(String name);
}
