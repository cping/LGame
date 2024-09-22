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

import loon.LSysException;
import loon.utils.IntArray;
import loon.utils.ObjectMap;

public class LDTKNeighbours {

	private ObjectMap<LDTKNeighbourDirection, IntArray> _neighbours;

	public LDTKNeighbours() {
		_neighbours = new ObjectMap<LDTKNeighbourDirection, IntArray>();
		_neighbours.put(LDTKNeighbourDirection.Left, new IntArray());
		_neighbours.put(LDTKNeighbourDirection.Right, new IntArray());
		_neighbours.put(LDTKNeighbourDirection.Up, new IntArray());
		_neighbours.put(LDTKNeighbourDirection.Down, new IntArray());
		_neighbours.put(LDTKNeighbourDirection.All, new IntArray());
	}

	public void add(int levelUid, char let) {
		add(levelUid, getForLetter(let));
	}

	public IntArray get(LDTKNeighbourDirection direction) {
		return _neighbours.get(direction);
	}

	public void add(int levelUid, LDTKNeighbourDirection dir) {
		if (dir == null) {
			throw new LSysException("The ldtk direction cannot be null!");
		}
		_neighbours.get(dir).add(levelUid);
		_neighbours.get(LDTKNeighbourDirection.All).add(levelUid);
	}

	public static LDTKNeighbourDirection getForLetter(char let) {
		switch (let) {
		case 'w':
			return LDTKNeighbourDirection.Left;
		case 'e':
			return LDTKNeighbourDirection.Right;
		case 'n':
			return LDTKNeighbourDirection.Up;
		case 's':
			return LDTKNeighbourDirection.Down;
		default:
			return LDTKNeighbourDirection.All;
		}
	}

}
