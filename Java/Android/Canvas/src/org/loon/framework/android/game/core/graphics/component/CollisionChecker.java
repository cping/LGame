package org.loon.framework.android.game.core.graphics.component;

import java.util.List;


/**
 * 
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

@SuppressWarnings("rawtypes")
public interface CollisionChecker {

	void initialize(int cellSize);

	void addObject(Actor actor);

	void removeObject(Actor actor);

	void clear();
	
	void updateObjectLocation(Actor actor, int x, int y);

	void updateObjectSize(Actor actor);

	List getObjectsAt(int x, int y, Class cls);

	List getIntersectingObjects(Actor actor, Class cls);

	List getObjectsInRange(int actor, int x, int y, Class cls);

	List getNeighbours(Actor actor, int x, boolean y, Class cls);

	List getObjects(Class actor);

	List getObjectsList();

	Actor getOnlyObjectAt(Actor actor, int x, int y, Class cls);

	Actor getOnlyIntersectingObject(Actor actor, Class cls);

}
