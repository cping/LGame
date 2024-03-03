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
package loon.action.map.tmx.tiles;

import loon.Json;
import loon.action.map.tmx.TMXProperties;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXTile {

	private int id;

	private int totalDuration;

	private int playerCount;

	private boolean animated;

	private boolean isMovable;

	private TArray<TMXAnimationFrame> frames;

	private TMXProperties properties;

	public TMXTile() {
		this(0);
	}

	public TMXTile(int id) {
		this.id = id;
		this.frames = new TArray<TMXAnimationFrame>();
		this.properties = new TMXProperties();
	}

	public void parse(Json.Object element) {

		id = element.getInt("id", id);
		Json.Array nodes = element.getArray("properties");

		if (nodes != null) {
			properties.parse(nodes);
		}
		nodes = element.getArray("animation");

		if (nodes != null) {
			animated = true;
			Json.Array tiles = element.getArray("frame");

			for (int i = 0; i < tiles.length(); i++) {
				Json.Object frame = tiles.getObject(i);

				int tileID = frame.getInt("tileid", 0);
				int duration = frame.getInt("duration", 0);

				TMXAnimationFrame animation = new TMXAnimationFrame(tileID, duration);
				frames.add(animation);
				totalDuration += duration;
			}
		}
	}

	public void parse(XMLElement element) {

		id = element.getIntAttribute("id", id);
		XMLElement nodes = element.getChildrenByName("properties");

		if (nodes != null) {
			properties.parse(nodes);
		}
		nodes = element.getChildrenByName("animation");

		if (nodes != null) {
			animated = true;
			TArray<XMLElement> tiles = nodes.list("frame");

			for (int i = 0; i < tiles.size; i++) {
				XMLElement frame = tiles.get(i);

				int tileID = frame.getIntAttribute("tileid", 0);
				int duration = frame.getIntAttribute("duration", 0);

				TMXAnimationFrame animation = new TMXAnimationFrame(tileID, duration);
				frames.add(animation);
				totalDuration += duration;
			}
		}
	}

	public int getID() {
		return id;
	}

	public int getTotalDuration() {
		return totalDuration;
	}

	public int getFrameCount() {
		return frames.size;
	}

	public boolean isAnimated() {
		return animated;
	}

	public TArray<TMXAnimationFrame> getFrames() {
		return frames;
	}

	public TMXProperties getProperties() {
		return properties;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public TMXTile setPlayerCount(int p) {
		this.playerCount = p;
		return this;
	}

	public boolean isMovable() {
		return isMovable;
	}

	public TMXTile setMovable(boolean m) {
		this.isMovable = m;
		return this;
	}
}
