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
package loon.action.map.battle;

import loon.LTexture;
import loon.action.sprite.Animation;
import loon.utils.IntMap;

public class BattleTileMake {

	public static class TileAnimation {

		public Animation backgroundAnim, groundAnim, effectAnim;

		public TileAnimation(String groundPath) {
			this(Animation.getDefaultAnimation(groundPath));
		}

		public TileAnimation(String bgPath, String groundPath) {
			this(Animation.getDefaultAnimation(bgPath), Animation.getDefaultAnimation(groundPath));
		}

		public TileAnimation(String bgPath, String groundPath, String effectPath) {
			this(Animation.getDefaultAnimation(bgPath), Animation.getDefaultAnimation(groundPath),
					Animation.getDefaultAnimation(effectPath));
		}

		public TileAnimation(LTexture ground) {
			this(null, ground, null);
		}

		public TileAnimation(LTexture bg, LTexture ground) {
			this(bg, ground, null);
		}

		public TileAnimation(LTexture bg, LTexture ground, LTexture effect) {
			this(Animation.getDefaultAnimation(bg), Animation.getDefaultAnimation(ground),
					Animation.getDefaultAnimation(effect));
		}

		public TileAnimation(Animation ground) {
			this(null, ground, null);
		}

		public TileAnimation(Animation bg, Animation ground) {
			this(bg, ground, null);
		}

		public TileAnimation(Animation bg, Animation ground, Animation effect) {
			backgroundAnim = bg;
			groundAnim = ground;
			effectAnim = effect;
		}
	}

	private final IntMap<BattleTileMake.TileAnimation> _anis = new IntMap<BattleTileMake.TileAnimation>();

	public void putTile(int tileId, TileAnimation ani) {
		_anis.put(tileId, ani);
	}

	public TileAnimation getTileAnimation(int tileId) {
		return _anis.get(tileId);
	}

	public TileAnimation removeTile(int tileId) {
		return _anis.remove(tileId);
	}
}
