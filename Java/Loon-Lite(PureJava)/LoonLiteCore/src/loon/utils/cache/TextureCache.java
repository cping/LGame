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
package loon.utils.cache;

import loon.LSysException;
import loon.LTexture;
import loon.utils.TimeUtils;

/**
 * 纹理的缓存对象实现
 */
public class TextureCache extends CacheObject {

	private int _gc_spawnCount;

	private LTexture _obj;

	public TextureCache(LTexture obj) {
		this(obj, false);
	}

	public TextureCache(LTexture obj, boolean spawned) {
		super(obj);
		if (obj == null) {
			throw new LSysException("Object is null");
		}
		_obj = obj;
		_gc_spawnCount = spawned ? 1 : 0;
	}

	@Override
	public boolean isUse() {
		return _gc_spawnCount > 0;
	}

	public int getSpawnCount() {
		return _gc_spawnCount;
	}

	public LTexture peek() {
		return _obj;
	}

	@Override
	public void onSpawn() {
		_gc_spawnCount++;
		_lastUseTime = TimeUtils.millis();
	}

	@Override
	public void onUnspawn() {
		_lastUseTime = TimeUtils.millis();
		_gc_spawnCount--;
		if (_gc_spawnCount < 0) {
			throw new LSysException("Spawn count is less than 0.");
		}
	}

	@Override
	public void disposed(boolean isShutdown) {
		if (isShutdown) {
			_obj.close(true);
		}
	}

}