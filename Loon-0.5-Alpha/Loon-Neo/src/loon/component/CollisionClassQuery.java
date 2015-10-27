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


public class CollisionClassQuery implements CollisionQuery {

	private String flag;

	private CollisionQuery subQuery;

	public CollisionClassQuery(String flag, CollisionQuery subQuery) {
		this.flag = flag;
		this.subQuery = subQuery;
	}

	@Override
	public boolean checkCollision(Actor actor) {
		return flag.equals(actor.getFlag()) ? this.subQuery.checkCollision(actor)
				: false;
	}
}
