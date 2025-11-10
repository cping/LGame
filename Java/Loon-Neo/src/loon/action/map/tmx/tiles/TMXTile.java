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

	private int _id;

	private int _totalDuration;

	private int _playerCount;

	private boolean _animated;

	private boolean _isMovable;

	private TArray<TMXAnimationFrame> _frames;

	private TMXProperties _properties;

	public TMXTile() {
		this(0);
	}

	public TMXTile(int id) {
		this._id = id;
		this._frames = new TArray<TMXAnimationFrame>();
		this._properties = new TMXProperties();
	}

	public void parse(Json.Object element) {

		_id = element.getInt("id", _id);
		Json.Array nodes = element.getArray("properties");

		if (nodes != null) {
			_properties.parse(nodes);
		}
		nodes = element.getArray("animation");

		if (nodes != null) {
			_animated = true;
			Json.Array tiles = element.getArray("frame");

			for (int i = 0; i < tiles.length(); i++) {
				Json.Object frame = tiles.getObject(i);

				int tileID = frame.getInt("tileid", 0);
				int duration = frame.getInt("duration", 0);

				TMXAnimationFrame animation = new TMXAnimationFrame(tileID, duration);
				_frames.add(animation);
				_totalDuration += duration;
			}
		}
	}

	public void parse(XMLElement element) {

		_id = element.getIntAttribute("id", _id);
		XMLElement nodes = element.getChildrenByName("properties");

		if (nodes != null) {
			_properties.parse(nodes);
		}
		nodes = element.getChildrenByName("animation");

		if (nodes != null) {
			_animated = true;
			TArray<XMLElement> tiles = nodes.list("frame");

			for (int i = 0; i < tiles.size; i++) {
				XMLElement frame = tiles.get(i);

				int tileID = frame.getIntAttribute("tileid", 0);
				int duration = frame.getIntAttribute("duration", 0);

				TMXAnimationFrame animation = new TMXAnimationFrame(tileID, duration);
				_frames.add(animation);
				_totalDuration += duration;
			}
		}
	}

	public int getID() {
		return _id;
	}

	public int getTotalDuration() {
		return _totalDuration;
	}

	public int getFrameCount() {
		return _frames.size;
	}

	public boolean isAnimated() {
		return _animated;
	}

	public TArray<TMXAnimationFrame> getFrames() {
		return _frames;
	}

	public TMXProperties getProperties() {
		return _properties;
	}

	public int getPlayerCount() {
		return _playerCount;
	}

	public TMXTile setPlayerCount(int p) {
		this._playerCount = p;
		return this;
	}

	public boolean isMovable() {
		return _isMovable;
	}

	public TMXTile setMovable(boolean m) {
		this._isMovable = m;
		return this;
	}
}
