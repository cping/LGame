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
package loon.component;

import loon.utils.LIterator;
import loon.utils.TArray;

public interface CollisionChecker{

	void initialize(int cellSize);

	void addObject(Actor actor);

	void removeObject(Actor actor);

	void clear();

	void updateObjectLocation(Actor actor, float x, float y);

	void updateObjectSize(Actor actor);

	TArray<Actor> getObjectsAt(float x, float y, String flag);

	TArray<Actor> getIntersectingObjects(Actor actor, String flag);

	TArray<Actor> getObjectsInRange(float x, float y, float r, String flag);

	TArray<Actor> getNeighbours(Actor actor, float distance, boolean d, String flag);

	TArray<Actor> getObjects(String actor);

	TArray<Actor> getObjectsList();

	Actor getOnlyObjectAt(Actor actor, float x, float y, String flag);

	Actor getOnlyIntersectingObject(Actor actor, String flag);

	LIterator<Actor> getActorsIterator();

	TArray<Actor> getActorsList();
	
	void dispose();
}
