/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch.BlendState;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.utils.StringUtils;

public class DefImage extends DefinitionObject {

	public LColor maskColor;

	public BlendState blend = BlendState.NonPremultiplied;

	public Vector2f anchor;

	public Vector2f place;

	public Vector2f pos;

	public Vector2f orig;

	public Vector2f size;

	public String uniqueID;

	DefImage() {
	}

	public static DefImage put(String uid, String name, Vector2f p, Vector2f s,
			Vector2f a, Vector2f pl) {
		return new DefImage(uid, name, p, s, a, pl);
	}

	DefImage(String uid, String name, Vector2f p, Vector2f o, Vector2f a,
			Vector2f pl) {
		this.uniqueID = uid;
		this.pos = p;
		this.orig = o;
		this.anchor = a;
		this.place = pl;
		this.fileName = name;
		LNDataCache.setImage(this, this.uniqueID);
	}

	@Override
	public void definitionObjectDidFinishParsing() {
		super.definitionObjectDidFinishParsing();
		LNDataCache.setImage(this, this.uniqueID);
	}

	@Override
	public void definitionObjectDidReceiveString(String v) {
		super.definitionObjectDidReceiveString(v);
		ArrayList<String> result = getResult(v);
		for (String list : result) {
			if (list.length() > 2) {
				String[] values = StringUtils.split(list, "=");
				String name = values[0];
				String value = values[1];
				if ("imageid".equalsIgnoreCase(name)) {
					this.uniqueID = value;
				} else if ("pos".equalsIgnoreCase(name)) {
					this.pos = DefinitionObject.strToVector2(value);
				} else if ("orig".equalsIgnoreCase(name)) {
					this.orig = DefinitionObject.strToVector2(value);
				} else if ("size".equalsIgnoreCase(name)) {
					this.size = DefinitionObject.strToVector2(value);
				} else if ("anchor".equalsIgnoreCase(name)) {
					this.anchor = DefinitionObject.strToVector2(value);
				} else if ("place".equalsIgnoreCase(name)) {
					this.place = DefinitionObject.strToVector2(value);
				} else if ("file".equalsIgnoreCase(name)) {
					this.fileName = value;
				} else if ("name".equalsIgnoreCase(name)) {
					this.uniqueID = value;
				} else if ("id".equalsIgnoreCase(name)) {
					this.uniqueID = value;
				} else if ("mask".equalsIgnoreCase(name)) {
					String[] colors = StringUtils.split(value, ",");
					if (colors.length == 3) {
						this.maskColor = new LColor(
								Integer.parseInt(colors[0]),
								Integer.parseInt(colors[1]),
								Integer.parseInt(colors[2]));
					} else if (colors.length == 4) {
						this.maskColor = new LColor(
								Integer.parseInt(colors[0]),
								Integer.parseInt(colors[1]),
								Integer.parseInt(colors[2]),
								Integer.parseInt(colors[4]));
					}
				} else if ("blend".equalsIgnoreCase(name)) {
					if ("non".equalsIgnoreCase(value)
							|| "NonPremultiplied".equalsIgnoreCase(value)) {
						blend = BlendState.NonPremultiplied;
					} else if ("add".equalsIgnoreCase(value)
							|| "Additive".equalsIgnoreCase(value)) {
						blend = BlendState.Additive;
					} else if ("alpha".equalsIgnoreCase(value)
							|| "AlphaBlend".equalsIgnoreCase(value)) {
						blend = BlendState.AlphaBlend;
					} else if ("op".equalsIgnoreCase(value)
							|| "Opaque".equalsIgnoreCase(value)) {
						blend = BlendState.Opaque;
					}
				}
			}
		}
		if (size == null && orig != null) {
			size = orig;
		} else if (orig == null && size != null) {
			orig = size;
		}
		if (anchor == null && size != null) {
			anchor = new Vector2f(size.x / 2, size.y / 2);
		}
		if (place == null) {
			place = new Vector2f();
		}
		result.clear();
	}
}
