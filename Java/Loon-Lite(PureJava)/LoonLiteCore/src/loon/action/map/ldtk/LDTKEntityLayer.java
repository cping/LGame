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
package loon.action.map.ldtk;

import loon.Json;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class LDTKEntityLayer extends LDTKLayer {

	private final LDTKMap _mapIn;

	private final ObjectMap<String, TArray<LDTKEntity>> _entityMap;

	public LDTKEntityLayer(LDTKMap map, Json.Object v, LDTKTypes types) {
		super(v);
		_mapIn = map;
		_entityMap = new ObjectMap<String, TArray<LDTKEntity>>();
		Json.Array entityInstanceList = v.getArray("entityInstances");
		for (int i = 0; i < entityInstanceList.length(); i++) {
			parseEntity(entityInstanceList.getObject(i), types);
		}
	}

	private void parseEntity(Json.Object entityJson, LDTKTypes types) {
		LDTKEntity entity = new LDTKEntity(entityJson, types, this);
		if (_entityMap.containsKey(entity.getId())) {
			_entityMap.get(entity.getId()).add(entity);
		} else {
			TArray<LDTKEntity> entityArray = new TArray<LDTKEntity>();
			entityArray.add(entity);
			_entityMap.put(entity.getId(), entityArray);
		}
	}

	public TArray<LDTKEntity> getEntitiesOfType(String type) {
		return _entityMap.get(type);
	}

	public LDTKMap getMap() {
		return _mapIn;
	}

}
