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

import loon.utils.ObjectMap;

public class BettleTalentTileRegistry {

	private static final ObjectMap<String, BattleTalentTileEffect> TALENT_MAP = new ObjectMap<String, BattleTalentTileEffect>();

	public static void registerTalent(BattleTalentTileEffect talent) {
		TALENT_MAP.put(talent.getTalentId(), talent);
	}

	public static float getTalentBonus(String talentId, BattleTileType tileType) {
		BattleTalentTileEffect effect = TALENT_MAP.get(talentId);
		if (effect != null) {
			return effect.getBonus(tileType);
		}
		return 0.0f;
	}
}
