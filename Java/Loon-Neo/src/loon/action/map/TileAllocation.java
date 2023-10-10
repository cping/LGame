/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.map;

import loon.BaseIO;
import loon.LRelease;
import loon.LSysException;
import loon.LTexture;
import loon.canvas.Image;
import loon.geom.RectF;
import loon.opengl.LTexturePackClip;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class TileAllocation<T extends LRelease> implements LRelease {

	public final static int TILE_NONE = 0;

	public final static int TILE_TEXTURE = 1;

	public final static int TILE_IMAGE = 2;

	public final static <T extends LRelease> TileAllocation<T> texture(String path) {
		return new TileAllocation<T>(path, TILE_TEXTURE);
	}

	public final static <T extends LRelease> TileAllocation<T> image(String path) {
		return new TileAllocation<T>(path, TILE_IMAGE);
	}

	public final static <T extends LRelease> TileAllocation<T> none(String path) {
		return new TileAllocation<T>(path, TILE_NONE);
	}

	private int _imgType;

	private String _pathName;

	final ObjectMap<String, RectF> _allocationMap = new ObjectMap<String, RectF>();

	T _image;

	public TileAllocation(String path, int imgType) {
		this._pathName = path;
		this._imgType = imgType;
		if (StringUtils.isNotEmpty(_pathName) && imgType != TILE_NONE) {
			switch (_imgType) {
			case TILE_TEXTURE:
				setData(BaseIO.loadTexture(path));
				break;
			case TILE_IMAGE:
				setData(BaseIO.loadImage(path));
				break;
			}
		} else {
			this._imgType = TILE_NONE;
		}
	}

	public TileAllocation(T img) {
		this.setData(img);
	}

	@SuppressWarnings("unchecked")
	protected void setData(Object img) {
		if (img == null) {
			this._imgType = TILE_NONE;
		} else {
			this._image = (T) img;
			if (_image instanceof LTexture) {
				_imgType = TILE_TEXTURE;
			} else if (_image instanceof Image) {
				_imgType = TILE_IMAGE;
			} else {
				_imgType = TILE_NONE;
			}
		}
	}

	public TileAllocation<T> clip(String name, float x, float y, float w, float h) {
		return addTile(name, x, y, w, h);
	}

	public TileAllocation<T> addTile(String name, float x, float y, float w, float h) {
		return addTile(name, new RectF(x, y, w, h));
	}

	public TileAllocation<T> addTile(String name, RectF rect) {
		if (StringUtils.isEmpty(name)) {
			throw new LSysException("The name is required !");
		}
		if (rect == null) {
			throw new LSysException("The rect is required !");
		}
		_allocationMap.put(name, rect);
		return this;
	}

	public RectF getTile(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new LSysException("The name is required !");
		}
		return _allocationMap.get(name);
	}

	protected LTexture getTexture() {
		return (LTexture) _image;
	}

	protected Image getImage() {
		return (Image) _image;
	}

	@SuppressWarnings("unchecked")
	public T sub(float x, float y, float w, float h) {
		switch (_imgType) {
		case TILE_TEXTURE:
			return (T) getTexture().copy(x, y, w, h);
		case TILE_IMAGE:
			return (T) getImage().getSubImage(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.ifloor(w),
					MathUtils.ifloor(h));
		}
		return null;
	}

	public T sub(String name) {
		RectF rect = getTile(name);
		if (rect == null) {
			throw new LSysException("The tile with the specified name has no data !");
		}
		return sub(rect.x, rect.y, rect.width, rect.height);
	}

	public T get(String name) {
		return sub(name);
	}

	public TArray<LTexturePackClip> getClips() {
		final TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>();
		final ObjectMap<String, RectF> rectMap = _allocationMap;
		int idx = 0;
		for (Entries<String, RectF> it = rectMap.iterator(); it.hasNext();) {
			Entry<String, RectF> entry = it.next();
			String key = entry.getKey();
			RectF rect = entry.getValue();
			if (key != null && rect != null) {
				clips.add(new LTexturePackClip(idx, key, rect.x, rect.y, rect.width, rect.height));
				idx++;
			}
		}
		return clips;
	}

	public String getPath() {
		return _pathName;
	}

	public boolean isTexture() {
		return _imgType == TILE_TEXTURE;
	}

	public boolean isImage() {
		return _imgType == TILE_IMAGE;
	}

	public boolean isNone() {
		return _imgType == TILE_NONE;
	}

	public TileAllocation<T> clear() {
		_allocationMap.clear();
		return this;
	}

	@Override
	public void close() {
		if (_image != null) {
			_image.close();
		}
	}

}
