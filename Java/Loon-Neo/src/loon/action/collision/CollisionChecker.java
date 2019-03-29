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
package loon.action.collision;

import loon.utils.LIterator;
import loon.utils.TArray;

public interface CollisionChecker{

	void initialize(int cellSize);

	void addObject(CollisionObject actor);

	void removeObject(CollisionObject actor);

	void clear();

	void updateObjectLocation(CollisionObject actor, float x, float y);

	void updateObjectSize(CollisionObject actor);

	TArray<CollisionObject> getObjectsAt(float x, float y, String flag);

	TArray<CollisionObject> getIntersectingObjects(CollisionObject actor, String flag);

	TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String flag);

	TArray<CollisionObject> getNeighbours(CollisionObject actor, float distance, boolean d, String flag);

	TArray<CollisionObject> getObjects(String actor);

	TArray<CollisionObject> getObjectsList();

	CollisionObject getOnlyObjectAt(CollisionObject actor, float x, float y, String flag);

	CollisionObject getOnlyIntersectingObject(CollisionObject actor, String flag);

	LIterator<CollisionObject> getActorsIterator();

	TArray<CollisionObject> getActorsList();
	
	void dispose();
}
