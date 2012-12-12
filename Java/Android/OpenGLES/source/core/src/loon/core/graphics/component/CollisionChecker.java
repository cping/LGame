package loon.core.graphics.component;

import java.util.Iterator;
import java.util.List;

import loon.core.LRelease;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */

@SuppressWarnings({ "rawtypes" })
public interface CollisionChecker extends LRelease {

	void initialize(int cellSize);

	void addObject(Actor actor);

	void removeObject(Actor actor);

	void clear();

	void updateObjectLocation(Actor actor, float x, float y);

	void updateObjectSize(Actor actor);

	List getObjectsAt(float x, float y, Class cls);

	List getIntersectingObjects(Actor actor, Class cls);

	List getObjectsInRange(float x, float y, float r, Class cls);

	List getNeighbours(Actor actor, float distance, boolean d, Class cls);

	List getObjects(Class actor);

	List getObjectsList();

	Actor getOnlyObjectAt(Actor actor, float x, float y, Class cls);

	Actor getOnlyIntersectingObject(Actor actor, Class cls);

	Iterator getActorsIterator();

	List getActorsList();
}
