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
package loon.action.map.items;

import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.SortedList;

public class Relationship {

	private ObjectMap<String, ObjectMap<String, Integer>> intimacyMap = new ObjectMap<String, ObjectMap<String, Integer>>();

	private ObjectMap<String, SortedList<String>> swornBrothersMap = new ObjectMap<String, SortedList<String>>();

	private ObjectMap<String, SortedList<String>> coupleMap = new ObjectMap<String, SortedList<String>>();

	private ObjectMap<String, SortedList<String>> enemyMap = new ObjectMap<String, SortedList<String>>();

	private ObjectMap<String, ObjectMap<String, Integer>> hatredMap = new ObjectMap<String, ObjectMap<String, Integer>>();

	public void setIntimacy(String generalId1, String generalId2, int intimacy) {
		if (!intimacyMap.containsKey(generalId1)) {
			intimacyMap.put(generalId1, new ObjectMap<String, Integer>());
		}
		intimacyMap.get(generalId1).put(generalId2, MathUtils.max(0, MathUtils.min(100, intimacy)));
		if (!intimacyMap.containsKey(generalId2)) {
			intimacyMap.put(generalId2, new ObjectMap<String, Integer>());
		}
		intimacyMap.get(generalId2).put(generalId1, MathUtils.max(0, MathUtils.min(100, intimacy)));
	}

	public int getIntimacy(String generalId1, String generalId2) {
		if (intimacyMap.containsKey(generalId1) && intimacyMap.get(generalId1).containsKey(generalId2)) {
			return intimacyMap.get(generalId1).get(generalId2);
		}
		return 50;
	}

	public boolean isSwornBrother(String generalId1, String generalId2) {
		return (swornBrothersMap.containsKey(generalId1) && swornBrothersMap.get(generalId1).contains(generalId2))
				|| (swornBrothersMap.containsKey(generalId2) && swornBrothersMap.get(generalId2).contains(generalId1));
	}

	public boolean isCouple(String generalId1, String generalId2) {
		return (coupleMap.containsKey(generalId1) && coupleMap.get(generalId1).contains(generalId2))
				|| (coupleMap.containsKey(generalId2) && coupleMap.get(generalId2).contains(generalId1));
	}

	public boolean isEnemy(String generalId1, String generalId2) {
		return (enemyMap.containsKey(generalId1) && enemyMap.get(generalId1).contains(generalId2))
				|| (enemyMap.containsKey(generalId2) && enemyMap.get(generalId2).contains(generalId1));
	}

	public void addSwornBrother(String generalId1, String generalId2) {
		if (!swornBrothersMap.containsKey(generalId1)) {
			swornBrothersMap.put(generalId1, new SortedList<String>());
		}
		swornBrothersMap.get(generalId1).add(generalId2);

		if (!swornBrothersMap.containsKey(generalId2)) {
			swornBrothersMap.put(generalId2, new SortedList<String>());
		}
		swornBrothersMap.get(generalId2).add(generalId1);

		setIntimacy(generalId1, generalId2, 100);

		if (enemyMap.containsKey(generalId1)) {
			enemyMap.get(generalId1).remove(generalId2);
		}
		if (enemyMap.containsKey(generalId2)) {
			enemyMap.get(generalId2).remove(generalId1);
		}
	}

	public void addCouple(String generalId1, String generalId2) {
		if (!coupleMap.containsKey(generalId1)) {
			coupleMap.put(generalId1, new SortedList<String>());
		}
		coupleMap.get(generalId1).add(generalId2);

		if (!coupleMap.containsKey(generalId2)) {
			coupleMap.put(generalId2, new SortedList<String>());
		}
		coupleMap.get(generalId2).add(generalId1);

		setIntimacy(generalId1, generalId2, 100);
	}

	public void addEnemy(String generalId1, String generalId2) {
		if (!enemyMap.containsKey(generalId1)) {
			enemyMap.put(generalId1, new SortedList<String>());
		}
		enemyMap.get(generalId1).add(generalId2);

		if (!enemyMap.containsKey(generalId2)) {
			enemyMap.put(generalId2, new SortedList<String>());
		}
		enemyMap.get(generalId2).add(generalId1);

		setIntimacy(generalId1, generalId2, 0);
	}

	public boolean willAssistAttack(String attackerId, String potentialHelperId) {
		int intimacy = getIntimacy(attackerId, potentialHelperId);

		if (isSwornBrother(attackerId, potentialHelperId) || isCouple(attackerId, potentialHelperId)) {
			return true;
		}

		if (isEnemy(attackerId, potentialHelperId)) {
			return false;
		}

		return intimacy >= 75;
	}

	public int getHatred(String attackerId, String targetId) {
		if (hatredMap.containsKey(attackerId) && hatredMap.get(attackerId).containsKey(targetId)) {
			return hatredMap.get(attackerId).get(targetId);
		}
		return 0;
	}

	public void addHatred(String attackerId, String targetId, int hatred) {
		if (!hatredMap.containsKey(attackerId)) {
			hatredMap.put(attackerId, new ObjectMap<String, Integer>());
		}
		int currentHatred = HelperUtils.toInt(hatredMap.get(attackerId));
		hatredMap.get(attackerId).put(targetId, MathUtils.min(100, currentHatred + hatred));
	}

}
